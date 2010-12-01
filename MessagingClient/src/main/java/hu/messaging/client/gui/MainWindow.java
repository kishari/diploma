package hu.messaging.client.gui;

import hu.messaging.client.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainWindow { //extends JFrame implements Runnable {
	/*
	private static final long serialVersionUID = 1L;
	private Client client = null;
	private TextArea logArea = new TextArea();
	private boolean done = false;
	
	public static MainWindow instance = null;
	public GroupPanel groupPanel;
	public MessageSendingPanel messagePanel;
	
	public MainWindow() {
		groupPanel = new GroupPanel();
		messagePanel = new MessageSendingPanel();
		instance = this;
	}
	
	public void run() {
		createGui();
		setVisible(true);
		client = new Client(logArea);
		client.init();
		
		groupPanel.buildTree(client.getGroupHelper().getGroupList());
		messagePanel.updateGroupList(client.getGroupHelper().getGroupList());
		
	    while (!done) {
	           try {
	               Thread.sleep(10);
	           }
	           catch (InterruptedException e1) {
	        	   e1.printStackTrace();
	           }
	       }
	    
	    client.sendBye();
	    client.dispose();
	    dispose();
	}
	
	private void createGui() {			
		setLayout(new BorderLayout());
        
        setTitle("Messaging Client");
                               
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel groupCard = new JPanel();
        groupCard.add(groupPanel);
        
        
        JPanel messageCard = new JPanel();
        messageCard.add(messagePanel);
        tabbedPane.addTab("groups", groupCard);
        tabbedPane.addTab("messages", messageCard);

        add(tabbedPane);
        
        setPreferredSize(new Dimension(400, 600));
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	done = true;
            }
        });
        setResizable(false);
    
        pack();        
 
	}
	
	public Client getClient() {
		return client;
	}


	public void updateMessagePanel() {
        this.messagePanel.updateGroupList(client.getGroupHelper().getGroupList());
	}
	

	public static void main(String[] args) throws Exception {
		MainWindow mainWin = new MainWindow();
		mainWin.run();
	}
	*/
}
