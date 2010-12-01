package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.awt.event.ActionEvent;


public class SortAscendingActionListener extends BuddyActionListener
{
	public SortAscendingActionListener(ICPController aIcpController, Container parent) 
	{
		super(aIcpController, parent);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		controller.sortAscending();
	}

}
