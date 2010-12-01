package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;

//import com.ericsson.winclient.dialog.SendInstantMessageDialog;

public class SendMessageActionListener extends BuddyActionListener
{

	public SendMessageActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e)
	{
		System.out.println(getClass().getSimpleName() + "actionPerformed...");
		//SendInstantMessageDialog dialog = new SendInstantMessageDialog(SwingUtil.getFrame(parent), icpController, controller.getSelectedBuddy());
		//dialog.setVisible(true);
	}

}
