package hu.messaging.client;

import java.awt.TextArea;
import java.io.IOException;
import java.net.InetAddress;

import java.util.*;

import hu.messaging.Constants;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;
import hu.messaging.msrp.util.MSRPUtil;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import com.ericsson.icp.services.PGM.IRLSManager;
import com.ericsson.icp.services.PGM.PGMFactory;
import com.ericsson.icp.util.*;


public class Client {//implements MSRPListener {
/*	
	private Timer timer = null;
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
			System.out.println("initICP started");
			
			platform = ICPFactory.createPlatform();
			platform.registerClient("MessagingClient");
			platform.addListener(new PlatformListener(logArea));
			profile = platform.createProfile("IMSSetting");
			profile.addListener(new ProfileAdapter(logArea));
			
			service = profile.createService("+g.messagingclient", "messageClientApp");
			service.addListener(new ServiceAdapter(logArea));
			
			session = service.createSession();
			System.out.println(session.getState());
			System.out.println(session.isValid());
			session.addListener(new SessionAdapter(logArea));
			
			rlsManager = PGMFactory.createRLSManager(profile);
			if (rlsManager == null) {
				System.out.println("rslManager null.");
			}
			rlsManager.addListener(new RLSMAdapter(logArea));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.groupHelper = new GroupHelper(this.rlsManager);
		this.sendSIPMessage(Constants.updateStatusMessage);
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new UpdateStatusTask(), 
									   Constants.onlineUserTimeOut - 10000, 
									   Constants.onlineUserTimeOut - 10000);
		
		System.out.println("initICP finished");
	}

	public void dispose() {		
		try {
			MessagingService.getMsrpStack().disposeResources();
			if (session != null)
				session.release();
			if (rlsManager != null)
				rlsManager.release();
			if (service != null)
				service.release();
			if (profile != null)
				profile.release();
			if (platform != null)
				platform.release();
			this.timer.cancel();
			

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
			
			String sessionId = MSRPUtil.generateRandomString(Constants.sessionIdLength);
			
			ISessionDescription sdp = createLocalSDP(MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress(),
													 MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort(),
													 sessionId);

			MessagingService.addLocalSDP(Constants.serverURI, sdp.format());
			MessagingService.addMSRPListener(this);
			
			logArea.append("send INVITE to: " + Constants.serverURI + "\n");
			session.start(Constants.serverURI, sdp, profile.getIdentity(), SdpFactory.createIMSContentContainer());
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
		logArea.append("send SIP MESSAGE: " + message + "\n");
		try {
			service.sendMessage("sip:alice@ericsson.com", "sip:weblogic@ericsson.com", "text/plain", message.getBytes(), message.length() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ISessionDescription createLocalSDP(InetAddress host, int port, String sessionId) {
	    ISessionDescription localSdp = null;
		try {
			List<String> m = new ArrayList<String>();

			String address = host.getHostAddress();

			m.add("text/plain");
			String path = "msrp://" + address + ":" + port + "/" + sessionId + ";tcp";

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
	
	public void brokenTrasmission(MSRPEvent event) {
		
	}

	public void messageSentSuccess(MSRPEvent event) {
		System.out.println("Client - sikeres küldés");
		
		event.getMessageId();
		
		List<Recipient> testRecipients = new ArrayList<Recipient>();
		for (int i = 0; i < 2; i++) {
			Recipient r = new Recipient("Alice", "sip:alice@ericsson.com");
			testRecipients.add(r);
		}
		this.sendSIPMessage(buildRecipientsSIPMessage(event.getMessageId(), testRecipients));
	}

	public void startTrasmission(MSRPEvent event) {
		
	}
	
	public void update() {
		this.sendSIPMessage(Constants.updateStatusMessage);
	}
	
	private class UpdateStatusTask extends TimerTask {
		public void run() {
			update();
		}		
	}
	
	private String buildRecipientsSIPMessage(String messageId, List<Recipient> recipients) {
		String msg = "RECIPIENTS\r\n";
		msg += "Message-ID: " + messageId + "\r\n\r\n"; 
		
		for (Recipient r : recipients) {
			msg += r.getName() + "#" + r.getSipURI() + "\r\n";
		}
		
		msg += "\r\n-----END";
		return msg;
	}
*/
}
