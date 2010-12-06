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

	private static int ackCounter = 0;
	private static int reqCounter = 0;
	private int reqNumber = 0;
	private int ackedRequestNumber = 0;
	private Map<String, Request> requests;
	private List<Request> l;
	private Session session;
	private Map<String, Request> acknowledgedMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> sentMessages = Collections.synchronizedMap(new HashMap<String, Request>());
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
		System.out.println("TManager Update");
		if (o.toString().contains("OutgoingMessageProcessor")) {
			requests = (Map<String, Request>) obj;
			reqNumber = requests.size();
			l = new ArrayList<Request>();
			for (String key : requests.keySet()) {
				l.add(requests.get(key));
			}
			Collections.sort(l);
			
			if (reqNumber > 50) {				
				this.sender.getSenderQueue().addAll(l.subList(0, 50));
			}
			else {
				this.sender.getSenderQueue().addAll(l);
			}
			
						
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
					event.setCompleteMessage(new CompleteMessage(req.getMessageId(), MSRPUtil.createMessageContentFromChunks(chunks), null));
					this.session.getMsrpStack().notifyListeners(event);
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				//System.out.println("200OK jött: " + resp.getTransactionId());
				ackCounter++;
				System.out.println("ackCounter: " + ackCounter);
				Request ackedReq = null;				
				while (ackedReq == null) {
					ackedReq = this.requests.remove(resp.getTransactionId());
					boolean b = ackedReq != null;
					//System.out.println("map remove: " + b);
					if (ackedReq == null) {
						try {
							Thread.sleep(20);	
						}
						catch(InterruptedException e) { }						
					}
					else {
						//System.out.println("ackedReq: " + ackedReq.getTransactionId() + " (" + ackedReq.getEndToken() + ")");
						if ((49 + ackCounter) < l.size())
							this.sender.getSenderQueue().add(l.get(49 + ackCounter));
						
					}
				}
				
				//this.acknowledgedMessages.put(ackedReq.getTransactionId(), ackedReq);
				
				if (isAckedTotalSentMessage(ackedReq)) {
					System.out.println("minden nyugtazva...");
					MSRPEvent event = new MSRPEvent(MSRPEvent.messageSentSuccess);
					event.setMessageId(ackedReq.getMessageId());
					this.session.getMsrpStack().notifyListeners(event);
					this.acknowledgedMessages.clear();
				}
			}
		}		
	}

	private boolean isAckedTotalSentMessage(Request ackedReq) {
		System.out.print("reqCounter: " + reqCounter);
		System.out.println(" ackCounter: " + ackCounter);
		int mapSize = this.requests.size();
		boolean empty = this.requests.isEmpty();
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
						System.out.println("send: " + data.getFirstByte() + " - " + data.getLastByte() + ":" + data.getEndToken());
						session.getSenderConnection().sendChunk(data.toString().getBytes());
						reqCounter++;
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
