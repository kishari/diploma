package hu.messaging.client.gui.listener.ui;

import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;

public class ContactListEvent
{
    /**
     * Possible event type
     */
    public enum ContactListEventType {
    	BuddyAdded, BuddyModified, BuddyRemoved, GroupAdded, GroupModified, GroupRemoved, ListSorted
    };
    
    /**
     * The group on which the event occured
     */
    public Group group;
    /**
     * The buddy on which the event occured
     */
    public Buddy buddy;
    /**
     * The event that occured
     */
    public ContactListEventType event;
    
    public ContactListEvent(ContactListEventType event) {
        this(event, null, null);
    }
    
    public ContactListEvent(ContactListEventType event, Group group) {
        this(event, group, null);
    }
    
    public ContactListEvent(ContactListEventType event, Buddy buddy) {
        this(event, null, buddy);    
    }
    
    public ContactListEvent(ContactListEventType event, Group group, Buddy buddy) {
        this.event = event;
        this.group = group;
        this.buddy = buddy;
    }
}
