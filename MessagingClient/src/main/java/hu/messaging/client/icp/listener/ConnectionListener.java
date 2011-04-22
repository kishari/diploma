package hu.messaging.client.icp.listener;

public interface ConnectionListener {

    public enum ConnectionState {Connecting, Connected, Refused, ConnectionFailed, Disconnected, RecipientsSentSuccessful, RecipientsSendFailed, ConnectionFinished};

    public void connectionChanged(ConnectionState event);
}
