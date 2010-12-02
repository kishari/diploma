package hu.messaging.client.icp.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.messaging.Constants;
import hu.messaging.client.icp.listener.ConnectionListener.ConnectionState;
import hu.messaging.msrp.SenderConnection;
import hu.messaging.service.MessagingService;
import hu.messaging.util.SessionDescription;
import hu.messaging.util.SDPUtil;

import com.ericsson.icp.ISessionListener;
import com.ericsson.icp.util.ErrorReason;
import com.ericsson.icp.util.IMSContentContainer;
import com.ericsson.icp.util.ISessionDescription;

public class SessionListener extends BaseListener implements ISessionListener{
	
	private List<ConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList<ConnectionListener>());
	
	public void processSessionStarted(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionStarted");
		try {
        	int mCount = sdpBody.getMediaDescriptionCount();
        	//log(Integer.toString(mCount));
        	//log(sdpBody.format());
            for (int i = 0; i < mCount; i++) {
            	SessionDescription remoteSdp = SDPUtil.parseSessionDescription(sdpBody.format());
            	
            	MessagingService.createSenderConnection(remoteSdp.getHost(), 
            											remoteSdp.getPort(), 
            											Constants.serverSipURI);
            	SenderConnection s = MessagingService.getMsrpStack().getConnections().findSenderConnection(Constants.serverSipURI);
            	SessionDescription localSdp = SDPUtil.parseSessionDescription(MessagingService.getLocalSDP(Constants.serverSipURI));
            	
            	MessagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), Constants.serverSipURI);
            	s.start();
            }
        }
        catch(Exception e) {}
        	
        notifyListeners(ConnectionState.Connected);
	}
	
	public void processSessionAlerting() {
		log(getClass().getSimpleName() + ": processSessionAlerting");
		notifyListeners(ConnectionState.Connecting);
	}

	
	public void processSessionCancelled() {
		log(getClass().getSimpleName() + ": processSessionCancelled");
	}

	
	public void processSessionEnded() {
		log(getClass().getSimpleName() + ": processSessionEnded");
		notifyListeners(ConnectionState.ConnectionFinished);
	}

	
	public void processSessionInformation(String contentType, byte[] body,
			int length) {
		log(getClass().getSimpleName() + ": processSessionInformation");
		
	}

	
	public void processSessionInformationFailed(ErrorReason reasonCode,
			long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionInformationFailed");
		
	}

	
	public void processSessionInformationSuccessful(String contentType,
			byte[] body, int length) {
		log(getClass().getSimpleName() + ": processSessionInformationSuccessful");
		
	}

	
	public void processSessionInvitation(String from,
			boolean preconditionRequired, ISessionDescription sdpBody,
			IMSContentContainer container) {
		log(getClass().getSimpleName() + ": processSessionInvitation");
		
	}

	
	public void processSessionMediaNegotiation(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionMediaNegotiation");
		
	}

	
	public void processSessionMessage(String contentType, byte[] message,
			int length) {
		log(getClass().getSimpleName() + ": processSessionMessage");
		
	}

	
	public void processSessionMessageFailed(ErrorReason reason, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionMessageFailed");
		notifyListeners(ConnectionState.RecipientsSendFailed);		
	}

	
	public void processSessionMessageSuccessful(String contentType,
			byte[] body, int length) {
		log(getClass().getSimpleName() + ": processSessionMessageSuccessful");
		notifyListeners(ConnectionState.RecipientsSentSuccessful);
		
	}

	
	public void processSessionOptions(String contentType,
			ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionOptions");
		
	}

	
	public void processSessionOptionsFailed(ErrorReason reason, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionOptionsFailed");
		
	}

	
	public void processSessionOptionsSuccessful(String contentType,
			ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionOptionsSuccessful");
		
	}

	
	public void processSessionPublishFailed(String event, byte[] state,
			int length, ErrorReason reason, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionPublishFailed");
		
	}

	
	public void processSessionPublishSuccessful(String event,
			int returnedExpires, byte[] state, int length) {
		log(getClass().getSimpleName() + ": processSessionPublishSuccessful");
		
	}

	
	public void processSessionReceivedPRACK(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionReceivedPRACK");
		
	}

	
	public void processSessionReceivedPRACKResponse(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionReceivedPRACKResponse");
		
	}

	
	public void processSessionRefer(String referTo, String contentType,
			byte[] body, int length) {
		log(getClass().getSimpleName() + ": processSessionRefer");
		
	}

	
	public void processSessionReferEnded(String referTo) {
		log(getClass().getSimpleName() + ": processSessionReferEnded");
		
	}

	
	public void processSessionReferFailed(String referTo,
			ErrorReason reasonCode, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionReferFailed");
		
	}

	
	public void processSessionReferNotify(String referTo, int referenceState,
			String subscriptionState) {
		log(getClass().getSimpleName() + ": processSessionReferNotify");
		
	}

	
	public void processSessionReferNotifyFailed(String referTo,
			ErrorReason reasonCode, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionReferNotifyFailed");
		
	}

	
	public void processSessionReferNotifySuccessful(String referTo) {
		log(getClass().getSimpleName() + ": processSessionReferNotifySuccessful");
		
	}

	
	public void processSessionReferSuccessful(String referTo) {
		log(getClass().getSimpleName() + ": processSessionReferSuccessful");
		
	}
	
	public void processSessionStartFailed(ErrorReason reasonCode,
			long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionStartFailed");
		notifyListeners(ConnectionState.ConnectionFailed);
		
	}

	
	public void processSessionSubscribeDeactived(String event,
			String contentType, byte[] content, int length) {
		log(getClass().getSimpleName() + ": processSessionSubscribeDeactived");
		
	}

	
	public void processSessionSubscribeFailed(String event, String contentType,
			byte[] content, int length, ErrorReason reason, long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionSubscribeFailed");
		
	}

	
	public void processSessionSubscribeNotification(String event,
			String contentType, byte[] content, int length) {
		log(getClass().getSimpleName() + ": processSessionSubscribeNotification");
		
	}

	
	public void processSessionSubscribeSuccessful(String event,
			String contentType, byte[] content, int length) {
		log(getClass().getSimpleName() + ": processSessionSubscribeSuccessful");
		
	}

	
	public void processSessionUpdate(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionUpdate");
		
	}

	public void processSessionUpdateFailed(ErrorReason reasonCode,
			long retryAfter) {
		log(getClass().getSimpleName() + ": processSessionUpdateFailed");
		
	}

	public void processSessionUpdateSuccessful(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionUpdateSuccessful");		
	}
	
	public void addConnectionListener(ConnectionListener listener) {
		this.connectionListeners.add(listener);
	}
	
	public void removeConnectionListener(ConnectionListener listener) {
		this.connectionListeners.remove(listener);
	}
	
	public synchronized void notifyListeners(ConnectionState event) {
		List<ConnectionListener> temp = new ArrayList<ConnectionListener>();
		synchronized(this.connectionListeners) {
			for (ConnectionListener l : this.connectionListeners ) {
				temp.add(l);
			}
		}
		for (ConnectionListener listener : temp) {
			listener.connectionChanged(event);
		}
	}

}
