package hu.messaging.client;

import java.awt.TextArea;
import java.net.InetAddress;

import java.util.*;
import hu.messaging.msrp.*;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import com.ericsson.icp.util.*;


public class Client {
	
	private MSRPStack msrpStack = new MSRPStack();
	
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

	public void disposeICP() {		
		try {
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
    	   path = "MSRP://" + address + ":" + port + "/" + sessionId + ";tcp";
  
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
		
			if (!getMsrpStack().getConnections().isReceiverConnection()) {
				getMsrpStack().getConnections().createReceiverConnection(InetAddress.getLocalHost());
				getMsrpStack().getConnections().getReceiverConnection().start();
			}
			else if (!getMsrpStack().getConnections().isRunningReceiverConnection()) {
				getMsrpStack().getConnections().getReceiverConnection().start();
			}
			
			ISessionDescription sdp = createLocalSDP(getMsrpStack().getConnections().getReceiverConnection().getHostAddress(),
													 getMsrpStack().getConnections().getReceiverConnection().getPort(),
													 "clientsessionid");

			logArea.append("send INVITE to: " + "sip:weblogic103@192.168.1.103" + "\n");
			session.start("sip:weblogic103@192.168.1.103", sdp, profile.getIdentity(), SdpFactory.createIMSContentContainer());
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
	
	public void sendMessage() {
		String msg = new String("MSG FROM CLIENT");
		
		logArea.append("send MESSAGE to alice: " + msg + "\n");
		
		try {
			service.sendMessage("sip:alice@ericsson.com", "sip:alice@ericsson.com", "text/plain", msg.getBytes(), msg.length() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	public void closeConnections() {
		getMsrpStack().getConnections().getReceiverConnection().stop();		
	}

}
