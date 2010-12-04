package hu.messaging.client.gui.dialog;

import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.msrp.CompleteMessage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellRenderer;

public class MessageListDialog extends JFrame {

    private static final long serialVersionUID = -6048051912258339134L;

    private JTabbedPane messagePane;
    
    private JPanel inboxPanel;
    private JPanel sentPanel;
    
    private DefaultTableModel inboxMessageTableModel;
    private DefaultTableModel sentMessageTableModel;
    
    private List<CompleteMessage> inboxMessageList = new ArrayList<CompleteMessage>();
    private List<CompleteMessage> inboxNewMessageList = new ArrayList<CompleteMessage>();
    private List<CompleteMessage> sentMessageList = new ArrayList<CompleteMessage>();

    private ICPController icpController;
    
    public MessageListDialog(ICPController icpController) {  
    	this.icpController = icpController;
    	
        setLocation(100, 100);
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
        messagePane.add("inbox", inboxPanel);
        messagePane.add("sent", sentPanel);
        
        add(messagePane);
        pack();
        setVisible(true);
    }    
    
    private JComponent createButtonPanel() {
    	JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout(new BorderLayout());
    	JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	buttonPanel.add(BorderLayout.CENTER, subPanel);

    	JButton okButton = new JButton("OK");
    	okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageListDialog.this.setVisible(false);
				MessageListDialog.this.dispose();				
			}
		});
	    subPanel.add(okButton);
	    
	    JButton deleteButton = new JButton("Delete");
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
         	public void mouseClicked(MouseEvent e) {
         		if (e.getClickCount() == 2) {
         			System.out.println(getSelectedMessage(((JTable)e.getComponent()).getSelectedRow(), messagePane.getSelectedIndex()).getSender());
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
         
         /*TableCellRenderer renderer = new MessageTableCellRenderer();
         for (int i = 0; i < messageTable.getTableHeader().getColumnModel().getColumnCount(); i++) {
        	 messageTable.getTableHeader().getColumnModel().getColumn(i).setCellRenderer(renderer);
         }
         */
         
         if (isInboxPanel) {
        	 inboxNewMessageList = icpController.getCommunicationController().getIncomingNewMessages();
             for (CompleteMessage m : inboxNewMessageList) {
            	 System.out.println(m.getMessageId() + " " + m.getSender());
            	 messageTableModel.addRow(new Object[] { "NEW", m.getSender(), m.getSubject(), false, m.getMessageId() });          
             }
             
        	 List<CompleteMessage> inboxMessages = getTestIncomingMessages();
             for (CompleteMessage m : inboxMessages) {
            	 messageTableModel.addRow(new Object[] { "READ", m.getSender(), m.getSubject(), false, m.getMessageId() });
             }
                          
         }
         else {
        	 List<CompleteMessage> sentMessages = getTestSentMessages();
             for (CompleteMessage m : sentMessages)
             {
            	 messageTableModel.addRow(new Object[] { m.getSubject(), false, m.getMessageId() });            	 
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
	
    private List<CompleteMessage> getTestIncomingMessages() {    	
    	for (int i = 0; i < 5; i++) {
    		CompleteMessage m = new CompleteMessage("messageId " + i , 
    												null, 
    												"extension " + i, 
    												"sender " + i,
    												"subject " + i);    		
    		inboxMessageList.add(m);
    	}
    	return inboxMessageList;
    }
    
    private List<CompleteMessage> getTestNewIncomingMessages() {    	
    	for (int i = 0; i < 5; i++) {
    		CompleteMessage m = new CompleteMessage("newmessageId " + i , 
    												null, 
    												null, 
    												"newsender " + i,
    												"newsubject " + i);    		
    		inboxNewMessageList.add(m);
    	}
    	return inboxNewMessageList;
    }
    
    private List<CompleteMessage> getTestSentMessages() {    	
    	for (int i = 0; i < 5; i++) {
    		CompleteMessage m = new CompleteMessage("sentmessageId " + i , 
    												null, 
    												"sentextension " + i, 
    												"sentsender " + i,
    												"sentsubject " + i);    		
    		sentMessageList.add(m);
    	}
    	return sentMessageList;
    }
    
    private CompleteMessage getSelectedMessage(int row, int tabIndex) {
    	CompleteMessage m = null;
    	if (tabIndex == 0) {
    		System.out.println("row: " + row);
    		if (row < inboxNewMessageList.size()) {
    			System.out.println("(inboxNewMessageList.size()): " + (inboxNewMessageList.size()));
    			m = inboxNewMessageList.get(row);
    		}
    		else {
    			System.out.println("(row - inboxNewMessageList.size()): " + (row - inboxNewMessageList.size()));
    			m = inboxMessageList.get(row - inboxNewMessageList.size());
    		}
    	}
    	else {
    		m = sentMessageList.get(row);
    	}
    	return m;
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
    			
    		List<CompleteMessage> list = (tabIndex == 0) ? inboxMessageList : sentMessageList;
    		int mIdx = 0;
    		for (CompleteMessage m : list) {    			
    			if (mId.equals(m.getMessageId())) {
    				list.remove(mIdx);
    				break;
    			}
    			mIdx++;
    		}
    	}
    }

}
