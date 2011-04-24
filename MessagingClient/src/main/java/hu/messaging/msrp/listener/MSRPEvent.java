package hu.messaging.msrp.listener;

import hu.messaging.msrp.model.FullMSRPMessage;

public class MSRPEvent {

	public enum MSRPEventType {
		messageSentSuccess, brokenTrasmission, sessionStarted, messageReceivingSuccess
	};
	
	private MSRPEventType eventType;
	private String remoteSipUri;
	private String messageId;
	private FullMSRPMessage fullMessage;
	

	public MSRPEvent(MSRPEventType eventType) {
		this.eventType = eventType;		
	}
	
	public MSRPEvent(MSRPEventType eventType, String remoteSipUri) {
		this.eventType = eventType;
		this.remoteSipUri = remoteSipUri;
	}
		
	public MSRPEvent(MSRPEventType eventType, String remoteSipUri, FullMSRPMessage fullMessage) {
		this.eventType = eventType;
		this.remoteSipUri = remoteSipUri;
		this.fullMessage = fullMessage;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
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

	public FullMSRPMessage getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(FullMSRPMessage fullMessage) {
		this.fullMessage = fullMessage;
	}
}
