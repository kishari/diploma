package hu.messaging.msrp;

import hu.messaging.msrp.util.MSRPUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class OutgoingMessageProcessor extends Observable implements Runnable {

	private boolean running = false;
	private BlockingQueue<byte[]> outgoingMessageQueue;
	private Session session;
	
	public OutgoingMessageProcessor(BlockingQueue<byte[]> outgoingMessageQueue, 
									Session session,
									TransactionManager transactionManager ) {
		this.outgoingMessageQueue = outgoingMessageQueue;
		this.session = session;
		this.addObserver(transactionManager);
	}
	
	public void run() {
		while (running) {
			try {
				//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
				//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
				byte[] data = this.outgoingMessageQueue.poll(500, TimeUnit.MILLISECONDS); 
				if (data != null) {
					processOutgoingMessage(data);
				}				
			}
			catch(InterruptedException e) {}
			
		}
	}
	
	private void processOutgoingMessage(byte[] completeMessage) {
		System.out.println("OutgoingProcessor processOutgoingMessage... ");
		System.out.println(new String(completeMessage));

		int chunkSize = 10;
		
		List<byte[]> chunks = splitMessageToChunks(completeMessage, chunkSize);
		
		int offset = 1;
		char endToken = '+';
		String tId = "transactionId";
		String messageId = "messageId";
		int i = 0;
		for (byte[] chunk : chunks) {
			tId += Integer.toString(i);
			i++;
			//tId = MSRPUtil.generateRandomString(10);
			if (chunk.length < chunkSize) {
				endToken = '$';
			}
			
			Request mOut = MSRPUtil.createRequest(chunk, session.getLocalUri(), session.getRemoteUri(),
												  tId, messageId, 
												  offset, chunk.length, completeMessage.length, 
												  endToken);
			offset += chunk.length;
			//System.out.println("OutgoingProcessor: after message create: \n" + mOut.toString());
			
			try {
				this.session.getSenderConnection().sendChunk(mOut.toString().getBytes());
			}
			catch (IOException e) {}
			this.setChanged();
			this.notifyObservers(mOut);
		}				
	}
	
	private List<byte[]> splitMessageToChunks(byte[] message, int chunkSize) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		
		double div = message.length / (double) chunkSize;
		int numOfChunks = (int)Math.floor(div);
		
		int i = 0;
		int index = 0;
		while( i < numOfChunks ) {
			byte[] c = new byte[chunkSize];
			System.arraycopy(message, index, c, 0, chunkSize);
			chunks.add(c);
			index += chunkSize;
			i++;
		}
		
		int lastChunkSize = message.length - index;
		byte[] c = new byte[lastChunkSize];
		System.arraycopy(message, index, c, 0, lastChunkSize);
		chunks.add(c);		
		
		return chunks;
	}
	
	public void start() {
		this.running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		this.running = false;
	}	
}
