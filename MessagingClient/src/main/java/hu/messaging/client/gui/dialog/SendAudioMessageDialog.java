package hu.messaging.client.gui.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import org.apache.commons.io.FileUtils;

import hu.messaging.client.gui.controller.ICPController;

public class SendAudioMessageDialog extends SendMessageDialog {
	
	private JFileChooser fileChooser;	

	public SendAudioMessageDialog( ICPController icpController) {
		super(icpController);
	}
	
	protected JComponent createCenterPanel()  {
		
	    JPanel panel = new JPanel(new BorderLayout());
	    JPanel subjectTextFieldPanel = new JPanel(new BorderLayout());
	    JPanel radioPanel = new JPanel(new GridLayout(1, 0));
	    JPanel centerPanel = new JPanel(new BorderLayout());
	    
	    final JPanel fromFilePanel = new JPanel(new BorderLayout());
	    final JPanel fromCaptureDevicePanel = new JPanel(new BorderLayout());
	    
	    centerPanel.add(BorderLayout.NORTH, fromFilePanel);
	    centerPanel.add(BorderLayout.SOUTH, fromCaptureDevicePanel);
	    
	    panel.add(BorderLayout.NORTH, radioPanel);
	    panel.add(BorderLayout.CENTER, centerPanel);
	    panel.add(BorderLayout.SOUTH, subjectTextFieldPanel);
	    
	    fileChooser = new JFileChooser();
	    fileChooser.setMultiSelectionEnabled(false);
	    
	    this.getFromCaptureDeviceButton().addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		fromCaptureDevicePanel.setVisible(true);
	    		fromFilePanel.setVisible(false);
	    	}
	    });
	    
	    this.getFromFileButton().addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		fromCaptureDevicePanel.setVisible(false);
	    		fromFilePanel.setVisible(true);
	    	}
	    });
	    
	    fromCaptureDevicePanel.add(new JTextField("Capture", 20)); 
	    fromFilePanel.add(new JTextField("File", 20));
	    
	    fromFilePanel.setVisible(false);
	    fromCaptureDevicePanel.setVisible(true);
	    
	    radioPanel.add(this.getFromCaptureDeviceButton());
	    radioPanel.add(this.getFromFileButton());
/*	    
	    JButton openFileButton = new JButton("Open file");
	    openFileButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent event) {
	    	  int retVal = fileChooser.showOpenDialog(SendAudioMessageDialog.this);
	    	  if (retVal == JFileChooser.APPROVE_OPTION) {
	    		  File selectedFile = fileChooser.getSelectedFile();
	    		  try {
	    			  setMessageContent(FileUtils.readFileToByteArray(selectedFile));
	    		  }
	    		  catch(IOException e) { }	    		  
	    		  setMessageExtension(getFileExtension(selectedFile));    		  
	    	  }
	      }
	    });
	    buttonsPanel.add(openFileButton);
*/	    
	    this.setSubjectTextField(new JTextField("Subject", 20));
	    this.getSubjectTextField().selectAll();
	    subjectTextFieldPanel.add(this.getSubjectTextField());
	    
	    

	    return panel;
	  }

}
