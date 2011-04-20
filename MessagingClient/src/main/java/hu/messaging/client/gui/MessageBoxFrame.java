package hu.messaging.client.gui;

import java.io.*;

import hu.messaging.Constants;
import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.icp.listener.ConnectionListener;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;
import hu.messaging.util.MessageUtils;
import hu.messaging.util.XMLUtils;
import hu.messaging.client.model.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.ericsson.icp.util.ISessionDescription;

public class MessageBoxFrame extends JFrame implements MSRPListener, ConnectionListener {

    private static final long serialVersionUID = -6048051912258339134L;

    private JTabbedPane messagePane;
    
    private JPanel inboxPanel;
    private JPanel sentPanel;
    
    private MessageContainer selectedMessage = null;
    
    private DefaultTableModel inboxMessageTableModel;
    private DefaultTableModel sentMessageTableModel;
    
    private List<MessageContainer> inboxMessageList = new ArrayList<MessageContainer>();
    private List<MessageContainer> sentMessageList = new ArrayList<MessageContainer>();

    private ICPController icpController;
    
    private JDesktopPane desktop;
    
    private List<MessageDetailsFrame> children = new ArrayList<MessageDetailsFrame>();
    
    public MessageBoxFrame(ICPController icpController) {  
    	this.icpController = icpController;
    	
    	inboxMessageList = MessageUtils.loadInboxMessages();
    	sentMessageList = MessageUtils.loadSentMessages();
    	
        setLocation(100, 100);
        setTitle(Resources.resources.get("frame.messagebox.title"));
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
        setPreferredSize(new Dimension(500, 307));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
       // desktop = new JDesktopPane();
       //   setContentPane(desktop);
        
        inboxPanel = new JPanel();
        inboxPanel.setLayout(new BorderLayout());
        inboxPanel.add(createMessageTablePanel(true), BorderLayout.NORTH);
        inboxPanel.add(createButtonPanel(), BorderLayout.EAST);
        
        sentPanel = new JPanel();
        sentPanel.setLayout(new BorderLayout());
        sentPanel.add(createMessageTablePanel(false), BorderLayout.NORTH);
        sentPanel.add(createButtonPanel(), BorderLayout.EAST);
        
        messagePane = new JTabbedPane();
        messagePane.add("inbox", inboxPanel);
        messagePane.add("sent", sentPanel);        
        
        getContentPane().add(messagePane);
        
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		for (JFrame f : children) {
        			f.setVisible(false);
        			f.dispose();
        		}
        	}
        });
        	
        pack();                
        setVisible(true);
    }    
    
    private JComponent createButtonPanel() {
    	JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout(new BorderLayout());
    	JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	buttonPanel.add(BorderLayout.CENTER, subPanel);

    	JButton okButton = new JButton(Resources.resources.get("button.ok"));
    	okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageBoxFrame.this.setVisible(false);
				for (MessageDetailsFrame f : children) {
        			f.setVisible(false);    
        			f.close();
        		}
				MessageBoxFrame.this.dispose();				
			}
		});
	    subPanel.add(okButton);
	    
	    JButton deleteButton = new JButton(Resources.resources.get("button.delete"));
	    deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedMessages(messagePane.getSelectedIndex());		
			}
		});
	    subPanel.add(deleteButton);
    	
    	return buttonPanel;
    }
    
    private JComponent createMessageTablePanel(boolean isInboxPanel) {    
    	 JPanel messageTablePanel = new JPanel();
    	 messageTablePanel.setLayout(new GridBagLayout());
    	 
    	 DefaultTableModel messageTableModel;
    	    	 
         JTable messageTable = new JTable();
        	 messageTable.addMouseListener(new MouseAdapter() {
              	public void mouseClicked(MouseEvent event) {
              		if (event.getClickCount() == 2) {
              			selectedMessage = getSelectedMessage(((JTable)event.getComponent()).getSelectedRow(), messagePane.getSelectedIndex());
              			//Itt kellene egy progressAblak, amiben a letöltés állapotát jelzi
              			if (!"SENT".equals(selectedMessage.getStatus()) && !selectedMessage.isContentAvailable()) {
              				System.out.println("content nem elerheto. get content from server...");         				
              				try {
              					createMSRPSessionToRemote(Constants.serverSipURI);         					
              				}
              				catch(Exception e) { } 
              			}
              			else {
              				if ("MP3".equals(selectedMessage.getMimeType().toUpperCase().trim())) {
              					children.add(new AudioMessageDetailsFrame(selectedMessage, getMessageContent(selectedMessage)));
              				}
              				else {
              					children.add(new ImageMessageDetailsFrame(selectedMessage, getMessageContent(selectedMessage)));
              				}
              			}
              		}         		
              	}
              }); 
         
         final int checkboxIndex = isInboxPanel ? 3 : 1;
         
         messageTableModel = new DefaultTableModel() {        	
 			private static final long serialVersionUID = -7718990533391539496L;

 			@Override
             public boolean isCellEditable(int row, int column) {
                 return column == checkboxIndex;
             }

             @Override
             public Class< ? > getColumnClass(int columnIndex) {
                 if (columnIndex == checkboxIndex) {
                     return Boolean.class;
                 }
                 else {
                     return String.class;
                 }
             }
         };
         
         if (isInboxPanel) {
        	 messageTableModel.addColumn("status");
        	 messageTableModel.addColumn("sender");
        	 inboxMessageTableModel = messageTableModel;
         }
         else {
        	 sentMessageTableModel = messageTableModel;
         }
         
         messageTableModel.addColumn("subject");
         messageTableModel.addColumn("");
         messageTableModel.addColumn("");
         
         messageTable.setModel(messageTableModel);      
         messageTable.setAutoCreateColumnsFromModel(false);
         
         JScrollPane listScroller = new JScrollPane(messageTable);
         listScroller.setPreferredSize(new Dimension(300, 200));

         messageTable.setRowHeight(20);
                 
         messageTable.setCellSelectionEnabled(false);
         messageTable.setColumnSelectionAllowed(false);
         messageTable.setRowSelectionAllowed(true);
         messageTable.setAutoCreateColumnsFromModel(true);   
         if (isInboxPanel) {
        	 messageTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(50);
         }         
         messageTable.getTableHeader().getColumnModel().getColumn(checkboxIndex).setMaxWidth(30);
         messageTable.getTableHeader().getColumnModel().getColumn(checkboxIndex+1).setMaxWidth(0);
         messageTable.getTableHeader().getColumnModel().getColumn(checkboxIndex+1).setResizable(false);
              
         if (isInboxPanel) {        	 
             for (MessageContainer m : this.inboxMessageList) {
            	 MessageContainer.Sender s = m.getSender();
            	 String senderLabelText = s.getName() != null && s.getName().trim().length() != 0 ? s.getName() : s.getSipUri();
            	 messageTableModel.addRow(new Object[] { m.getStatus(), senderLabelText, m.getSubject(), false, m.getId() });
             }
                          
         }
         else {
             for (MessageContainer m : this.sentMessageList)
             {
            	 messageTableModel.addRow(new Object[] { m.getSubject(), false, m.getId() });            	 
             }  
         }
              
         GridBagConstraints constraint = new GridBagConstraints();
         
         constraint = new GridBagConstraints();
         constraint.gridx = 0;
         constraint.gridy = 1;
         constraint.anchor = GridBagConstraints.CENTER;
         constraint.insets = new Insets(5, 5, 5, 5);
         constraint.weightx = 1;
         constraint.weighty = 1;
         constraint.fill = GridBagConstraints.BOTH;
                 
         messageTablePanel.add(listScroller,constraint);
         
         return messageTablePanel;
    }
    
    private MessageContainer getSelectedMessage(int row, int tabIndex) {
    	MessageContainer m = null;
    	if (tabIndex == 0) {
    		System.out.println("row: " + row);
   			System.out.println("(inboxMessageList.size()): " + (inboxMessageList.size()));
   			m = inboxMessageList.get(row);
    	}
    	else {
    		m = sentMessageList.get(row);
    	}
    	return m;
    }
    
    private byte[] getMessageContent(MessageContainer m) {
		File contentFile = new File(Constants.messagesPath + 
									Constants.messagesContentsRelativePath + 
									m.getId() + "." + m.getMimeType());
		byte content[] = new byte[(int) contentFile.length()];

		try {
			FileInputStream fin = new FileInputStream(contentFile);
			fin.read(content);
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
		}
		return content;
	}
    
    @SuppressWarnings("unchecked")
	private void deleteSelectedMessages(int tabIndex) {
    	Vector<Vector> rows = null;
    	List<String> selectedMessageIds = new ArrayList<String>();
    	int checkboxIndex = (tabIndex == 0) ? inboxMessageTableModel.getColumnCount() - 2 : sentMessageTableModel.getColumnCount() - 2;
    	
    	rows = (tabIndex == 0) ? inboxMessageTableModel.getDataVector() : sentMessageTableModel.getDataVector();
    	
   		for (Vector row : rows) {
   			boolean selected = (Boolean) row.get(checkboxIndex);
   			String messageId = (String) row.get(checkboxIndex + 1);
   			if (selected) {
   				selectedMessageIds.add(messageId);
   			}
   		}

   		for (String mId : selectedMessageIds) {
    		int idx = 0;
    		for (Vector v : rows) {
    			if (mId.equals((String)v.get(checkboxIndex + 1))) {
    				if (tabIndex == 0) {
    					inboxMessageTableModel.removeRow(idx);
    				}
    				else {
    					sentMessageTableModel.removeRow(idx);
    				}    					
    				break;
    			}
    			idx++;
    		}
    			
    		List<MessageContainer> list = (tabIndex == 0) ? inboxMessageList : sentMessageList;
    		int mIdx = 0;
    		for (MessageContainer m : list) {    			
    			if (mId.equals(m.getId())) {
    				list.remove(mIdx);
    				break;
    			}
    			mIdx++;
    		}
    	}
    }
    
    public void fireMsrpEvent(MSRPEvent event) {
		try {
			switch(event.getCode()) {
				case MSRPEvent.sessionStarted :
					System.out.println("session started event");
					InfoMessage info = new InfoMessage();
					info.setInfoType("DOWNLOAD_MESSAGE");
					InfoMessage.InfoDetail detail = new ObjectFactory().createInfoMessageInfoDetail();
					detail.setId(selectedMessage.getId());
					info.setInfoDetail(detail);
					String msg = XMLUtils.createStringXMLFromInfoMessage(info);
					icpController.getCommunicationController().sendSIPMessage(Constants.serverSipURI, msg);
					break;
				case MSRPEvent.brokenTrasmission :
					break;
				case MSRPEvent.messageSentSuccess :
					break;
				case MSRPEvent.messageReceivingSuccess :
					CompleteMessage m = event.getCompleteMessage();
					MessageUtils.updateMessageContainerFile(MessageUtils.createMessageContainerFromCompleteMessage(m, false), m.getContent());
					this.printToFile(m.getContent(), m.getExtension());
					icpController.getCommunicationController().sendBye();
					this.selectedMessage = null;
					break;
			}
		}
		catch (Exception e) { 
    		e.printStackTrace();
    	}
    }

    public void connectionChanged(ConnectionState event) {
    	switch (event) {
            case Connecting:
                break;
            case Connected:      
                break;
            case Refused:
                break;
            case ConnectionFailed:           
                break;
            case Disconnected:
                break;    
            case ConnectionFinished:
            	icpController.getCommunicationController().removeMSRPListener(this);
				icpController.getSessionListener().removeConnectionListener(this);
				icpController.getCommunicationController().getMsrpStack().disposeResources();
            	break;
            case RecipientsSentSuccessful:
            	break;            
        }
    }
    
	private void createMSRPSessionToRemote(String sipURI) throws Exception {
		
		System.out.println("createMSRPSessionToRemote : " + sipURI);
		
		ISessionDescription sdp = icpController.getCommunicationController().getLocalSDP();
		icpController.getCommunicationController().addMSRPListener(MessageBoxFrame.this);
		  
		icpController.getSessionListener().addConnectionListener(MessageBoxFrame.this);
		icpController.getCommunicationController().sendInvite(sdp);
		icpController.getCommunicationController().addLocalSDP(sipURI, sdp.format());
	}
	
	//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, String fileExtension) {
		try {
			OutputStream out = null;
			File recreatedContentFile = new File("c:\\diploma\\testing\\clientInboxContentFile." + fileExtension);
			out = new BufferedOutputStream(new FileOutputStream(recreatedContentFile, true));
			
			out.write(data);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
//<<<<<<<<<<<<<<TESZT	
}
