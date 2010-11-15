package hu.messaging.msrp;

import hu.messaging.service.MessagingService;

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
		System.out.println("MSRPStack createSenderConnection");
		return getConnections().createSenderConnection(host, port, sipUri, this);		
	}
	
	public void putNewSession(Session session) {
		if ( findSession( session.getId() ) != null ) {
			return;
		}
		System.out.println("MSRPStack putNewSession to: " + session.getSenderConnection().getSipUri());
		getActiveSessions().put(session.getId(), session);
	}
	
	public Session findSession(String sessionId) {
		System.out.println("MSRPRstack findSession: " + sessionId);
		
		System.out.println("activeSessions mapben levo kulcsok lekerese: ");
		for (String s : getActiveSessions().keySet()) {
			System.out.println(s);			
		}
		
		if ( getActiveSessions().containsKey( sessionId ) ) {
			System.out.println("MSRPRstack findSession: van talalat");
			return getActiveSessions().get(sessionId);
		}
		return null;
	}
	
	public void removeSession(String sessionId) {
		System.out.println("MSRPStack removeSession: " + sessionId);
		
		Session s = getActiveSessions().remove(sessionId);
		
		System.out.println("MSRPStack: session torolve: " + s.getId());
	}
	
	public void sendMessage(byte[] completeMessage, String sipUri) {
		SenderConnection s = getConnections().findSenderConnection(sipUri);
		Session session = s.getSession();
		if (session != null) {
			session.sendMessage(completeMessage);
		}
		else {
			System.out.println("MSRPStack sendMessage. session null");
		}
	}
	
	public Connections getConnections() {
		return connections;
	}

	public Map<String, Session> getActiveSessions() {
		return activeSessions;
	}
	
	public void disposeResources() {
		System.out.println("disposeResources...");
		SenderConnection s = getConnections().findSenderConnection(MessagingService.serverURI);
		if (s == null) {
			System.out.println("SenderConn null");
		}
		else {
				s.stop();
		}
		getConnections().getReceiverConnection().stop();
	}
}
