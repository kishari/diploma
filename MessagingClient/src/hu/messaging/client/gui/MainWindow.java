package hu.messaging.client.gui;

import hu.messaging.client.*;
import hu.messaging.msrp.MSRPMessage;

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

public class MainWindow implements Runnable {
	
	private Frame mainFrame;
	private Client client = null;
	private TextArea logArea = new TextArea();
	private boolean done = false;
	
	public void run() {
		mainFrame = createGui();
		mainFrame.setVisible(true);
		
		client = new Client(logArea);
		client.initICP();
		
	    while (!done) {
	           try {
	               Thread.sleep(10);
	           }
	           catch (InterruptedException e1) {
	        	   e1.printStackTrace();
	           }
	       }
	    
	    //mainFrame.setVisible(false);
	    client.sendBye();
	    client.disposeICP();
	    mainFrame.dispose();
	}
	
	private Frame createGui() {
	   final Frame frame = new Frame();
       frame.setLayout(new GridBagLayout());
     
       Button messageButton = new Button("sendTestMessage");
       Button inviteButton = new Button("Invite");
       Button clearButton = new Button("Clear");
       
       messageButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
        	   MSRPMessage m = new MSRPMessage();
        	   m.createTestMessage();
       			client.sendData(m.toString().getBytes(), "sip:weblogic103@192.168.1.103");
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
       frame.add(mainPanel);
     
       frame.setSize(new Dimension(500, 500));
       frame.addWindowListener(new WindowAdapter()
       {
           public void windowClosing(WindowEvent e) {
               done = true;
           }
       });
       
		return frame;
	}
	
	public static void main(String[] args) throws Exception {
		MainWindow mainWin = new MainWindow();
		mainWin.run();
	}

}
