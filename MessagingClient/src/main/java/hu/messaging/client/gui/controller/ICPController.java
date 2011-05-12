package hu.messaging.client.gui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.icp.listener.PlatformListener;
import hu.messaging.client.icp.listener.ProfileListener;
import hu.messaging.client.icp.listener.ServiceListener;
import hu.messaging.client.icp.listener.SessionListener;
import hu.messaging.client.icp.controller.ICPGroupListController;
import hu.messaging.client.gui.util.IcpUtils;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import java.util.Map;

public class ICPController {
	private IPlatform icpPlatform;
	private IProfile profile;
	private IService service;
	private Map<String, ISession> sessions;
	private List<SessionListener> sessionListeners;

	private Buddy localUser;

	private CommunicationController communicationController;
	private ContactListController contactListController;
	private ICPGroupListController icpGroupListController;

	private static final String PLATFORM_ID = "IMSSetting";
	private static final String CLIENT_NAME = "MessagingClient";
	private static final String SERVICE_ID = "+g.multicastClient";
	private static final String APPLICATION_ID = "messageClientApp";

	public ICPController() throws Exception {
		icpPlatform = ICPFactory.createPlatform();
		icpPlatform.registerClient(CLIENT_NAME);
		icpPlatform.addListener(new PlatformListener(this));

		profile = icpPlatform.createProfile(PLATFORM_ID);

		localUser = new Buddy(profile.getIdentity());

		contactListController = new ContactListController();
		contactListController.setLocalUser(localUser);

		profile.addListener(new ProfileListener(this));

		icpGroupListController = new ICPGroupListController(this,
				contactListController);

		communicationController = new CommunicationController(this);

		service = profile.createService(SERVICE_ID, APPLICATION_ID);
		service.addListener(new ServiceListener(this));
		
		sessions = new HashMap<String, ISession>();
		sessionListeners = new ArrayList<SessionListener>();
	}

	public ISession createNewSipSession(String remoteSipUri) throws Exception {
		
		SessionListener sessionListener = new SessionListener(this, remoteSipUri);
		
		ISession session = service.createSession();
		
		session.addListener(sessionListener);
		sessionListener.addConnectionListener(communicationController);
		
		sessions.put(remoteSipUri, session);
		sessionListeners.add(sessionListener);
		return session;
	}
	
	public void deleteSipSession(String remoteSipUri) {
		sessions.remove(remoteSipUri);
		int i = 0;
		for (SessionListener l : sessionListeners) {
			if (l.getRemoteSipUri().equals(remoteSipUri)) {
				sessionListeners.remove(i);
				break;
			}
			i++;
		}
	}
	
	public void release() {
		try {
			icpGroupListController.release();

			for (String s : sessions.keySet()) {
				IcpUtils.release(sessions.get(s));
			}
			
			IcpUtils.release(service);
			IcpUtils.release(icpPlatform);
		} catch (Exception e) {

		}
	}

	public ContactListController getContactListController() {
		return contactListController;
	}

	public IService getService() {
		return service;
	}

	public IProfile getProfile() {
		return profile;
	}

	public ISession getSession(String sipUri) {
		return sessions.get(sipUri);
	}

	public CommunicationController getCommunicationController() {
		return communicationController;
	}
	
	public SessionListener getSessionListener(String sipUri) {
		System.out.println("getSessionListener");
		for (SessionListener s : sessionListeners) {
			if (s.getRemoteSipUri().equals(sipUri)) {
				return s;
			}
		}
		return null;
	}

	public Buddy getLocalUser() {
		return localUser;
	}

}
