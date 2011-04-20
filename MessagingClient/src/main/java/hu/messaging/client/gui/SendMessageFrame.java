package hu.messaging.client.gui;

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
import java.util.ArrayList;
import java.util.List;

import hu.messaging.Constants;
import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.icp.listener.ConnectionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.FileUtils;

import com.ericsson.icp.util.ISessionDescription;

import hu.messaging.client.Resources;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;
import hu.messaging.client.model.*;
import hu.messaging.util.*;

public class SendMessageFrame extends JFrame implements ConnectionListener, ListSelectionListener, MSRPListener {
	
	private static final long serialVersionUID = -211908165523434927L;
	
	private JList availableGroupList;
	private JList selectedGroupList;
	
	private CaptureFrame cDialog = null;
	
	private JFileChooser fileChooser;

	private ICPController icpController;
	private ContactListController contactListController;
	
	private DefaultListModel availableGroupListModel = new DefaultListModel();
	private DefaultListModel selectedGroupListModel = new DefaultListModel();
	
	private JButton selectAllButton;
	private JButton selectButton;
	private JButton deselectAllButton;
	private JButton deselectButton;
	private JButton sendButton;
	
	private JTextField subjectTextField;
	
	private JRadioButton fromFileButton = new JRadioButton("Létezõ fájl küldése");
	private JRadioButton fromCaptureDeviceButton = new JRadioButton("Felvétel készítése");
	private ButtonGroup radioGroup = new ButtonGroup();
	
	private CompleteMessage completeMessage = new CompleteMessage();
	
    private boolean closing = false;
    
    public SendMessageFrame(ICPController icpController) {
        this.icpController = icpController;
        this.contactListController = icpController.getContactListController();
        
        setLocation(100, 100);
        //setPreferredSize(new Dimension(500, 300));
        setTitle(Resources.resources.get("dialog.communication.sendMessage"));
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
        
        JPanel content = new JPanel(new BorderLayout());
	    add(content);
	    
	    radioGroup.add(fromFileButton);
	    radioGroup.add(fromCaptureDeviceButton);
	    fromCaptureDeviceButton.setSelected(true);
	    
	    content.add(BorderLayout.NORTH, createGroupSelectionPanel());
	    content.add(BorderLayout.CENTER, createCenterPanel());
	    content.add(BorderLayout.SOUTH, createButtonsPanel());
	    
	    List<String> groupNames = contactListController.getGroupDisplayNames();
	    String[] groups = groupNames.toArray(new String[groupNames.size()]);
	    initAvailableGroupList(groups);
	    	 
	    cDialog = new CaptureFrame();
		pack();		
    }

    public void connectionChanged(ConnectionState event){
        switch (event) {
            case Connecting:
                break;
            case Connected:      
                break;
            case Refused:
                JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.refused"));
                break;
            case ConnectionFailed:           
                JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.failed"));
                break;
            case Disconnected:
                // Do not display the message is the user closed the diaog by itself
                if (!closing)
                {
                    JOptionPane.showMessageDialog(this, Resources.resources.get("message.communication.end"));
                    //setGuiEnabled(false);
                    //processCompletedTime();
                }
                break;           
            case RecipientsSentSuccessful:
        		icpController.getCommunicationController().sendBye();
        		if (!Constants.sendTestMessageMySelf) {
        			MessageUtils.createMessageContainerFile(MessageUtils.createMessageContainerFromCompleteMessage(completeMessage, true), completeMessage.getContent());
        		}
            	break;
            case ConnectionFinished:
            	icpController.getCommunicationController().removeMSRPListener(this);
            	icpController.getSessionListener().removeConnectionListener(this);
        		icpController.getCommunicationController().getMsrpStack().disposeResources();        		
        }
    }
    
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        }        
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
    
	  protected JComponent createButtonsPanel() {
		    JPanel buttonsPanel = new JPanel(new BorderLayout());
		    buttonsPanel.add(BorderLayout.NORTH, new JSeparator());
		    JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		    buttonsPanel.add(BorderLayout.CENTER, subPanel);

		    sendButton = new JButton("Send");
		    sendButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent event) {
		    	  completeMessage.setSender(getLocalUserSipURI());
		    	  completeMessage.setSubject(subjectTextField.getText());
		    	  if (fromCaptureDeviceButton.isSelected() && cDialog.getCapturedContent() != null) {
		    		  completeMessage.setContent(cDialog.getCapturedContent());
		    		  completeMessage.setExtension(cDialog.getCapturedContentExtension());
		    	  }
		    	  if (completeMessage.isReady() && selectedGroupListModel.size()> 0) {		    		  
		    		  try {
		    			  sendButton.setEnabled(false);
		    			  ISessionDescription sdp = icpController.getCommunicationController().getLocalSDP();
		    			  
		    			  icpController.getCommunicationController().addMSRPListener(SendMessageFrame.this);
		    			  icpController.getSessionListener().addConnectionListener(SendMessageFrame.this);
		    			  
		    			  icpController.getCommunicationController().sendInvite(sdp);
		    			  icpController.getCommunicationController().addLocalSDP(Constants.serverSipURI, sdp.format());
		    		  }
		    		  catch(Exception e) { }		    		  		    		  
		    	  }
		    	  else {
		    		  System.out.println("Nincs rendesen kitöltve az üzenet vagy nincsenek címzettek");
		    	  }
		      }
		    });		   
		    
		    subPanel.add(sendButton);

		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  SendMessageFrame.this.setVisible(false);
		    	  SendMessageFrame.this.dispose();
		      }
		    });
		    subPanel.add(cancelButton);

		    return buttonsPanel;
	  }
	  
	  protected JComponent createCenterPanel() {
		  JPanel subjectTextFieldPanel = new JPanel(new BorderLayout());
		  JPanel radioPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		  JPanel centerPanel = new JPanel(new BorderLayout());
		    
		  centerPanel.add(BorderLayout.NORTH, radioPanel);
		  centerPanel.add(BorderLayout.SOUTH, subjectTextFieldPanel);
		   
		  fileChooser = new JFileChooser();
		  fileChooser.setMultiSelectionEnabled(false);
		  
		  final JButton openFileButton = new JButton("Open file");
		  openFileButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
		  	  int retVal = fileChooser.showOpenDialog(SendMessageFrame.this);
		   	  if (retVal == JFileChooser.APPROVE_OPTION) {
		    		  File selectedFile = fileChooser.getSelectedFile();
		    		  try {
		    			  setMessageContent(FileUtils.readFileToByteArray(selectedFile));
		    		  }
		    		  catch(IOException e) { }	    		  
		    		  setMessageExtension(getFileExtension(selectedFile));    		  
		    	  }
		      }
		    });
		    
		    openFileButton.setEnabled(false);
		    
		    final JButton captureButton = new JButton("Capture message");
		    captureButton.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent event) {
		    	  cDialog.setVisible(true);
		      }
		    });
		    
		    getFromCaptureDeviceButton().addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent event) {
		    		openFileButton.setEnabled(false);
		    		captureButton.setEnabled(true);
		    	}
		    });
		    
		    getFromFileButton().addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent event) {
		    		openFileButton.setEnabled(true);
		    		captureButton.setEnabled(false);
		    	}
		    });	    	      	  	    	   
		    
		    radioPanel.add(getFromCaptureDeviceButton());
		    radioPanel.add(captureButton);
		    radioPanel.add(getFromFileButton());
		    radioPanel.add(openFileButton);
		    
		    this.setSubjectTextField(new JTextField("Subject", 20));
		    this.getSubjectTextField().selectAll();
		    subjectTextFieldPanel.add(this.getSubjectTextField());
		    	    
		    return centerPanel;		
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
		return completeMessage.getContent();
	}

	public void setMessageContent(byte[] messageContent) {
		completeMessage.setContent(messageContent);
	}
	
	protected List<Buddy> getSelectedGroupsMembers() {
		String[] selectedGroupNames = getSelectedGroupNames();
		List<Group> allGroup = contactListController.getGroups();
		
		List<Buddy> selectedGroupMembers = new ArrayList<Buddy>();		
		List<Group> selectedGroups = new ArrayList<Group>();
		
		for (int i = 0; i < selectedGroupNames.length; i++) {
			System.out.println("selected group name: " + selectedGroupNames[i]);
			for (Group g : allGroup) {
				System.out.println(" ---***--- "+ g.getDisplayName() + " " + g.getName());
				if (g.getDisplayName().equals(selectedGroupNames[i]))  {
					selectedGroups.add(g);
					break;
				}
			}
		}
		
		for (Group g : selectedGroups) {
			System.out.println("selected group: " + g.getName());
			for (Buddy b : g.getBuddies()) {
				//System.out.println("Buddy: " + b.getContact());
				boolean isRedundant = false;
				for (Buddy sb : selectedGroupMembers) {
					if (b.getContact().equals(sb.getContact())) {
						isRedundant = true;
						break;
					}
				}
				if (!isRedundant) {
					System.out.println("add: " + b.getContact());
					selectedGroupMembers.add(b);
				}
			}
		}
		
		System.out.println("selected: " + selectedGroupMembers.size());
		return selectedGroupMembers;
	}
	
	public void fireMsrpEvent(MSRPEvent event) {
		System.out.println(getClass().getSimpleName() + " fireMSRPEvent...");
		try {
			switch(event.getCode()) {
				case MSRPEvent.sessionStarted :  
					icpController.getCommunicationController().sendMessageInMSRPSession(completeMessage, Constants.serverSipURI);
					break;
				case MSRPEvent.brokenTrasmission :
					break;
				case MSRPEvent.messageSentSuccess :
					System.out.println("message sent successful event");
					String sipMsg = buildRecipientsSIPMessage(event.getMessageId(), getSelectedGroupsMembers());
					icpController.getSession().sendMessage("text/plain", sipMsg.getBytes(), sipMsg.length());
					break;
				case MSRPEvent.messageReceivingSuccess :
					break;
			}
		}
		catch (Exception e) { 
    		e.printStackTrace();
    	}	 
	}
	
	protected String buildRecipientsSIPMessage(String messageId, List<Buddy> recipients) {
		ObjectFactory factory = new ObjectFactory();
		
		InfoMessage m = factory.createInfoMessage();
		completeMessage.setMessageId(messageId);
		
		m.setInfoType(InfoMessage.messageData);		
		InfoDetail detail = factory.createInfoDetail();
		detail.setContent(factory.createContentDescription());
		detail.setId(completeMessage.getMessageId());
		detail.getContent().setMimeType(completeMessage.getExtension());
		detail.setSubject(completeMessage.getSubject());
		
		UserInfo sender = factory.createUserInfo();
		sender.setName("");
		sender.setSipUri(completeMessage.getSender());		
		detail.setSender(sender);
		
		detail.setRecipientList(factory.createInfoDetailRecipientList());
		for (Buddy r : recipients) {
			UserInfo recipient = factory.createUserInfo();
			recipient.setName(r.getDisplayName());
			recipient.setSipUri(r.getContact());			
			detail.getRecipientList().getRecipient().add(recipient);
		}
		
		InfoMessage.DetailList detailList = factory.createInfoMessageDetailList();
		detailList.getDetail().add(detail);
		m.setDetailList(detailList);
		
		return XMLUtils.createStringXMLFromInfoMessage(m);
	}
	
	protected String getFileExtension(File file) {
		String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		return extension;
	}
	
	protected String getLocalUserSipURI() {
		String sipURI = null;
		try {
			sipURI = icpController.getProfile().getIdentity().toString();
		}
		catch(Exception e) { }
		
		return sipURI;
	}
	
	public void setMessageExtension(String extension) {
		completeMessage.setExtension(extension);
	}

	public JTextField getSubjectTextField() {		
		return subjectTextField;
	}

	public void setSubjectTextField(JTextField subjectTextField) {
		this.subjectTextField = subjectTextField;
	}

	public JRadioButton getFromFileButton() {
		return fromFileButton;
	}

	public void setFromFileButton(JRadioButton fromFileButton) {
		this.fromFileButton = fromFileButton;
	}

	public JRadioButton getFromCaptureDeviceButton() {
		return fromCaptureDeviceButton;
	}

	public void setFromCaptureDeviceButton(JRadioButton fromCaptureDeviceButton) {
		this.fromCaptureDeviceButton = fromCaptureDeviceButton;
	}
	
}

