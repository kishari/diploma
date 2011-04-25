package hu.messaging.msrp;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.net.InetAddress;

public class Connections extends Observable implements Observer{

	private MSRPStack msrpStack;
	private ReceiverConnection receiverConnection = null;
	private Map<String, SenderConnection> senderConnections = new HashMap<String, SenderConnection>();
	
	public Connections(MSRPStack msrpStack) {
		this.msrpStack = msrpStack;
		this.addObserver(msrpStack);
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
			System.out.println(getClass().getSimpleName() + " SenderConnection dispose finished!");
			SenderConnection conn = (SenderConnection) obj;
			senderConnections.remove(conn.getRemoteSipUri());			
			System.out.println(getClass().getSimpleName() + senderConnections.size());
			this.setChanged();
			this.notifyObservers(conn);
		}
		
		if (o.toString().contains(ReceiverConnection.class.getSimpleName())) {
			System.out.println("ReceiverConnection dispose finished!");
			ReceiverConnection conn = (ReceiverConnection)obj;
			this.setChanged();
			this.notifyObservers(conn);
			this.receiverConnection = null;
		}
	}
}