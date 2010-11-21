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
 * A csoportfa kezel�s�t v�gz� oszt�ly.
 * @author Harangoz� Csaba
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
     * T�r�l minden csom�pontot, kiv�ve a gy�keret.
     */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Elt�vol�tja az aktu�lisan kiv�lasztott csom�pontot a f�b�l.
     * A gy�k�r nem t�vol�that� el.
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
     * Visszaadja, hogy az aktu�lisan kiv�lasztott csom�pont milyen m�lyen van a f�ban.
     * @return �t m�lys�ge
     */
    public int getPathDepth() {
    	if (tree.getSelectionCount() > 0) {
    		TreePath parentPath = tree.getSelectionPath();
    		return parentPath.getPathCount();
    	}    	    	
      	return 0; 
    }
    
    /**
     * Visszaadja az aktu�lisan kiv�lasztott csom�pont el�r�si �tvonal�t.
     * @return el�r�si �tvonal
     */
    public TreePath getPath() {    	
    	return tree.getSelectionPath();
    }
    
    /**
     * Hozz�ad egy �j elemet az aktu�lisan kiv�lasztott csom�ponthoz.
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
     * Hozz�ad egy �j elemet az parent v�ltoz�ban megadott csom�ponthoz.
     * @param parent
     * @param child
     * @return DefaultMutableTreeNode child
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    /**
     * Hozz�ad egy �j elemet az parent v�ltoz�ban megadott csom�ponthoz, a megadott l�that�s�gi felt�tellel.
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
     * A csoportf�ban bek�vetkezett v�ltoz�sok jelz�s�re l�tehozott oszt�ly.
     * @author Harangoz� Csaba
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

