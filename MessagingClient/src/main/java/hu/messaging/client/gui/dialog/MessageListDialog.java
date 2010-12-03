package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.msrp.CompleteMessage;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Dialog to handle the black listed buddies
 */
public class MessageListDialog extends JFrame
{

    private static final long serialVersionUID = -6048051912258339134L;
    /**
     * The table model
     */
    private DefaultTableModel tableModel;
    
    private JTable messageTable;

    private ICPController icpController;
    
    public MessageListDialog(final ICPController icpController)
    {
    	this.icpController = icpController;
    	
        setTitle(Resources.resources.get("dialog.messagelist.title"));
        setLocation(100, 100);
        setPreferredSize(new Dimension(400, 300));
        JPanel messageListPanel = new JPanel();
        messageListPanel.setLayout(new GridBagLayout());

        messageTable = new JTable();
        messageTable.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getClickCount() == 2) {
        			System.out.println("double click");
        			getSelectedRow(messageTable.getSelectedRow(), messageTable.getSelectedColumn());
        		}
        		
        	}
        });
        // Create a custom model
        tableModel = new DefaultTableModel() {
        	
            private static final long serialVersionUID = 8238285502929367774L;

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }

            @Override
            public Class< ? > getColumnClass(int columnIndex)
            {
                if (columnIndex == 0)
                {
                    return Boolean.class;
                }
                else
                {
                    return String.class;
                }
            }
        };
        messageTable.setModel(tableModel);      
        messageTable.setAutoCreateColumnsFromModel(false);
        
        JScrollPane listScroller = new JScrollPane(messageTable);
        listScroller.setPreferredSize(new Dimension(300, 200));

        tableModel.addColumn(Resources.resources.get("status.blacklisted"));
        tableModel.addColumn(Resources.resources.get("dialog.message.sender"));
        messageTable.setCellSelectionEnabled(false);
        messageTable.setColumnSelectionAllowed(false);
        messageTable.setRowSelectionAllowed(true);
        messageTable.setAutoCreateColumnsFromModel(true);
        JTableHeader header = messageTable.getTableHeader();
        header.getColumnModel().getColumn(0).setPreferredWidth(0);
       
        
        // Fill the table
        List<CompleteMessage> inboxMessages = icpController.getCommunicationController().getIncomingNewMessages();
        for (CompleteMessage m : inboxMessages)
        {
            tableModel.addRow(new Object[] { false, m.getSender()});
        }
       /* 
        // Add black listed buddies not in our contact list
        String[] blackListedBuddies = controller.getBlackList();
        for (int count = 0; count < blackListedBuddies.length; count++)
        {
            if (controller.getUser(blackListedBuddies[count]) == null)
            {
                tableModel.addRow(new Object[] { true, new Buddy(blackListedBuddies[count])});
            }
        }
        */
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.anchor = GridBagConstraints.WEST;
        constraint.insets = new Insets(5, 5, 5, 5);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.insets = new Insets(5, 5, 5, 5);
        constraint.weightx = 1;
        constraint.weighty = 1;
        constraint.fill = GridBagConstraints.BOTH;
        messageListPanel.add(listScroller, constraint);
        add(messageListPanel);
        pack();
    }    
    
    private String getSelectedRow(int row, int column) {
    	System.out.println(messageTable.getValueAt(row, column));
    	return "haha";
    }
}
