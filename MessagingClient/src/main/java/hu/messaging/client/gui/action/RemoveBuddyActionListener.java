package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;

import java.awt.Container;
import java.awt.event.ActionEvent;

public class RemoveBuddyActionListener extends BuddyActionListener {
	public RemoveBuddyActionListener(ICPController aIcpController,
			Container parent) {
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e) {
		Buddy contact = controller.getSelectedBuddy();
		if (contact != null) {
			controller.removeBuddy(contact);
		}
	}
}
