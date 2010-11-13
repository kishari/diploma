package hu.messaging.msrp;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Runnable {

	private BlockingQueue<MSRPMessage> messageQueue;
	//private BlockingQueue<MSRPMessage> outputQueue;
	private SenderConnection senderConnection;
	
	public void run() {
		while(true) {
			try {
				MSRPMessage m = messageQueue.take();
				System.out.println("TransactionManager kivett egy üzenetet a session messagequeue-ból:");
				System.out.println(m.toString());
				System.out.println();
				//outputQueue.put(processMessage(m));
				
				//Tesztelés végett visszaküldjük ugyanazt az üzenetet a usernak, amikor kaptunk
				System.out.println(processMessage(m).toString());
				if (senderConnection != null) {
					System.out.println("TransactionManager sendig message to " + senderConnection.getSipUri() + " " +
										senderConnection.getRemoteAddress()+ ":" + senderConnection.getRemotePort());
					for (int i = 0; i < 3; i++) {
						senderConnection.send(processMessage(m).toString().getBytes());
						Thread.sleep(200);
					}
					for (int i = 0; i < 3; i++) {
						senderConnection.send(processMessage(m).toString().getBytes());
					}

				}
				else {
					System.out.println("tranzakciomanager sender null");
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public TransactionManager(BlockingQueue<MSRPMessage> messageQueue, SenderConnection senderConnection) {
		this.messageQueue = messageQueue;
		//this.outputQueue = outputQueue;
		this.senderConnection = senderConnection;
	}
	
	private MSRPMessage processMessage(MSRPMessage m) {
		System.out.println("TransactionManager.processMessage");
		//Itt majd szépen feldolgozzuk
		MSRPMessage retMessage = new MSRPMessage();
		retMessage.setContent(m.getContent());
		retMessage.setContentType(m.getContentType());
		retMessage.setEndToken(m.getEndToken());
		retMessage.setFromPath(m.getToPath());
		retMessage.setToPath(m.getFromPath());
		
		//return retMessage;
		return m;
	}

}
