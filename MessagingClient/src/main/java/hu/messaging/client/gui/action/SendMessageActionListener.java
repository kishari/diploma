package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.dialog.SendMessageDialog;

import java.awt.Container;
import java.awt.event.ActionEvent;

public class SendMessageActionListener extends BuddyActionListener {

	public SendMessageActionListener(ICPController aIcpController,
			Container parent) {
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e) {
		SendMessageDialog dialog = new SendMessageDialog(icpController);
		dialog.setVisible(true);

	}

}
