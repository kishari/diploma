package hu.messaging.client.gui.bean;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.action.AddBuddyActionListener;
import hu.messaging.client.gui.action.AddGroupActionListener;
import hu.messaging.client.gui.action.EditBuddyActionListener;
import hu.messaging.client.gui.action.EditGroupActionListener;
import hu.messaging.client.gui.action.MoveBuddyActionListener;
import hu.messaging.client.gui.action.RemoveBuddyActionListener;
import hu.messaging.client.gui.action.RemoveGroupActionListener;
import hu.messaging.client.gui.action.SendMessageActionListener;
import hu.messaging.client.gui.action.SortAscendingActionListener;
import hu.messaging.client.gui.action.SortDescendingActionListener;
import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuItem;


public class EditItems
{
    /**
     * Mapping between menu name and the menu item itself.
     */
    private Map<String, JMenuItem> listItems = Collections.synchronizedMap(new LinkedHashMap<String, JMenuItem>());

    /**
     * Creates edit menu items.
     * @param contactListController
     * @param parent
     */
    public EditItems(ICPController icpController, Container parent)
    {
        // Add contact item
    	JMenuItem addContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.add"));
        addContactItem.setName("menu.edit.buddy.add");
        addContactItem.addActionListener(new AddBuddyActionListener(icpController,parent));
        listItems.put("menu.edit.buddy.add", addContactItem);

        // Edit contact item
        JMenuItem editContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.edit"));
        editContactItem.setName("menu.edit.buddy.edit");
        editContactItem.addActionListener(new EditBuddyActionListener(icpController,parent));
        listItems.put("menu.edit.buddy.edit", editContactItem);

        // Remove contact item
        JMenuItem removeContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.remove"));
        removeContactItem.setName("menu.edit.buddy.remove");
        removeContactItem.addActionListener(new RemoveBuddyActionListener(icpController,parent));
        listItems.put("menu.edit.buddy.remove", removeContactItem);

        // Move contact to an other group
        JMenuItem moveContactItem = new JMenuItem(Resources.resources.get("menu.edit.buddy.move"));
        moveContactItem.setName("menu.edit.buddy.move");
        moveContactItem.addActionListener(new MoveBuddyActionListener(icpController,parent));
        listItems.put("menu.edit.buddy.move", moveContactItem);

        // Open communication dialog to send message to this contact
        JMenuItem sendMessage = new JMenuItem(Resources.resources.get("menu.edit.sendmessage"));
        sendMessage.setName("menu.edit.sendmessage");
        sendMessage.addActionListener(new SendMessageActionListener(icpController, parent));
        listItems.put("menu.edit.sendmessage", sendMessage);

        // Add a separator between buddy and group menu elemnent
        listItems.put("buddy.separator", null);
        // Add group item
        JMenuItem addGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.add"));
        addGroupItem.setName("menu.edit.group.add");
        addGroupItem.addActionListener(new AddGroupActionListener(icpController,parent));
        listItems.put("menu.edit.group.add", addGroupItem);

        // Edit group item
        JMenuItem editGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.edit"));
        editGroupItem.setName("menu.edit.group.edit");
        editGroupItem.addActionListener(new EditGroupActionListener(icpController,parent));
        listItems.put("menu.edit.group.edit", editGroupItem);

        // Remove group item
        JMenuItem removeGroupItem = new JMenuItem(Resources.resources.get("menu.edit.group.remove"));
        removeGroupItem.setName("menu.edit.group.remove");
        removeGroupItem.addActionListener(new RemoveGroupActionListener(icpController,parent));
        listItems.put("menu.edit.group.remove", removeGroupItem);

        // Add a separator between group and other menu elemnent
        listItems.put("group.separator", null);

        // Sort item
        JMenuItem sortAscendingItem = new JMenuItem(Resources.resources.get("menu.edit.sort.ascending"));
        sortAscendingItem.setName("menu.edit.sort.ascending");
        sortAscendingItem.addActionListener(new SortAscendingActionListener(icpController,parent));
        listItems.put("menu.edit.sort.ascending", sortAscendingItem);

        JMenuItem sortDescendingItem = new JMenuItem(Resources.resources.get("menu.edit.sort.descending"));
        sortDescendingItem.setName("menu.edit.sort.descending");
        sortDescendingItem.addActionListener(new SortDescendingActionListener(icpController, parent));
        listItems.put("menu.edit.sort.descending", sortDescendingItem);
    }

    /**
     * @return all the menu item in list. Separator have null value.
     */
    public JMenuItem[] getAll()
    {
        return listItems.values().toArray(new JMenuItem[listItems.size()]);
    }

    public JMenuItem get(String itemName)
    {
        return listItems.get(itemName);
    }

    /**
     * Enabled/disables the named menu item. Does nothing if the item does not exist.
     * @param name
     * @param enabled
     */
    public void setEnable(String name, boolean enabled)
    {
        JMenuItem item = listItems.get(name);
        if(item != null)
        {
            item.setEnabled(enabled);
        }
    }
}
