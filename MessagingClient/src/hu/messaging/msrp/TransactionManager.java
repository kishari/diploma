package hu.messaging.msrp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Observer {

	private Map<String, Map<String, Message>> acknowledgedMessage = Collections.synchronizedMap(new HashMap<String, Map<String, Message>>());
	private Map<String, Map<String, Message>> sentMessage = Collections.synchronizedMap(new HashMap<String, Map<String, Message>>());


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
			if (this.sentMessage.containsKey(r.getMessageId())) {
				this.sentMessage.get(r.getMessageId()).put(r.getTransactionId(), r);
			}
			else {
				this.sentMessage.put(r.getMessageId(), new HashMap<String, Message>());
				this.sentMessage.get(r.getMessageId()).put(r.getTransactionId(), r);
			}
			if (r.getEndToken() == '$') {
				for (String key : this.sentMessage.keySet()) {
					System.out.println("Outer key: " + key);
					for (String key2 : this.sentMessage.get(key).keySet()) {
						System.out.println("Inner key: " + key2 + " >> value: " + this.sentMessage.get(key).get(key2));
					}
				}
			}
			
		}
		else if (o.toString().contains("IncomingMessageProcessor")) {
			//System.out.println("hehe");
		}		
	}

}
