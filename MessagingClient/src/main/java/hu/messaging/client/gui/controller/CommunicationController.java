package hu.messaging.client.gui.controller;

import hu.messaging.Constants;
import hu.messaging.client.gui.dialog.SendMessageDialog;

import java.util.Timer;
import java.util.TimerTask;

import com.ericsson.icp.util.ISessionDescription;
import com.ericsson.icp.util.SdpFactory;

/**
 * Handle the communications in the client. This class interacs with the ICP controler to create the appropriate data object, and delegate the
 * functionality to a GUI class to interact with the user
 */
public class CommunicationController
{

	private Timer timer = null;
	private ICPController icpController;
	
	public CommunicationController(ICPController icpController) { 
		this.icpController = icpController;
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new UpdateStatusTask(), 
									   3000, 
									   Constants.onlineUserTimeOut - 10000);
	}
	
    /**
     * Send a text instant message to a remote party
     * 
     * @param to The sip address were send the message (ex: sip:user@ericsson.com)
     * @param message The text message to send.
     */
	public void sendSIPMessage(String to, String message) {
        try
        {
           // Send the message
            byte[] messageBytes = message.getBytes("UTF-8");
            icpController.getService().sendMessage(icpController.getProfile().getIdentity(), to, "text/plain", messageBytes, messageBytes.length);
        }
        catch (Exception e)
        {            
        }
	}
	
    /**
     * 
     * @param remote The callee
     * @param message The minstant message
     */
    public void incomingSIPMessage(String to, String message)
    {
       System.out.println("CommunicationController.incomingInstantMessage(): to: " + to + ". \nMessage: " + message);
    }
    
    public void sendInvite(ISessionDescription localSdp) throws Exception {        
    	icpController.getSession().start(Constants.serverSipURI, localSdp, 
    									 icpController.getProfile().getIdentity(), 
    									 SdpFactory.createIMSContentContainer());
	}
     
	public void sendBye() {
		try {
			icpController.getSession().end();
		}
		catch(Exception e) { }		
	}
	
    private void update() {
		this.sendSIPMessage(Constants.serverSipURI, Constants.updateStatusMessage);
	}
    
    private class UpdateStatusTask extends TimerTask {
		public void run() {
			update();
		}		
	}

}
