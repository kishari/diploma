package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.dialog.SendAudioMessageDialog;

import java.awt.Container;
import java.awt.event.ActionEvent;


public class SendMessageActionListener extends BuddyActionListener
{

	public SendMessageActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e)
	{
		System.out.println(getClass().getSimpleName() + " actionPerformed...");		
			
		SendAudioMessageDialog dialog = new SendAudioMessageDialog(icpController);
    	dialog.setVisible(true);
    	
    	
	}

}
