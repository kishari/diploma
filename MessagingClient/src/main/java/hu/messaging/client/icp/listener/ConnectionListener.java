package hu.messaging.client.icp.listener;

/**
 * Listener to communication connection events
 */
public interface ConnectionListener
{
    /**
     * The possible connection events
     */
    public enum ConnectionState {Connecting, Connected, Refused, ConnectionFailed, Disconnected, RecipientsSentSuccessful, RecipientsSendFailed, ConnectionFinished};
    /**
     * A connection event occured con 
     */
    public void connectionChanged(ConnectionState event);
}
