package hu.messaging.msrp;

import hu.messaging.msrp.model.Constants;
import hu.messaging.msrp.model.Message;
import hu.messaging.msrp.model.Request;
import hu.messaging.msrp.model.Response;

import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class IncomingMessageProcessor extends Observable implements Runnable {
	
	private BlockingQueue<Message> incomingMessageQueue;
	private boolean running = false;
	
	public IncomingMessageProcessor(BlockingQueue<Message> incomingMessageQueue, 
									TransactionManager transactionManager) {
		this.incomingMessageQueue = incomingMessageQueue;
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
		this.setChanged();
		this.notifyObservers();
	}
	
	private void processIncomingMessage(Message chunk) throws IOException {		
		if (chunk.getMethod().equals(Message.MethodType.Send)) {
			Request req = (Request) chunk;						
			this.setChanged();
			this.notifyObservers(req);
			
		}
		else if ( chunk.getMethod().equals(Message.MethodType._200OK)){
			Response resp = (Response) chunk;
			this.setChanged();
			this.notifyObservers(resp);
		}		
	}	
	
	protected void start() {
		if (!running) {
			this.running = true;
			new Thread(this).start();
		}		
	}
	
	protected void stop() {
		this.running = false;
	}

}
