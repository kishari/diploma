package hu.messaging.msrp.event;

public class MSRPEvent {

	public static final int messageSentSuccessCode = 1;
	public static final int startTrasmissionCode = 2;
	public static final int brokenTrasmissionCode = 3;
	public static final int sessionStartedCode = 4;
	
	private String description;
	private String messageId;
	private int code;
	
	public MSRPEvent(String description, int code) {
		this.description = description;
		this.code = code;
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
}
