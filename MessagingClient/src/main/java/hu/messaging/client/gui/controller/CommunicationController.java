package hu.messaging.client.gui.controller;

import hu.messaging.client.gui.util.Resources;

import javax.swing.JDialog;
import javax.swing.JOptionPane;


/**
 * Handle the communications in the client. This class interacs with the ICP controler to create the appropriate data object, and delegate the
 * functionality to a GUI class to interact with the user
 */
public class CommunicationController
{

    public CommunicationController()
    {

    }


    /**
     * Display the instant message windows
     * 
     * @param to The callee
     * @param message The minstant message
     */
    public static void incomingInstantMessage(String to, String message)
    {
       System.out.println("CommunicationController.incomingInstantMessage()");
    }

    /**
     * Prompt the user to accept an incoming communication
     * 
     * @param communicationTypeKey The key containing the text describing the incoming communication type
     * @return <code>true</code> if the user accepts the communication, false otherwise
     */
    private static boolean acceptIncomingCommunication(String communicationTypeKey, String from)
    {
        String communicationType = hu.messaging.client.Resources.resources.get(communicationTypeKey);
        String label = Resources.getInstance().get("dialog.communication.incoming.label", new Object[] { communicationType, from });
        JOptionPane optionPane = new JOptionPane(label, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = optionPane.createDialog(null, hu.messaging.client.Resources.resources.get("dialog.communication.incoming.title"));
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        int returnValue = (Integer) optionPane.getValue();
        return (returnValue == JOptionPane.YES_OPTION);
    }

}
