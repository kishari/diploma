package hu.messaging.client.gui.bean;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.MessagingClient;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.MessageBoxDialog;
import hu.messaging.client.gui.listener.ui.ContactSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar implements ContactSelectionListener {
	private MessagingClient parent;
	private JMenuBar menuBar;

	private ICPController icpController;
	private EditItems editMenuItems;

	public MenuBar(MessagingClient parent, ICPController icpController) {
		this.icpController = icpController;
		this.icpController.getContactListController()
				.addSelectionListener(this);
		this.parent = parent;

		menuBar = new JMenuBar();
		menuBar.setName("MenuBar");
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createMessagesMenu());
	}

	private JMenu createMessagesMenu() {
		final JMenu messagesMenu = createMenu("menu.messages");
		JMenuItem itemBox = new JMenuItem(Resources.resources.get("menu.messages.box"));
		itemBox.setName("menu.messages.box");
		itemBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageBoxDialog dialog = new MessageBoxDialog(icpController);
				dialog.setVisible(true);
			}
		});
		
		messagesMenu.add(itemBox);

		return messagesMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = createMenu("menu.file");

		JMenuItem itemQuit = new JMenuItem(Resources.resources.get("menu.file.exit"));
		itemQuit.setName("menu.file.exit");
		itemQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				parent.close();
				System.exit(0);
			}
		});
		fileMenu.add(itemQuit);

		return fileMenu;
	}

	private JMenu createEditMenu() {
		JMenu editMenu = createMenu("menu.edit");
		editMenuItems = new EditItems(icpController, editMenu);
		JMenuItem[] menuItems = editMenuItems.getAll();
		for (int i = 0; i < menuItems.length; i++) {
			if (menuItems[i] != null) {
				editMenu.add(menuItems[i]);
			} else {
				editMenu.addSeparator();
			}
		}
		return editMenu;
	}

	private JMenu createMenu(String name) {
		JMenu menu = new JMenu(Resources.resources.get(name));
		menu.setName(name);
		return menu;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public void selectionChanged(Object newSelection) {
		boolean groupSelected = false;
		boolean buddySelected = false;
		if (newSelection instanceof Group) {
			groupSelected = true;
		} else if (newSelection instanceof Buddy) {
			buddySelected = true;
		}

		editMenuItems.setEnable("menu.edit.buddy.remove", buddySelected);
		editMenuItems.setEnable("menu.edit.buddy.edit", buddySelected);
		editMenuItems.setEnable("menu.edit.buddy.move", buddySelected);
		
		editMenuItems.setEnable("menu.edit.group.remove", groupSelected);
		editMenuItems.setEnable("menu.edit.group.edit", groupSelected);
		editMenuItems.setEnable("menu.edit.sendmessage", groupSelected);
	}
}
