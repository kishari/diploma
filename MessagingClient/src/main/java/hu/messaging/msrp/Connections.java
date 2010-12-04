package hu.messaging.msrp;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;

public class Connections {

	private MSRPStack msrpStack;
	private ReceiverConnection receiverConnection = null;
	private List<SenderConnection> senderConnections = new ArrayList<SenderConnection>();
		
	
	public Connections(MSRPStack msrpStack) {
		this.msrpStack = msrpStack;
	}
	
	public void createReceiverConnection(InetAddress localhost) throws IOException {
		System.out.println("MessagingService.createReceiverConnection");
		if (this.receiverConnection == null || !this.receiverConnection.isRunning()) {
			this.setReceiverConnection(new ReceiverConnection(localhost, msrpStack));
		}
	}
	
	public SenderConnection createSenderConnection(InetAddress addr, int port, 
													String sipUri, MSRPStack msrpStack) throws IOException {
		for ( SenderConnection s : this.senderConnections ) {
			if ( s.getRemoteAddress().equals(addr) && s.getRemotePort() == port ) {
				return s;
			}
		}
		
		SenderConnection c = new SenderConnection(addr, port, sipUri, msrpStack);
		this.senderConnections.add(c);
		return c;
	}
	
	public boolean deleteSenderConnection(String sipUri) {
		SenderConnection c = findSenderConnection(sipUri);
		if (c != null) {
			c.stop();
			this.senderConnections.remove(c);
			System.out.println("Connections deleteSenderConnection finished");
			for (SenderConnection s : this.senderConnections) {
				System.out.println("Van meg senderConnection a listaban: " + s.getSipUri());
			}
			return true;
		}
		return false;
	}
	
	public void deleteSenderConnections() {
		for (SenderConnection s : this.senderConnections) {
			s.stop();
		}
		this.senderConnections.clear();
	}
	
	public SenderConnection findSenderConnection(String sipUri) {
		System.out.println("Connections findSenderConn to sipUri: " + sipUri);
		for ( SenderConnection c : senderConnections ) {
			System.out.println(c.getSipUri());
			if (sipUri.equals(c.getSipUri())) {
				System.out.println("Connections findSenderConn return");
				return c;
			}
		}
		return null;
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