package hu.messaging.client.icp.listener;

public interface ConnectionListener {

    public void connectionChanged(ConnectionStateType event);
}
