package hu.messaging.client.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import hu.messaging.Constants;
import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.util.FileUtils;
import hu.messaging.client.gui.util.MessageUtil;
import hu.messaging.client.icp.listener.ConnectionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ericsson.icp.util.ISessionDescription;

import hu.messaging.client.Resources;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.util.SDPUtil;

public class SendMessageDialog extends JFrame implements ConnectionListener, ListSelectionListener, MSRPListener {
	
	private static final long serialVersionUID = -211908165523434927L;
	
	private JList availableGroupList;
	private JList selectedGroupList;

	private ICPController icpController;
	private ContactListController contactListController;
	
	private DefaultListModel availableGroupListModel = new DefaultListModel();
	private DefaultListModel selectedGroupListModel = new DefaultListModel();
	
	private JButton selectAllButton;
	private JButton selectButton;
	private JButton deselectAllButton;
	private JButton deselectButton;
	private JButton sendButton;
	
	private CompleteMessage message = new CompleteMessage();
	
	private JFileChooser fileChooser;
	/**
     * Indicates if the dialog is closing
     */
    private boolean closing = false;

    /**
     * The main menu bar
     */
    protected JMenuBar menuBar; 
    
    public SendMessageDialog(ICPController icpController, ContactListController contactListController)
    {
        this.icpController = icpController;
        this.contactListController = contactListController;
        
        setLocation(100, 100);
        //setPreferredSize(new Dimension(500, 300));
        setTitle(Resources.resources.get("dialog.communication.sendMessage"));
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
        
        JPanel content = new JPanel(new BorderLayout());
	    add(content);
	    content.add(BorderLayout.NORTH, createGroupSelectionPanel());
	    content.add(BorderLayout.CENTER, createCenterPanel());
	    content.add(BorderLayout.SOUTH, createButtonsPanel());
	    
	    List<String> groupNames = contactListController.getGroupDisplayNames();
	    String[] groups = groupNames.toArray(new String[groupNames.size()]);
	    initAvailableGroupList(groups);
	    
	    
		pack();
		
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
            	System.out.println("Connected!");            	            
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
            case RecipientsSentSuccessful:
            {
        		icpController.getCommunicationController().sendBye();
        		MessageUtil.createMessageFile(message);
            	break;
            }
            case ConnectionFinished:
            {
            	icpController.getCommunicationController().removeMSRPListener(this);
            	icpController.getSessionListener().removeConnectionListener(this);
        		icpController.getCommunicationController().getMsrpStack().disposeResources();        		
        		//JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.end"));
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
	      public void actionPerformed(ActionEvent event) {
	    	  int retVal = fileChooser.showOpenDialog(SendMessageDialog.this);
	    	  if (retVal == JFileChooser.APPROVE_OPTION) {
	    		  File selectedFile = fileChooser.getSelectedFile();
	    		  setMessageContent(FileUtils.readFileToByteArray(selectedFile));
	    		  message.setExtension(getFileExtension(selectedFile));
	    		  //System.out.println(new String(FileUtils.readFileToByteArray(selectedFile)));	    		  
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

		    sendButton = new JButton("Send");
		    sendButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  message.setSender(getLocalUserSipURI());
		    	  if (message.isReady()) {		    		  
		    		  try {
		    			  sendButton.setEnabled(false);
		    			  ISessionDescription sdp = getLocalSDP();
		    			  icpController.getCommunicationController().addMSRPListener(SendMessageDialog.this);
		    			  
		    			  icpController.getSessionListener().addConnectionListener(SendMessageDialog.this);
		    			  icpController.getCommunicationController().sendInvite(sdp);
		    			  icpController.getCommunicationController().addLocalSDP(Constants.serverSipURI, sdp.format());
		    		  }
		    		  catch(Exception e1) {
		    			  
		    		  }		    		  		    		  
		    	  }
		      }
		    });
		    subPanel.add(sendButton);

		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  SendMessageDialog.this.setVisible(false);
		    	  SendMessageDialog.this.dispose();
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
	  
	  public String[] getSelectedGroupNames() {
		  ListModel model = selectedGroupList.getModel();		  
		  String[] chosenGroups = new String[model.getSize()];
		  
		  for (int i = 0; i < model.getSize(); i++) {
			  chosenGroups[i] = model.getElementAt(i).toString();
		  }
		  
		  return chosenGroups;
	  }

	public byte[] getMessageContent() {
		return message.getContent();
	}

	public void setMessageContent(byte[] messageContent) {
		this.message.setContent(messageContent);
	}
	
	private List<Buddy> getSelectedGroupsMembers() {
		String[] selectedGroupNames = getSelectedGroupNames();		
		List<Group> allGroup = contactListController.getGroups();
		
		List<Buddy> selectedGroupMembers = new ArrayList<Buddy>();		
		List<Group> selectedGroups = new ArrayList<Group>();
		
		for (int i = 0; i < selectedGroupNames.length; i++) {
			for (Group g : allGroup) {
				if (g.getDisplayName().equals(selectedGroupNames[i]))  {
					selectedGroups.add(g);
					break;
				}
			}
		}
		
		for (Group g : selectedGroups) {
			for (Buddy b : g.getBuddies()) {
				boolean isRedundant = false;
				for (Buddy sb : selectedGroupMembers) {
					if (b.getContact().equals(sb.getContact())) {
						isRedundant = true;
						break;
					}
				}
				if (!isRedundant) {
					selectedGroupMembers.add(b);
				}
			}
		}
		
		return selectedGroupMembers;
	}
	
	private ISessionDescription getLocalSDP() throws IOException {
		
		if (!icpController.getCommunicationController().getMsrpStack().getConnections().isReceiverConnection()) {
			icpController.getCommunicationController().getMsrpStack().getConnections().createReceiverConnection(InetAddress.getLocalHost());
			icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().start();
		}
		else if (!icpController.getCommunicationController().getMsrpStack().getConnections().isRunningReceiverConnection()) {
			icpController.getCommunicationController().getMsrpStack().getConnections().createReceiverConnection(InetAddress.getLocalHost());
			icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().start();
		}
		
		InetAddress localhost = icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().getHostAddress();
		int port = icpController.getCommunicationController().getMsrpStack().getConnections().getReceiverConnection().getPort();
		String sessionId = MSRPUtil.generateRandomString(Constants.sessionIdLength);
		
		return SDPUtil.createSDP(localhost, port, sessionId);
	}
	
	public void brokenTrasmission(MSRPEvent event) {
		
	}
	
	public void messageSentSuccess(MSRPEvent event) {
		String message = buildRecipientsSIPMessage(event.getMessageId(), getSelectedGroupsMembers());
		//icpController.getCommunicationController().sendSIPMessage(Constants.serverSipURI, message);
		try {
			icpController.getSession().sendMessage("text/plain", message.getBytes(), message.length());
		}
		catch(Exception e) {}
	}
	
	public void sessionStarted(MSRPEvent event) {
    	try {
    		System.out.println("Session started EVENT");
    		if (true) {
    			icpController.getCommunicationController().sendMessageInMSRPSession(message, Constants.serverSipURI);
    		}
    		else {
    			String message = "GETMESSAGES\r\n" + 
    							 "Message-IDs:\r\n" +
    							 "a0147afc88\r\n" +
    							 "6887b5824e" + 
    							 "\r\n\r\n-----END";
    			icpController.getCommunicationController().sendSIPMessage(Constants.serverSipURI, message);
    		}
    	}
    	catch (Exception e) { 
    		
    	} 
	}
	public void startTrasmission(MSRPEvent event) {
		
	}
	
	private String buildRecipientsSIPMessage(String messageId, List<Buddy> recipients) {
		message.setMessageId(messageId);
		String msg = "RECIPIENTS\r\n";
		msg += "Message-ID: " + message.getMessageId() + "\r\n" + 
			   "Extension: " + message.getExtension() + "\r\n" +
			   "Sender: " + message.getSender() + "\r\n\r\n"; 
		
		for (Buddy r : recipients) {
			msg += r.getDisplayName() + "#" + r.getContact() + "\r\n";
		}
		
		msg += "\r\n-----END";
		
		System.out.println(msg);
		return msg;
	}
	
	private String getFileExtension(File file) {
		String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		return extension;
	}
	
	private String getLocalUserSipURI() {
		String sipURI = null;
		try {
			sipURI = icpController.getProfile().getIdentity().toString();
		}
		catch(Exception e) { }
		
		return sipURI;
	}
}

