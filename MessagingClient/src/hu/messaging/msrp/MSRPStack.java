package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;

public class MSRPStack {

	private Connections connections = new Connections(this);
	

	public void createReceiverConnection(InetAddress host) throws IOException {
		getConnections().createReceiverConnection(host);
	}
	
	public SenderConnection createSenderConnection(InetAddress host, int port, String sipUri) throws IOException {
		return getConnections().createSenderConnection(host, port, sipUri);		
	}
	
	public void sendMessage(byte[] message, String sipUri) {	
		getConnections().findSenderConnection(sipUri);
	}
	
	public Connections getConnections() {
		return connections;
	}

	public void setConnections(Connections connections) {
		this.connections = connections;
	}
	
	public void disposeResources() {
		System.out.println("disposeResources...");
		SenderConnection s = getConnections().findSenderConnection("sip:weblogic103@192.168.1.103");
		if (s == null) {
			System.out.println("SenderConn null");
		}
		else {
			s.stop();
		}
		getConnections().getReceiverConnection().stop();
	}
}
