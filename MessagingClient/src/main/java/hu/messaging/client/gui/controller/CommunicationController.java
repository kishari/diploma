package hu.messaging.client.gui.controller;

import hu.messaging.msrp.model.Constants;
import hu.messaging.msrp.model.CompleteMSRPMessage;
import hu.messaging.msrp.MSRPStack;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.util.*;

import hu.messaging.client.Resources;
import hu.messaging.client.icp.listener.ConnectionStateType;
import hu.messaging.client.model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.icp.util.ISessionDescription;
import com.ericsson.icp.util.SdpFactory;

public class CommunicationController implements hu.messaging.client.icp.listener.ConnectionListener {

	private Map<String, String> localSDPs = new HashMap<String, String>();
	
	private MSRPStack msrpStack;	
	private ICPController icpController;
	
	public CommunicationController(ICPController icpController) { 
		this.icpController = icpController;	
		msrpStack = new MSRPStack();
	}
	
	public void sendMessageInMSRPSession(CompleteMessage completeMessage, String sipUri) {
		getMsrpStack().sendMessage(new CompleteMSRPMessage(completeMessage.getMessageId(), completeMessage.getContent()), sipUri);
	}

	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	private void addLocalSDP(String remoteId, String sdp) {
		localSDPs.put(remoteId, sdp);
	}
	
	public void removeLocalSDP(String remoteId) {
		localSDPs.remove(remoteId);
	}
	
	public String getLocalSDP(String remoteId) {
		String sdp = localSDPs.get(remoteId);
		return sdp;
	}
	
	public void addMSRPListener(MSRPListener listener) {
		getMsrpStack().addMSRPListener(listener);
	}
	
	public void removeMSRPListener(MSRPListener listener) {
		getMsrpStack().removeMSRPListener(listener);
	}
	
	public void sendSIPMessageInSIPSession(String to, String message) {
		try {
			icpController.getSession(to).sendMessage("text/plain", message.getBytes(), message.getBytes().length);
		}
		catch(Exception e) {e.printStackTrace();}
		
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
    
    public void sendInvite(String remoteSipURI) throws Exception {
    	System.out.println("sendInvite");
    	ISessionDescription localSdp = createLocalSDP();
    	icpController.getSession(remoteSipURI).start(remoteSipURI, localSdp, 
    									 icpController.getLocalUser().getContact(), 
    									 SdpFactory.createIMSContentContainer());
    	
    	icpController.getCommunicationController().getMsrpStack().startReceiverConnection();
    	addLocalSDP(remoteSipURI, localSdp.format());
	}
     
	public void sendBye(String remoteSipURI) {
		try {
			icpController.getSession(remoteSipURI).end();
		}
		catch(Exception e) { }				
	}
    
	private ISessionDescription createLocalSDP() throws IOException {
		
		InetAddress localhost = getMsrpStack().getReceiverConnectionHostAddress();
		int port = getMsrpStack().getReceiverConnectionPort();
		
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
	
	@Override
	public void connectionChanged(ConnectionStateType event) {
		switch (event.getConnectionState()) {
        case Connecting:
            break;
        case Connected:      
            break;
        case Refused:
            break;
        case ConnectionFailed:           
            break;
        case Disconnected:
            break;    
        case ConnectionFinished:
        	icpController.deleteSipSession(event.getRemoteSipUri());
        	removeLocalSDP(event.getRemoteSipUri());
        	break;
        case RecipientsSentSuccessful:
        	break;            
    }
	}

}