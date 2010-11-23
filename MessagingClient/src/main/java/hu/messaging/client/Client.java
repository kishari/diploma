package hu.messaging.client;

import java.awt.TextArea;
import java.io.IOException;
import java.net.InetAddress;

import java.util.*;

import hu.messaging.client.model.GroupListStruct;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.service.MessagingService;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import com.ericsson.icp.services.PGM.IRLSGroup;
import com.ericsson.icp.services.PGM.IRLSManager;
import com.ericsson.icp.services.PGM.PGMFactory;
import com.ericsson.icp.util.*;


public class Client {
	
	private IPlatform platform = null;
	private IProfile profile = null;
	private IService service = null;
	private ISession session = null;
	private IRLSManager rlsManager = null;
	private GroupHelper groupHelper = null;
	
	private TextArea logArea = new TextArea();	

	public Client(TextArea logArea) {
		this.logArea = logArea;
	}
	
	public void init() {
		try {
			platform = ICPFactory.createPlatform();
			platform.registerClient("MessagingClient");
			platform.addListener(new PlatformAdapter(logArea));
			profile = platform.createProfile("IMSSetting");
			profile.addListener(new ProfileAdapter(logArea));
			
			service = profile.createService("+g.messagingclient", "messageClientApp");
			service.addListener(new ServiceAdapter(logArea));
			
			session = service.createSession();
			System.out.println(session.getState());
			System.out.println(session.isValid());
			session.addListener(new SessionAdapter(logArea));
			
			rlsManager = PGMFactory.createRLSManager(profile);
			System.out.println("initICP");
			if (rlsManager == null) {
				System.out.println("rslManager null. fuck you");
			}
			rlsManager.addListener(new RLSMAdapter(logArea));
			System.out.println("client rlsManager: " + rlsManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.groupHelper = new GroupHelper(this.rlsManager);
	}

	public void dispose() {		
		try {
			MessagingService.getMsrpStack().disposeResources();
			session.release();
			rlsManager.release();
			service.release();
			profile.release();
			platform.release();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
   
	public void sendInvite() {        
		try {
		
			if (!MessagingService.getMsrpStack().getConnections().isReceiverConnection()) {
				MessagingService.getMsrpStack().getConnections().createReceiverConnection(InetAddress.getLocalHost());
				MessagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			}
			else if (!MessagingService.getMsrpStack().getConnections().isRunningReceiverConnection()) {
				MessagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			}
			
			String sessionId = MSRPUtil.generateRandomString(20);
			ISessionDescription sdp = createLocalSDP(MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress(),
													 MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort(),
													 sessionId);

			MessagingService.addLocalSDP(MessagingService.serverURI, sdp.format());
			
			logArea.append("send INVITE to: " + MessagingService.serverURI + "\n");
			session.start(MessagingService.serverURI, sdp, profile.getIdentity(), SdpFactory.createIMSContentContainer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendBye() {
		try {
			session.end();
		}
		catch(Exception e) { }
		
	}

	public void sendMessage(byte[] completeMessage, String sipUri) {
		try {
			MessagingService.sendMessage(completeMessage, sipUri);
		}
		catch(IOException e) { }
	
	}
	
	public void sendSIPMessage(String message) {
	/*	String msg = new String("MSG FROM CLIENT");
		
		logArea.append("send MESSAGE to alice: " + msg + "\n");
		
		try {
			service.sendMessage("sip:alice@ericsson.com", "sip:alice@ericsson.com", "text/plain", msg.getBytes(), msg.length() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/
	}
	
	private ISessionDescription createLocalSDP(InetAddress host, int port, String sessionId) {
	    ISessionDescription localSdp = null;
		try {
			List<String> m = new ArrayList<String>();

			String address = host.getHostAddress();

			m.add("text/plain");
			String path = new String();
			path = "msrp://" + address + ":" + port + "/" + sessionId + ";tcp";

			String codes = "*";
			codes = codes.trim();

			localSdp = SdpFactory.createSessionDescription("");
			localSdp.setField(ISessionDescription.FieldType.ProtocolVersion,
					"0");
			localSdp.setField(ISessionDescription.FieldType.Owner,
					"client 121123222 984773827 IN IP4 " + address);
			localSdp.setField(ISessionDescription.FieldType.SessionName, "-");
			localSdp.setField(ISessionDescription.FieldType.Connection,
					"IN IP4 " + address);
			ITimeDescription timeDescription = SdpFactory
					.createTimeDescription();
			timeDescription.setSessionActiveTime("0 0");
			localSdp.addTimeDescription(timeDescription);

			IMediaDescription mediaDescription = SdpFactory
					.createMediaDescription();
			mediaDescription.setField(IMediaDescription.FieldType.Name,
					"message " + port + " TCP/MSRP " + codes);

			IAttribute attr = SdpFactory.createAttribute("accept-types",
					"text/plain");
			mediaDescription.appendAttribute(attr);
			attr = SdpFactory.createAttribute("path", path);
			mediaDescription.appendAttribute(attr);

			localSdp.addMediaDescription(mediaDescription);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localSdp;
	}

	public GroupHelper getGroupHelper() {
		return groupHelper;
	}
	
	public void addGroup(String groupName) {
		try {
			
			IRLSGroup group = PGMFactory.createGroup(groupName);
			System.out.println("checkout1");
			if (rlsManager == null) {
				System.out.println("rlsManager null");
			}
			
			rlsManager.addGroup(group);
			System.out.println("checkout2");
			GroupListStruct temp = new GroupListStruct();
			System.out.println("checkout3");
			temp.groupName = groupName;
			System.out.println("checkout4");
			temp.members = new LinkedList<String>();
			System.out.println("checkout5");
			
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " +
					"Sikertelen csoport létrehozás!");
			//e.printStackTrace();			
		}
	}
}
