package hu.messaging.msrp.listener;

import hu.messaging.msrp.model.CompleteMessage;

public class MSRPEvent {

	public enum MSRPEventType {
		messageSentSuccess, brokenTrasmission, sessionStarted, messageReceivingSuccess
	};
	
	private MSRPEventType eventType;
	private String remoteSipUri;
	private String messageId;
	private CompleteMessage completeMessage;
	

	public MSRPEvent(MSRPEventType eventType) {
		this.eventType = eventType;		
	}
	
	public MSRPEvent(MSRPEventType eventType, String remoteSipUri) {
		this.eventType = eventType;
		this.remoteSipUri = remoteSipUri;
	}
		
	public MSRPEvent(MSRPEventType eventType, String remoteSipUri, CompleteMessage completeMessage) {
		this.eventType = eventType;
		this.remoteSipUri = remoteSipUri;
		this.completeMessage = completeMessage;
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

	public MSRPEventType getEventType() {
		return eventType;
	}

	public void setEventType(MSRPEventType eventType) {
		this.eventType = eventType;
	}
}
