package hu.messaging.client.gui.controller;

import hu.messaging.Constants;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.MSRPStack;
import hu.messaging.msrp.SenderConnection;
import hu.messaging.msrp.Session;
import hu.messaging.msrp.event.MSRPListener;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.util.*;
import hu.messaging.client.listener.NotifyListener;

import hu.messaging.client.model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.icp.util.ISessionDescription;
import com.ericsson.icp.util.SdpFactory;

public class CommunicationController {
	
	private List<InfoMessage> incomingNewMessageDescriptors = new ArrayList<InfoMessage>();
	
	private Map<String, String> localSDPs = new HashMap<String, String>();	
	private MSRPStack msrpStack = new MSRPStack();
	private List<NotifyListener> notifyListeners = new ArrayList<NotifyListener>();
	
	private ICPController icpController;
	
	public CommunicationController(ICPController icpController) { 
		this.icpController = icpController;		
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
	
	public void sendMessagesInMSRPSession(List<CompleteMessage> messages, String sipUri) {
		for (CompleteMessage m : messages) {
			sendMessageInMSRPSession(m, sipUri);
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
		System.out.println("CommunicationController.getLocalSDP to: " + remoteId);
		String sdp = localSDPs.get(remoteId);
		//System.out.println("sdp: " + sdp );
		return sdp;
	}
	
	public Session createNewMSRPSession(URI localURI, URI remoteURI, String sipUri) {
		SenderConnection s = getMsrpStack().getConnections().getSenderConnection(sipUri);
		System.out.println("CommunicationController createNewMSRPSession");
		
		if (s == null) {
			System.out.println("nem találtunk a sessionhoz sendert");
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
	
	public void addNotifyListener(NotifyListener listener) {
		this.notifyListeners.add(listener);
	}
	
	public void removeNotifyListener(NotifyListener listener) {
		this.notifyListeners.remove(listener);
	}
	
	public synchronized void notifyListeners(InfoMessage infoMessage) {
		List<NotifyListener> temp = new ArrayList<NotifyListener>();
		synchronized(this.notifyListeners) {
			for (NotifyListener l : this.notifyListeners ) {
				temp.add(l);
			}
		}
		for (NotifyListener listener : temp) {					
			listener.notifyNewMessage(infoMessage);
		}
	}
	
	public void sendSIPMessage(String to, String message) {
        try
        {
           // Send the message
            byte[] messageBytes = message.getBytes("UTF-8");
            icpController.getService().sendMessage(icpController.getProfile().getIdentity(), to, "text/plain", messageBytes, messageBytes.length);
        }
        catch (Exception e)
        {            
        }
	}
	
    public void incomingSIPMessage(String to, String message) {
    	System.out.println(getClass().getSimpleName() + " incomingSIPMessage: ");
    	System.out.println(message);
    	if (message.startsWith("<?xml")) {
    		InfoMessage info = (InfoMessage)XMLUtils.createInfoMessageFromStringXML(message);
    		if ("NOTIFY_USER".equals(info.getInfoType().toUpperCase().trim())) {
    			MessageContainer c = MessageUtils.createMessageContainerFromNotifyInfoMessage(info);
    			MessageUtils.createMessageContainerFile(c, null);
    			
    		}    		
    	}
    }
    
    public void sendInvite(ISessionDescription localSdp) throws Exception {        
    	icpController.getSession().start(Constants.serverSipURI, localSdp, 
    									 icpController.getProfile().getIdentity(), 
    									 SdpFactory.createIMSContentContainer());
	}
     
	public void sendBye() {
		try {
			icpController.getSession().end();
		}
		catch(Exception e) { }		
	}
    
    public List<CompleteMessage> getIncomingNewMessages() {
    	List<CompleteMessage> newMessages = new ArrayList<CompleteMessage>();
    	for (InfoMessage descr : this.incomingNewMessageDescriptors) {
    		CompleteMessage cm = new CompleteMessage(descr.getInfoDetail().getId(), null, descr.getInfoDetail().getMimeType(), 
    												 descr.getInfoDetail().getSender().getSipUri(), descr.getInfoDetail().getSubject());
        	newMessages.add(cm);    	
    	}
    	
    	return newMessages;
    }
    
	public ISessionDescription getLocalSDP() throws IOException {
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

}