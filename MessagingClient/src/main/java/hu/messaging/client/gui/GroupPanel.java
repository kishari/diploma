package hu.messaging.client.gui;

import hu.messaging.client.model.GroupListStruct;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * A csoportok kezel�se ezen a panelen val�sul meg.
 * @author Harangoz� Csaba
 *
 */
public class GroupPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 571508598589333611L;
	
	private static String ADD_COMMAND = "add";
    private static String REMOVE_COMMAND = "remove";
    private static String CLEAR_COMMAND = "clear";
    
    /**
     * A felhaszn�l� csoportjainak sz�ma.
     */
    private int numOfGroups = 0;
    
    /**
     * A csoportfa kezel�s�t v�gz� oszt�ly p�ld�nya.
     */
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

    /**
     * fel�p�ti a csoportf�t
     */
    public void buildTree() {
    	buildTree(treePanel);
    }
    
    /**
     * Inicializ�lja a csoportf�t.
     * @param treePanel
     */
    public void buildTree(GroupTree treePanel) {
 
        DefaultMutableTreeNode node;

        Iterator<GroupListStruct> it = MainWindow.instance.getGroupList().iterator();
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

    /**
     * Gombok lenyom�s�t figyel� esem�nykezel�.
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int depth = 0;
        if (ADD_COMMAND.equals(command)) {
           	
        	//ha t�bb csoport lenne, mint amennyi a megengedett maximum (100), akkor nem hozhat l�tre.
        	
        	if (!(Main.instance.numberOfGroupsMax > numOfGroups)) {
        		JOptionPane.showMessageDialog(Main.instance,
        			    "T�ll�pte a l�trehozhat� csoportok maxim�lis sz�m�t("
        				+ Integer.toString(Main.instance.numberOfGroupsMax) + ")",
        			    "Figyelem!",
        			    JOptionPane.WARNING_MESSAGE);
        				return;        		
        	}
        	numOfGroups++;
        	depth = treePanel.getPathDepth();
           	        	
        	if (depth == 1) {
        		String s = (String)JOptionPane.showInputDialog(Main.instance, "�j csoport neve:",
        				                                       "�j csoport felv�tele",
        				                                       JOptionPane.PLAIN_MESSAGE);
        		if (s != null) {
        			Main.instance.addGroup(s);
        			Main.instance.updateMessagePanel();
        			treePanel.addObject(s);
        		}        		
        	}
        	if (depth == 2) {
        		
        		String groupName = treePanel.getPath().getLastPathComponent().toString();
        		String s = (String)JOptionPane.showInputDialog(Main.instance, "�j csoporttag URI-ja:",
                        									   "�j csoporttag felv�tele",
                        									   	JOptionPane.PLAIN_MESSAGE);
        		if (s != null) {
        			Main.instance.addBuddyToGroup(s, groupName);
        			Main.instance.updateMessagePanel();
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
        		
        		default: JOptionPane.showMessageDialog(Main.instance,
        			    "A gy�k�r csoport nem t�r�lhet�!",
        			    "Figyelem!",
        			    JOptionPane.WARNING_MESSAGE);
        				return;        		
        	}
        	
        	int answer = JOptionPane.showConfirmDialog(Main.instance, "Biztos t�rl�d a kiv�lasztott "+ msg + "?",
        												"Figyelem!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        	if(answer == JOptionPane.YES_OPTION) {
        		String groupName = new String();
        		String memberName = new String();
        		
        		TreePath path = treePanel.getPath();
        		switch (path.getPathCount()) {
        		
        			case 2: groupName = path.getLastPathComponent().toString();
        					Main.instance.delGroup(groupName);
        					numOfGroups--;
        					break;
        			
        			case 3: groupName = path.getParentPath().getLastPathComponent().toString();
        					memberName = path.getLastPathComponent().toString();
        					Main.instance.delBuddyFromGroup(memberName, groupName);
        					break;
        		}
        		
        		Main.instance.updateMessagePanel();
        		treePanel.removeCurrentNode();
        		
        	}
        	
        } else if (CLEAR_COMMAND.equals(command)) {
        	
        	int answer = JOptionPane.showConfirmDialog(Main.instance, "Biztos t�r�lsz minden csoportot?",
														"Figyelem!", JOptionPane.YES_NO_OPTION, 
														JOptionPane.QUESTION_MESSAGE);
        	if(answer == JOptionPane.YES_OPTION) {
        		treePanel.clear();
        		Main.instance.delAllGroupsFromPGM();
        		numOfGroups = 0;
        	}            
        }
    }
}

