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
		
	    JPanel subjectTextFieldPanel = new JPanel(new BorderLayout());
	    JPanel radioPanel = new JPanel(new GridLayout(0, 2, 10, 10));
	    JPanel centerPanel = new JPanel(new BorderLayout());
	    
	    centerPanel.add(BorderLayout.NORTH, radioPanel);
	    centerPanel.add(BorderLayout.SOUTH, subjectTextFieldPanel);
	    
	    fileChooser = new JFileChooser();
	    fileChooser.setMultiSelectionEnabled(false);
	    
	    final JButton openFileButton = new JButton("Open file");
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
	    
	    openFileButton.setEnabled(false);
	    
	    final JButton captureAudioButton = new JButton("Create audio message");
	    captureAudioButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent event) {
	      
	      }
	    });
	    
	    getFromCaptureDeviceButton().addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		openFileButton.setEnabled(false);
	    		captureAudioButton.setEnabled(true);
	    	}
	    });
	    
	    getFromFileButton().addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		openFileButton.setEnabled(true);
	    		captureAudioButton.setEnabled(false);
	    	}
	    });	    	      	  	    	   
	    
	    radioPanel.add(getFromCaptureDeviceButton());
	    radioPanel.add(captureAudioButton);
	    radioPanel.add(getFromFileButton());
	    radioPanel.add(openFileButton);
	    
	    this.setSubjectTextField(new JTextField("Subject", 20));
	    this.getSubjectTextField().selectAll();
	    subjectTextFieldPanel.add(this.getSubjectTextField());
	    	    
	    return centerPanel;
	  }

}
