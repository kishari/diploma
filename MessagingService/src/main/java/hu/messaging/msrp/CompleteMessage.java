package hu.messaging.msrp;

public class CompleteMessage {

	private byte[] content;
	private String messageId = null;
	private String extension;
	private String sender;
	
	public CompleteMessage() { }
	
	public CompleteMessage(byte[] content, String extension) {
		this.extension = extension;
		this.content = content;
	}
	
	public CompleteMessage(String messageId, byte[] content, String extension) {
		this.extension = extension;
		this.content = content;
		this.messageId = messageId;
	}
	
	public CompleteMessage(String messageId, byte[] content, String extension, String sender) {
		this.extension = extension;
		this.content = content;
		this.messageId = messageId;
		this.sender = sender;
	}


	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isReady() {
		boolean ready = true;
		
		ready = ready && (getContent() != null);
		ready = ready && (getExtension() != null && !"".equals(getExtension()));
		ready = ready && (getSender() != null && !"".equals(getSender()));
		
		System.out.println(ready);
		return ready;
	}

}
