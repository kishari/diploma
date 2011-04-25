package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.util.ImageUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

public class GroupAndBuddyTree extends JTree {

	private static final long serialVersionUID = 3391246108808709853L;

	public GroupAndBuddyTree(DefaultTreeModel model, final ICPController icpController) {
		super(model);
		setOpaque(false);

		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new CustomTreeCellRenderer());
		setRootVisible(false);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				setSelectionPath(path);
			}
		});

		addMouseListener(new BuddyListPopupListener(this, icpController));

		addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				JTree tree = (JTree) e.getSource();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				Object selection = null;
				if (node != null) {
					selection = node.getUserObject();
				}
				icpController.getContactListController().fireSelectionEvent(
						selection);
			}
		});
	}

	private class CustomTreeCellRenderer extends JLabel implements TreeCellRenderer {

		private static final long serialVersionUID = 1352306366458659669L;

		private ImageIcon folderOpened;
		private ImageIcon folderClosed;

		private boolean isSelected = false;

		CustomTreeCellRenderer() {
			folderClosed = ImageUtil.createImageIcon("closed.png");
			folderOpened = ImageUtil.createImageIcon("opened.png");
			setOpaque(false);
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			this.isSelected = selected;
			String text = "";
			if (value instanceof DefaultMutableTreeNode) {
				String toolTip = "";
				Color colorForeground = selected ? Color.BLUE : Color.BLACK;
				Color colorBackground = selected ? UIManager.getColor("Tree.selectionBackground") : null;
				setForeground(colorForeground);
				setBackground(colorBackground);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				Object data = node.getUserObject();
				if (data instanceof Group) {
					Group group = (Group) data;
					text = group.getDisplayName();
					ImageIcon icon = expanded ? folderOpened : folderClosed;
					setIcon(icon);
				} else if (data instanceof Buddy) {
					Buddy contact = (Buddy) data;
					text = contact.getDisplayName();
					setIcon(contact.getUserImage());
					toolTip = contact.getContact();
				}
				setText(text);
				setToolTipText(toolTip);
				GroupAndBuddyTree.this.setToolTipText(toolTip);
			}
			return this;
		}

		public void paintComponent(Graphics g) {
			Color background = getBackground();
			if (isSelected) {
				g.setColor(background);
				Dimension dim = getPreferredSize();
				g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
			}
			super.paintComponent(g);
		}
	}
}
