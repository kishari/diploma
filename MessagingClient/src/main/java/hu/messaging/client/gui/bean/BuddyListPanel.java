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


/**
 * This bean contains the contacts and groups of the user.
 */
public class BuddyListPanel extends BaseBean implements ContactManager
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 7621649136162309060L;
 
    /**
     * Tree model
     */
    private DefaultTreeModel treeModel;

    /**
     * Root node
     */
    private DefaultMutableTreeNode rootNode;

    /**
     * Tree component
     */
    private final JTree tree;

    /**
     * Creates a panel containing the groups/buddies.
     * 
     * @param controller The ICP controller to interact with
     */
    public BuddyListPanel(ICPController icpController)
    {
        super(icpController.getContactListController());
        System.out.println(getClass().getSimpleName() + "  BuddyListPanel(ICPController icpController)");
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

        // Add the status bar
        StatusBar statusBar = new StatusBar(contactListController);
        add(statusBar, BorderLayout.SOUTH);
        contactListController.addContactListener(new MyContactListener());
    }
    /**
     * @inheritDoc
     */
    public void setData(Object data)
    {
        super.setData(data);
        fillTree(tree, treeModel, rootNode, (ContactList) getData());
        tree.setSelectionRow(0);
    }
    /**
     * Get the tree node associated with a group
     * @param group The group for which to find the tree node
     * @return The node if any
     */
    private MutableTreeNode getNodeForGroup(Group group)
    {
        return getNodeForData(rootNode, group);
    }
    /**
     * Get the tree node associated with a buddy
     * @param buddy The buddy for which to find the tree node
     * @return The node if any
     */
    private MutableTreeNode getNodeForBuddy(Buddy buddy)
    {
        return getNodeForData(rootNode, buddy);
    }

    /**
     * Get the tree node associated with a group or buddy
     * @param parent The parent node
     * @param searchedItem The group or buddy for which to find the tree node
     * @return The node if any
     */
    private MutableTreeNode getNodeForData(DefaultMutableTreeNode parent, Object searchedItem)
    {
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
            Object data = node.getUserObject();

            if(searchedItem.equals(data))
            {
                return node;
            }

            MutableTreeNode groupNode = getNodeForData(node, searchedItem);
            if(groupNode != null)
            {
                return groupNode;
            }
        }
        return null;
    }
    /**
     * Fill the tree with data
     * @param tree The tree to fill
     * @param treeModel The tree model holdeuing the groups and buddies
     * @param root The root element
     * @param list The contact list to populate the tree
     */
    public void fillTree(final JTree tree, final DefaultTreeModel treeModel, final DefaultMutableTreeNode root, final ContactList list)
    {
        // Process all groups
        for(int groupIndex=0; groupIndex<list.getGroupCount(); groupIndex++)
        {
            Group group = list.getGroup(groupIndex);
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.getDisplayName());
            groupNode.setUserObject(group);
            
            treeModel.insertNodeInto(groupNode, root, groupIndex);
            
            // PRocess all buddies
            for(int contactIndex=0; contactIndex<group.getBuddiesCount(); contactIndex++)
            {
                Buddy contact = group.getBuddy(contactIndex);
                DefaultMutableTreeNode contactNode = new DefaultMutableTreeNode(contact.getDisplayName());
                contactNode.setUserObject(contact);
                treeModel.insertNodeInto(contactNode, groupNode, contactIndex);
                
                // makes the node visible
                tree.makeVisible(new TreePath(contactNode.getPath()));
            }
        }
        //Get the first group of the tree and call make visible on the tree with it to make visible the tree visible
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeModel.getChild(root, 0);
        tree.makeVisible(new TreePath(node.getPath()));    
    }

    private class MyContactListener implements ContactListListener
    {

        public void contactListChanged(ContactListEvent event)
        {
            Group group = event.group;
            Buddy buddy = event.buddy;
            switch (event.event)
            {
                case BuddyAdded:
                {
                    buddyAdded(group, buddy);
                    break;
                }
                case BuddyModified:
                {
                    treeModel.nodeChanged(getNodeForBuddy(buddy));
                    break;
                }
                case BuddyRemoved:
                {
                    buddyRemoved(group, buddy);
                    break;
                }
                case GroupAdded:
                {
                    groupAdded(group);
                    break;
                }
                case GroupModified:
                {
                    treeModel.nodeChanged(getNodeForGroup(group));
                    break;
                }
                case GroupRemoved:
                {
                    groupRemoved(group);
                    break;
                }
                case ListSorted:
                {
                    listSorted();
                    break;
                }
            }
            tree.repaint();
        }

        /**
         * @inheritDoc
         */
        private void buddyAdded(Group group, Buddy contact)
        {
            MutableTreeNode node = getNodeForGroup(group);
            MutableTreeNode contactNode = new DefaultMutableTreeNode(contact.getDisplayName());
            contactNode.setUserObject(contact);
            // add element to last position and select it
            addNode(node, contactNode);
        }

        /**
         * @inheritDoc
         */
        private void buddyRemoved(Group group, Buddy contact)
        {
            removeNodeWithObject(contact);
        }

        /**
         * @inheritDoc
         */
        private void groupAdded(Group group)
        {
            MutableTreeNode groupNode = new DefaultMutableTreeNode();
            groupNode.setUserObject(group);

            addNode(rootNode, groupNode);

        }
        /**
         * Add a node in the model
         * @param parent The parent node
         * @param child The new node
         */
        private void addNode(MutableTreeNode parent, MutableTreeNode child)
        {
            treeModel.insertNodeInto(child, parent, 0);
            TreePath path = new TreePath(treeModel.getPathToRoot(child));
            tree.addSelectionPath(path);
        }
        /**
         * @inheritDoc
         */
        private void groupRemoved(Group group)
        {
            removeNodeWithObject(group);
        }
        /**
         * Remove a node
         * @param data The group or buddy to remove
         */
        private void removeNodeWithObject(Object data)
        {
            MutableTreeNode node = getNodeForData(rootNode, data);
            if(node != null)
            {
                treeModel.removeNodeFromParent(node);
            }
        }
        /**
         * The list was sorted
         *
         */
        private void listSorted()
        {
            rootNode = new DefaultMutableTreeNode("Root Node");
            treeModel.setRoot(rootNode);
            fillTree(tree, treeModel, rootNode, (ContactList) getData());
        }
       
    }

    /**
     * @return the selected group in the tree. If nothing is selected, return
     *         null. If a buddy is selected, return his parent group.
     */
    public Group getSelectedGroup()
    {
        Group group = null;

        TreePath path = tree.getSelectionPath();
        Object elem = null;
        if(path != null)
        {
            elem = getDataForPath(path);
            if(elem instanceof Group)
            {
                group = (Group) elem;
            }
            else if(elem instanceof Buddy)
            {
                group = (Group) getDataForPath(path.getParentPath());
            }
            else
            {
                group = contactListController.getDefaultGroup();
            }
        }
        return group;
    }
    /**
     * Get the data object for a tree path
     * @param path The path
     * @return The data object
     */
    private Object getDataForPath(TreePath path)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object elem = node.getUserObject();
        return elem;
    }

    /**
     * Returns the currently selected contact. When nothing is selected, the
     * default group is considered as selected
     */
    public Object getSelectedElement()
    {
        Object data = null;

        TreePath path = tree.getSelectionPath();
        if(path == null)
        {
            // select default node by default
            data = contactListController.getDefaultGroup();
        }
        else
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            data = node.getUserObject();
        }
        return data;
    }
}
