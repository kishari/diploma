package hu.messaging.msrp;

import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSRPStack {

	private Connections connections = new Connections(this);
	private Map<String, Session> activeSessions = Collections.synchronizedMap(new HashMap<String, Session>());
	private List<MSRPListener> msrpListeners = new ArrayList<MSRPListener>();
	
	public void createReceiverConnection(InetAddress host) throws IOException {
		getConnections().createReceiverConnection(host);
	}
	
	public SenderConnection createSenderConnection(InetAddress host, int port, String sipUri) throws IOException {
		return getConnections().createSenderConnection(host, port, sipUri, this);		
	}
	
	public void putNewSession(Session session) {
		if ( findSession( session.getId() ) != null ) {
			return;
		}
		getActiveSessions().put(session.getId(), session);
	}
	
	public Session findSession(String sessionId) {
		if ( getActiveSessions().containsKey( sessionId ) ) {
			return getActiveSessions().get(sessionId);
		}
		return null;
	}
	
	public void removeSession(String sessionId) {
		getActiveSessions().remove(sessionId);
	}
	
	public void sendMessage(CompleteMessage completeMessage, String sipUri) {
		System.out.println(getClass().getSimpleName() + " sendMessage()");
		SenderConnection s = getConnections().getSenderConnection(sipUri);
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(!s.isConnected());
		Session session = s.getSession();
		session.sendMessage(completeMessage);
	}
	
	public Connections getConnections() {
		return connections;
	}

	public Map<String, Session> getActiveSessions() {
		return activeSessions;
	}
	
	public void disposeResources() {
		System.out.println("MSRPStack disposeResources...");
		getConnections().deleteSenderConnections();
		if (getConnections().getReceiverConnection() != null) {
			getConnections().getReceiverConnection().stop();
		}
	}
	
	public synchronized void addMSRPListener(MSRPListener listener) {
		this.msrpListeners.add(listener);
	}
	
	public synchronized void removeMSRPListener(MSRPListener listener) {
		this.msrpListeners.remove(listener);
	}

	public synchronized void notifyListeners(MSRPEvent event) {
		List<MSRPListener> temp = new ArrayList<MSRPListener>();
		synchronized(this.msrpListeners) {
			for (MSRPListener l : this.msrpListeners ) {
				temp.add(l);
			}
		}
		for (MSRPListener listener : temp) {					
			listener.fireMsrpEvent(event);
		}
	}
	
}
