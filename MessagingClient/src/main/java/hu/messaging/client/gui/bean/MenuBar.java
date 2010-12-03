package hu.messaging.client.gui.bean;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.MessagingClient;
import hu.messaging.client.gui.action.AboutActionListener;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.dialog.MessageListDialog;
import hu.messaging.client.gui.listener.ui.ContactSelectionListener;
import hu.messaging.client.gui.util.SwingUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class MenuBar implements ContactSelectionListener
{
    private MessagingClient parent;
    /**
     * The menu bar itself
     */
	private JMenuBar menuBar;
    /**
     * The ICP controller to interact with 
     */
    private ICPController icpController;
    /**
     * The edit menu item
     */
    private EditItems editMenuItems;
    /**
     * The sesion menu item
     */
    private JMenu sessionMenu;
    /**
     * Create the mneu bar instance
     * @param parent The main client instance
     * @param icpController The ICP controller to interact with
     */
	public MenuBar(MessagingClient parent, ICPController icpController)
	{
        this.icpController = icpController;
        this.icpController.getContactListController().addSelectionListener(this);
        this.parent = parent;
        
    	menuBar = new JMenuBar();
    	menuBar.setName("MenuBar");
    	// Create menu items
    	menuBar.add(createFileMenu());
    	menuBar.add(createEditMenu());
    	menuBar.add(createSessionMenu());
    	menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());
	}
	
	private JMenu createSessionMenu()
	{
		//Create session menu
		sessionMenu = createMenu("menu.session");
		//add Start chat session item
		JMenuItem chatItem = new JMenuItem(Resources.resources.get("menu.session.start.chat"));
		chatItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//Invite the selectioned user.
                //icpController.startChatSession();
				
				//on success, open chat window.
			}});
		sessionMenu.add(chatItem);
		
		//add Start voice session item
        JMenuItem voiceItem = new JMenuItem(Resources.resources.get("menu.session.start.voice"));
        voiceItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				//icpController.startVoipCall();
			}});
        sessionMenu.add(voiceItem);
        
		return sessionMenu;
	}
	
	private JMenu createViewMenu()
	{
		final JMenu viewMenu = createMenu("menu.view");
		JMenuItem itemBlackList = new JMenuItem(Resources.resources.get("menu.view.inbox"));
    	itemBlackList.setName("menu.view.inbox");
    	itemBlackList.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				MessageListDialog dialog = new MessageListDialog(icpController);
				dialog.setVisible(true);
			}});
    	viewMenu.add(itemBlackList);
    	
    	return viewMenu;
	}

	private JMenu createFileMenu()
	{
		JMenu fileMenu = createMenu("menu.file");

		JMenuItem itemQuit = new JMenuItem(Resources.resources.get("menu.file.exit"));
    	itemQuit.setName("menu.file.exit");
    	itemQuit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
            {
				parent.close();
                System.exit(0);
			}});
    	fileMenu.add(itemQuit);
    	
    	return fileMenu;
	}
	
	private JMenu createEditMenu()
	{
		JMenu editMenu = createMenu("menu.edit");
		editMenuItems = new EditItems(icpController, editMenu);
        JMenuItem[] menuItems = editMenuItems.getAll();
		for(int i = 0; i < menuItems.length; i++)
		{			
			if(menuItems[i] != null)
			{
				editMenu.add(menuItems[i]);
			}
			else
			{
				editMenu.addSeparator();
			}
		}
		return editMenu;
	}
    
    private JMenu createHelpMenu()
    {
        JMenu menu = createMenu("menu.help");
        JMenuItem aboutItem = new JMenuItem(Resources.resources.get("menu.help.about"));
        menu.add(aboutItem);
        aboutItem.addActionListener(new AboutActionListener(icpController, menu));
        return menu;
    }
	
	private JMenu createMenu(String name)
	{
		JMenu menu = new JMenu(Resources.resources.get(name));
		menu.setName(name);
		return menu;
	}
	
	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

    /**
     * @inheritDoc
     */
    public void selectionChanged(Object newSelection)
    {   
        // empty selection must be taken into account...
        boolean groupSelected = false;
        boolean buddySelected = false;
        if(newSelection instanceof Group)
        {
            groupSelected = true;
        }
        else if(newSelection instanceof Buddy)
        {
            buddySelected = true;
        }
        
        // the items should be enabled when a buddy is selected
        editMenuItems.setEnable("menu.edit.buddy.remove", buddySelected);
        editMenuItems.setEnable("menu.edit.buddy.edit", buddySelected);
        editMenuItems.setEnable("menu.edit.buddy.move", buddySelected);
        //enable when the selected buddy is on-line
        editMenuItems.setEnable("menu.edit.sendmessage", buddySelected);
        sessionMenu.setEnabled(buddySelected);
        
        // these items should be enabled when a group is selected
        editMenuItems.setEnable("menu.edit.group.remove", groupSelected);
        editMenuItems.setEnable("menu.edit.group.edit", groupSelected);
    }
}
