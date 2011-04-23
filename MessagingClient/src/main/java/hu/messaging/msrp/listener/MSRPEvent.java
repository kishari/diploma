package hu.messaging.msrp.listener;

import hu.messaging.msrp.model.CompleteMessage;

public class MSRPEvent {

	public static final int messageSentSuccess = 1;
	public static final int brokenTrasmission = 2;
	public static final int sessionStarted = 3;
	public static final int messageReceivingSuccess = 4;
	
	private String remoteSipUri;
	private String messageId;
	private CompleteMessage completeMessage;
	private int code;
	

	public MSRPEvent(int code) {
		this.code = code;		
	}
	
	public MSRPEvent(int code, String remoteSipUri) {
		this.code = code;
		this.remoteSipUri = remoteSipUri;
	}
		
	public MSRPEvent(int code, String remoteSipUri, CompleteMessage completeMessage) {
		this.code = code;
		this.remoteSipUri = remoteSipUri;
		this.completeMessage = completeMessage;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public CompleteMessage getCompleteMessage() {
		return completeMessage;
	}

	public void setCompleteMessage(CompleteMessage completeMessage) {
		this.completeMessage = completeMessage;
	}

	public String getRemoteSipUri() {
		return remoteSipUri;
	}

	public void setRemoteSipUri(String remoteSipUri) {
		this.remoteSipUri = remoteSipUri;
	}
}
