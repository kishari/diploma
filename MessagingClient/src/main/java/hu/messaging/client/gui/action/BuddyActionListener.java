package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.awt.event.ActionListener;


public abstract class BuddyActionListener implements ActionListener 
{
    protected ContactListController controller;
	protected ICPController icpController;
	protected Container parent;
	public BuddyActionListener(ICPController aIcpController, Container parent)
	{
		this.icpController = aIcpController;
        this.controller = aIcpController.getContactListController();
		this.parent = parent;
	}
}
