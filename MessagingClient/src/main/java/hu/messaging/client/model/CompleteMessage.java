package hu.messaging.client.model;


import org.apache.commons.codec.binary.Base64;

public class CompleteMessage {

	private byte[] content = null;
	private String messageId = null;
	private String mimeType = null;	
	private UserInfo sender = null;
	private String subject = null;
	
	public CompleteMessage() { }
	
	public CompleteMessage(String messageId, byte[] content) {
		this.messageId = messageId;
		setContent(content);		
	}
	
	public CompleteMessage(String messageId, byte[] content, String mimeType) {
		this.mimeType = mimeType;
		setContent(content);
		this.messageId = messageId;
	}
	
	public CompleteMessage(String messageId, byte[] content, String mimeType, UserInfo sender, String subject) {
		this.mimeType = mimeType;
		setContent(content);
		this.messageId = messageId;
		this.sender = sender;
		this.subject = subject;
	}


	public UserInfo getSender() {
		return sender;
	}

	public void setSender(UserInfo sender) {
		this.sender = sender;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		if (Base64.isArrayByteBase64(content)) {
			this.content = Base64.decodeBase64(content);
		}
		else {
			this.content = content;
		}
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public boolean isReady() {
		boolean ready = true;
		
		ready = ready && (getContent() != null);
		ready = ready && (getMimeType() != null && !"".equals(getMimeType()));
		ready = ready && (getSender() != null && !"".equals(getSender().getName()) && !"".equals(getSender().getSipUri()));
		ready = ready && (getSubject() != null && !"".equals(getSubject()));
		
		return ready;
	}

}
