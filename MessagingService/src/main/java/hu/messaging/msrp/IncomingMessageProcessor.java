package hu.messaging.msrp;

import hu.messaging.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class IncomingMessageProcessor extends Observable implements Runnable {
	
	private BlockingQueue<Message> incomingMessageQueue;
	private boolean running = false;
	
	public IncomingMessageProcessor(BlockingQueue<Message> incomingMessageQueue, 
									TransactionManager transactionManager ) {
		this.incomingMessageQueue = incomingMessageQueue;
		this.addObserver(transactionManager);
	}
	
	public void run() {
		int cnt = 0;
		while(running) {
			try {
				Message mIn = this.incomingMessageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS);
				if (mIn != null) {
					cnt++;
					//System.out.println("incoming message counter: " + cnt);
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
		if (chunk.getMethod() == Constants.methodSEND) {
			Request req = (Request) chunk;
			Response ack = createAcknowledgement(req);
			
			Map<String, Message> map = new HashMap<String, Message>();
			map.put(Keys.incomingRequest, req);
			map.put(Keys.createdAck, ack);
			
			this.setChanged();
			this.notifyObservers(map);
			
		}
		else if ( chunk.getMethod() == Constants.method200OK ){
			Response resp = (Response) chunk;
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
