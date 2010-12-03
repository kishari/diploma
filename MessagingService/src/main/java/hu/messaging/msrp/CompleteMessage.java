package hu.messaging.msrp;

public class CompleteMessage {

	private byte[] content;
	private String messageId = null;
	
	public CompleteMessage(byte[] content) {
		this.content = content;
	}
	
	public CompleteMessage(byte[] content, String messageId) {
		this.content = content;
		this.messageId = messageId;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

}
