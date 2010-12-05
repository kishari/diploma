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
import com.ericsson.icp.IPlatform.Version;
import com.ericsson.icp.IProfile.State;

/**
 * The ICP controller is the component between the GUI and the actual ICP. 
 * It wraps main events of the ICP and provide the API to initiate ICP events
 */
public class ICPController {
    private IPlatform icpPlatform;

    /**
     * The profile to use for this application.
     */
    private IProfile profile;

    /**
     * The ICP service
     */
    private IService service;
    
    private ISession session;
    private SessionListener sessionListener;
    /**
     * The local user
     */
    private Buddy localUser;

    /**
     * The incoming communication handler
     */
    private CommunicationController communicationController;

    /**
     * The platform static ID
     */
    private static final String PLATFORM_ID = "IMSSetting";

    /**
     * The application static ID
     */
    private static final String CLIENT_NAME = "MessagingClient";
    
    /**
     * The service static ID
     */
    private static final String SERVICE_ID = "+g.messagingclient";

    /**
     * The application static ID
     */
    private static final String APPLICATION_ID = "messageClientApp";
    
    /**
     * The contact list controller to use
     */
    private ContactListController contactListController;

    /**
     * ICP group list controller
     */
    private ICPGroupListController icpGroupListController;

    /**
     * Build an ICP controller
     */
    public ICPController() throws Exception
    {    
        icpPlatform = ICPFactory.createPlatform();
        icpPlatform.registerClient(CLIENT_NAME);
        icpPlatform.addListener(new PlatformListener(this));

        profile = icpPlatform.createProfile(PLATFORM_ID);

        localUser = new Buddy(profile.getIdentity());
      
        contactListController = new ContactListController();
        contactListController.setUser(localUser);
        
        profile.addListener(new ProfileListener(this));
        
        icpGroupListController = new ICPGroupListController(this, contactListController);

        communicationController = new CommunicationController(this);

        createService();
        createSession();
    }

    /**
     * Create a service on the ICP platform
     */
    private void createService() throws Exception
    {
        // Create the service and add a listener on it to be notified on incoming calls
        service = profile.createService(SERVICE_ID, APPLICATION_ID);
        service.addListener(new ServiceListener(this));
    }
    
    private void createSession() throws Exception {
    	session = service.createSession();
    	sessionListener = new SessionListener(this);
    	session.addListener(sessionListener);
    }

    /**
     * Release all ICP objects
     */
    public void release()
    {
        try
        {
            // Release all sessions
            releaseSessions();
            icpGroupListController.release();

            // Release all other allocated objects
            IcpUtils.release(service);
            IcpUtils.release(icpPlatform);
        }
        catch (Exception e)
        {
         
        }
    }

    /**
     * Release all ongoing sessions
     */
    private void releaseSessions()
    {
       
    }

    /**
     * @return The ICP version or -1 if unable to get the version.
     */
    public int getICPVersion()
    {
        try
        {
            Version version = icpPlatform.getVersion();
            return version.majorVersion;
        }
        catch (Throwable t)
        {
            return -1;
        }
    }

    /**
     * @return The state(0:not registered, 1:registered) or -1 if unable to get the state.
     */
    public int getICPState()
    {
        try
        {
            State state = profile.getState();
            return state.registrationState;
        }
        catch (Throwable t)
        {
            return -1;
        }
    }

    /**
     * Retrieve a list of available services on the platform
     * 
     * @param service The service(s) to find. null return all the available services.
     * @return The service list based on what was requested
     */
    public StringBuffer getAvailableServices(String service)
    {
        try
        {
            StringBuffer servicesBuffer = new StringBuffer();
            profile.queryAvailableServices(service, servicesBuffer);
            return servicesBuffer;
        }
        catch (Throwable t)
        {
            StringBuffer buffer = new StringBuffer("unable to get services list");
            return buffer;
        }
    }

    /**
     * Process an incoming instant message
     * 
     * @param to The calle
     * @param message The message
     */
    public void processIncomingSIPMessage(String to, String message)
    {
        // Delegate to the communication controller
    	communicationController.incomingSIPMessage(to, message);
    }

    /**
     * Retrieve the contact list controller
     * 
     * @return The controller
     */
    public ContactListController getContactListController()
    {
        return contactListController;
    }

	public IService getService() {
		return service;
	}

	public IProfile getProfile() {
		return profile;
	}

	public CommunicationController getCommunicationController() {
		return communicationController;
	}

	public ISession getSession() {
		return session;
	}

	public SessionListener getSessionListener() {
		return sessionListener;
	}
	
	
}
