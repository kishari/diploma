package hu.messaging.client.gui.action;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.AddBuddyDialog;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;

public class AddBuddyActionListener extends BuddyActionListener {
	public AddBuddyActionListener(ICPController aIcpController, Container parent) {
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e) {
		List<String> groupsNames = controller.getGroupDisplayNames();

		AddBuddyDialog dialog = new AddBuddyDialog(SwingUtil.getFrame(parent),
				groupsNames.toArray(new String[groupsNames.size()]));

		Group group = controller.getSelectedGroup();
		if (group == null) {
			group = controller.getDefaultGroup();
		}
		dialog.setSelectedGroup(group.getName());

		dialog.setVisible(true);
		Buddy newUser = (Buddy) dialog.getData();
		if ((newUser != null) && (!newUser.getContact().equals(""))) {
			controller.addBuddy(controller.getGroup(dialog.getGroup()), newUser);
		}
	}
}
