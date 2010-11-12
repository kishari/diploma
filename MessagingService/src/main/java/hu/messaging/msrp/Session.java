package hu.messaging.msrp;

import java.net.URI;

public class Session {

	private URI localUri;
	private URI remoteUri;
	
	public URI getLocalUri() {
		return localUri;
	}
	public void setLocalUri(URI localUri) {
		this.localUri = localUri;
	}
	public URI getRemoteUri() {
		return remoteUri;
	}
	public void setRemoteUri(URI remoteUri) {
		this.remoteUri = remoteUri;
	}
}
