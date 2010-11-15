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
		this.transactionManager = new TransactionManager(incomingMessageQueue, outgoingMessageQueue, this);
	}

	public void sendMessage(byte[] completeMessage) {		
		List<byte[]> chunks = splitMessageToChunks(completeMessage, 100);
		for (byte[] chunk : chunks) {
			try {
				putMessageIntoOutgoingMessageQueue(chunk);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		double div = message.length / (double) chunkSize;
		System.out.println("Oszto (double)" + div);
		int numOfChunks = (int)Math.floor(div);
		System.out.println("numOfChunks: " + numOfChunks);
		
		int i = 0;
		int index = 0;
		while( i < numOfChunks ) {
			byte[] c = new byte[chunkSize];
			System.arraycopy(message, index, c, 0, chunkSize);
			index += chunkSize;
		}
		
		int lastChunkSize = 10;
		byte[] c = new byte[lastChunkSize];
		System.arraycopy(message, index, c, 0, lastChunkSize);
		
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
	
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
