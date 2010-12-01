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


public class BuddyListPopupListener extends MouseAdapter
{
	private JPopupMenu popupMenu;
	private EditItems items;
	public BuddyListPopupListener(Container container, ICPController icpController)
	{
		popupMenu = new JPopupMenu();
		popupMenu.setName("buddy.list.popup.menu");
		items = new EditItems(icpController, container);
	}
	
	public void fillMenu(JTree tree)
	{
		popupMenu.removeAll();
		Object data = null;

		TreePath selectPath = tree.getSelectionPath();
		if(selectPath != null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectPath.getLastPathComponent();
			data = node.getUserObject();
		}
        
        //Nothing is selected
        if(data == null)
        {
        	//Add group
        	addItem(items.get("menu.edit.group.add"));
        	popupMenu.addSeparator();
        	//Add buddy
        	addItem(items.get("menu.edit.buddy.add"));
        }
        //A buddy is selected
        else if(data instanceof Buddy)
        {
        	//Edit contact
        	addItem(items.get("menu.edit.buddy.edit"));
        	//removeContact
        	addItem(items.get("menu.edit.buddy.remove"));
        	//Move to other group
        	addItem(items.get("menu.edit.buddy.move"));
        	//Send message to this contact
        	addItem(items.get("menu.edit.sendmessage"));
        }
        //A group is selected
        else
        {
        	//addBuddy
        	addItem(items.get("menu.edit.buddy.add"));
        	popupMenu.addSeparator();
        	//addGroup
        	addItem(items.get("menu.edit.group.add"));
        	//Edit group
        	addItem(items.get("menu.edit.group.edit"));
        	//removeGroup
        	addItem(items.get("menu.edit.group.remove"));
        }     
        popupMenu.addSeparator();
//      sortContentAscending
    	addItem(items.get("menu.edit.sort.ascending"));
//    	sortContentDescending
    	addItem(items.get("menu.edit.sort.descending"));
	}
	
	//Verify an item is not null before adding it to the popup menu.
	private void addItem(JMenuItem item) 
	{
		if(item != null)
		{
			popupMenu.add(item);
		}
	}

	public void addItems(JMenuItem[] menuItems)
	{
		for(int i = 0; i < menuItems.length; i++)
		{
			popupMenu.add(menuItems[i]);
		}
	}

	public void mousePressed(MouseEvent e) 
	{
        showPopup(e);
    }

    public void mouseReleased(MouseEvent e) 
    {
        showPopup(e);
    }

    private void showPopup(MouseEvent e) 
    {
        if (e.isPopupTrigger()) 
        {
        	//fill popup menu
        	fillMenu((JTree)e.getComponent());
        	//show popup menu
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
