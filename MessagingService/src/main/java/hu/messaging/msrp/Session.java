package hu.messaging.msrp;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session {

	private URI localUri;
	private URI remoteUri;	
	private String id;
	private SenderConnection senderConnection;
	private MSRPStack msrpStack;
	
	private BlockingQueue<Message> incomingMessageQueue = new LinkedBlockingQueue<Message>();
	private BlockingQueue<CompleteMessage> outgoingMessageQueue = new LinkedBlockingQueue<CompleteMessage>();

	private TransactionManager transactionManager = null;

	public Session(URI localUri, URI remoteUri, SenderConnection senderConnection, MSRPStack msrpStack) {
		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.id = localUri.toString()+remoteUri.toString();
		this.senderConnection = senderConnection;
		this.msrpStack = msrpStack;
		this.transactionManager = new TransactionManager(incomingMessageQueue, outgoingMessageQueue, this);
	}
	
	public void sendMessage(CompleteMessage completeMessage) {		
		try {
			putMessageIntoOutgoingMessageQueue(completeMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void putMessageIntoIncomingMessageQueue(Message message) throws InterruptedException {
		//System.out.println("Session.putMessageIntoIncomingMessageQueue");
		this.incomingMessageQueue.put(message);
	}
	
	public void putMessageIntoOutgoingMessageQueue(CompleteMessage message) throws InterruptedException {
		//System.out.println("Session.putMessageIntoOutgoingMessageQueue");
		this.outgoingMessageQueue.put(message);
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

	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
}
