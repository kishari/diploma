package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ContactManager;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.ContactList;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.listener.ui.ContactListEvent;
import hu.messaging.client.gui.listener.ui.ContactListListener;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class BuddyListPanel extends BaseBean implements ContactManager {
	
	private static final long serialVersionUID = 7621649136162309060L;

	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;

	private final JTree tree;

	public BuddyListPanel(ICPController icpController) {
		super(icpController.getContactListController());
		this.setName("BuddyListPanel");
		this.setLayout(new BorderLayout());

		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		rootNode = new DefaultMutableTreeNode("Root Node");
		treeModel = new DefaultTreeModel(rootNode);
		tree = new GroupAndBuddyTree(treeModel, icpController);

		JScrollPane treeView = new JScrollPane(tree);
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;
		constraint.fill = GridBagConstraints.BOTH;
		panel.add(treeView, constraint);

		tree.setName("ContactList");
		add(panel, BorderLayout.CENTER);

		contactListController.setContactManager(this);

		contactListController.addContactListener(new MyContactListener());
	}

	public void setData(Object data) {
		super.setData(data);
		fillTree(tree, treeModel, rootNode, (ContactList) getData());
		tree.setSelectionRow(0);
	}

	private MutableTreeNode getNodeForGroup(Group group) {
		return getNodeForData(rootNode, group);
	}

	private MutableTreeNode getNodeForBuddy(Buddy buddy) {
		return getNodeForData(rootNode, buddy);
	}

	private MutableTreeNode getNodeForData(DefaultMutableTreeNode parent,
			Object searchedItem) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
					.getChildAt(i);
			Object data = node.getUserObject();

			if (searchedItem.equals(data)) {
				return node;
			}

			MutableTreeNode groupNode = getNodeForData(node, searchedItem);
			if (groupNode != null) {
				return groupNode;
			}
		}
		return null;
	}

	public void fillTree(final JTree tree, final DefaultTreeModel treeModel,
			final DefaultMutableTreeNode root, final ContactList list) {
		
		for (int groupIndex = 0; groupIndex < list.getGroupCount(); groupIndex++) {
			Group group = list.getGroup(groupIndex);
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group
					.getDisplayName());
			groupNode.setUserObject(group);

			treeModel.insertNodeInto(groupNode, root, groupIndex);

			for (int contactIndex = 0; contactIndex < group.getBuddiesCount(); contactIndex++) {
				Buddy contact = group.getBuddy(contactIndex);
				DefaultMutableTreeNode contactNode = new DefaultMutableTreeNode(
						contact.getDisplayName());
				contactNode.setUserObject(contact);
				treeModel.insertNodeInto(contactNode, groupNode, contactIndex);

				tree.makeVisible(new TreePath(contactNode.getPath()));
			}
		}

		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel
				.getChild(root, 0);
		tree.makeVisible(new TreePath(node.getPath()));
	}

	private class MyContactListener implements ContactListListener {

		public void contactListChanged(ContactListEvent event) {
			Group group = event.group;
			Buddy buddy = event.buddy;
			switch (event.event) {
			case BuddyAdded: {
				buddyAdded(group, buddy);
				break;
			}
			case BuddyModified: {
				treeModel.nodeChanged(getNodeForBuddy(buddy));
				break;
			}
			case BuddyRemoved: {
				buddyRemoved(group, buddy);
				break;
			}
			case GroupAdded: {
				groupAdded(group);
				break;
			}
			case GroupModified: {
				treeModel.nodeChanged(getNodeForGroup(group));
				break;
			}
			case GroupRemoved: {
				groupRemoved(group);
				break;
			}

			}
			tree.repaint();
		}

		private void buddyAdded(Group group, Buddy contact) {
			MutableTreeNode node = getNodeForGroup(group);
			MutableTreeNode contactNode = new DefaultMutableTreeNode(contact
					.getDisplayName());
			contactNode.setUserObject(contact);
			addNode(node, contactNode);
		}

		private void buddyRemoved(Group group, Buddy contact) {
			removeNodeWithObject(contact);
		}

		private void groupAdded(Group group) {
			MutableTreeNode groupNode = new DefaultMutableTreeNode();
			groupNode.setUserObject(group);

			addNode(rootNode, groupNode);
		}

		private void addNode(MutableTreeNode parent, MutableTreeNode child) {
			treeModel.insertNodeInto(child, parent, 0);
			TreePath path = new TreePath(treeModel.getPathToRoot(child));
			tree.addSelectionPath(path);
		}

		private void groupRemoved(Group group) {
			removeNodeWithObject(group);
		}

		private void removeNodeWithObject(Object data) {
			MutableTreeNode node = getNodeForData(rootNode, data);
			if (node != null) {
				treeModel.removeNodeFromParent(node);
			}
		}
		

	}

	public Group getSelectedGroup() {
		Group group = null;

		TreePath path = tree.getSelectionPath();
		Object elem = null;
		if (path != null) {
			elem = getDataForPath(path);
			if (elem instanceof Group) {
				group = (Group) elem;
			} else if (elem instanceof Buddy) {
				group = (Group) getDataForPath(path.getParentPath());
			} else {
				group = contactListController.getDefaultGroup();
			}
		}
		return group;
	}

	private Object getDataForPath(TreePath path) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		Object elem = node.getUserObject();
		return elem;
	}

	public Object getSelectedElement() {
		Object data = null;

		TreePath path = tree.getSelectionPath();
		if (path == null) {
			data = contactListController.getDefaultGroup();
		} else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			data = node.getUserObject();
		}
		return data;
	}
}
