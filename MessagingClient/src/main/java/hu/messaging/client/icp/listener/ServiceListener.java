package hu.messaging.client.icp.listener;

import com.ericsson.icp.IServiceListener;
import com.ericsson.icp.ISession;

public class ServiceListener extends BaseListener implements IServiceListener {

	public ServiceListener() {
		super();
	}

	public void processIncomingSession(ISession session) {
		log(getClass().getSimpleName() + ": ");

	}

	public void processMessage(String remote, String msgType, byte[] message, int length) {
		log(getClass().getSimpleName() + ": processMessage: "  + new String(message));
	}

	public void processOptions(String preferedContact, String remote,
			String type, byte[] content, int length) {
		log(getClass().getSimpleName() + ": processOptions");

	}

	
	public void processPublishResult(boolean status, String remote,
			String event, long retryAfter) {
		log(getClass().getSimpleName() + ": processPublishResult");

	}

	
	public void processRefer(String referID, String remote, String thirdParty,
			String contentType, byte[] content, int length) {
		log(getClass().getSimpleName() + ": processRefer");
	}

	
	public void processReferEnded(String referID) {
		log(getClass().getSimpleName() + ": processReferEnded");
	}

	
	public void processReferNotification(String referId, int state) {
		log(getClass().getSimpleName() + ": processReferNotification");

	}

	
	public void processReferNotifyResult(boolean status, String referID,
			long retryAfter) {
		log(getClass().getSimpleName() + ": processReferNotifyResult");

	}

	
	public void processReferResult(boolean status, String referId,
			long retryAfter) {
		log(getClass().getSimpleName() + ": processReferResult");

	}

	
	public void processSendMessageResult(boolean status, long retryAfter) {
		log(getClass().getSimpleName() + ": processSendMessageResult: " + status);
	}

	
	public void processSendOptionsResult(boolean status,
			String preferedContact, String remote, String type, byte[] content,
			int length, long retryAfter) {
		log(getClass().getSimpleName() + ": ");

	}

	
	public void processSubscribeEnded(String preferedContact, String remote,
			String event) {
		log(getClass().getSimpleName() + ": ");

	}

	
	public void processSubscribeNotification(String remote, String contact,
			String event, String type, byte[] content, int length) {
		log(getClass().getSimpleName() + ": ");

	}

	
	public void processSubscribeResult(boolean status, String remote,
			String event, long retryAfter) {
		log(getClass().getSimpleName() + ": ");

	}

	
	public void processUnsubscribeResult(boolean status, String remote,
			String event, long retryAfter) {
		log(getClass().getSimpleName() + ": processSendOptionsResult");
	}
}
