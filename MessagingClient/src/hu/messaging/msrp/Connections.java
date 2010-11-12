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
	
	public void listSenderConnections() {
		System.out.println("Listing sender connections");
		for (SenderConnection s : senderConnections ) {
			System.out.println(s.getSipUri());
		}
	}
	
	public void send(byte[] data, String sipUri) throws IOException {
	   	SenderConnection sender = findSenderConnection(sipUri);
	   	listSenderConnections();
	   	if (sender != null) {
	   		sender.send(data);
	   		listSenderConnections();
	   	}
	   	else {
	   		System.out.println("SenderConnection is not find to sipUri = " + sipUri);
	   	}
	}

	public void createReceiverConnection(InetAddress localhost) throws IOException {
		this.setReceiverConnection(new ReceiverConnection(localhost, this));
	}
	
	public SenderConnection createSenderConnection(InetAddress addr, int port, String sipUri) throws IOException {
		SenderConnection c = new SenderConnection(addr, port, this, sipUri);
		this.senderConnections.add(c);
		return c;
	}
	
	public boolean deleteSenderConnection(InetAddress addr, int port) throws IOException {
		SenderConnection c = findSenderConnection(addr, port);
		System.out.println("deleteSender");
		if (c != null) {
			this.senderConnections.remove(c);
			return true;
		}
		return false;
	}
	
	public SenderConnection findSenderConnection(InetAddress addr, int port) {
		System.out.println("findSenderConn data: " + addr.getHostAddress() + ":" + port);
		for (SenderConnection c : senderConnections) {
			System.out.println(c.getRemoteAddress().getHostAddress() + ":" + c.getRemotePort());
			if (c.getRemoteAddress().getHostAddress().equals(addr.getHostAddress()) && c.getRemotePort() == port) {
				return c;
			}
		}
		return null;
	}
	
	public SenderConnection findSenderConnection(String sipUri) {
		listSenderConnections();
		System.out.println("findSenderConn to sipUri: " + sipUri);
		for (SenderConnection c : senderConnections) {
			System.out.println(c.getSipUri());
			if (sipUri.equals(c.getSipUri())) {
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
		if ( getReceiverConnection() != null ) {
			return true;
		}			
		return false;
	}
	
	public boolean isRunningReceiverConnection() {
		if ( isReceiverConnection() ) {
			return getReceiverConnection().isRunning();
		}			
		return false;
	}
}