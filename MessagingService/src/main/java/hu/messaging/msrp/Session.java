package hu.messaging.msrp;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session {

	private URI localUri;
	private URI remoteUri;	
	private String id;
	private SenderConnection senderConnection;
	
	private BlockingQueue<MSRPMessage> messageQueue = new LinkedBlockingQueue<MSRPMessage>();
	private BlockingQueue<MSRPMessage> outputQueue = new LinkedBlockingQueue<MSRPMessage>();


	private TransactionManager transactionManager = null;

	public Session(URI localUri, URI remoteUri, SenderConnection senderConnection) {
		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.id = localUri.toString()+remoteUri.toString();
		this.senderConnection = senderConnection;
		this.transactionManager = new TransactionManager(messageQueue, senderConnection);
		Thread t = new Thread(transactionManager);
		t.setDaemon(true);
		t.start();
	}
	
	public void putMessageIntoMessageQueue(MSRPMessage message) throws InterruptedException {
		System.out.println("Session.putMessageIntoMessageQueue");
		this.messageQueue.put(message);
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
