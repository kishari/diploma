package hu.messaging.msrp;

import java.net.URI;
import java.net.URISyntaxException;

public class Message {

	private int method = 0; //undefined
	private String transactionId = "";
	private URI fromPath = null;
	private URI toPath = null;
	private char endToken;
	
	public void createToPath(String uri) throws URISyntaxException {
		this.toPath = new URI(uri);
	}
	
	public void createFromPath(String uri) throws URISyntaxException {
		this.fromPath = new URI(uri);
	}
	
	public int getMethod() {
		return method;
	}
	public void setMethod(int method) {
		this.method = method;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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
	public void setEndToken(char endToken) {
		this.endToken = endToken;
	}
	public char getEndToken() {
		return endToken;
	}
	
}

