package hu.messaging.client.icp.listener;

public class ConnectionStateType {

	public enum ConnectionState {Connecting, Connected, Refused, ConnectionFailed, Disconnected, RecipientsSentSuccessful, RecipientsSendFailed, ConnectionFinished};
	
	private ConnectionState connectionState;
	
	private String remoteSipUri;
	
	public ConnectionStateType(ConnectionState state, String remoteSipUri) {
		this.connectionState = state;
		this.remoteSipUri = remoteSipUri;		
	}
	
	public String getRemoteSipUri() {
		return remoteSipUri;
	}
	public void setRemoteSipUri(String remoteSipUri) {
		this.remoteSipUri = remoteSipUri;
	}
	
	public ConnectionState getConnectionState() {
		return connectionState;
	}
	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}
}
