package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class BuddyListPopupListener extends MouseAdapter {
	private JPopupMenu popupMenu;
	private EditItems items;

	public BuddyListPopupListener(Container container,
			ICPController icpController) {
		popupMenu = new JPopupMenu();
		popupMenu.setName("buddy.list.popup.menu");
		items = new EditItems(icpController, container);
	}

	public void fillMenu(JTree tree) {
		popupMenu.removeAll();
		Object data = null;

		TreePath selectPath = tree.getSelectionPath();
		if (selectPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectPath
					.getLastPathComponent();
			data = node.getUserObject();
		}

		if (data == null) {
			addItem(items.get("menu.edit.group.add"));
			popupMenu.addSeparator();
			addItem(items.get("menu.edit.buddy.add"));
		}
		else if (data instanceof Buddy) {
			addItem(items.get("menu.edit.buddy.edit"));
			addItem(items.get("menu.edit.buddy.remove"));
			addItem(items.get("menu.edit.buddy.move"));
		}
		else {
			//Csoportra bökött
			addItem(items.get("menu.edit.buddy.add"));
			popupMenu.addSeparator();
			addItem(items.get("menu.edit.group.add"));
			addItem(items.get("menu.edit.group.edit"));
			addItem(items.get("menu.edit.group.remove"));
			addItem(items.get("menu.edit.sendmessage"));

		}
	}

	private void addItem(JMenuItem item) {
		if (item != null) {
			popupMenu.add(item);
		}
	}

	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			fillMenu((JTree) e.getComponent());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
