package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.data.Buddy;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Dialog to handle the black listed buddies
 */
public class MessageListDialog extends BaseDialog
{

    private static final long serialVersionUID = -6048051912258339134L;
    /**
     * The table model
     */
    private DefaultTableModel tableModel;

    public MessageListDialog(JFrame frame, final ContactListController controller)
    {
        super(frame);
        setTitle(Resources.resources.get("dialog.messagelist.title"));
        JPanel blackListPanel = new JPanel();
        blackListPanel.setLayout(new GridBagLayout());

        JTable blackListTable = new JTable();
        // Create a custom model
        tableModel = new DefaultTableModel() {

            /**
             * 
             */
            private static final long serialVersionUID = 8238285502929367774L;

            @Override
            public boolean isCellEditable(int row, int column)
            {
                // Buddy name not editable
                return column != 1;
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
        blackListTable.setModel(tableModel);      
        blackListTable.setAutoCreateColumnsFromModel(false);
        
        JScrollPane listScroller = new JScrollPane(blackListTable);
        listScroller.setPreferredSize(new Dimension(300, 200));

        tableModel.addColumn(Resources.resources.get("status.blacklisted"));
        tableModel.addColumn(Resources.resources.get("dialog.buddy.name"));
        blackListTable.setCellSelectionEnabled(false);
        blackListTable.setColumnSelectionAllowed(false);
        blackListTable.setRowSelectionAllowed(true);
        blackListTable.setAutoCreateColumnsFromModel(true);
        JTableHeader header = blackListTable.getTableHeader();
        header.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Fill the table
        List<Buddy> buddies = controller.getContactList().getBuddies();
        for (Buddy buddy : buddies)
        {
            tableModel.addRow(new Object[] { controller.isBlackListed(buddy.getContact()), buddy});
        }
        // Add black listed buddies not in our contact list
        String[] blackListedBuddies = controller.getBlackList();
        for (int count = 0; count < blackListedBuddies.length; count++)
        {
            if (controller.getUser(blackListedBuddies[count]) == null)
            {
                tableModel.addRow(new Object[] { true, new Buddy(blackListedBuddies[count])});
            }
        }

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
        blackListPanel.add(listScroller, constraint);
        add(blackListPanel);
        pack();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void save()
    {
    	/*
        tableModel.getDataVector();
        Vector<Vector> rows = tableModel.getDataVector();
        for (Vector row : rows)
        {
            // Black or white list the buddies
            boolean selected = (Boolean)row.get(0);
            Buddy buddy = (Buddy)row.get(1);
            boolean blackListed = controller.isBlackListed(buddy.getContact());
            
            if (selected && !blackListed)
            {
                controller.addToBlackList(buddy.getDisplayName());
            }
            else if (!selected && blackListed)
            {
                controller.removeFromBlackList(buddy.getDisplayName());
            }
        }
        */
    }
    
}
