package hu.messaging.msrp;

import hu.messaging.Constants;

public class Request extends Message implements Comparable{
	
	private int sumByte = 0;
	private int firstByte = 0;
	private int lastByte = 0;
	
	private String contentType = "";
	private String messageId = "";
	private byte[] content = null;

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
	
	@Override
	public String toString() {
		String msg = new String();
		msg += "MSRP " + getTransactionId() + " " + Constants.methods.get( getMethod() ) + "\r\n";
		msg += "To-Path: " + getToPath() + "\r\n";
		msg += "From-Path: " + getFromPath() + "\r\n";
		msg += "Message-ID: " + getMessageId() + "\r\n";
		msg += "Byte-Range: " + getFirstByte() + "-" + getLastByte() + "/" + getSumByte() + "\r\n";
		msg += "Content-Type: " + getContentType() + "\r\n\r\n";
		msg += new String( getContent() ) + "\r\n";
		msg += "-------" + getTransactionId() + getEndToken();
		return msg;
	}

	public int compareTo(Object otherRequest){
		
		if ( !(otherRequest instanceof Request) ) {
			throw new ClassCastException("Invalid object");
		}
		Request r = (Request)otherRequest;
		
 		if (this.getFirstByte() > r.getFirstByte() ) {
 			return 1;
		}
 		else if (this.getFirstByte() < r.getFirstByte()) {
 			return -1;
 		}
 		
		return 0;
	}
	
}
