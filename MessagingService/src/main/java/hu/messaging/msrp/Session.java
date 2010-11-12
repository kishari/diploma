package hu.messaging.msrp;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session {

	private URI localUri;
	private URI remoteUri;	
	private String id;
	private BlockingQueue<MSRPMessage> messageQueue = new LinkedBlockingQueue<MSRPMessage>();
	
	private TransactionManager transactionManager = new TransactionManager(messageQueue);
	
	public Session(URI localUri, URI remoteUri) {
		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.id = localUri.toString()+remoteUri.toString();
		Thread t = new Thread(transactionManager);
		t.setDaemon(true);
		t.start();
	}
	
	public void putMessageIntoMessageQueue(MSRPMessage message) throws InterruptedException {
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
}
