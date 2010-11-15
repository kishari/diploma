package hu.messaging.msrp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session {

	private URI localUri;
	private URI remoteUri;	
	private String id;
	private SenderConnection senderConnection;
	
	private BlockingQueue<Message> incomingMessageQueue = new LinkedBlockingQueue<Message>();
	private BlockingQueue<byte[]> outgoingMessageQueue = new LinkedBlockingQueue<byte[]>();

	private TransactionManager transactionManager = null;

	public Session(URI localUri, URI remoteUri, SenderConnection senderConnection) {
		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.id = localUri.toString()+remoteUri.toString();
		this.senderConnection = senderConnection;
		this.transactionManager = new TransactionManager(incomingMessageQueue, outgoingMessageQueue, senderConnection, this);
		Thread t = new Thread(transactionManager);
		t.setDaemon(true);
		t.start();
	}
	
	public void sendMessage(byte[] completeMessage) {		
		//List<byte[]> chunks = splitMessageToChunks(completeMessage, 10);
		//System.out.println("Elkezdi bedobalni a queue-ba az uzeneteket");
		//for (byte[] chunk : chunks) {
			try {
				putMessageIntoOutgoingMessageQueue(completeMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	//	}
	}
	
	public void putMessageIntoIncomingMessageQueue(Message message) throws InterruptedException {
		System.out.println("Session.putMessageIntoIncomingMessageQueue");
		this.incomingMessageQueue.put(message);
	}
	
	public void putMessageIntoOutgoingMessageQueue(byte[] message) throws InterruptedException {
		System.out.println("Session.putMessageIntoOutgoingMessageQueue");
		this.outgoingMessageQueue.put(message);
	}
	
	private List<byte[]> splitMessageToChunks(byte[] message, int chunkSize) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		//System.out.println("message length: " + message.length);
		double div = message.length / (double) chunkSize;
		//System.out.println("Oszto (double)" + div);
		int numOfChunks = (int)Math.floor(div);
		//System.out.println("numOfChunks: (floor) " + numOfChunks);
		
		int i = 0;
		int index = 0;
		while( i < numOfChunks ) {
			byte[] c = new byte[chunkSize];
			//System.out.println("copy to chunk from: " + index + " length " + chunkSize);
			
			for (int j = 0; j < chunkSize; j++) {
				 char ch = (char)message[index + j];
				//System.out.print(ch);
			}
			//System.out.println();
			System.arraycopy(message, index, c, 0, chunkSize);
			chunks.add(c);
			index += chunkSize;
			i++;
		}
		
		int lastChunkSize = message.length - index;
		byte[] c = new byte[lastChunkSize];
		System.arraycopy(message, index, c, 0, lastChunkSize);
		chunks.add(c);
		//System.out.println("last:");
		for (int j = 0; j < lastChunkSize; j++) {
			 char ch = (char)message[index + j];
			//System.out.print(ch);
		}
		
		return chunks;
	}
	
	public URI getLocalUri() {
		return localUri;
	}
	public void setLocalUri(URI localUri) {
		this.localUri = localUri;
	}
	public URI getRemoteUri() {
		return remoteUri;
	}
	public void setRemoteUri(URI remoteUri) {
		this.remoteUri = remoteUri;
	}

	public String getId() {
		return id;
	}
	
	public SenderConnection getSenderConnection() {
		return senderConnection;
	}

	public void setSenderConnection(SenderConnection senderConnection) {
		this.senderConnection = senderConnection;
	}
}
