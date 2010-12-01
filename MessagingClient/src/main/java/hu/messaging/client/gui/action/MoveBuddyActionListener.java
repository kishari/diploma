package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.SelectGroupDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;



public class MoveBuddyActionListener extends BuddyActionListener
{
	public MoveBuddyActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}
	
	public void actionPerformed(ActionEvent e)
	{
        List<String> groupList = controller.getGroupDisplayNames();
        groupList.remove(controller.getSelectedGroup().getDisplayName());
		SelectGroupDialog dialog = new SelectGroupDialog(SwingUtil.getFrame(parent), groupList);

		Group group = controller.getSelectedGroup();
		Buddy buddy = controller.getSelectedBuddy();
		if((group != null)&&(buddy != null))
		{
			dialog.setSelection(group.getDisplayName());
			
	        dialog.setVisible(true);
	        String groupName = (String) dialog.getData();
	        if((groupName != null) && (!groupName.equals("")))
	        {
	        	controller.removeBuddy(buddy);
	        	Group selectedGroup = controller.getGroup(groupName);
	        	controller.addBuddy(selectedGroup, buddy);
	        }
		}
	}

}
