package hu.messaging.msrp.model;

import java.net.URI;
import java.net.URISyntaxException;

public class Message {

	public enum MethodType {
		Undefined, Send, _200OK
	};
	
	private MethodType method = MethodType.Undefined; //undefined
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
	
	public MethodType getMethod() {
		return method;
	}
	public void setMethod(MethodType method) {
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

