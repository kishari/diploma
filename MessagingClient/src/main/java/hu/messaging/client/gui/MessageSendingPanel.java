package hu.messaging.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.ericsson.icp.util.SdpFactory;

/**
 * Az üzenetek küldése ezen a panelen valósul meg.
 * @author Harangozó Csaba
 *
 */
public class MessageSendingPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1296253198192003019L;
	
	private static String INPUT_FILE_PATH = "c:\\<csatolandó fájl elérési út>";
	private static String SEND_COMMAND = "send";
	
	/**
	 * A csatolandó fájl elérési útját tartalmazõ szövegmezõ.
	 */
	private JTextField fileField;
	
	/**
	 * Az üzenet szövegét tartalmazó rész.
	 */
	private JTextArea msgArea;
	
	/**
	 * A TextArea-t tartalmazó komponens.
	 */
	private JScrollPane scrollPane;
	
	/**
	 * A címzett csoportok kiválasztására használt CheckBoxok.
	 */
	private JCheckBox[] selectedGroups;
	
	/**
	 * A csoportok maximális számát tároló lokális változó, értéket a Main ugyanezen funkciót szolgáló
	 * változójától kapja.
	 */
	private int numOfGroupsMax;
	
	
	public MessageSendingPanel() {
		
		super(new BorderLayout());
		
		JButton sendButton = new JButton("Send");
        sendButton.setActionCommand(SEND_COMMAND);
        sendButton.addActionListener(this);
        
        numOfGroupsMax = Main.instance.numberOfGroupsMax;
        
        msgArea = new JTextArea();
        msgArea.setColumns(5);
        msgArea.setLineWrap(true);
        msgArea.setRows(5);
        msgArea.setWrapStyleWord(true);
        msgArea.setDocument(new JTextAreaLimit(100));
        
        scrollPane = new JScrollPane(msgArea);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(390,200));
        panel.add(new JLabel("Üzenet: (maximum 100 karakter)"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        fileField = new JTextField(INPUT_FILE_PATH);
        
        panel.add(fileField, BorderLayout.SOUTH);
        add(panel, BorderLayout.NORTH);
        
        JPanel groupPanel = new JPanel(new FlowLayout());
        groupPanel.setPreferredSize(new Dimension(390,300));
                
        add(sendButton, BorderLayout.SOUTH);
		
        selectedGroups = new JCheckBox[numOfGroupsMax];
		
		for (int i = 0; i < numOfGroupsMax; i++) {
			selectedGroups[i] = new JCheckBox();
			selectedGroups[i].setVisible(false);
			groupPanel.add(selectedGroups[i]);
		}
		add(groupPanel, BorderLayout.CENTER);
		
	}

	/**
	 * frissíti a kiválasztható csoportokat. Ez azért kell, hogy az üzenetküldõ panelon mindig 
	 * csak az aktuális csoportokat lehessek kiválasztani címzettnek.
	 */
	public void updateGroupList() {
		
		for (int i = 0; i < numOfGroupsMax; i++) {
			selectedGroups[i].setVisible(false);
		}
		String groupName;
		Iterator <GroupListStruct> i = Main.instance.getGroupList().iterator();
		int index = -1;
		while (i.hasNext()) {
    		groupName = i.next().groupName;
    		index++;
    		selectedGroups[index].setText(groupName);
    		selectedGroups[index].setVisible(true);
    		
    	}
	}
	
	/**
	 * A groupName nevû csoport tagjainak elküldi az üzenetet.
	 * @param groupName
	 */
	private void sendMessageToGroup(String groupName) {
		Iterator<GroupListStruct> i = Main.instance.getGroupList().iterator();
		GroupListStruct element;
		while (i.hasNext()) {
			element = i.next();
    		if (groupName.equals(element.groupName)) {
    			Iterator<String> i2 = element.members.iterator();
    			while (i2.hasNext()) {
    				String buddyURI = i2.next();
    				sendMessage(buddyURI);
    			}
    		}
    		
    	}
	}
	
	/**
	 * Elküldi a buddyURI azonosítójú felhasználónak az üzenetet.
	 * @param buddyURI
	 */
	private void sendMessage(String buddyURI) {
		try {
			System.out.println("sendMessage to: " + buddyURI);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * A Send gomb lenyomására hívódik meg.
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (SEND_COMMAND.equals(command)) {
			for (int i = 0; i < 100; i++) {
				if (selectedGroups[i].isSelected())
				sendMessageToGroup(selectedGroups[i].getText());
			}
		}				
	}
	
	/**
	 * Megvalósítja, hogy a szöveges üzenet maximum 100 karakter hosszú lehet.
	 */
	private class JTextAreaLimit extends PlainDocument {

		private static final long serialVersionUID = 1L;
		
		private int limit;
		  JTextAreaLimit(int limit) {
		    super();
		    this.limit = limit;
		  }

		  JTextAreaLimit(int limit, boolean upper) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		    if (str == null)
		      return;

		    if ((getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
		}
}
