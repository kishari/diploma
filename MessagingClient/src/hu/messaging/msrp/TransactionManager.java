package hu.messaging.msrp;

import java.util.concurrent.BlockingQueue;

public class TransactionManager {

	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<byte[]> outgoingMessageQueue,
			  				  Session session) {
		
			outgoingMessageProcessor = new OutgoingMessageProcessor(outgoingMessageQueue, session);
			outgoingMessageProcessor.start();
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, session);
			incomingMessageProcessor.start();
			
	}
	
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
	}

}
