package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.util.MSRPUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TransactionManager implements Observer {

	private int ackCounter = 0;
	private int numOfRequest = 0;
	private int sentRequestNumber = 0;
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
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, session, this);
			incomingMessageProcessor.start();
			
			sender = new SenderThread();
			sender.start();
			
	}
	
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
	}

	public void update(Observable o, Object obj) {
		if (o.toString().contains("OutgoingMessageProcessor")) {
			requestList = (List<Request>) obj;
			numOfRequest = requestList.size();
			
			Collections.sort(requestList);
			
			requestMap = new HashMap<String, Request>();
			
			for (Request r : requestList) {
				requestMap.put(r.getTransactionId(), r);
			}
			
			this.sender.getSenderQueue().add(requestList.get(0));
		}
		else if (o.toString().contains("IncomingMessageProcessor")) {
			Message m = (Message) obj;
			if (m.getMethod() == Constants.methodSEND) {
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);
				
				if (req.getEndToken() == '$') {
					System.out.println("Utolso csomag is megjott...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageReceivingSuccess, "completeMessage arrived", null);
					event.setMessageId(req.getMessageId());
					List<Request> chunks = new ArrayList<Request>();
					for (String key : this.incomingMessages.keySet()) {
						chunks.add(this.incomingMessages.get(key));
					}
					Collections.sort(chunks);
					
					event.setCompleteMessage(new CompleteMessage(req.getMessageId(), MSRPUtil.createMessageContentFromChunks(chunks)));
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				//System.out.println("200OK jött: " + resp.getTransactionId());
				ackCounter++;
				Request ackedReq = this.requestMap.remove(resp.getTransactionId());
				if (ackCounter < requestList.size()) {
					this.sender.getSenderQueue().add(requestList.get(ackCounter));
				}												
				
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
		System.out.print("sentRequestNumber / numOfRequest: " + sentRequestNumber + " / " + numOfRequest);
		System.out.println(" ackCounter: " + ackCounter);
		int mapSize = this.requestMap.size();
		boolean empty = this.requestMap.isEmpty();
		boolean lastChunk = (ackedReq.getEndToken() == '$');
		boolean t = empty && lastChunk;
		System.out.println("isAckedTotalSentMessage(mapsize: " + mapSize + " token:" + ackedReq.getEndToken() + ") : " + empty + " and " + lastChunk);
		return t;
	}
	
	private class SenderThread implements Runnable {
		
		private BlockingQueue<Request> senderQueue = new java.util.concurrent.LinkedBlockingQueue<Request>();
		private boolean isRunning = false;
		public void run() {
			while (isRunning) {
				try {
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
					Request data = senderQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 
					if (data != null) {
						//System.out.println("send: " + data.getFirstByte() + " - " + data.getLastByte() + ":" + data.getEndToken());
						session.getSenderConnection().sendChunk(data.toString().getBytes());
						sentRequestNumber++;
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

		public BlockingQueue<Request> getSenderQueue() {
			return senderQueue;
		}
	}
}
