package hu.messaging.client.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import hu.messaging.client.icp.listener.ConnectionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import hu.messaging.client.Resources;
public class SendMessageDialog extends JFrame implements ConnectionListener, ListSelectionListener {
	
	private static final long serialVersionUID = -211908165523434927L;
	
	private JList availableGroupList;
	private JList selectedGroupList;

	private DefaultListModel availableGroupListModel = new DefaultListModel();
	private DefaultListModel selectedGroupListModel = new DefaultListModel();
	
	private JButton selectAllButton;
	private JButton selectButton;
	private JButton deselectAllButton;
	private JButton deselectButton;
	
	private JFileChooser fileChooser;
	/**
     * Indicates if the dialog is closing
     */
    private boolean closing = false;

    /**
     * The main menu bar
     */
    protected JMenuBar menuBar; 
    
    public SendMessageDialog(String[] aGroupList)//ConnectionWrapper aConnection, String connectedStringKey) throws Exception
    {
        super();
        //setPreferredSize(new Dimension(500, 300));
        setTitle(Resources.resources.get("dialog.communication.sendMessage"));
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
        
        JPanel content = new JPanel(new BorderLayout());
	    add(content);
	    content.add(BorderLayout.NORTH, createGroupSelectionPanel());
	    content.add(BorderLayout.CENTER, createCenterPanel());
	    content.add(BorderLayout.SOUTH, createButtonsPanel());
	    initAvailableGroupList(aGroupList);
	    
	    
		pack();
		
		/*
		// Send panel
        JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		
        JTextField messageToSend = new JTextField();
        messageToSend.setName("communication.message.field");
		messageToSend.setColumns(50);
		sendPanel.add(messageToSend, constraints);
		messageToSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) 
			{
				System.out.println("sendMessage");
			}
		});
		
        JButton sendButton = new JButton(Resources.resources.get("button.send"));
        sendButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent event)
			{
				System.out.println("sendMessage");
			}
		});
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.insets = new Insets(0, 5, 0, 0);
		sendPanel.add(sendButton, buttonConstraints);
		textMessagePanel.add(sendPanel, BorderLayout.SOUTH);
		add(textMessagePanel, BorderLayout.CENTER);
		pack();
        
        // grab focus to be able to type text right away
        messageToSend.grabFocus();

        // Start the communication
        try {
            startCommunication();
        }
        catch(Exception e) {}

*/
    }

    /**
     * Process the connection change events
     */
    public void connectionChanged(ConnectionState event)
    {
        switch (event)
        {
            case Connecting:
            {
                break;
            }
            case Connected:
            { 
                //setTitle(Resources.getInstance().get(connectedStringKey, new Object[] {connection.getRemoteUser().getDisplayName()}));
                //setGuiEnabled(true);
                break;
            }
            case Refused:
            { 
                JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.refused"));
                break;
            }
            case ConnectionFailed:
            {
                JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.failed"));
                break;
            }
            case Disconnected:
            {
                // Do not display the message is the user closed the diaog by itself
                if (!closing)
                {
                    JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.end"));
                    //setGuiEnabled(false);
                    //processCompletedTime();
                }
                break;
            }
        }
    }
    
    /**
     * Close the icp session on closing the dialog.
     * @param e 
     * @see javax.swing.JDialog#processWindowEvent(java.awt.event.WindowEvent)
     */
    @Override
    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) 
        {
            stopCommunication();
        }        
    }

    /**
     * Start the connection 
     */
    protected void startCommunication() throws Exception
    {
    	System.out.println("startCommunication");
        //setGuiEnabled(false);
        //connection.startConnection();
    }
    /**
     * Stop the connecton
     */
    protected void stopCommunication() 
    {
        //cancelTask();
        closing = true;
        //setGuiEnabled(false);
        //connection.stopConnection();
    }

    /**
     * Create a menu item.
     * @param name of the menu item to create.
     * @return The created menu item.
     */
    protected JMenu createMenu(String name)
    {
        JMenu menu = new JMenu(Resources.resources.get(name));
        menu.setName(name);
        return menu;
    }
    
    protected JComponent createGroupSelectionPanel() {
	    JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

	    JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
	    leftPanel.add(BorderLayout.NORTH, new JLabel("Available groups:"));
	    availableGroupList = new JList(availableGroupListModel);
	    availableGroupList.getSelectionModel().addListSelectionListener(this);
	    JScrollPane leftPane = new JScrollPane(availableGroupList);
	    leftPane.setPreferredSize(new Dimension(100, 150));
	    leftPanel.add(leftPane);
	    mainPanel.add(BorderLayout.WEST, leftPanel);

	    JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
	    mainPanel.add(centerPanel);

	    JPanel p1 = new JPanel();
	    centerPanel.add(BorderLayout.SOUTH, p1);
	    JPanel p2 = new JPanel(new BorderLayout());
	    p1.add(p2);
	    JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
	    p2.add(BorderLayout.NORTH, buttonsPanel);

	    selectButton = new JButton(">");
	    selectButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        Object[] selectedItems = availableGroupList.getSelectedValues();
	        for (int i = 0; i < selectedItems.length; i++) {
	          selectedGroupListModel.addElement(selectedItems[i]);
	          availableGroupListModel.removeElement(selectedItems[i]);
	          updateButtonsState();
	        }
	      }
	    });
	    buttonsPanel.add(selectButton);

	    selectAllButton = new JButton(">>");
	    selectAllButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        Object[] items = availableGroupListModel.toArray();
	        for (int i = 0; i < items.length; i++) {
	        	selectedGroupListModel.addElement(items[i]);
	        	availableGroupListModel.removeElement(items[i]);
	        	updateButtonsState();
	        }
	      }
	    });
	    buttonsPanel.add(selectAllButton);

	    deselectButton = new JButton("<");
	    deselectButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        Object[] selectedItems = selectedGroupList.getSelectedValues();
	        for (int i = 0; i < selectedItems.length; i++) {
	        	availableGroupListModel.addElement(selectedItems[i]);
	        	selectedGroupListModel.removeElement(selectedItems[i]);
	          updateButtonsState();
	        }
	      }
	    });
	    buttonsPanel.add(deselectButton);

	    deselectAllButton = new JButton("<<");
	    deselectAllButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        Object[] items = selectedGroupListModel.toArray();
	        for (int i = 0; i < items.length; i++) {
	        	availableGroupListModel.addElement(items[i]);
	        	selectedGroupListModel.removeElement(items[i]);
	          updateButtonsState();
	        }
	      }
	    });
	    buttonsPanel.add(deselectAllButton);

	    JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
	    rightPanel.add(BorderLayout.NORTH, new JLabel("Chosen groups:"));
	    selectedGroupList = new JList(selectedGroupListModel);
	    JScrollPane rightPane = new JScrollPane(selectedGroupList);
	    rightPane.setPreferredSize(new Dimension(100, 150));
	    selectedGroupList.getSelectionModel().addListSelectionListener(this);
	    rightPanel.add(rightPane);
	    mainPanel.add(BorderLayout.EAST, rightPanel);

	    updateButtonsState();
	    return mainPanel;
	  }
    
    protected JComponent createCenterPanel() {
	    JPanel centerPanel = new JPanel(new BorderLayout());
	    centerPanel.add(BorderLayout.NORTH, new JSeparator());
	    JPanel buttonsPanel = new JPanel(new BorderLayout());
	    
	    centerPanel.add(BorderLayout.NORTH, buttonsPanel);

	    fileChooser = new JFileChooser(); 
	    fileChooser.setMultiSelectionEnabled(false);
	    
	    JButton openFileButton = new JButton("Open file");
	    openFileButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  int retVal = fileChooser.showOpenDialog(SendMessageDialog.this);
	    	  if (retVal == JFileChooser.APPROVE_OPTION) {
	    		  File selectedFile = fileChooser.getSelectedFile();
	    		  StringBuffer contents = new StringBuffer();
	    		  BufferedReader reader = null;
	    		  
	    		  try  {
	    			  reader = new BufferedReader(new FileReader(selectedFile));	    		  
	    			  String text = null;
	    			  while ((text = reader.readLine()) != null)  {
	    				  contents.append(text).append(System.getProperty("line.separator"));
	    			  }
	    		  } catch (FileNotFoundException e1){
	    			  e1.printStackTrace();
	    		  } catch (IOException e3) {
	    			  e3.printStackTrace();
	    		  } finally {
	    			  try {
	    				  if (reader != null) {
	    					  reader.close();
	    				  }
	    			  } catch (IOException e2) {
	    				  e2.printStackTrace();
	    			  }
	    		  }
	    		  // show file contents here
	    		  System.out.println(contents.toString());
	    	  }
	      }
	    });
	    buttonsPanel.add(openFileButton);

	    return centerPanel;
	  }
    
	  protected JComponent createButtonsPanel() {
		    JPanel buttonsPanel = new JPanel(new BorderLayout());
		    buttonsPanel.add(BorderLayout.NORTH, new JSeparator());
		    JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		    buttonsPanel.add(BorderLayout.CENTER, subPanel);

		    JButton okButton = new JButton("OK");
		    okButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        //performOK();
		      }
		    });
		    subPanel.add(okButton);

		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        //performCancel();
		      }
		    });
		    subPanel.add(cancelButton);

		    return buttonsPanel;
		  }
	  
	  private void updateButtonsState() {
		    selectButton.setEnabled(!availableGroupList.getSelectionModel().isSelectionEmpty());
		    selectAllButton.setEnabled(!availableGroupListModel.isEmpty());
		    deselectButton.setEnabled(!selectedGroupList.getSelectionModel().isSelectionEmpty());
		    deselectAllButton.setEnabled(!selectedGroupListModel.isEmpty());
	  }
	  
	  private void initAvailableGroupList(String[] aGroupList) {
		  for (int i = 0; i < aGroupList.length; i++) {
			  availableGroupListModel.addElement(aGroupList[i]);
		  }		  
	  }

	  public void valueChanged(ListSelectionEvent e) {
	    if (e.getSource() == availableGroupList.getSelectionModel()
	      || e.getSource() == selectedGroupList.getSelectionModel()) {
	      updateButtonsState();
	    }
	  }
}
