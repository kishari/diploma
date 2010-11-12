package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class MSRPStack {

	private Connections connections = new Connections(this);
	private List<Session> sessions = new ArrayList<Session>();
	

	public void createReceiverConnection(InetAddress host) throws IOException {
		getConnections().createReceiverConnection(host);
	}
	
	public SenderConnection createSenderConnection(InetAddress host, int port, String sipUri) throws IOException {
		return getConnections().createSenderConnection(host, port, sipUri);		
	}
	
	public void update(SocketChannel channel) {
		SenderConnection s = getConnections().findSenderConnection(channel.socket().getInetAddress(), channel.socket().getLocalPort());
		if (s != null) {
			System.out.println("MSRPStack update: " + s.getRemoteAddress().getHostAddress() + " : " + s.getRemotePort());			
		}
		else {
			System.out.println("MSRPStack update error: SenderConnection is null!");
		}
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

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public List<Session> getSessions() {
		return sessions;
	}
}
