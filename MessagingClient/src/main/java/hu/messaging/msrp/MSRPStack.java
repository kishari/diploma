package hu.messaging.msrp;

import hu.messaging.msrp.listener.MSRPEvent;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.model.FullMSRPMessage;
import hu.messaging.util.SessionDescription;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MSRPStack implements Observer {

	private Connections connections = new Connections(this);
	private Map<String, Session> activeSessions = Collections.synchronizedMap(new HashMap<String, Session>());
	private List<MSRPListener> msrpListeners = new ArrayList<MSRPListener>();
	
	public void sendMessage(FullMSRPMessage fullMSRPMessage, String remoteSipUri) {
		SenderConnection s = getConnections().getSenderConnection(remoteSipUri);
		Session session = s.getSession();
		session.sendMessage(fullMSRPMessage);
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
		activeSessions.put(newSession.getId(), newSession);
		
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
	
	protected Session findSession(String sessionId) {
		if ( activeSessions.containsKey( sessionId ) ) {
			return activeSessions.get(sessionId);
		}
		return null;
	}
	
	protected void stopSession(String remoteSipUri) {
		Session s = getConnections().getSenderConnection(remoteSipUri).getSession();
		s.stop();
	}
	
	protected Connections getConnections() {
		return connections;
	}
	
	public void disposeResources() {
		System.out.println("MSRPStack disposeResources...");
		for (String id : activeSessions.keySet()) {
			Session s = activeSessions.get(id);
			s.stop();		
		}
		
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
	
	public void update(Observable o, Object obj) {
		if (o.toString().contains(Connections.class.getSimpleName())) {
			if (obj instanceof SenderConnection) {
				System.out.println(getClass().getSimpleName() + " update: senderConnection stopped");
				SenderConnection s = (SenderConnection)obj;
				activeSessions.remove(s.getSession().getId());
				
				System.out.println(getClass().getSimpleName() + this.activeSessions.size());
			}
			else if (obj instanceof ReceiverConnection) {
				System.out.println(getClass().getSimpleName() + " update: ReceiverConnection stopped");
			}
		}
		
	}
	
}
