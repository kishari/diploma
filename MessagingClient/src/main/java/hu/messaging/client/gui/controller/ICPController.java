package hu.messaging.client.gui.controller;

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

public class ICPController {
	private IPlatform icpPlatform;
	private IProfile profile;
	private IService service;
	private ISession session;

	private SessionListener sessionListener;

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
		
		session = service.createSession();
		sessionListener = new SessionListener(this);
		session.addListener(sessionListener);
	}

	public void release() {
		try {
			icpGroupListController.release();

			IcpUtils.release(session);
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

	public ISession getSession() {
		return session;
	}

	public CommunicationController getCommunicationController() {
		return communicationController;
	}
	
	public SessionListener getSessionListener() {
		return sessionListener;
	}

	public Buddy getLocalUser() {
		return localUser;
	}

}
