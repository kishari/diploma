package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

public class MSRPStack {

	private Connections connections = new Connections(this);
	

	public void createReceiverConnection(InetAddress host) throws IOException {
		getConnections().createReceiverConnection(host);
	}
	
	public SenderConnection createSenderConnection(InetAddress host, int port) throws IOException {
		return getConnections().createSenderConnection(host, port);		
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
	
	public Connections getConnections() {
		return connections;
	}

	public void setConnections(Connections connections) {
		this.connections = connections;
	}
}
