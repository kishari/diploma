package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.dialog.AboutDialog;
import hu.messaging.client.gui.dialog.BaseDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;


public class AboutActionListener extends BuddyActionListener
{

	public AboutActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

    public void actionPerformed(ActionEvent e)
    {
        BaseDialog dialog = new AboutDialog(SwingUtil.getFrame(parent));
        dialog.setVisible(true);
        
    }

}
