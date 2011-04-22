package hu.messaging.client.gui.action;

import hu.messaging.client.gui.SendMessageFrame;
import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.awt.event.ActionEvent;

public class SendMessageActionListener extends BuddyActionListener {

	public SendMessageActionListener(ICPController aIcpController,
			Container parent) {
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e) {
		SendMessageFrame dialog = new SendMessageFrame(icpController);
		dialog.setVisible(true);

	}

}
