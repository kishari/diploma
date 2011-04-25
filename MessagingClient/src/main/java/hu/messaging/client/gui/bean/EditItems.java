package hu.messaging.client.gui.bean;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.action.*;
import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuItem;

public class EditItems {

	private Map<String, JMenuItem> listItems = Collections.synchronizedMap(new LinkedHashMap<String, JMenuItem>());

	public EditItems(ICPController icpController, Container parent) {
		JMenuItem addContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.add"));
		addContactItem.setName("menu.edit.buddy.add");
		addContactItem.addActionListener(new AddBuddyActionListener(
				icpController, parent));
		listItems.put("menu.edit.buddy.add", addContactItem);

		JMenuItem editContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.edit"));
		editContactItem.setName("menu.edit.buddy.edit");
		editContactItem.addActionListener(new EditBuddyActionListener(
				icpController, parent));
		listItems.put("menu.edit.buddy.edit", editContactItem);

		JMenuItem removeContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.remove"));
		removeContactItem.setName("menu.edit.buddy.remove");
		removeContactItem.addActionListener(new RemoveBuddyActionListener(
				icpController, parent));
		listItems.put("menu.edit.buddy.remove", removeContactItem);

		JMenuItem moveContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.move"));
		moveContactItem.setName("menu.edit.buddy.move");
		moveContactItem.addActionListener(new MoveBuddyActionListener(
				icpController, parent));
		listItems.put("menu.edit.buddy.move", moveContactItem);

		JMenuItem sendMessage = new JMenuItem(Resources.resources.get("menu.edit.sendmessage"));
		sendMessage.setName("menu.edit.sendmessage");
		sendMessage.addActionListener(new SendMessageActionListener(
				icpController, parent));
		listItems.put("menu.edit.sendmessage", sendMessage);

		listItems.put("buddy.separator", null);
		
		JMenuItem addGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.add"));
		addGroupItem.setName("menu.edit.group.add");
		addGroupItem.addActionListener(new AddGroupActionListener(
				icpController, parent));
		listItems.put("menu.edit.group.add", addGroupItem);

		JMenuItem editGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.edit"));
		editGroupItem.setName("menu.edit.group.edit");
		editGroupItem.addActionListener(new EditGroupActionListener(
				icpController, parent));
		listItems.put("menu.edit.group.edit", editGroupItem);

		JMenuItem removeGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.remove"));
		removeGroupItem.setName("menu.edit.group.remove");
		removeGroupItem.addActionListener(new RemoveGroupActionListener(
				icpController, parent));
		listItems.put("menu.edit.group.remove", removeGroupItem);

		listItems.put("group.separator", null);
	}

	public JMenuItem[] getAll() {
		return listItems.values().toArray(new JMenuItem[listItems.size()]);
	}

	public JMenuItem get(String itemName) {
		return listItems.get(itemName);
	}

	public void setEnable(String name, boolean enabled) {
		JMenuItem item = listItems.get(name);
		if (item != null) {
			item.setEnabled(enabled);
		}
	}
}
