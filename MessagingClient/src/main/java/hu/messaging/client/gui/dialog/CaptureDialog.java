package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.util.ImageUtil;

import hu.messaging.client.media.audio.AudioRecorder;
import hu.messaging.client.media.video.VideoRecorder;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class CaptureDialog extends JFrame implements Observer {

	private byte[] capturedContent = null;
    private String capturedContentMimeType = "";
    
    private AudioRecorder audioRecorder = null;
    private VideoRecorder videoRecorder = null;

    public CaptureDialog() {
    	
        final JButton captureBtn = new JButton(Resources.resources.get("button.capture"));
        final JButton stopBtn = new JButton(Resources.resources.get("button.stop"));
        final JButton playBtn = new JButton(Resources.resources.get("button.play"));
        final JButton okButton = new JButton(Resources.resources.get("button.ok"));
        String[] comboElement = {"Audio", "Picture", "Video"};
        final JComboBox combobox = new JComboBox(comboElement);
        combobox.setSelectedIndex(0);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        okButton.setEnabled(false);

        setIconImage(ImageUtil.createImage("record_24x24.png"));
        
        captureBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	switch(combobox.getSelectedIndex()) {
            	case 0: 
            		captureAudio();
            		break;
            	case 1: 
            		takeImage();
            		break;
            	case 2: 
            		captureVideo();
            		break;
            	}
            	
                captureBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                playBtn.setEnabled(false);
                okButton.setEnabled(false);
                
            }
        });

        buttonPanel.add(captureBtn);

        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	switch(combobox.getSelectedIndex()) {
            	case 0: 
            		audioRecorder.stopCapture();
            		break;
            	case 1: 
            		
            		break;
            	case 2: 
            		videoRecorder.stopCapture();
            		break;
            	}
                
                captureBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                okButton.setEnabled(true);
                            
            }
        });

        buttonPanel.add(stopBtn);

        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	switch(combobox.getSelectedIndex()) {
            	case 0: 
            		audioRecorder.playAudio();
            		break;
            	case 1: 
            		
            		break;
            	case 2: 
            		
            		break;
            	}
            	okButton.setEnabled(true);
            }
        });                

        buttonPanel.add(playBtn);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {            	
                CaptureDialog.this.setVisible(false);
            }
        }); 
        
        buttonPanel.add(okButton);
        
        combobox.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		int selected = combobox.getSelectedIndex();
            	boolean isAudio = false;
            	boolean isPicture = false;
            	boolean isVideo = false;
        		switch (selected) {
        		case 0:
        			isAudio = true;
        			audioRecorder = new AudioRecorder(CaptureDialog.this);
        			if (videoRecorder != null) {
        				videoRecorder.dispose();
        			}
        			break;
        		case 1: 
        			isPicture = true;
        			if (videoRecorder != null) {
        				videoRecorder.dispose();
        			}
        			break;
        		case 2: 
        			isVideo = true;
        			videoRecorder = new VideoRecorder(CaptureDialog.this);
        			break;
        		}
        		if (isAudio || isVideo) {
        			captureBtn.setText(Resources.resources.get("button.capture"));
        			playBtn.setText(Resources.resources.get("button.play"));        			
        		}
        		else {
        			captureBtn.setText(Resources.resources.get("button.take.image"));
        			playBtn.setText(Resources.resources.get("button.show"));      
        		}
        		stopBtn.setVisible(!isPicture);
    			stopBtn.setText(Resources.resources.get("button.stop"));
    	        okButton.setText(Resources.resources.get("button.ok"));
        	}
        });
        
        JPanel comboboxPanel = new JPanel();
        comboboxPanel.setLayout(new FlowLayout());
        comboboxPanel.add(combobox);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(comboboxPanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
        setTitle(Resources.resources.get("capture.window.title"));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(300, 100);
        setVisible(false);
    }

    private void captureAudio() {
        audioRecorder.startCapture();
    }

    private void takeImage() {
    	System.out.println("take image");
    }
    
    private void captureVideo() {
    	videoRecorder.startCapture();
    }

	public void update(Observable o, Object obj) {
		if (o.toString().contains(AudioRecorder.class.getSimpleName())) {
			byte[] capturedContent = null;
			if (obj != null) {
				capturedContent = (byte[])obj;
			}
			
			this.capturedContent = capturedContent;
			this.capturedContentMimeType = "audio/mpeg";
		}
		
		if (o.toString().contains(VideoRecorder.class.getSimpleName())) {
			if (o.toString().contains(VideoRecorder.class.getSimpleName())) {
				byte[] capturedContent = null;
				if (obj != null) {
					capturedContent = (byte[])obj;
				}
				
				this.capturedContent = capturedContent;
				this.capturedContentMimeType = "video/x-msvideo";	
				
			}
		}
	}

	public byte[] getCapturedContent() {
		return capturedContent;
	}

	public String getCapturedContentMimeType() {
		return capturedContentMimeType;
	}
    
    public static void main(String[] args) {
    	CaptureDialog f = new CaptureDialog();
    	f.setVisible(true);
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

