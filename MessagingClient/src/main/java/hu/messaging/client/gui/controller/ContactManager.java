package hu.messaging.client.gui.controller;

import hu.messaging.client.gui.data.Group;

public interface ContactManager {
	
    /**
     * Returns the currently selected contact. Can be <code>null</code> if 
     * no contact is currently selected.
     * @return the currently selected contact, or <code>null</code>
     */
    public Object getSelectedElement();
    
    public Group getSelectedGroup();
}
