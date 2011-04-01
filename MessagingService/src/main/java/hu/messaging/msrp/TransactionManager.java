package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.util.MSRPUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TransactionManager implements Observer {

	private int numOfUnacknowledgedChunks = 0;
	private int sendCounterTest = 0;
	private int incCounterTest = 0;
	
	private Map<String, Request> requestMap;
	private List<Request> requestList;
	private Session session;
	private Map<String, Request> incomingMessages = Collections.synchronizedMap(new HashMap<String, Request>());

	private SenderThread sender;

	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<CompleteMessage> outgoingMessageQueue,
			  				  Session session) {
		
			this.session = session;
			outgoingMessageProcessor = new OutgoingMessageProcessor(outgoingMessageQueue, session, this);
			outgoingMessageProcessor.start();
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, this);
			incomingMessageProcessor.start();
			
			sender = new SenderThread();
			sender.start();
			
	}
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
		this.sender.stop();
	}

	@SuppressWarnings("unchecked")
	public void update(Observable o, Object obj) {
		//A kimenõ processort csak akkor használjuk, ha mi küldünk chunk-okat.(nyugtánál nem kerül bele semmi.)
		if (o.toString().contains(OutgoingMessageProcessor.class.getSimpleName())) {
			requestList = (List<Request>) obj;
			
			requestMap = new HashMap<String, Request>();
			
			for (Request r : requestList) {
				requestMap.put(r.getTransactionId(), r);
			}
			
			this.sender.getSenderQueue().addAll(requestList);
		}
		else if (o.toString().contains(IncomingMessageProcessor.class.getSimpleName())) {	
			Map<String, Message> map = null;
			Message m = null;
			if (obj instanceof Map<?, ?>) {
				map = (Map<String, Message>) obj;
				m = map.get(Keys.incomingRequest);
			}
			else if (obj instanceof Message) {
				m = (Message) obj;
			}
			if (m.getMethod() == Constants.methodSEND) {
				printTo(m, false);
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);
				incCounterTest++;
				if (incCounterTest % 20 == 0 || incCounterTest > 4900) {
					System.out.println("incCounterTest: " + incCounterTest);
				}
				//Nyugtát küldünk
				this.sender.getSenderQueue().add(map.get(Keys.createdAck));
				
				if (req.getEndToken() == '$') {
					System.out.println("Utolso csomag is megjott...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageReceivingSuccess, "complete message arrived", null);
					event.setMessageId(req.getMessageId());
					
					List<Request> chunks = new ArrayList<Request>();
					for (String key : this.incomingMessages.keySet()) {
						chunks.add(this.incomingMessages.get(key));
					}
					
					event.setCompleteMessage(new CompleteMessage(req.getMessageId(), MSRPUtil.createMessageContentFromChunks(chunks)));				
					
					System.out.println("senderQueue merete: " + this.sender.getSenderQueue().size());
					//Várunk amíg minden nyugtát vissza nem küldtünk
					while (this.sender.getSenderQueue().size() > 0) {
						try {
							Thread.sleep(100);
							System.out.println("var amig nem 0. senderQueue merete: " + this.sender.getSenderQueue().size());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;

				Request ackedReq = this.requestMap.remove(resp.getTransactionId());
				numOfUnacknowledgedChunks--;

				if (isAckedTotalSentMessage(ackedReq)) {
					System.out.println("minden nyugtazva...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageSentSuccess);
					event.setMessageId(ackedReq.getMessageId());
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
		}		
	}

	private boolean isAckedTotalSentMessage(Request ackedReq) {
		//System.out.println(" ackCounter: " + ackCounter);
		int mapSize = this.requestMap.size();
		boolean empty = this.requestMap.isEmpty();
		boolean lastChunk = (ackedReq.getEndToken() == '$');
		boolean t = empty && lastChunk;
		//System.out.println("isAckedTotalSentMessage(mapsize: " + mapSize + " token:" + ackedReq.getEndToken() + ") : " + empty + " and " + lastChunk);
		return t;
	}
	
	private class SenderThread implements Runnable {
		
		private BlockingQueue<Message> senderQueue = new LinkedBlockingQueue<Message>();
		private boolean isRunning = false;
		boolean isAck = false;
		public void run() {
			while (isRunning) {
				try {
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
					Message data = senderQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 					
					if (data != null) {
						isAck = data instanceof Response;
						
						if (!isAck) { 
							numOfUnacknowledgedChunks++;
							printTo(data, false);
						}
						else {
							printTo(data, true);
						}
						
						
						session.getSenderConnection().send(data.toString().getBytes());
						sendCounterTest++;						
						if (sendCounterTest % 250 == 0 || sendCounterTest > 4900) {
							System.out.println("sent counter: " + sendCounterTest);
						}
						
						if (!isAck && (numOfUnacknowledgedChunks)  > Constants.unAcknoledgedChunksLimit) {
							do {
								Thread.sleep(100);
								System.out.println("numOfUnacknowledgedChunks : " + numOfUnacknowledgedChunks);
							} while (numOfUnacknowledgedChunks > Constants.unAcknoledgedChunksLimit);
						}											
					}				
				}
				catch(IOException e) {}
				catch(InterruptedException e) {}				
			}
		}

		public void start() {
			isRunning = true;
			new Thread(this).start();
		}
		public void stop() {
			isRunning = false;
		}

		public BlockingQueue<Message> getSenderQueue() {
			return senderQueue;
		}
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TESZT
	public void printTo(Message data, boolean isAck) {
		try {
			String line = "";
			File f = null;
			if (isAck) {
				Response r = (Response)data;
				line =  r.getTransactionId() + "\n";
				f = new File("c:\\diploma\\testing\\serverAckBytes.txt");
			}
			else {
				Request d = (Request)data;
				line = d.getFirstByte() + "\t" + d.getLastByte() + "\t" + d.getTransactionId() + "\n";
				f = new File("c:\\diploma\\testing\\serverMsgBytes.txt");
			}

			OutputStream out = null;

			out = new BufferedOutputStream(new FileOutputStream(f, true));
			
			out.write(line.getBytes());
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
}
