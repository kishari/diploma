package hu.messaging.client.listener;

import hu.messaging.client.model.InfoMessage;

public interface NotifyListener {

	public void notifyNewMessage(InfoMessage infoMessage);
}
