package hu.messaging.client.gui;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.bean.BuddyListPanel;
import hu.messaging.client.gui.bean.MenuBar;
import hu.messaging.client.gui.controller.ICPController;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;


public class MessagingClient extends JFrame {

    private static final long serialVersionUID = 3029290647174678346L;

    private ICPController icpController;

    private BuddyListPanel buddyListPanel;
    
    public MessagingClient() {
        try {
        	setTitle(Resources.resources.get("application.title"));
            getContentPane().setLayout(new GridBagLayout());
            icpController = new ICPController();
            MenuBar menuBar = new MenuBar(this, icpController);
            setJMenuBar(menuBar.getMenuBar());
            setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
    
            buddyListPanel = new BuddyListPanel(icpController);
            buddyListPanel.setData(icpController.getContactListController().getContactList());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
    
            getContentPane().add(buddyListPanel, c);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            pack();
        }
        catch (Exception e)
        {
            //ICPController.error("Error starting the client", e);
            showError(this, "application.start.error", e);
            System.exit(0);
        }
    }

    /**
     * Release object needed to be release on th icpController before exiting
     * the application.
     * 
     * @see javax.swing.JFrame#processWindowEvent(java.awt.event.WindowEvent)
     */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            close();
        }
        super.processWindowEvent(e);
    }
    /**
     * Close the clientinstance
     */
    public void close()
    {
        try
        {
            icpController.release();
        }
        catch (Throwable t)
        {
            System.out.println("Error releasing the controller: " + t);
            t.printStackTrace();
        }

    }
    public Object getData()
    {
        return buddyListPanel.getData();
    }
    /**
     * Display an error message
     * @param title The error title
     * @param exception The exception that occured
     */
    public static void showError(String titleKey, Throwable exception)
    {
        showError(null, titleKey, exception);
    }
    /**
     * Display an error message
     * @param parent The parent component
     * @param title The error title
     * @param exception The exception that occured
     */
    public static void showError(Container parent, String titleKey, Throwable exception)
    {
        JOptionPane.showMessageDialog(parent, exception, Resources.resources.get(titleKey), JOptionPane.ERROR_MESSAGE);
    }
 
}
