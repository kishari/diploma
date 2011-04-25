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
import java.util.List;

public class ContactListController {

	private ContactList contactList;

	private Buddy localUser;

	private final List<ContactListListener> contactListeners = new ArrayList<ContactListListener>();

	private final List<ContactSelectionListener> selectionListeners = new ArrayList<ContactSelectionListener>();

	private ContactManager contactManager;

	private ICPGroupListController icpGroupListController;

	public ContactListController() {
		this.contactList = new ContactList();
	}

	public void addBuddy(Group group, Buddy buddy) {
		addBuddy(group, buddy, true);
	}

	public void addBuddy(Group group, Buddy buddy, boolean addToIcpGroup) {
		try {
			if (addToIcpGroup) {
				icpGroupListController.addBuddy(group, buddy);
			}
			contactList.addBuddy(group, buddy);
			fireContactListEvent(new ContactListEvent(ContactListEventType.BuddyAdded, group, buddy));
		} catch (Exception e) {
			MessagingClient.showError("add.buddy.error", e);
		}
	}

	public void addGroup(Group group) {
		try {
			icpGroupListController.addGroup(group);
			addGroupToGui(group);
			fireContactListEvent(new ContactListEvent(ContactListEventType.GroupAdded, group));
		} catch (Exception e) {
			MessagingClient.showError("add.group.error", e);
		}
	}

	public void addGroupToGui(Group group) {
		contactList.addGroup(group);
	}

	private void fireContactListEvent(ContactListEvent event) {
		List<ContactListListener> copiedList = new ArrayList<ContactListListener>(contactListeners);
		for (ContactListListener listener : copiedList) {
			listener.contactListChanged(event);
		}
	}

	public void removeBuddy(Buddy contact) {
		try {
			Group group = getGroupForBuddy(contact);

			icpGroupListController.removeBuddy(group, contact);
			group.removeBuddy(contact);
			fireContactListEvent(new ContactListEvent(ContactListEventType.BuddyRemoved, group, contact));
		} catch (Exception e) {
			MessagingClient.showError("remove.buddy.error", e);
		}
	}

	public boolean removeGroup(Group group) {
		boolean success = false;
		try {
			icpGroupListController.removeGroup(group);
			success = contactList.removeGroup(group);
			if (success) {
				Group defaultGroup = getDefaultGroup();
				int nbBuddy = group.getBuddiesCount();
				for (int i = 0; i < nbBuddy; i++) {
					addBuddy(defaultGroup, group.getBuddy(i));
				}
				fireContactListEvent(new ContactListEvent(ContactListEventType.GroupRemoved, group));
			}
		} catch (Exception e) {
			MessagingClient.showError("remove.group.error", e);
		}

		return success;
	}

	private Group getGroupForBuddy(Buddy contact) {
		Group group = null;
		for (int groupIndex = 0; groupIndex < contactList.getGroupCount(); groupIndex++) {
			Group currentGroup = contactList.getGroup(groupIndex);
			for (int contactIndex = 0; contactIndex < currentGroup.getBuddiesCount(); contactIndex++) {
				Buddy currentContact = currentGroup.getBuddy(contactIndex);
				if (currentContact.equals(contact)) {
					return currentGroup;
				}
			}
		}
		return group;
	}


	public void addContactListener(ContactListListener listener) {
		contactListeners.add(listener);
	}

	public void removeContactListener(ContactListListener listener) {
		contactListeners.remove(listener);
	}

	public Buddy getSelectedBuddy() {
		Buddy buddy = null;
		Object element = contactManager.getSelectedElement();
		if (element instanceof Buddy) {
			buddy = (Buddy) element;
		}
		return buddy;
	}

	public Group getSelectedGroup() {
		return contactManager.getSelectedGroup();

	}

	public void setContactManager(ContactManager manager) {
		this.contactManager = manager;
	}

	public Group getDefaultGroup() {
		return contactList.getDefaultGroup();
	}

	public List<Group> getGroups() {
		int nbGroup = contactList.getGroupCount();
		List<Group> groupList = new ArrayList<Group>();
		for (int i = 0; i < nbGroup; i++) {
			groupList.add(contactList.getGroup(i));
		}
		return groupList;
	}

	public Group getGroup(String name) {
		return contactList.getGroupNamed(name);
	}	

	public Buddy getLocalUser() {
		return localUser;
	}

	public void setLocalUser(Buddy aUser) {
		localUser = aUser;
	}

	public void addSelectionListener(ContactSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void fireSelectionEvent(Object source) {
		for (int i = 0; i < selectionListeners.size(); i++) {
			ContactSelectionListener listener = selectionListeners.get(i);
			listener.selectionChanged(source);
		}
	}

	public ContactList getContactList() {
		return contactList;
	}

	public void updateGroup(Group group) {
		try {
			icpGroupListController.modifyGroup(group);
			fireContactListEvent(new ContactListEvent(ContactListEventType.GroupModified, group));
		} catch (Exception e) {
			MessagingClient.showError("update.group.error", e);
		}
	}

	public void updateBuddy(Buddy buddy) {
		try {
			Group group = getGroupForBuddy(buddy);
			icpGroupListController.modifyBuddy(group, buddy);
			fireContactListEvent(new ContactListEvent(
					ContactListEventType.BuddyModified, group, buddy));
		} catch (Exception e) {
			MessagingClient.showError("update.buddy.error", e);
		}
	}

	public List<String> getGroupDisplayNames() {
		List<String> groupNames = new ArrayList<String>();

		int nbGroups = contactList.getGroupCount();
		for (int i = 0; i < nbGroups; i++) {
			Group group = contactList.getGroup(i);
			groupNames.add(group.getDisplayName());
		}
		return groupNames;
	}

	public void setIcpGroupListController(
			ICPGroupListController icpGroupListController) {
		this.icpGroupListController = icpGroupListController;
	}

}
