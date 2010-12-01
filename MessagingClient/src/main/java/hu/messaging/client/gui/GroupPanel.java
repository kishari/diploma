package hu.messaging.client.gui;

import hu.messaging.client.model.GroupListStruct;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * A csoportok kezelése ezen a panelen valósul meg.
 * @author Harangozó Csaba
 *
 */
public class GroupPanel { //extends JPanel implements ActionListener {
	
	/*
	private static final long serialVersionUID = 571508598589333611L;
	
	private static String ADD_COMMAND = "add";
    private static String REMOVE_COMMAND = "remove";
    private static String CLEAR_COMMAND = "clear";
    

    private int numOfGroups = 0;
    
    private GroupTree treePanel;

    public GroupPanel() {
        super(new BorderLayout());

        treePanel = new GroupTree();

        JButton addButton = new JButton("Add");
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);

        JButton removeButton = new JButton("Remove");
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);

        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand(CLEAR_COMMAND);
        clearButton.addActionListener(this);

        treePanel.setPreferredSize(new Dimension(400, 510));
        add(treePanel, BorderLayout.CENTER);
        
        JPanel panel = new JPanel(new GridLayout(0,3));
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(clearButton);
	    add(panel, BorderLayout.SOUTH);
    }


    public void buildTree(LinkedList<GroupListStruct> groupList) {
    	buildTree(treePanel, groupList);
    }
    
    public void buildTree(GroupTree treePanel, LinkedList<GroupListStruct> groupList) {
 
        DefaultMutableTreeNode node;

        Iterator<GroupListStruct> it = groupList.iterator();
        while (it.hasNext()) {
           GroupListStruct element = (GroupListStruct) it.next();
           String gName = element.groupName;
           node = treePanel.addObject(null, gName);
           numOfGroups++;
           
           Iterator<String> it2 = element.members.iterator();
           while (it2.hasNext()) {
        	   System.out.println("felveszi a tagokat is");
               String mName = (String) it2.next();
               treePanel.addObject(node, mName);
           }
        }
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int depth = 0;
        if (ADD_COMMAND.equals(command)) {
           	
        	//ha több csoport lenne, mint amennyi a megengedett maximum (100), akkor nem hozhat létre.
        	
        	//if (!(MainWindow.instance.numberOfGroupsMax > numOfGroups)) {
        	if (!(100 > numOfGroups)) {
        		JOptionPane.showMessageDialog(MainWindow.instance,
        			    "Túllépte a létrehozható csoportok maximális számát("
        				+ Integer.toString(100) + ")",
        			    "Figyelem!",
        			    JOptionPane.WARNING_MESSAGE);
        				return;        		
        	}
        	numOfGroups++;
        	depth = treePanel.getPathDepth();
           	        	
        	if (depth == 1) {
        		String s = (String)JOptionPane.showInputDialog(MainWindow.instance, "Új csoport neve:",
        				                                       "Új csoport felvétele",
        				                                       JOptionPane.PLAIN_MESSAGE);
        		if (s != null) {
        			
        			MainWindow.instance.getClient().getGroupHelper().addGroup(s);
        			//MainWindow.instance.getClient().addGroup(s);
        			MainWindow.instance.updateMessagePanel();
        			treePanel.addObject(s);
        		}        		
        	}
        	if (depth == 2) {
        		
        		String groupName = treePanel.getPath().getLastPathComponent().toString();
        		String s = (String)JOptionPane.showInputDialog(MainWindow.instance, "Új csoporttag URI-ja:",
                        									   "Új csoporttag felvétele",
                        									   	JOptionPane.PLAIN_MESSAGE);
        		if (s != null) {
        			MainWindow.instance.getClient().getGroupHelper().addBuddyToGroup(s, groupName);
        			MainWindow.instance.updateMessagePanel();
        			treePanel.addObject(s);
        		}
        	}
        	
        } else if (REMOVE_COMMAND.equals(command)) {
        	
        	depth = treePanel.getPathDepth();
        	
        	String msg = new String();
        	
        	switch (depth) {
        		case 2: msg = "csoportot";
        				break;
        		
        		case 3: msg = "csoporttagot";
        				break;
        		
        		default: JOptionPane.showMessageDialog(MainWindow.instance,
        			    "A gyökér csoport nem törölhetõ!",
        			    "Figyelem!",
        			    JOptionPane.WARNING_MESSAGE);
        				return;        		
        	}
        	
        	int answer = JOptionPane.showConfirmDialog(MainWindow.instance, "Biztos törlöd a kiválasztott "+ msg + "?",
        												"Figyelem!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        	if(answer == JOptionPane.YES_OPTION) {
        		String groupName = new String();
        		String memberName = new String();
        		
        		TreePath path = treePanel.getPath();
        		switch (path.getPathCount()) {
        		
        			case 2: groupName = path.getLastPathComponent().toString();
        					MainWindow.instance.getClient().getGroupHelper().delGroup(groupName);
        					numOfGroups--;
        					break;
        			
        			case 3: groupName = path.getParentPath().getLastPathComponent().toString();
        					memberName = path.getLastPathComponent().toString();
        					MainWindow.instance.getClient().getGroupHelper().delBuddyFromGroup(memberName, groupName);
        					break;
        		}
        		
        		MainWindow.instance.updateMessagePanel();
        		treePanel.removeCurrentNode();
        		
        	}
        	
        } else if (CLEAR_COMMAND.equals(command)) {
        	
        	int answer = JOptionPane.showConfirmDialog(MainWindow.instance, "Biztos törölsz minden csoportot?",
														"Figyelem!", JOptionPane.YES_NO_OPTION, 
														JOptionPane.QUESTION_MESSAGE);
        	if(answer == JOptionPane.YES_OPTION) {
        		treePanel.clear();
        		MainWindow.instance.getClient().getGroupHelper().delAllGroupsFromPGM();
        		numOfGroups = 0;
        	}            
        }
    }
    */
}

