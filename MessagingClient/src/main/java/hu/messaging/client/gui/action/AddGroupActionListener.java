package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.AddGroupDialog;
import hu.messaging.client.gui.dialog.BaseDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;


public class AddGroupActionListener extends BuddyActionListener 
{
	public AddGroupActionListener(ICPController aIcpController, Container parent) 
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e) 
	{
		BaseDialog dialog = new AddGroupDialog(SwingUtil.getFrame(parent));

        dialog.setVisible(true);
        String groupName = (String) dialog.getData();
        if(groupName != null)
        {
            Group group = new Group(groupName);
            controller.addGroup(group);
        }
	}
}
