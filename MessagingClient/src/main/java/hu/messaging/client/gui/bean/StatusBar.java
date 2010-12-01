package hu.messaging.client.gui.bean;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.util.ImageUtil;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class StatusBar extends JPanel
{

    private static final long serialVersionUID = 6354673162300557410L;

    /**
     * Controller
     */
    private ContactListController controller;

    /**
     * Icon status
     */
    private JLabel statusLabel;

    public StatusBar(ContactListController aController)
    {
        super();
        System.out.println("statusbar konstruktor");
        this.controller = aController;
        statusLabel = new JLabel();
        Buddy user = controller.getUser();
        setLayout(new BorderLayout());
        
        if(user != null)
        {
            statusLabel.setIcon(user.getUserImage());
            statusLabel.setToolTipText(user.getDisplayName());
        }
        else
        {
            statusLabel.setText(Resources.resources.get("status.nouser"));
            statusLabel.setIcon(ImageUtil.createImageIcon("offline.gif"));
        }
        add(statusLabel, BorderLayout.WEST);

    }
}
