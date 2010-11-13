package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MSRPStack {

	private Connections connections = new Connections(this);
	private Map<String, Session> activeSessions = Collections.synchronizedMap(new HashMap<String, Session>());
	

	public void createReceiverConnection(InetAddress host) throws IOException {
		getConnections().createReceiverConnection(host);
	}
	
	public SenderConnection createSenderConnection(InetAddress host, int port, String sipUri) throws IOException {
		return getConnections().createSenderConnection(host, port, sipUri, this);		
	}
	
	public void sendMessage(byte[] message, String sipUri) {	
		getConnections().findSenderConnection(sipUri);
	}
	
	public synchronized void putNewSession(Session session) {
		if ( findSession( session ) != null ) {
			return;
		}
		System.out.println("MSRPStack putNewSession");
		System.out.println(session.getSenderConnection().getSipUri());
		getActiveSessions().put(session.getId(), session);
	}
	
	public Session findSession(Session session) {
		if ( getActiveSessions().containsKey( session.getId() ) ) {
			return getActiveSessions().get(session.getId());
		}
		return null;
	}
	
	public Session findSession(String sessionId) {
		System.out.println("MSRPRstack findSession: " + sessionId);
		for (String s : getActiveSessions().keySet()) {
			System.out.println(s);
		}
		if ( getActiveSessions().containsKey( sessionId ) ) {
			System.out.println("MSRPRstack findSession: van találat");
			return getActiveSessions().get(sessionId);
		}
		return null;
	}
	
	public void removeSession(String sessionId) {
		System.out.println("MSRPStack removeSession: " + sessionId);
		Session s = getActiveSessions().remove(sessionId);
		System.out.println(s.getId());
	}
	
	public Connections getConnections() {
		return connections;
	}

	public Map<String, Session> getActiveSessions() {
		return activeSessions;
	}
}
