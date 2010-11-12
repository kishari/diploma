package hu.messaging.msrp;

import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Runnable {

	private BlockingQueue<MSRPMessage> messageQueue;
	
	public void run() {
		while(true) {
			try {
				MSRPMessage m = messageQueue.take();
				System.out.println("TransactionManager kivett egy üzenetet a session messagequeue-ból:");
				System.out.println(m.toString());
				System.out.println();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public TransactionManager(BlockingQueue<MSRPMessage> queue) {
		this.messageQueue = queue;
	}

}
