package hu.messaging.msrp;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.net.InetAddress;

public class Connections implements Observer{

	private MSRPStack msrpStack;
	private ReceiverConnection receiverConnection = null;
	private Map<String, SenderConnection> senderConnections = new HashMap<String, SenderConnection>();
	private boolean isFinishedAllSenderConnections = false;	
	
	public Connections(MSRPStack msrpStack) {
		this.msrpStack = msrpStack;
	}
	
	protected void createReceiverConnection(InetAddress localhost) throws IOException {
		System.out.println("MessagingService.createReceiverConnection");
		this.receiverConnection = new ReceiverConnection(localhost, msrpStack);
	}
	
	protected SenderConnection createSenderConnection(InetAddress addr, int port, 
												   String sipUri, MSRPStack msrpStack) throws IOException {

		SenderConnection c = new SenderConnection(addr, port, sipUri, msrpStack);
		senderConnections.put(sipUri, c);
		return c;
	}
	
	protected void deleteSenderConnection(String sipUri) {
		SenderConnection c = senderConnections.get(sipUri);
		if (c != null) {
			c.stop();
			senderConnections.remove(sipUri);
		}
	}
	
	protected void deleteSenderConnections() {
		for (String key : this.senderConnections.keySet()) {
			SenderConnection s = senderConnections.get(key);
			s.stop();
		}		
		isFinishedAllSenderConnections = true;
	}
	
	protected SenderConnection getSenderConnection(String sipUri) {
		return senderConnections.get(sipUri);
	}
	
	protected ReceiverConnection getReceiverConnection() {
		return receiverConnection;
	}
	
	protected boolean isReceiverConnection() {
		return (getReceiverConnection() != null);
	}
	
	protected boolean isRunningReceiverConnection() {
		if ( isReceiverConnection() ) {
			return getReceiverConnection().isRunning();
		}			
		return false;
	}
	
	public void update(Observable o, Object obj) {
		if (o.toString().contains(SenderConnection.class.getSimpleName())) {
			System.out.println("SenderConnection dispose finished!");
			SenderConnection conn = (SenderConnection) obj;
			do {
				if (isFinishedAllSenderConnections) {
					senderConnections.remove(conn.getSipUri());
				}
				else {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
			} while (!isFinishedAllSenderConnections);
		}
		
		if (o.toString().contains(ReceiverConnection.class.getSimpleName())) {
			System.out.println("ReceiverConnection dispose finished!");
			this.receiverConnection = null;	
		}
	}
}