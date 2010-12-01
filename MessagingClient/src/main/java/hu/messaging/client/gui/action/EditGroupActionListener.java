package hu.messaging.client.gui.action;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.SimpleTextDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;

/**
 * Action called when a group is edited
 */
public class EditGroupActionListener extends BuddyActionListener
{

	public EditGroupActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e)
	{
		Group group = controller.getSelectedGroup();
		if(group != null)
		{
		    // Open the edition dialog
            SimpleTextDialog dialog = new SimpleTextDialog(SwingUtil.getFrame(parent), "dialog.group.name.label");
            dialog.setTitle(Resources.resources.get("dialog.group.edit.title"));
			dialog.setTextValue(group.getDisplayName());
            dialog.pack();
			dialog.setVisible(true);
            // update the name
	        String groupName = (String) dialog.getData();
	        if((groupName != null) && (!groupName.equals("")))
	        {
	            group.setDisplayName(groupName);
                controller.updateGroup(group);
	        }
		}
	}

}
