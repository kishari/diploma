package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.awt.event.ActionEvent;


public class SortDescendingActionListener extends BuddyActionListener
{
	public SortDescendingActionListener(ICPController aIcpController, Container parent) 
	{
		super(aIcpController, parent);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		controller.sortDescending();
	}

}
