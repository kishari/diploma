package hu.messaging.msrp.model;

import org.apache.commons.codec.binary.Base64;

public class CompleteMSRPMessage {
	
	private byte[] content = null;
	private String messageId = null;
	
	public CompleteMSRPMessage(String messageId, byte[] content) {
		this.messageId = messageId;
		if (Base64.isArrayByteBase64(content)) {
			this.content = Base64.decodeBase64(content);
		}
		else {
			this.content = content;
		}
		
		
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

}
