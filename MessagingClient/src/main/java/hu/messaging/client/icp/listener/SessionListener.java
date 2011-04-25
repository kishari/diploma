package hu.messaging.client.icp.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.icp.listener.ConnectionStateType;
import hu.messaging.msrp.util.SessionDescription;
import hu.messaging.util.SDPUtil;

import com.ericsson.icp.ISessionListener;
import com.ericsson.icp.util.ErrorReason;
import com.ericsson.icp.util.IMSContentContainer;
import com.ericsson.icp.util.ISessionDescription;

public class SessionListener extends BaseListener implements ISessionListener{
	
	private List<ConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList<ConnectionListener>());
	private String remoteSipUri;
	
	public SessionListener(ICPController icpController, String remoteSipUri) {
		super(icpController);
		this.remoteSipUri = remoteSipUri;
	}
	public void processSessionStarted(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionStarted");
		try {
        	int mCount = sdpBody.getMediaDescriptionCount();
            for (int i = 0; i < mCount; i++) {
            	SessionDescription remoteSdp = SDPUtil.parseSessionDescription(sdpBody.format());
            	SessionDescription localSdp = SDPUtil.parseSessionDescription(icpController.getCommunicationController().getLocalSDP(Resources.serverSipURI));            	            	
            	     
            	icpController.getCommunicationController().getMsrpStack().createMSRPSession(localSdp, remoteSdp, Resources.serverSipURI);

            }
        }
        catch(Exception e) {}
        	
        notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.Connected, remoteSipUri));
	}
	
	public void processSessionAlerting() {
		log(getClass().getSimpleName() + ": processSessionAlerting");
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.Connecting, remoteSipUri));
	}

	
	public void processSessionCancelled() {
		log(getClass().getSimpleName() + ": processSessionCancelled");
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.Refused, remoteSipUri));
	}

	
	public void processSessionEnded() {
		log(getClass().getSimpleName() + ": processSessionEnded");
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.ConnectionFinished, remoteSipUri));
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
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.RecipientsSendFailed, remoteSipUri));		
	}

	
	public void processSessionMessageSuccessful(String contentType,
			byte[] body, int length) {
		log(getClass().getSimpleName() + ": processSessionMessageSuccessful");
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.RecipientsSentSuccessful, remoteSipUri));
		
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
		notifyListeners(new ConnectionStateType(ConnectionStateType.ConnectionState.ConnectionFailed, remoteSipUri));
		
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
	
	public synchronized void notifyListeners(ConnectionStateType event) {
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
	public String getRemoteSipUri() {
		return remoteSipUri;
	}

}
