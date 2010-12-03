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
		//System.out.println("MSRPStack putNewSession to: " + session.getSenderConnection().getSipUri());
		getActiveSessions().put(session.getId(), session);
	}
	
	public Session findSession(String sessionId) {
		//System.out.println("MSRPRstack findSession: " + sessionId);
		
		//System.out.println("activeSessions mapben levo kulcsok lekerese: ");
		for (String s : getActiveSessions().keySet()) {
			//System.out.println(s);			
		}
		
		if ( getActiveSessions().containsKey( sessionId ) ) {
			//System.out.println("MSRPRstack findSession: van talalat");
			return getActiveSessions().get(sessionId);
		}
		return null;
	}
	
	public void removeSession(String sessionId) {
		//System.out.println("MSRPStack removeSession: " + sessionId);
		
		Session s = getActiveSessions().remove(sessionId);
		
		//System.out.println("MSRPStack: session torolve: " + s.getId());
	}
	
	public void sendMessage(CompleteMessage completeMessage, String sipUri) {
		SenderConnection s = getConnections().findSenderConnection(sipUri);
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
		switch (event.getCode()) {
		case MSRPEvent.messageSentSuccessCode :	for (MSRPListener listener : temp) {					
														listener.messageSentSuccess(event);
												}			
												break;
		case MSRPEvent.startTrasmissionCode :	for (MSRPListener listener : temp) {					
													listener.startTrasmission(event);
												}	
												break;					
		case MSRPEvent.brokenTrasmissionCode :	for (MSRPListener listener : temp) {					
													listener.brokenTrasmission(event);
												}	
												break;
		}
	}
	
}
