package hu.messaging.msrp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.net.InetAddress;

public class Connections {

	private MSRPStack msrpStack;
	private ReceiverConnection receiverConnection = null;
	private Map<String, SenderConnection> senderConnections = new HashMap<String, SenderConnection>();
		
	
	public Connections(MSRPStack msrpStack) {
		this.msrpStack = msrpStack;
	}
	
	public void createReceiverConnection(InetAddress localhost) throws IOException {
		System.out.println("MessagingService.createReceiverConnection");
		//if (this.receiverConnection == null || !this.receiverConnection.isRunning()) {
			this.setReceiverConnection(new ReceiverConnection(localhost, msrpStack));
		//}
	}
	
	public SenderConnection createSenderConnection(InetAddress addr, int port, 
													String sipUri, MSRPStack msrpStack) throws IOException {
/*		
		if (senderConnections.containsKey(sipUri)) {
			return senderConnections.get(sipUri);
		}
*/		
		SenderConnection c = new SenderConnection(addr, port, sipUri, msrpStack);
		this.senderConnections.put(sipUri, c);
		
		return c;
	}
	
	public void deleteSenderConnection(String sipUri) {
		SenderConnection c = getSenderConnection(sipUri);
		if (c != null) {
			c.stop();
			senderConnections.remove(sipUri);
			/*
			this.senderConnections.remove(c);
			System.out.println("Connections deleteSenderConnection finished");
			for (SenderConnection s : this.senderConnections) {
				System.out.println("Van meg senderConnection a listaban: " + s.getSipUri());
			}
			*/
		}
	}
	
	public void deleteSenderConnections() {
		for (String key : this.senderConnections.keySet()) {
			SenderConnection s = senderConnections.get(key);
			s.stop();
		}
	}
	
	public SenderConnection getSenderConnection(String sipUri) {
		return senderConnections.get(sipUri);
	}

	public void setReceiverConnection(ReceiverConnection receiverConnection) {
		this.receiverConnection = receiverConnection;
	}

	public ReceiverConnection getReceiverConnection() {
		return receiverConnection;
	}
	
	public boolean isReceiverConnection() {
		return (getReceiverConnection() != null);
	}
	
	public boolean isRunningReceiverConnection() {
		if ( isReceiverConnection() ) {
			return getReceiverConnection().isRunning();
		}			
		return false;
	}
	
	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
}