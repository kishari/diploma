package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.msrp.event.MSRPEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Observer {

	private boolean isSentMessageChunk = false;
	private Session session;
	private Map<String, Request> acknowledgedMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> sentMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> incomingMessages = Collections.synchronizedMap(new HashMap<String, Request>());


	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<byte[]> outgoingMessageQueue,
			  				  Session session) {
		
			this.session = session;
			outgoingMessageProcessor = new OutgoingMessageProcessor(outgoingMessageQueue, session, this);
			outgoingMessageProcessor.start();
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, session, this);
			incomingMessageProcessor.start();
			
	}
	
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
	}

	public void update(Observable o, Object obj) {
		System.out.println("TManager update: " + o.toString());
		if (o.toString().contains("OutgoingMessageProcessor")) {
			Request r = (Request) obj;
			this.sentMessages.put(r.getTransactionId(), r);		
			isSentMessageChunk = true;
		}
		else if (o.toString().contains("IncomingMessageProcessor")) {
			Message m = (Message) obj;
			if (m.getMethod() == Constants.methodSEND) {
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);
				
				if (req.getEndToken() == '$') {
					System.out.println("Utolso csomag is megjott...");
					//Itt majd szépen feldolgozzuk (pl mentés fájlba)
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				Request ackedReq = this.sentMessages.remove(resp.getTransactionId());
				this.acknowledgedMessages.put(ackedReq.getTransactionId(), ackedReq);
				
				if (isAckedTotalSentMessage()) {
					this.isSentMessageChunk = false;
					MSRPEvent event = new MSRPEvent("sentSuccess", MSRPEvent.messageSentSuccessCode);
					event.setMessageId(ackedReq.getMessageId());
					this.session.getMsrpStack().notifyListeners(event);
					this.acknowledgedMessages.clear();
				}
			}
		}		
	}

	private boolean isAckedTotalSentMessage() {
		boolean t = this.sentMessages.isEmpty() && this.isSentMessageChunk;
		return t;
	}
}
