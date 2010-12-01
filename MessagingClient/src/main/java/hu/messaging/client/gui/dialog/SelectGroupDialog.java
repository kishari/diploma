package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;



public class SelectGroupDialog extends BaseDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7558213042912942466L;

	//List of group
	private JList list;
	public SelectGroupDialog(JFrame frame, List<String> groupList)
	{
		super(frame);
		setTitle(Resources.resources.get("dialog.contact.move.title"));
        
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(Resources.resources.get("dialog.contact.move.label"));
        content.add(label, BorderLayout.NORTH);
        list = new JList(groupList.toArray());
		list.setSelectedIndex(0);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        content.add(scrollPane, BorderLayout.CENTER);
		setContent(content);
		pack();
	}

	@Override
	protected void save()
	{
		setData(list.getSelectedValue());
	}

	public void setSelection(String name)
	{
		list.setSelectedValue(name, true);
	}
}
