package hu.messaging.client.gui.listener.ui;

public interface ContactListListener {
    /**
     * The contact list has changed
     * @param event The event that occured
     */
    public void contactListChanged(ContactListEvent event);
}
