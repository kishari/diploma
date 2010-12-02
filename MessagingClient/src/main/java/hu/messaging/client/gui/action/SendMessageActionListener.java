package hu.messaging.client.gui.action;

import hu.messaging.Constants;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.util.SwingUtil;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.util.SDPUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.List;

//import com.ericsson.winclient.dialog.SendInstantMessageDialog;

public class SendMessageActionListener extends BuddyActionListener
{

	public SendMessageActionListener(ICPController aIcpController, Container parent)
	{
		super(aIcpController, parent);
	}

	public void actionPerformed(ActionEvent e)
	{
		System.out.println(getClass().getSimpleName() + " actionPerformed...");
		//this.icpController.getCommunicationController().sendSIPMessage("sip:weblogic@ericsson.com", "TESTINVITE");
		/*try {
			this.icpController.getCommunicationController().sendInvite(SDPUtil.createSDP(InetAddress.getLocalHost(), 
																	   9876, 
																	   MSRPUtil.generateRandomString(Constants.sessionIdLength)));
		}
		catch(Exception e1) {}
		*/
		List<String> groupsNames = controller.getGroupDisplayNames();
		icpController.getCommunicationController().openSendMessageDialog(groupsNames.toArray(new String[groupsNames.size()]));
	}

}
