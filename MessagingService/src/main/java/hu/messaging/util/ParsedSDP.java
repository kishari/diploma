package hu.messaging.util;

import java.net.URI;
import java.net.InetAddress;

public class ParsedSDP {

	InetAddress host;
	int port;
	String sessionId;
	URI remotePath;
	
	
	public InetAddress getHost() {
		return host;
	}
	public void setHost(InetAddress host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public URI getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(URI remotePath) {
		this.remotePath = remotePath;
	}
	
}
