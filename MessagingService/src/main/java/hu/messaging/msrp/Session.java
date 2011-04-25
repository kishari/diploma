package hu.messaging.msrp;

import hu.messaging.msrp.model.*;

import java.net.URI;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session implements java.util.Observer{

	private URI localUri;
	private URI remoteUri;	
	private String id;
	private SenderConnection senderConnection;
	private MSRPStack msrpStack;
	
	private BlockingQueue<Message> incomingMessageQueue = new LinkedBlockingQueue<Message>();
	private BlockingQueue<CompleteMSRPMessage> outgoingMessageQueue = new LinkedBlockingQueue<CompleteMSRPMessage>();

	private TransactionManager transactionManager = null;

	public Session(URI localUri, URI remoteUri, SenderConnection senderConnection, MSRPStack msrpStack) {
		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.id = localUri.toString()+remoteUri.toString();
		this.senderConnection = senderConnection;
		this.msrpStack = msrpStack;
		this.transactionManager = new TransactionManager(incomingMessageQueue, outgoingMessageQueue, this);
	}
	
	protected void sendMessage(CompleteMSRPMessage fullMessage) {		
		try {
			putMessageIntoOutgoingMessageQueue(fullMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void putMessageIntoIncomingMessageQueue(Message message) throws InterruptedException {
		this.incomingMessageQueue.put(message);
	}
	
	protected void putMessageIntoOutgoingMessageQueue(CompleteMSRPMessage message) throws InterruptedException {
		this.outgoingMessageQueue.put(message);
	}
	
	protected URI getLocalUri() {
		return localUri;
	}

	protected URI getRemoteUri() {
		return remoteUri;
	}

	protected String getId() {
		return id;
	}
	
	protected SenderConnection getSenderConnection() {
		return senderConnection;
	}

	protected MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	protected void stop() {
		transactionManager.stop();
	}
	
	public void update(Observable o, Object obj) {
		if (o.toString().contains(TransactionManager.class.getSimpleName())) {
			System.out.println("TransactionManager stopped");
			this.senderConnection.stop();
		}
		
	}
}
