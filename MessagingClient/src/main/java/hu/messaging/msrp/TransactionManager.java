package hu.messaging.msrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.StringEscapeUtils;

public class TransactionManager implements Observer {

	private Map<String, Request> acknowledgedMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> sentMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> incomingMessages = Collections.synchronizedMap(new HashMap<String, Request>());


	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<byte[]> outgoingMessageQueue,
			  				  Session session) {
		
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
		System.out.println("update");
		System.out.println(o.toString());
		if (o.toString().contains("OutgoingMessageProcessor")) {
			Request r = (Request) obj;
			this.sentMessages.put(r.getTransactionId(), r);					
		}
		else if (o.toString().contains("IncomingMessageProcessor")) {
			Message m = (Message) obj;
			if (m.getMethod() == Constants.methodSEND) {
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);				
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				Request ackedReq = this.sentMessages.remove(resp.getTransactionId());
				this.acknowledgedMessages.put(ackedReq.getTransactionId(), ackedReq);
				
				if (this.sentMessages.isEmpty()) {
					System.out.println("Mindenre jott ack");
					List<Request> allMessage = new ArrayList<Request>();
					for (String key : this.acknowledgedMessages.keySet()) {
						allMessage.add(this.acknowledgedMessages.get(key));
					}
					
					Collections.sort(allMessage);
					
					String total = "";
					for (Request r : allMessage) {
						total += new String(r.getContent());
					}
					System.out.println(total);
				}
			}
		}		
	}

}