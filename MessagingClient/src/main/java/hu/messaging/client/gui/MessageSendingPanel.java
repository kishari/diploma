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
 * Az �zenetek k�ld�se ezen a panelen val�sul meg.
 * @author Harangoz� Csaba
 *
 */
public class MessageSendingPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1296253198192003019L;
	
	private static String INPUT_FILE_PATH = "c:\\<csatoland� f�jl el�r�si �t>";
	private static String SEND_COMMAND = "send";
	
	/**
	 * A csatoland� f�jl el�r�si �tj�t tartalmaz� sz�vegmez�.
	 */
	private JTextField fileField;
	
	/**
	 * Az �zenet sz�veg�t tartalmaz� r�sz.
	 */
	private JTextArea msgArea;
	
	/**
	 * A TextArea-t tartalmaz� komponens.
	 */
	private JScrollPane scrollPane;
	
	/**
	 * A c�mzett csoportok kiv�laszt�s�ra haszn�lt CheckBoxok.
	 */
	private JCheckBox[] selectedGroups;
	
	/**
	 * A csoportok maxim�lis sz�m�t t�rol� lok�lis v�ltoz�, �rt�ket a Main ugyanezen funkci�t szolg�l�
	 * v�ltoz�j�t�l kapja.
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
        panel.add(new JLabel("�zenet: (maximum 100 karakter)"), BorderLayout.NORTH);
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
	 * friss�ti a kiv�laszthat� csoportokat. Ez az�rt kell, hogy az �zenetk�ld� panelon mindig 
	 * csak az aktu�lis csoportokat lehessek kiv�lasztani c�mzettnek.
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
	 * A groupName nev� csoport tagjainak elk�ldi az �zenetet.
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
	 * Elk�ldi a buddyURI azonos�t�j� felhaszn�l�nak az �zenetet.
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
	 * A Send gomb lenyom�s�ra h�v�dik meg.
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
	 * Megval�s�tja, hogy a sz�veges �zenet maximum 100 karakter hossz� lehet.
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
