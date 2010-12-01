package hu.messaging.client.gui.action;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Group;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;


public class RemoveGroupActionListener extends BuddyActionListener
{
	/**
	 * Constructor
	 * @param contactListController
	 * @param parent
	 */
	public RemoveGroupActionListener(ICPController aIcpController, Container parent) 
	{
		super(aIcpController, parent);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Group group = controller.getSelectedGroup();
		if(group!=null)
		{
			if(!controller.removeGroup(group))
			{
				//Default group cannot be deleted.
				JOptionPane.showMessageDialog(parent, Resources.resources.get("message.remove.group.default"));
			}
		}
	}
}
