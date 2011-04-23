package hu.messaging.msrp;

import hu.messaging.msrp.listener.MSRPEvent;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.model.CompleteMessage;
import hu.messaging.util.SessionDescription;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSRPStack {

	private Connections connections = new Connections(this);
	private Map<String, Session> activeSessions = Collections.synchronizedMap(new HashMap<String, Session>());
	private List<MSRPListener> msrpListeners = new ArrayList<MSRPListener>();
	
	public void sendMessage(CompleteMessage completeMessage, String remoteSipUri) {
		SenderConnection s = getConnections().getSenderConnection(remoteSipUri);
		Session session = s.getSession();
		session.sendMessage(completeMessage);
	}
	
	public InetAddress getReceiverConnectionHostAddress() {
		try {
			createReceiverConnection(InetAddress.getLocalHost());
		}
		catch(UnknownHostException e) {}
		
		return getConnections().getReceiverConnection().getHostAddress();
	}
	
	public int getReceiverConnectionPort() {
		try {
			createReceiverConnection(InetAddress.getLocalHost());
		}
		catch(UnknownHostException e) {}
		
		return getConnections().getReceiverConnection().getPort();
	}
	
	public void createMSRPSession(SessionDescription localSDP, SessionDescription remoteSDP, String remoteSipUri) {
				
		if (!getConnections().isRunningReceiverConnection()) {
			if (getConnections().isReceiverConnection()) {
				getConnections().getReceiverConnection().start();
			}
		}
		
		SenderConnection s = null;
		
		try {
			s = getConnections().createSenderConnection(remoteSDP.getHost(), remoteSDP.getPort(), remoteSipUri, this);
		}
		catch (IOException e) {
			
		}
				
		if (s == null) {
			return;
		}
		
		Session newSession = new Session(localSDP.getPath(), remoteSDP.getPath(), s, this);
		putNewSession(newSession);
		
		s.setSession(newSession);
		s.start();
				
	}
	
	private void createReceiverConnection(InetAddress host) {
		if (!getConnections().isReceiverConnection()) {
			try {
				getConnections().createReceiverConnection(host);
			}
			catch(IOException e) { }
			
		}		
	}
	
	private void putNewSession(Session session) {
		if ( findSession( session.getId() ) != null ) {
			return;
		}
		activeSessions.put(session.getId(), session);
	}
	
	protected Session findSession(String sessionId) {
		if ( activeSessions.containsKey( sessionId ) ) {
			return activeSessions.get(sessionId);
		}
		return null;
	}
	
	protected void removeSession(String sessionId) {
		activeSessions.remove(sessionId);
	}
	
	protected Connections getConnections() {
		return connections;
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

	protected synchronized void notifyListeners(MSRPEvent event) {
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
