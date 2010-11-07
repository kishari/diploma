package hu.messaging.msrp;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.nio.channels.SocketChannel;

public class Connections implements Observer {

	private MSRPStack msrpStack;
	private ReceiverConnection receiverConnection = null;
	private List<SenderConnection> senderConnections = new ArrayList<SenderConnection>();
		
	public Connections(MSRPStack msrpStack) {
		this.msrpStack = msrpStack;
	}
	
	public void send(String data, InetAddress remoteAddress, int remotePort) throws IOException {
	   	SenderConnection sender = findSenderConnection(remoteAddress, remotePort);
	   	if (sender != null) {
	   		sender.send(data);
	   	}
	   	else {
	   		System.out.println("SenderConnection is not find to address:port = " +
	   						   remoteAddress.getHostAddress() + " : " + remotePort);
	   	}
	}

	public void update(Observable o, Object arg) {
		SocketChannel s = (SocketChannel) arg;
		System.out.println("Connections update: " + o.toString());
		//System.out.println("SenderConnection accept with host:port = " 
		//					+ s.socket().getInetAddress().getHostName() + ":" + s.socket().getPort());
		System.out.println("SenderConnection accept with host:port = " 
				+ s.socket().getInetAddress().getHostAddress() + ":" + s.socket().getLocalPort());
		msrpStack.update(s);
	}
	
	public void createReceiverConnection(InetAddress localhost) throws IOException {
		this.setReceiverConnection(new ReceiverConnection(localhost, this));
	}
	
	public SenderConnection createSenderConnection(InetAddress addr, int port) throws IOException {
		SenderConnection c = new SenderConnection(addr, port, this);
		this.senderConnections.add(c);
		return c;
	}
	
	public boolean deleteSenderConnection(InetAddress addr, int port) throws IOException {
		SenderConnection c = findSenderConnection(addr, port);
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