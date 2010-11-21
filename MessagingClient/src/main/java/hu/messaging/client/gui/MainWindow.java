package hu.messaging.client.gui;

import hu.messaging.client.*;
import hu.messaging.client.model.GroupListStruct;
import hu.messaging.service.MessagingService;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FileUtils;

public class MainWindow extends JFrame implements Runnable {
	
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
		
		client = new Client(logArea);
		client.init();
		
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
 
        /*
       Button messageButton = new Button("sendTestMessage");
       Button inviteButton = new Button("Invite");
       Button clearButton = new Button("Clear");
       
       messageButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {        	          	   
        	   String tmp = "";
        	   try {
        		  tmp = FileUtils.readFileToString(new File("C:\\diploma\\MessagingClient\\testData\\input.txt"));
        		  System.out.println(tmp);
        		  System.out.println(tmp.length());
        	   }
        	   catch(IOException exc) { }
        	   
        	   //String testData = StringEscapeUtils.escapeJava(tmp);
        	   String testData = tmp;
        		client.sendMessage(testData.getBytes(), MessagingService.serverURI);
        	  
        	   //client.sendData(m.toString().getBytes(), MessagingService.serverURI);
           }
       });
       
       inviteButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
       			client.sendInvite();
           }
       });
       
       clearButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
        	   logArea.setText("");
           }
       });
       
       Panel mainPanel = new Panel();
       mainPanel.setLayout(new BorderLayout());
       Panel buttonPanel = new Panel();
       buttonPanel.add(messageButton);
       buttonPanel.add(inviteButton);
       buttonPanel.add(clearButton);
       mainPanel.add(buttonPanel, BorderLayout.NORTH);
       mainPanel.add(logArea, BorderLayout.CENTER);
       */
	}
	
	public static void main(String[] args) throws Exception {
		MainWindow mainWin = new MainWindow();
		mainWin.run();
	}

}
