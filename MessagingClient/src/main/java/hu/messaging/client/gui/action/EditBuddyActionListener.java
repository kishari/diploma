package hu.messaging.client.gui.action;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.dialog.SimpleTextDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;

/**
 * Action called when a buddy is edited
 */
public class EditBuddyActionListener extends BuddyActionListener
{

	public EditBuddyActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e)
	{
		Buddy buddy = controller.getSelectedBuddy();
		if(buddy != null)
		{
            // Open the edition dialog
            SimpleTextDialog dialog = new SimpleTextDialog(SwingUtil.getFrame(parent), "dialog.buddy.name.label");
            dialog.setTitle(Resources.resources.get("dialog.buddy.edit.title"));
			dialog.setTextValue(buddy.getDisplayName());
            dialog.pack();
	        dialog.setVisible(true);
	        // Update the name
            String buddyName = (String) dialog.getData();
	        if((buddyName != null) && (!buddyName.equals("")))
	        {
	            buddy.setDisplayName(buddyName);
                controller.updateBuddy(buddy);
	        }
		}
	}

}
