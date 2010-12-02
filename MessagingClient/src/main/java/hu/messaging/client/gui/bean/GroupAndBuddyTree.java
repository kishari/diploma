package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.util.ImageUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * Buddy list containing groups and contacts.
 */
public class GroupAndBuddyTree extends JTree {
	
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3391246108808709853L;
    
    /**
     * The IMS icon 
     */
    private Image imsIcon;
    
    /**
     * The Logo width
     */
    private int logoWidth;
    
    /**
     * The Logo height
     */
    private int logoHeigth;

    public GroupAndBuddyTree(DefaultTreeModel model, final ICPController icpController)
    {
        super(model);
        setOpaque(false);
/*
        imsIcon = ImageUtil.createImage("ericsson.gif");
        // Wait for the image to be loaded
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(imsIcon, 0);
        try
        {
            mt.waitForID(0);
        }
        catch (InterruptedException e1) { }
        
        // Get the image size
        logoWidth = imsIcon.getWidth(null);
        logoHeigth = imsIcon.getHeight(null);
*/        // Only single selection is allowed.
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new CustomTreeCellRenderer());
        setRootVisible(false);

        // add listener so that clicking anywhere will select the tree component
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                TreePath path = getPathForLocation(e.getX(), e.getY());
                setSelectionPath(path);
            }
        });

        // Associate the popup menu with the tree.
        addMouseListener(new BuddyListPopupListener(this, icpController));

        addTreeSelectionListener(new TreeSelectionListener()
        {

            public void valueChanged(TreeSelectionEvent e)
            {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object selection = null;
                if(node != null)
                {
                    selection = node.getUserObject();
                }
                icpController.getContactListController().fireSelectionEvent(selection);
            }

        });
    }

    /**
     * Override paint to draw a gradient.
     * 
     * @inheritDoc
     */
    public void paintComponent(Graphics g1)
    {
        Graphics2D g = (Graphics2D) g1;

        Dimension dim = getSize();
        Color colorStart = Color.WHITE;
        Color colorEnd = new Color(140, 177, 255);

        g.setPaint(new GradientPaint((int) dim.getWidth() / 2, 0, colorStart, (int) dim.getWidth() / 2, (int) dim.getHeight(), colorEnd));
        g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());

        super.paintComponent(g);
        
        int xIndex = (int)(dim.getWidth() - logoWidth - 5);
        int yIndex = (int)(dim.getHeight() - logoHeigth - 5);
        g.drawImage(imsIcon, xIndex, yIndex, null);
        
    }

    /**
     * Custom cell renderer. Renders the label transparently.
     */
    private class CustomTreeCellRenderer extends JLabel implements TreeCellRenderer
    {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1352306366458659669L;

        private ImageIcon folderOpened;

        private ImageIcon folderClosed;
        
        private boolean isSelected = false;

        CustomTreeCellRenderer()
        {
        	folderClosed = ImageUtil.createImageIcon("closed.png");
            folderOpened = ImageUtil.createImageIcon("opened.png");
            setOpaque(false);
        }

        /**
         * Get rendered component
         * 
         * @inheritDoc
         */
        public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus)
        {
            this.isSelected = selected;
            String text = "";
            if(value instanceof DefaultMutableTreeNode)
            {
                String toolTip = "";
                Color colorForeground = selected ? Color.BLUE : Color.BLACK;
                Color colorBackground = selected ? UIManager.getColor("Tree.selectionBackground") : null;
                setForeground(colorForeground);
                setBackground(colorBackground);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object data = node.getUserObject();
                if(data instanceof Group)
                {
                    // groups are always folders... change icon
                    Group group = (Group) data;
                    text = group.getDisplayName();
                    ImageIcon icon = expanded ? folderOpened : folderClosed;
                    setIcon(icon);
                }
                else if(data instanceof Buddy)
                {
                	//System.out.println("GroupAndBuddyTree.getTreeCellRendererComponent data is Buddy");
                    Buddy contact = (Buddy) data;
                    text = contact.getDisplayName();
                    setIcon(contact.getUserImage());
                    toolTip  = contact.getContact();
                }
                setText(text);
                setToolTipText(toolTip);
                GroupAndBuddyTree.this.setToolTipText(toolTip);
            }
            return this;
        }

        public void paintComponent(Graphics g)
        {
            Color background = getBackground();
            if(isSelected)
            {
                g.setColor(background);
                Dimension dim = getPreferredSize();
                g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
            }
            super.paintComponent(g);
        }
    }
}
