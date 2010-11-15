package hu.messaging.msrp;

import java.net.URI;
import java.net.URISyntaxException;

public class MSRPMessage {

	private int method = 0; //undefined
	private int sumByte = 0;
	private int firstByte = 0;
	private int lastByte = 0;
	
	private URI fromPath = null;
	private URI toPath = null;
	private String contentType;
	private String messageId;
	private byte[] content = null;
	private String transactionId;
	private char endToken;
	public void createTestMessage() {
		try {
			createToPath("localhost", 9082, "sessionId2");
			createFromPath("localhost", 9080, "sessionId1");
			this.method = Constants.methodSEND;
			this.content = new String("én vagyok a teszt tartalom").getBytes();
			this.firstByte = 1;
			this.lastByte = content.length;
			this.sumByte = content.length;
			this.transactionId = "12345678910";
			this.messageId = "123ID321ABC";
			this.contentType = "text/plain";
			this.endToken = '+';
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	public void createToPath(String host, int port, String sessionId) throws URISyntaxException {
		if (toPath == null) {
			toPath = new URI("msrp", null, host, port, "/" + sessionId +";tcp", null, null);
		}
	}
	
	public void createFromPath(String host, int port, String sessionId) throws URISyntaxException {
		if (fromPath == null) {
			fromPath = new URI("msrp", null, host, port, "/" + sessionId +";tcp", null, null);
		}
	}
	
	public void createToPath(String uri) throws URISyntaxException {
		this.toPath = new URI(uri);
	}
	
	public void createFromPath(String uri) throws URISyntaxException {
		this.fromPath = new URI(uri);
	}
	
	@Override
	public String toString() {
		String msg = new String();
		msg += "MSRP " + transactionId + " " + Constants.methods.get(method) + "\r\n";
		msg += "To-Path: " + toPath + "\r\n";
		msg += "From-Path: " + fromPath + "\r\n";
		msg += "Message-ID: " + messageId + "\r\n";
		msg += "Byte-Range: " + firstByte + "-" + lastByte + "/" + sumByte + "\r\n";
		msg += "Content-Type: " + contentType + "\r\n\r\n";
		msg += new String(content) + "\r\n";
		msg += "-------" + transactionId + endToken;
		return msg;
	}

	public int getSumByte() {
		return sumByte;
	}
	public void setSumByte(int sumByte) {
		this.sumByte = sumByte;
	}
	public int getFirstByte() {
		return firstByte;
	}
	public void setFirstByte(int firstByte) {
		this.firstByte = firstByte;
	}
	public int getLastByte() {
		return lastByte;
	}
	public void setLastByte(int lastByte) {
		this.lastByte = lastByte;
	}
	public URI getFromPath() {
		return fromPath;
	}
	public void setFromPath(URI fromPath) {
		this.fromPath = fromPath;
	}
	public URI getToPath() {
		return toPath;
	}
	public void setToPath(URI toPath) {
		this.toPath = toPath;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public byte[] getContent() {
		return content;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public int getMethod() {
		return method;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setEndToken(char endToken) {
		this.endToken = endToken;
	}
	public char getEndToken() {
		return endToken;
	}
	
}

