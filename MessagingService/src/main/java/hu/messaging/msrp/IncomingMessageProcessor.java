package hu.messaging.msrp;

import hu.messaging.Constants;

import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class IncomingMessageProcessor extends Observable implements Runnable {
	
	private BlockingQueue<Message> incomingMessageQueue;
	private Session session;
	private boolean running = false;
	
	public IncomingMessageProcessor(BlockingQueue<Message> incomingMessageQueue, 
									Session session, 
									TransactionManager transactionManager ) {
		this.incomingMessageQueue = incomingMessageQueue;
		this.session = session;
		this.addObserver(transactionManager);
	}
	
	public void run() {
		while(running) {
			try {
				Message mIn = this.incomingMessageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS);
				if (mIn != null) {
					processIncomingMessage(mIn);
				}				
			}
			catch(InterruptedException e) {}
			catch(IOException e) { 
				e.printStackTrace();
			}
		}
	}
	
	private void processIncomingMessage(Message chunk) throws IOException {
		System.out.println("IncomingMessageProcessor processIncomingMessage... ");
		
		if (chunk.getMethod() == Constants.methodSEND) {
			System.out.println("IncomingMessageProcessor.processIncomingMessage. Incoming message is 'send' message!");
			Request req = (Request) chunk;
			System.out.println(req.toString());
			Response ack = createAcknowledgement(req);
			System.out.println("IncomingMessageProcessor.processIncomingMessage. Incoming message is 'send' message! Ack created: \n" + ack.toString() );
			
			this.session.getSenderConnection().sendChunk(ack.toString().getBytes());
		}
		else if ( chunk.getMethod() == Constants.method200OK ){
			System.out.println("IncomingMessageProcessor.processIncomingMessage. Incoming message is '200 OK' message!");
			Response resp = (Response) chunk;
			System.out.println(resp.toString());
			this.setChanged();
			this.notifyObservers(resp);
		}		
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
	
	public void start() {
		this.running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		this.running = false;
	}

}
