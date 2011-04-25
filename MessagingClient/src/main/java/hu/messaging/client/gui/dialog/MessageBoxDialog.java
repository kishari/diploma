package hu.messaging.client.gui.dialog;

import java.io.*;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.icp.listener.ConnectionListener;
import hu.messaging.client.icp.listener.ConnectionStateType;
import hu.messaging.msrp.listener.MSRPEvent;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.model.CompleteMSRPMessage;
import hu.messaging.util.MessageUtils;
import hu.messaging.util.XMLUtils;
import hu.messaging.client.media.MimeHelper;
import hu.messaging.client.model.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MessageBoxDialog extends JFrame implements MSRPListener, ConnectionListener {

    private static final long serialVersionUID = -6048051912258339134L;

    private JTabbedPane messagePane;
    
    private JPanel inboxPanel;
    private JPanel sentPanel;
    
    private MessageInfoContainer selectedMessage = null;
    
    private DefaultTableModel inboxMessageTableModel;
    private DefaultTableModel sentMessageTableModel;
    
    private List<MessageInfoContainer> inboxMessageList = new ArrayList<MessageInfoContainer>();
    private List<MessageInfoContainer> sentMessageList = new ArrayList<MessageInfoContainer>();

    private ICPController icpController;
 
    private ProgressDialog progressDialog = null;
    
    private List<MessageDetailsDialog> children = new ArrayList<MessageDetailsDialog>();
    
    public MessageBoxDialog(ICPController icpController) {  
    	this.icpController = icpController;
    	
    	inboxMessageList = MessageUtils.loadInboxMessages();
    	sentMessageList = MessageUtils.loadSentMessages();
    	
        setLocation(100, 100);
        setTitle(Resources.resources.get("frame.messagebox.title"));
        setIconImage(ImageUtil.createImage("logo.gif"));
        setPreferredSize(new Dimension(500, 307));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inboxPanel = new JPanel();
        inboxPanel.setLayout(new BorderLayout());
        inboxPanel.add(createMessageTablePanel(true), BorderLayout.NORTH);
        inboxPanel.add(createButtonPanel(), BorderLayout.EAST);
        
        sentPanel = new JPanel();
        sentPanel.setLayout(new BorderLayout());
        sentPanel.add(createMessageTablePanel(false), BorderLayout.NORTH);
        sentPanel.add(createButtonPanel(), BorderLayout.EAST);
        
        messagePane = new JTabbedPane();
        messagePane.add(Resources.resources.get("messagepane.label.inbox"), inboxPanel);
        messagePane.add(Resources.resources.get("messagepane.label.sent"), sentPanel);        
        
        getContentPane().add(messagePane);
        
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		for (JFrame f : children) {
        			f.setVisible(false);
        			f.dispose();
        		}
        	}
        });
        	
        progressDialog = new ProgressDialog(this, true);
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
				MessageBoxDialog.this.setVisible(false);
				for (MessageDetailsDialog f : children) {
        			f.setVisible(false);    
        			f.close();
        		}
				MessageBoxDialog.this.dispose();				
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
              			if (!"SENT".equals(selectedMessage.getStatus()) && !selectedMessage.getContentDescription().isContentAvailable()) {         				
              				try {              					
              					createMSRPSessionToRemote(Resources.serverSipURI);
              					setProgressWindowVisibily(true);
              				}
              				catch(Exception e) { } 
              			}
              			else {
              					if ("audio/mpeg".equals(selectedMessage.getContentDescription().getMimeType().toLowerCase().trim())) {
                  					children.add(new AudioMessageDetailsDialog(selectedMessage, getMessageContent(selectedMessage)));
                  				}
                  				else {
                  					children.add(new ImageMessageDetailsDialog(selectedMessage, getMessageContent(selectedMessage)));
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
             for (MessageInfoContainer m : this.inboxMessageList) {
            	 UserInfo sender = m.getSender();
            	 String senderLabelText = sender.getName() != null && sender.getName().trim().length() != 0 ? sender.getName() : sender.getSipUri();
            	 messageTableModel.addRow(new Object[] { m.getStatus(), senderLabelText, m.getSubject(), false, m.getId() });
             }
                          
         }
         else {
             for (MessageInfoContainer m : this.sentMessageList)
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
    
    private MessageInfoContainer getSelectedMessage(int row, int tabIndex) {
    	MessageInfoContainer m = null;
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
    
    private byte[] getMessageContent(MessageInfoContainer m) {
		File contentFile = new File(Resources.messageContentsDirectory + 
									m.getId() + "." + MimeHelper.getExtensionByMIMEType(m.getContentDescription().getMimeType()));
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
    			
    		List<MessageInfoContainer> list = (tabIndex == 0) ? inboxMessageList : sentMessageList;
    		int mIdx = 0;
    		for (MessageInfoContainer m : list) {    			
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
			
			ObjectFactory factory = new ObjectFactory();
			switch(event.getEventType()) {
				case sessionStarted :
					System.out.println("session started event: " + event.getRemoteSipUri());
					InfoMessage info = new InfoMessage();
					info.setInfoType(InfoMessage.downloadContent);
					
					InfoDetail detail = factory.createInfoDetail();
					detail.setId(selectedMessage.getId());
					
					InfoMessage.DetailList detailList = factory.createInfoMessageDetailList();
					detailList.getDetail().add(detail);
					
					info.setDetailList(detailList);
					String msg = XMLUtils.createStringXMLFromInfoMessage(info);
					icpController.getCommunicationController().sendSIPMessageInSIPSession(Resources.serverSipURI, msg);					
					
					break;
				case brokenTrasmission :
					break;
				case messageSentSuccess :
					break;
				case messageReceivingSuccess :
					setProgressWindowVisibily(false);
					CompleteMSRPMessage fullMessage = event.getCompleteMessage();	
					System.out.println("messageReceivingSuccess: " + fullMessage.getMessageId());
					MessageUtils.updateMessageContainerFile(MessageUtils.readMessageContainerFromFile(fullMessage.getMessageId()), fullMessage.getContent());					
					
					icpController.getCommunicationController().sendBye(Resources.serverSipURI);
					this.selectedMessage = null;
					inboxMessageList = MessageUtils.loadInboxMessages();
					break;
			}
		}
		catch (Exception e) { 
    		e.printStackTrace();
    	}
    }

    public void connectionChanged(ConnectionStateType event) {
    	switch (event.getConnectionState()) {
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
				icpController.getSessionListener(Resources.serverSipURI).removeConnectionListener(this);
				icpController.getCommunicationController().getMsrpStack().disposeResources();
            	break;
            case RecipientsSentSuccessful:
            	break;            
        }
    }
    
	private void createMSRPSessionToRemote(String sipURI) throws Exception {
		
		System.out.println("createMSRPSessionToRemote : " + sipURI);
		
		icpController.getCommunicationController().addMSRPListener(MessageBoxDialog.this);	
		icpController.createNewSipSession(Resources.serverSipURI);
		icpController.getSessionListener(Resources.serverSipURI).addConnectionListener(MessageBoxDialog.this);
		icpController.getCommunicationController().sendInvite(sipURI);	
	}
	
	private class ProgressDialog extends JDialog {
		public ProgressDialog(JFrame frame, boolean modal) {
			super(frame, "Info", modal);
			setSize(200, 70);
			System.out.println("ProgressDialog konstruktor");
			setLayout(new BorderLayout());
			JPanel p = new JPanel();
			
			JLabel l = new JLabel("A tartalom letöltése folyamatban...");
			p.add(l);						
			
			add(BorderLayout.CENTER, p);
			setVisible(false);
			
		}
	}
	
	private void setProgressWindowVisibily(boolean visible) {
		progressDialog.setLocation((int)(this.getLocation().x + this.getSize().getWidth()/2 - 100), 
								   (int)(this.getLocation().y + this.getSize().getHeight()/2 - 35));
		progressDialog.setVisible(visible);
	}
	//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, String mimeType) {
		try {
			OutputStream out = null;
			File recreatedContentFile = new File("c:\\diploma\\testing\\clientInboxContentFile." + MimeHelper.getExtensionByMIMEType(mimeType));
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
