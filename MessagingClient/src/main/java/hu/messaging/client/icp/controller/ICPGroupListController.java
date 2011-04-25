package hu.messaging.client.icp.controller;

import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.ContactList;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.util.IcpUtils;
import hu.messaging.client.gui.util.StringUtil;
import hu.messaging.client.icp.listener.GroupListManagerListener;

import com.ericsson.icp.services.PGM.IRLSGroup;
import com.ericsson.icp.services.PGM.IRLSMListener;
import com.ericsson.icp.services.PGM.IRLSManager;
import com.ericsson.icp.services.PGM.PGMFactory;
import com.ericsson.icp.util.IBuddy;
import com.ericsson.icp.util.IIterator;
import hu.messaging.client.gui.controller.ICPController;

public class ICPGroupListController {

	private IRLSManager groupListManager;
	private ContactListController contactListController;

	private GroupListManagerListener glmListener;

	public ICPGroupListController(ICPController icpController,
			ContactListController contactListController) throws Exception {
		contactListController.setIcpGroupListController(this);
		groupListManager = PGMFactory.createRLSManager(icpController.getProfile());

		glmListener = new GroupListManagerListener(icpController);

		groupListManager.addListener((IRLSMListener) glmListener);
		this.contactListController = contactListController;

		loadGroups();

		createDefaultGroup();
	}

	public void addBuddy(Group group, Buddy contact) throws Exception {
		IRLSGroup icpGroup = findGroup(group.getName());
		IBuddy newBuddy = PGMFactory.createBuddy(contact.getContact());
		newBuddy.setDisplayName(contact.getDisplayName());
		icpGroup.addMember(newBuddy);
		glmListener.clear();
		groupListManager.modifyGroup(icpGroup);
		glmListener.waitForCallback();
	}

	public void removeBuddy(Group group, Buddy contact) throws Exception {
		IRLSGroup icpGroup = findGroup(group.getName());
		if (icpGroup != null) {
			IBuddy buddyToRemove = findBuddy(icpGroup, contact.getContact());
			if (buddyToRemove != null) {
				icpGroup.removeMember(buddyToRemove);
				groupListManager.modifyGroup(icpGroup);
			}
		}
	}

	public void addGroup(Group group) throws Exception {
		IRLSGroup icpGroup = PGMFactory.createGroup(group.getName());
		icpGroup.setDisplayName(group.getName());
		glmListener.clear();
		groupListManager.addGroup(icpGroup);
		glmListener.waitForCallback();
	}

	public void removeGroup(Group group) throws Exception {
		IRLSGroup icpGroup = findGroup(group.getName());
		if (icpGroup != null) {
			groupListManager.removeGroup(icpGroup);
			icpGroup.release();
		}
	}

	public void modifyBuddy(Group group, Buddy contact) throws Exception {
		IRLSGroup icpGroup = findGroup(group.getName());
		if (icpGroup != null) {
			IBuddy buddyToUpdate = findBuddy(icpGroup, contact.getContact());
			if (buddyToUpdate != null) {
				buddyToUpdate.setDisplayName(contact.getDisplayName());
				icpGroup.modifyMember(buddyToUpdate);
				groupListManager.modifyGroup(icpGroup);
			}
		}
	}

	public void modifyGroup(Group group) throws Exception {
		IRLSGroup icpGroup = findGroup(group.getName());
		if (icpGroup != null) {
			icpGroup.setDisplayName(group.getDisplayName());
			groupListManager.modifyGroup(icpGroup);
		}
	}

	private IRLSGroup findGroup(String name) {
		IRLSGroup icpGroup = null;
		try {
			icpGroup = groupListManager.searchGroup(name);
		} catch (Exception e) {

		}
		return icpGroup;
	}

	private IBuddy findBuddy(IRLSGroup group, String contactUri) {
		IBuddy icpBuddy = null;
		try {
			IIterator itr = group.getMembers();
			while (itr.hasNext() && (icpBuddy == null)) {
				itr.next();
				icpBuddy = (IBuddy) itr.getElement();
				if (!icpBuddy.getUri().equals(contactUri)) {
					icpBuddy = null;
				}
			}
		} catch (Exception e) {
			
		}
		return icpBuddy;
	}

	private void loadGroups() throws Exception {
		IIterator groupList = groupListManager.getGroupIterator();
		while (groupList.hasNext()) {
			groupList.next();
			IRLSGroup icpGroup = (IRLSGroup) groupList.getElement();

			if (!StringUtil.isEmpty(icpGroup.getDisplayName())) {
				Group group = new Group(icpGroup.getName());
				group.setDisplayName(icpGroup.getDisplayName());
				contactListController.addGroupToGui(group);

				IIterator buddyList = icpGroup.getMembers();
				while (buddyList.hasNext()) {
					buddyList.next();
					IBuddy icpBuddy = (IBuddy) buddyList.getElement();
					Buddy buddy = new Buddy(icpBuddy.getUri());
					buddy.setDisplayName(icpBuddy.getDisplayName());
					contactListController.addBuddy(group, buddy, false);
				}
			}
		}
	}

	private void createDefaultGroup() {
		if (contactListController.getDefaultGroup() == null) {
			Group group = new Group(ContactList.DEFAULT_GROUP_NAME);
			contactListController.addGroup(group);
		}
	}

	public void release() {
		try {
			IcpUtils.release(groupListManager);
		} catch (Exception e) {

		}
	}

}
