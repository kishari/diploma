package hu.messaging.msrp.util;

import java.net.URI;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SessionDescription {

	private InetAddress host;
	private int port;
	private Map<String, String> attributes = new HashMap<String, String>();
	
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
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
	public URI getPath() {		
		URI path = null;
		try {
			path = new URI(attributes.get("PATH"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}

	
	
}
