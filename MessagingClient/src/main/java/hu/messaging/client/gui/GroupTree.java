package hu.messaging.client.gui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * A csoportfa kezelését végzõ osztály.
 * @author Harangozó Csaba
 *
 */
public class GroupTree extends JPanel {
	
	private static final long serialVersionUID = -2513336628891709285L;
	
	protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;

    public GroupTree() {
        super(new GridLayout(1,0));

        rootNode = new DefaultMutableTreeNode("Csoportok");
        treeModel = new DefaultTreeModel(rootNode);
	    treeModel.addTreeModelListener(new TreeModelAdapter());
	    
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    /** 
     * Töröl minden csomópontot, kivéve a gyökeret.
     */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Eltávolítja az aktuálisan kiválasztott csomópontot a fából.
     * A gyökér nem távolítható el.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }
    }

    /**
     * Visszaadja, hogy az aktuálisan kiválasztott csomópont milyen mélyen van a fában.
     * @return út mélysége
     */
    public int getPathDepth() {
    	if (tree.getSelectionCount() > 0) {
    		TreePath parentPath = tree.getSelectionPath();
    		return parentPath.getPathCount();
    	}    	    	
      	return 0; 
    }
    
    /**
     * Visszaadja az aktuálisan kiválasztott csomópont elérési útvonalát.
     * @return elérési útvonal
     */
    public TreePath getPath() {    	
    	return tree.getSelectionPath();
    }
    
    /**
     * Hozzáad egy új elemet az aktuálisan kiválasztott csomóponthoz.
     * @param child
     * @return DefaultMutableTreeNode child
     */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();
        if (parentPath == null) {
            parentNode = rootNode;
            
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    /**
     * Hozzáad egy új elemet az parent változóban megadott csomóponthoz.
     * @param parent
     * @param child
     * @return DefaultMutableTreeNode child
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    /**
     * Hozzáad egy új elemet az parent változóban megadott csomóponthoz, a megadott láthatósági feltétellel.
     * @param parent
     * @param child
     * @param shouldBeVisible
     * @return child
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    /**
     * A csoportfában bekövetkezett változások jelzésére létehozott osztály.
     * @author Harangozó Csaba
     *
     */
    class TreeModelAdapter implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
        	System.out.println("treeNodesChanged");
        }
        public void treeNodesInserted(TreeModelEvent e) {
        	System.out.println("treeNodesInserted");
        }
        
        public void treeNodesRemoved(TreeModelEvent e) {
        	System.out.println("treeNodesRemoved");
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
        	System.out.println("treeStructureChanged");
        }
        
    }
}

