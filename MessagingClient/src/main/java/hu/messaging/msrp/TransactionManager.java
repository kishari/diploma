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
import java.util.concurrent.TimeUnit;

import java.util.concurrent.LinkedBlockingQueue;

public class TransactionManager implements Observer {

	private int ackCounter = 0;
	private int sentMsgCounter = 0;
	
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
		if (o.toString().contains(OutgoingMessageProcessor.class.getSimpleName())) {
			requestList = (List<Request>) obj;
			
			requestMap = new HashMap<String, Request>();
			
			for (Request r : requestList) {
				requestMap.put(r.getTransactionId(), r);
			}
			
			//this.sender.getSenderQueue().add(requestList.get(0));
/*			if (requestMap.size() > Constants.burstSize) {
				this.sender.getSenderQueue().addAll(requestList.subList(0, Constants.burstSize));
			}
			else {
*/				this.sender.getSenderQueue().addAll(requestList);
//			}
			
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
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);
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
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				ackCounter++;
				Request ackedReq = this.requestMap.remove(resp.getTransactionId());

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
		public void run() {
			while (isRunning) {
				try {
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
					Message data = senderQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 
					if (data != null) {
						System.out.println("send data: " + sentMsgCounter);
						//Request r = (Request)data;
						//printToFile(Base64.decodeBase64(r.getContent()), "mp3");
						if (sentMsgCounter % Constants.burstSize == 0) {
							do {
								Thread.sleep(10);
								System.out.println(ackCounter);
							} while (ackCounter % Constants.burstSize != 0);
						}
						
						session.getSenderConnection().send(data.toString().getBytes());
						sentMsgCounter++;
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
//<<<<<<<<<<<<<<TESZT
}
