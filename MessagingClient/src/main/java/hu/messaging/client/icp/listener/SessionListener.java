package hu.messaging.client.icp.listener;

import hu.messaging.Constants;
import hu.messaging.msrp.SenderConnection;
import hu.messaging.service.MessagingService;
import hu.messaging.util.ParsedSDP;
import hu.messaging.util.SDPUtil;

import com.ericsson.icp.ISessionListener;
import com.ericsson.icp.util.ErrorReason;
import com.ericsson.icp.util.IMSContentContainer;
import com.ericsson.icp.util.ISessionDescription;

public class SessionListener extends BaseListener implements ISessionListener{

	public void processSessionStarted(ISessionDescription sdpBody) {
		log(getClass().getSimpleName() + ": processSessionStarted");
		try {
        	int mCount = sdpBody.getMediaDescriptionCount();
        	//log(Integer.toString(mCount));
        	log(sdpBody.format());
            for (int i = 0; i < mCount; i++) {
            	SDPUtil sdpUtil = new SDPUtil();
            	ParsedSDP remoteSdp = sdpUtil.parseSessionDescription(sdpBody.format());
            	log(remoteSdp.getHost().getHostAddress());
            	log(Integer.toString(remoteSdp.getPort()));
            	
            	/*MessagingService.createSenderConnection(remoteSdp.getHost(), 
            											remoteSdp.getPort(), 
            											Constants.serverURI);
            	SenderConnection s = MessagingService.getMsrpStack().getConnections().findSenderConnection(Constants.serverURI);

            	ParsedSDP localSdp = sdpUtil.parseSessionDescription(MessagingService.getLocalSDP(Constants.serverURI));
            	log("localSdp path: " + localSdp.getPath());
            	MessagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), Constants.serverURI);
            	s.start();
            	*/
            }
        }
        catch(Exception e) {}
		
	}
	public void processSessionAlerting() {
		log(getClass().getSimpleName() + ": processSessionAlerting");
	}

	
	public void processSessionCancelled() {
		log(getClass().getSimpleName() + ": processSessionCancelled");
		
	}

	
	public void processSessionEnded() {
		log(getClass().getSimpleName() + ": processSessionEnded");
		
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
		
	}

	
	public void processSessionMessageSuccessful(String contentType,
			byte[] body, int length) {
		log(getClass().getSimpleName() + ": processSessionMessageSuccessful");
		
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

}
