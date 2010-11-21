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
		System.out.println("TransactionManager update");
		System.out.println(o.toString());
	}

}
