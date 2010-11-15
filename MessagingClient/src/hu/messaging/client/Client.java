package hu.messaging.client;

import java.awt.TextArea;
import java.io.IOException;
import java.net.InetAddress;

import java.util.*;
import hu.messaging.service.MessagingService;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import com.ericsson.icp.util.*;


public class Client {
	
	//private MSRPStack msrpStack = new MSRPStack();
	
	private IPlatform platform = null;
	private IProfile profile = null;
	private IService service = null;
	private ISession session = null;
	
	private TextArea logArea = new TextArea();	

	public Client(TextArea logArea) {
		this.logArea = logArea;
	}
	
	public void initICP() {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dispose() {		
		try {
			MessagingService.getMsrpStack().disposeResources();
			session.release();
			service.release();
			profile.release();
			platform.release();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private ISessionDescription createLocalSDP(InetAddress host, int port, String sessionId)    {
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
           localSdp.setField(ISessionDescription.FieldType.ProtocolVersion, "0");
           localSdp.setField(ISessionDescription.FieldType.Owner, "client 121123222 984773827 IN IP4 " + address);
           localSdp.setField(ISessionDescription.FieldType.SessionName, "-");
           localSdp.setField(ISessionDescription.FieldType.Connection, "IN IP4 " + address);
           ITimeDescription timeDescription = SdpFactory.createTimeDescription();
           timeDescription.setSessionActiveTime("0 0");
           localSdp.addTimeDescription(timeDescription);
           
           IMediaDescription mediaDescription = SdpFactory.createMediaDescription();
           mediaDescription.setField(IMediaDescription.FieldType.Name, "message " + port + " TCP/MSRP " + codes);
                    
       	   IAttribute attr = SdpFactory.createAttribute("accept-types", "text/plain");
       	   mediaDescription.appendAttribute(attr);
       	   attr = SdpFactory.createAttribute("path", path);
       	   mediaDescription.appendAttribute(attr);
       	   
           localSdp.addMediaDescription(mediaDescription);

       }
       catch (Exception e)
       {
       	e.printStackTrace();
       }
       return localSdp;
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
			
			ISessionDescription sdp = createLocalSDP(MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress(),
													 MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort(),
													 "clientsessionid");

			MessagingService.addLocalSDP("clientsessionid", sdp.format());
			
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

	/*
	public void sendTestData() {
		MSRPMessage m = new MSRPMessage();
		SenderConnection s = MessagingService.getMsrpStack().getConnections().findSenderConnection(MessagingService.serverURI);
		if (s == null) {
			System.out.println("Client SenderConnection is null!!!!!!!!!!!!!!!");
			return;
		}
		m.createTestMessage(
				s.getRemoteAddress(), s.getRemotePort(),
				MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress(),
				MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort());

		try {
			for (int i = 0; i < 1; i++) {
				Thread.sleep(20);
				s.send(m.toString().getBytes());
			}
		} catch(IOException ex) {}
		  catch(InterruptedException exc) {}
		
		
	}
	*/
	
	public void sendData(byte[] data, String sipUri) {
		try {
			MessagingService.sendData(data, sipUri);
		}
		catch(IOException e) { }
	
	}
	
	public void sendMessage(String message) {
	/*	String msg = new String("MSG FROM CLIENT");
		
		logArea.append("send MESSAGE to alice: " + msg + "\n");
		
		try {
			service.sendMessage("sip:alice@ericsson.com", "sip:alice@ericsson.com", "text/plain", msg.getBytes(), msg.length() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/
	}

	/*
	public void closeConnections() {
		getMsrpStack().getConnections().findSenderConnection(MessagingService.serverURI).stop();
		getMsrpStack().getConnections().getReceiverConnection().stop();		
	}
	 */
}
