package hu.messaging.msrp;

import hu.messaging.msrp.model.Constants;
import hu.messaging.msrp.listener.MSRPEvent;
import hu.messaging.msrp.model.CompleteMessage;
import hu.messaging.msrp.model.Keys;
import hu.messaging.msrp.model.Message;
import hu.messaging.msrp.model.Request;
import hu.messaging.msrp.model.Response;
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
import java.util.concurrent.TimeUnit;

import java.util.concurrent.LinkedBlockingQueue;

public class TransactionManager extends Observable implements Observer {

	private int numOfUnacknowledgedChunks = 0;	
	private int sendCounterTest = 0;
	
	private Map<String, Request> requestMap;
	private List<Request> requestList;
	private Session session;
	private Map<String, Request> incomingMessages = Collections.synchronizedMap(new HashMap<String, Request>());

	private SenderThread sender;

	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	private boolean incomingMessageProcessorStopped = true;
	private boolean outgoingMessageProcessorStopped = true;
	private boolean senderStopped = true;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<CompleteMessage> outgoingMessageQueue,
			  				  Session session) {
		
			this.session = session;
			this.addObserver(session);
			outgoingMessageProcessor = new OutgoingMessageProcessor(outgoingMessageQueue, session, this);
			outgoingMessageProcessor.start();
			outgoingMessageProcessorStopped = false;
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, this);
			incomingMessageProcessor.start();
			incomingMessageProcessorStopped = false;
			
			sender = new SenderThread(this);
			sender.start();
			
	}
	
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
		this.sender.stop();
		
		(new Thread() {
			public void run() {
				do {
					try {
						Thread.sleep(50);
					}
					catch(InterruptedException e) {}					
				}while(!incomingMessageProcessorStopped || !outgoingMessageProcessorStopped || !senderStopped);
				
				TransactionManager.this.setChanged();
				TransactionManager.this.notifyObservers();
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	public void update(Observable o, Object obj) {
		if (o.toString().contains(OutgoingMessageProcessor.class.getSimpleName())) {
			if (obj == null) {
				outgoingMessageProcessorStopped = true;
				return;
			}
			
			requestList = (List<Request>) obj;			
			requestMap = new HashMap<String, Request>();
			
			for (Request r : requestList) {
				requestMap.put(r.getTransactionId(), r);
			}
			
			this.sender.getSenderQueue().addAll(requestList);
			
		}
		else if (o.toString().contains(IncomingMessageProcessor.class.getSimpleName())) {	
			if (obj == null) {
				incomingMessageProcessorStopped = true;
				return;
			}
			
			Map<String, Message> map = null;
			Message m = null;
			if (obj instanceof Map<?, ?>) {
				map = (Map<String, Message>) obj;
				m = map.get(Keys.incomingRequest);
			}
			else if (obj instanceof Message) {
				m = (Message) obj;
			}
			if (m.getMethod().equals(Message.MethodType.Send)) {
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);
				//Nyugtát küldünk
				this.sender.getSenderQueue().add(map.get(Keys.createdAck));
				
				if (req.getEndToken() == '$') {
					System.out.println("Utolso csomag is megjott...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageReceivingSuccess, this.session.getSenderConnection().getRemoteSipUri());
					event.setMessageId(req.getMessageId());
					
					List<Request> chunks = new ArrayList<Request>();
					for (String key : this.incomingMessages.keySet()) {
						chunks.add(this.incomingMessages.get(key));
					}
					
					//Megvárjuk, hogy minden nyugta kiküldésre kerüljön
					/*while (this.sender.getSenderQueue().size() > 0) {
						try {
							Thread.sleep(200);
							System.out.println("var amig nem 0. senderQueue merete: " + this.sender.getSenderQueue().size());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					*/
					event.setCompleteMessage(new CompleteMessage(req.getMessageId(), MSRPUtil.createMessageContentFromChunks(chunks)));
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
			else if (m.getMethod().equals(Message.MethodType._200OK)) {
				Response resp = (Response) m;
				Request ackedReq = this.requestMap.remove(resp.getTransactionId());
				//printTo(m, true);
				decrementNumOfUnacknowledgedChunks();

				//Ha van még küldendõ kérés, akkor elküldjük
				//if (ackCounter < requestList.size()) {
				//	this.sender.getSenderQueue().add(requestList.get(ackCounter));
				//}												
				
				if (isAckedTotalSentMessage(ackedReq)) {
					System.out.println("minden nyugtazva...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageSentSuccess);
					event.setMessageId(ackedReq.getMessageId());
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
		}	
		else if (o.toString().contains(SenderThread.class.getSimpleName())) {	
			senderStopped = true;
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
	
	private class SenderThread extends Observable implements Runnable {
		
		private BlockingQueue<Message> senderQueue = new LinkedBlockingQueue<Message>();
		private boolean isRunning = false;
		boolean isAck = false;
		
		public SenderThread(TransactionManager tManager) {
			this.addObserver(tManager);
		}
		
		public void run() {
			senderStopped = false;
			while (isRunning) {
				try {
					
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
					Message data = senderQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS);
					if (data != null) {						
						isAck = data instanceof Response;
						
						if (!isAck) incrementNumOfUnacknowledgedChunks();
						
						session.getSenderConnection().send(data.toString().getBytes());
						//printTo(data, false);
						sendCounterTest++;
						
						Thread.sleep(Constants.senderThreadSleepTime);
						
						if (sendCounterTest % 100 == 0 || sendCounterTest > 4900) {
							System.out.println("sent counter: " + sendCounterTest);
							if (sendCounterTest > 4910) {
								//Request r = (Request)data;
								//System.out.println(data.toString());
							}
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
			this.setChanged();
			this.notifyObservers();
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
	
	private void incrementNumOfUnacknowledgedChunks() {
		synchronized(this) {
			numOfUnacknowledgedChunks++;
		}		
	}
	
	private void decrementNumOfUnacknowledgedChunks() {
		synchronized(this) {
			numOfUnacknowledgedChunks--;
		}		
	}

	
	//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, String fileExtension) {
		try {
			OutputStream out = null;
			File contentFile = new File("c:\\diploma\\testing\\clientQueueContentFile." + fileExtension);
			out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			
			out.write(data);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
	
	public void printTo(Message data, boolean isAck) {
		try {
			String line = "";
			File f = null;
			if (isAck) {
				Response r = (Response)data;
				line =  r.getTransactionId() + "\n";
				f = new File("c:\\diploma\\testing\\clientAckBytes.txt");
			}
			else {
				Request d = (Request)data;
				line = d.getFirstByte() + "\t" + d.getLastByte() + "\t" + d.getTransactionId() + "\n";
				f = new File("c:\\diploma\\testing\\clientMsgBytes.txt");
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
//<<<<<<<<<<<<<<TESZT
}
