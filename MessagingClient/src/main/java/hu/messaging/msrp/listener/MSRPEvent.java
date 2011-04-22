package hu.messaging.msrp.listener;

import hu.messaging.msrp.model.CompleteMessage;

public class MSRPEvent {

	public static final int messageSentSuccess = 1;
	public static final int brokenTrasmission = 2;
	public static final int sessionStarted = 3;
	public static final int messageReceivingSuccess = 4;
	
	private String description;
	private String messageId;
	private CompleteMessage completeMessage;
	private int code;

	public MSRPEvent(int code) {
		this.code = code;		
	}
	
	public MSRPEvent(int code, String description) {
		this.code = code;
		this.description = description;		
	}
	
	public MSRPEvent(int code, String description, CompleteMessage completeMessage) {
		this.code = code;
		this.description = description;
		this.completeMessage = completeMessage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
}
