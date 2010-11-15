package hu.messaging.msrp;

import hu.messaging.msrp.util.MSRPUtil;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Runnable {

	private BlockingQueue<Message> incomingMessageQueue;
	private BlockingQueue<byte[]> outgoingMessageQueue;
	private SenderConnection senderConnection;
	private boolean running = false;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<byte[]> outgoingMessageQueue,
			  				  SenderConnection senderConnection) {
		
			this.incomingMessageQueue = incomingMessageQueue;
			this.outgoingMessageQueue = outgoingMessageQueue;
			this.senderConnection = senderConnection;
	}
	
	public void run() {
		while(running) {
				Message mIn = incomingMessageQueue.poll();
				if (mIn != null) {
					System.out.println("TransactionManager kivett egy üzenetet a session incomingMessageQueue-bol:");
					System.out.println(mIn.toString());
					System.out.println();
					
					processIncomingMessage(mIn);
				}
				byte[] mOut = outgoingMessageQueue.poll();
				if (mOut != null) {
					processOutgoingMessage(mOut);
				}
		}
	}
	
	public void start() {
		this.running = true;
		this.start();
	}
	
	public void stop() {
		this.running = false;
	}
	
	private void processIncomingMessage(Message chunk) {
		System.out.println("TransactionManager.processIncomingMessage... ");
		
		if (chunk.getMethod() == Constants.methodSEND) {
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is 'send' message!");
			Request req = (Request) chunk;
			System.out.println(req.toString());
			Response ack = createAcknowledgement(req);
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is 'send' message! Ack created: \n" + ack.toString() );
		}
		else if ( chunk.getMethod() == Constants.method200OK ){
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is '200 OK' message!");
			Response resp = (Response) chunk;
			System.out.println(resp.toString());
		}		
	}
	
	private void processOutgoingMessage(byte[] chunk) {
		System.out.println("TransactionManager.processOutgoingMessage... ");
		Message mOut = MSRPUtil.createMessage(new String(chunk));
		System.out.println("TransactionManager.processOutgoingMessage: after message create: " + mOut.toString());
		
	}
	
	private Response createAcknowledgement(Request incomingMessage) {
		Response ack = new Response();
		
		ack.setMethod(Constants.method200OK);
		ack.setToPath(incomingMessage.getFromPath());
		ack.setFromPath(incomingMessage.getToPath());
		ack.setTransactionId(incomingMessage.getTransactionId());
		ack.setEndToken('$');
				
		return ack;
	}

}
