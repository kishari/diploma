package hu.messaging.client.gui.controller;

import hu.messaging.client.gui.MessagingClient;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.ContactList;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.icp.controller.ICPGroupListController;
import hu.messaging.client.gui.listener.ui.ContactListEvent;
import hu.messaging.client.gui.listener.ui.ContactListListener;
import hu.messaging.client.gui.listener.ui.ContactSelectionListener;
import hu.messaging.client.gui.listener.ui.ContactListEvent.ContactListEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import com.ericsson.winclient.data.Buddy.BuddyStatus;

public class ContactListController
{
    /**
     * List of groups and contacts in the controller
     */
    private ContactList contactList;

    private Buddy currentUser;

    /**
     * List of contact listeners. These are notified whenever a contact is added, removed or edited
     */
    private final List<ContactListListener> contactListeners = new ArrayList<ContactListListener>();

    /**
     * List of selection listeners. Notified when selection changed in the tree.
     */
    private final List<ContactSelectionListener> selectionListeners = new ArrayList<ContactSelectionListener>();

    private ContactManager contactManager;

    /**
     * ICP controller for groups
     */
    private ICPGroupListController icpGroupListController;

    public ContactListController()
    {
        this.contactList = new ContactList();
    }

    public void addBuddy(Group group, Buddy buddy)
    {
        addBuddy(group, buddy, true);
    }

    /**
     * Adds a contact to a specific group
     * 
     * @param group The group holding the buddy
     * @param buddy The buddy to add
     */
    public void addBuddy(Group group, Buddy buddy, boolean addToIcpGroup)
    {
        try
        {
            if(addToIcpGroup)
            {
                icpGroupListController.addBuddy(group, buddy);
            }
            contactList.addBuddy(group, buddy);
            fireContactListEvent(new ContactListEvent(ContactListEventType.BuddyAdded, group, buddy));
        }
        catch (Exception e)
        {
            // Unable to create buddy.
            //ICPController.error("Could not add buddy", e);
            MessagingClient.showError("add.buddy.error", e);
        }
    }

    public void addGroup(Group group)
    {
        try
        {
            icpGroupListController.addGroup(group);
            addGroupToGui(group);
            fireContactListEvent(new ContactListEvent(ContactListEventType.GroupAdded, group));
        }
        catch (Exception e)
        {
            //ICPController.error("Could not add group", e);
            MessagingClient.showError("add.group.error", e);
        }
    }

    public void addGroupToGui(Group group)
    {
        contactList.addGroup(group);
    }

    /**
     * Notify the listeners that something happened
     * 
     * @param event The vent that occured
     */
    private void fireContactListEvent(ContactListEvent event)
    {
        // Avoid concurent modifications
        List<ContactListListener> copiedList = new ArrayList<ContactListListener>(contactListeners);
        for (ContactListListener listener : copiedList)
        {
            listener.contactListChanged(event);
        }
    }

    /**
     * Removes the contact from the contact list.
     * 
     * @param contact the contact to remove
     */
    public void removeBuddy(Buddy contact)
    {
        try
        {
            Group group = getGroupForBuddy(contact);

            icpGroupListController.removeBuddy(group, contact);
            group.removeBuddy(contact);
            fireContactListEvent(new ContactListEvent(ContactListEventType.BuddyRemoved, group, contact));
        }
        catch (Exception e)
        {
            // Unable to get the members of icpGroup.
            //ICPController.error("Could not remove buddy", e);
            MessagingClient.showError("remove.buddy.error", e);
        }
    }

    /**
     * Remove a group
     * 
     * @param group The group to remove
     * @return <code>true</code> if it worked
     */
    public boolean removeGroup(Group group)
    {
        boolean success = false;
        try
        {
            icpGroupListController.removeGroup(group);
            success = contactList.removeGroup(group);
            if(success)
            {
                Group defaultGroup = getDefaultGroup();
                int nbBuddy = group.getBuddiesCount();
                for (int i = 0; i < nbBuddy; i++)
                {
                    addBuddy(defaultGroup, group.getBuddy(i));
                }
                fireContactListEvent(new ContactListEvent(ContactListEventType.GroupRemoved, group));
            }
        }
        catch (Exception e)
        {
            // unable to remove icpGroup from groupListManagement.
            //ICPController.error("Could not remove group", e);
            MessagingClient.showError("remove.group.error", e);
        }

        return success;
    }

    /**
     * Returns the group containing this contact
     * 
     * @param contact
     * @return
     */
    private Group getGroupForBuddy(Buddy contact)
    {
        Group group = null;
        for (int groupIndex = 0; groupIndex < contactList.getGroupCount(); groupIndex++)
        {
            Group currentGroup = contactList.getGroup(groupIndex);
            for (int contactIndex = 0; contactIndex < currentGroup.getBuddiesCount(); contactIndex++)
            {
                Buddy currentContact = currentGroup.getBuddy(contactIndex);
                if(currentContact.equals(contact))
                {
                    return currentGroup;
                }
            }
        }
        return group;
    }

    /**
     * Adds a new contact list listener.
     * 
     * @param listener
     */
    public void addContactListener(ContactListListener listener)
    {
        contactListeners.add(listener);
    }

    /**
     * Removes a contact listener. If the listener is not present, nothing happens.
     * 
     * @param listener
     */
    public void removeContactListener(ContactListListener listener)
    {
        contactListeners.remove(listener);
    }

    public Buddy getSelectedBuddy()
    {
        Buddy buddy = null;
        Object element = contactManager.getSelectedElement();
        if(element instanceof Buddy)
        {
            buddy = (Buddy) element;
        }
        System.out.println(getClass().getSimpleName() + " getSelectedBuddy()");
        return buddy;
    }

    public Group getSelectedGroup()
    {
    	System.out.println(getClass().getSimpleName() + " getSelectedGroup()");
        return contactManager.getSelectedGroup();

    }

    public void setContactManager(ContactManager manager)
    {
        this.contactManager = manager;
    }

    /**
     * @return the default group
     */
    public Group getDefaultGroup()
    {
        return contactList.getDefaultGroup();
    }

    // Return list of groups.
    public List<Group> getGroups()
    {
        int nbGroup = contactList.getGroupCount();
        List<Group> groupList = new ArrayList<Group>();
        for (int i = 0; i < nbGroup; i++)
        {
            groupList.add(contactList.getGroup(i));
        }
        return groupList;
    }

    public Group getGroup(String name)
    {
        return contactList.getGroupNamed(name);
    }

    public void sortAscending()
    {
        contactList.sortGroupAscending();
        for (int i = 0; i < contactList.getGroupCount(); i++)
        {
            contactList.getGroup(i).sortAscending();
        }
        fireContactListEvent(new ContactListEvent(ContactListEventType.ListSorted));
    }

    public void sortDescending()
    {
        contactList.sortGroupDescending();
        for (int i = 0; i < contactList.getGroupCount(); i++)
        {
            contactList.getGroup(i).sortDescending();
        }
        fireContactListEvent(new ContactListEvent(ContactListEventType.ListSorted));
    }

    /**
     * Get a buddy by name
     * 
     * @param buddyName
     * @return
     */
    private Buddy getContactNamed(String buddyName)
    {
        return contactList.getBuddyNamed(buddyName);
    }

    /**
     * Get the current user
     * 
     * @return The current user
     */
    public Buddy getUser()
    {
    	System.out.println(getClass().getSimpleName() + " getUser()");
        return currentUser;
    }

    /**
     * Set the current user
     * 
     * @param aUser The current user
     */
    public void setUser(Buddy aUser)
    {
        currentUser = aUser;
    }

    /**
     * Adds a selection listener. These are notified when a new buddy/group is selected from the current list.
     * 
     * @param listener
     */
    public void addSelectionListener(ContactSelectionListener listener)
    {
        selectionListeners.add(listener);
    }

    /**
     * Notifies all listeners that a new object (buddy or group) was selected from the buddy list.
     * 
     * @param source
     */
    public void fireSelectionEvent(Object source)
    {
        for (int i = 0; i < selectionListeners.size(); i++)
        {
            ContactSelectionListener listener = selectionListeners.get(i);
            listener.selectionChanged(source);
        }
    }

    /**
     * Return the Buddy with the specified Uri or the Uri argument if it can't be find.
     * 
     * @param uri The Uri of the buddy to find.
     * @return The user name of the buddy finded or the from parameter.
     */
    public Buddy getUser(String uri)
    {
    	System.out.println(getClass().getSimpleName() + " getUser(String uri)");
        List<Buddy> list = getUsers(uri);
        if(list.size() > 0)
        {
            return list.get(0);
        }
        return null;                
    }
    
    /**
     * Return the List of buddifes with the specified Uri or the Uri argument if it can't be find.
     * 
     * @param uri The Uri of the buddy to find.
     * @return The user name of the buddy finded or the from parameter.
     */
    public List<Buddy> getUsers(String uri)
    {
        List<Buddy> list = new ArrayList<Buddy>();
        
        for (int i = 0; i < contactList.getGroupCount(); i++)
        {
            Group group = contactList.getGroup(i);
            for (int e = 0; e < group.getBuddiesCount(); e++)
            {
                Buddy buddy = group.getBuddy(e);
                if(buddy.getContact().equals(uri))
                {
                    list.add(buddy);
                }
            }
        }
        return list;
    }

    /**
     * Retrieve the contact list
     */
    public ContactList getContactList()
    {
        return contactList;
    }

    /**
     * Update a group information
     * 
     * @param group The group to update
     */
    public void updateGroup(Group group)
    {
        try
        {
            icpGroupListController.modifyGroup(group);
            fireContactListEvent(new ContactListEvent(ContactListEventType.GroupModified, group));
        }
        catch (Exception e)
        {
            // Unable to get the members of icpGroup.
            //ICPController.error("Could not update group", e);
            MessagingClient.showError("update.group.error", e);
        }
    }

    /**
     * Update the buddy information
     * 
     * @param buddy The buddy to update
     */
    public void updateBuddy(Buddy buddy)
    {
        try
        {
        	System.out.println(getClass().getSimpleName() + " updateBuddy()");
            Group group = getGroupForBuddy(buddy);
            icpGroupListController.modifyBuddy(group, buddy);
            fireContactListEvent(new ContactListEvent(ContactListEventType.BuddyModified, group, buddy));
        }
        catch (Exception e)
        {
            // Unable to get the members of icpGroup.
            //ICPController.error("Could not update buddy", e);
            MessagingClient.showError("update.buddy.error", e);
        }
    }

    /**
     * Get all the display names for all groups
     * 
     * @return A list of String
     */
    public List<String> getGroupDisplayNames()
    {
        List<String> groupNames = new ArrayList<String>();

        int nbGroups = contactList.getGroupCount();
        for (int i = 0; i < nbGroups; i++)
        {
            Group group = contactList.getGroup(i);
            groupNames.add(group.getDisplayName());
        }
        return groupNames;
    }

    /**
     * @param icpGroupListController
     */
    public void setIcpGroupListController(ICPGroupListController icpGroupListController)
    {
        this.icpGroupListController = icpGroupListController;
    }
    
}
