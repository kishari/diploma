package hu.messaging.client.gui.controller;

import hu.messaging.msrp.model.Constants;
import hu.messaging.msrp.MSRPStack;
import hu.messaging.msrp.SenderConnection;
import hu.messaging.msrp.Session;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.model.CompleteMessage;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.util.*;

import hu.messaging.client.Resources;
import hu.messaging.client.model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.icp.util.ISessionDescription;
import com.ericsson.icp.util.SdpFactory;

public class CommunicationController {
	
	private Map<String, String> localSDPs = new HashMap<String, String>();
	
	private MSRPStack msrpStack;	
	private ICPController icpController;
	
	public CommunicationController(ICPController icpController) { 
		this.icpController = icpController;	
		msrpStack = new MSRPStack();
	}
	
	public void createSenderConnection(InetAddress host, int port, String sipUri) {
		try {
			getMsrpStack().createSenderConnection(host, port, sipUri);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createReceiverConnection(InetAddress host) {
		try {
			getMsrpStack().createReceiverConnection(host);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessageInMSRPSession(CompleteMessage completeMessage, String sipUri) {
		getMsrpStack().sendMessage(completeMessage, sipUri);
	}
	
	public boolean isRunningReceiverConnection() {
		return getMsrpStack().getConnections().isRunningReceiverConnection();
	}
	
	public boolean isReceiverConnection() {
		return getMsrpStack().getConnections().isReceiverConnection();
	}

	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	public void addLocalSDP(String remoteId, String sdp) {
		localSDPs.put(remoteId, sdp);
	}
	
	public void removeLocalSDP(String remoteId) {
		localSDPs.remove(remoteId);
	}
	
	public String getLocalSDP(String remoteId) {
		String sdp = localSDPs.get(remoteId);
		return sdp;
	}
	
	public Session createNewMSRPSession(URI localURI, URI remoteURI, String remoteSipUri) {
		SenderConnection s = getMsrpStack().getConnections().getSenderConnection(remoteSipUri);
		
		if (s == null) {
			return null;
		}
		
		Session newSession = new Session(localURI, remoteURI, s, msrpStack);
		getMsrpStack().putNewSession(newSession);
		
		s.setSession(newSession);
		
		return newSession;
	}
	
	public void addMSRPListener(MSRPListener listener) {
		getMsrpStack().addMSRPListener(listener);
	}
	
	public void removeMSRPListener(MSRPListener listener) {
		getMsrpStack().removeMSRPListener(listener);
	}
	
	public void sendSIPMessage(String to, String message) {
        try {
            byte[] messageBytes = message.getBytes("UTF-8");
            icpController.getService().sendMessage(icpController.getLocalUser().getContact(), to, "text/plain", messageBytes, messageBytes.length);
        }
        catch (Exception e) {
        	
        }
	}
	
    public void processIncomingSIPMessage(String to, String message) {
    	if (message.startsWith("<?xml")) {
    		InfoMessage info = (InfoMessage)XMLUtils.createInfoMessageFromStringXML(message);
    		System.out.println(info.getInfoType());
    		if (InfoMessage.notifyUser.equals(info.getInfoType().toUpperCase().trim())) {
    			List<MessageInfoContainer> cList = MessageUtils.createMessageInfoContainerListFromNotifyInfoMessage(info);
    			for (MessageInfoContainer c : cList) {    				
    				MessageUtils.createMessageContainerFile(c, null);
    			}    				    			
    		}    		
    	}
    }
    
    public void sendInvite(ISessionDescription localSdp) throws Exception {        
    	icpController.getSession().start(Resources.serverSipURI, localSdp, 
    									 icpController.getLocalUser().getContact(), 
    									 SdpFactory.createIMSContentContainer());
	}
     
	public void sendBye() {
		try {
			icpController.getSession().end();
		}
		catch(Exception e) { }		
	}
    
	public ISessionDescription createLocalSDP() throws IOException {
		if (!icpController.getCommunicationController().getMsrpStack().getConnections().isReceiverConnection() ||
			!icpController.getCommunicationController().getMsrpStack().getConnections().isRunningReceiverConnection()) {
			
			icpController.getCommunicationController().getMsrpStack().getConnections().createReceiverConnection(InetAddress.getLocalHost());
			icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().start();
		}
		
		InetAddress localhost = icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().getHostAddress();
		int port = icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().getPort();
		String sessionId = MSRPUtil.generateRandomString(Constants.sessionIdLength);
		
		return SDPUtil.createSDP(localhost, port, sessionId);
	}
	
	public void fetchNewMessageInfos() {
		ObjectFactory f = new ObjectFactory();
		InfoMessage m = f.createInfoMessage();
		m.setInfoType(InfoMessage.pullNewMessageInfos);
		m.setDetailList(f.createInfoMessageDetailList());
		
		this.sendSIPMessage(Resources.serverSipURI, XMLUtils.createStringXMLFromInfoMessage(m));
	}

}