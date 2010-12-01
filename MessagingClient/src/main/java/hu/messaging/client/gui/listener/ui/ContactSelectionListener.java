package hu.messaging.client.gui.listener.ui;

/**
 * Listener for buddy/group selection changed.
 *
 */
public interface ContactSelectionListener
{
    /**
     * Notifies that a new buddy or group was selected from the tree.
     * @param newSelection new selected object.
     */
    public void selectionChanged(Object newSelection);
}
