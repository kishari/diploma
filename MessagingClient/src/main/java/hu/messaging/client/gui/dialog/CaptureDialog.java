package hu.messaging.client.gui.dialog;

import hu.messaging.Keys;
import hu.messaging.client.Resources;
import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.media.MimeHelper;
import hu.messaging.client.media.audio.AudioConverter;
import hu.messaging.client.media.video.VideoRecorder;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.protocol.DataSource;
import javax.media.protocol.SourceCloneable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.*;

public class CaptureDialog extends JFrame {

    private boolean stopCapture = false;
    private ByteArrayOutputStream tempCapturedContent;
    private byte[] capturedContent = null;
    private String capturedContentMimeType = "";
    
    private MyCamera videoCaptureFrame = null;

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
            	boolean isAudio = false;
            	boolean isPicture = false;
            	boolean isVideo = false;
            	switch(combobox.getSelectedIndex()) {
            	case 0: 
            		isAudio = true;
            		break;
            	case 1: 
            		isPicture = true;
            		break;
            	case 2: 
            		isVideo = true;
            		break;
            	}
                
                captureBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                okButton.setEnabled(true);
                stopCapture = true;
                
                if (isAudio) {
                	AudioConverter converter = new AudioConverter();                
                    try {
                    	capturedContent = converter.encodeStream(tempCapturedContent.toByteArray(), Resources.getMessagesDirectoryPath() + "captured.mp3");
                    	capturedContentMimeType = MimeHelper.getMIMETypeByExtension("mp3");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (isPicture) {
                	
                }
                if (isVideo) {
                	videoCaptureFrame.stopCapture();
                }                
            }
        });

        buttonPanel.add(stopBtn);

        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	boolean isAudio = false;
            	boolean isPicture = false;
            	boolean isVideo = false;
            	switch(combobox.getSelectedIndex()) {
            	case 0: 
            		isAudio = true;
            		break;
            	case 1: 
            		isPicture = true;
            		break;
            	case 2: 
            		isVideo = true;
            		break;
            	}
            	okButton.setEnabled(true);
                if (isAudio) {
                	playAudio();	
                }
                if (isPicture) {
                	
                }
                if (isVideo) {
                	
                }  

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
        			break;
        		case 1: 
        			isPicture = true;
        			break;
        		case 2: 
        			isVideo = true;
        			videoCaptureFrame = new MyCamera();
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
        try {
        	AudioFormat audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread(targetDataLine));
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void playAudio() {
        try {
            byte audioData[] = tempCapturedContent.toByteArray();
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioFormat audioFormat = getAudioFormat();
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream,
                    audioFormat,
                    audioData.length / audioFormat.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            Thread playThread = new Thread(new PlayThread(audioInputStream, sourceDataLine));
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private Map<String, javax.media.Format> getVideoAudioFormat() {
    	Map<String, javax.media.Format> formats = new HashMap<String, javax.media.Format>();
    	
    	javax.media.format.AudioFormat audioFormat = Resources.getVideoAudioFormat();
    	javax.media.format.VideoFormat videoFormat = 
    		new javax.media.format.VideoFormat(javax.media.format.VideoFormat.YUV);
    	formats.put(Keys.videoAudioFormat, audioFormat);
    	formats.put(Keys.videoVideoFormat, videoFormat);
    	
    	return formats;
    }
    
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 2;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }
    
    private void takeImage() {
    	System.out.println("take image");
    }
    
    private void captureVideo() {
    	videoCaptureFrame.startCapture();
    }

    class CaptureThread extends Thread {
        byte tempBuffer[] = new byte[10000];
        
        TargetDataLine targetDataLine = null;
        public CaptureThread(TargetDataLine targetDataLine) {
        	this.targetDataLine = targetDataLine;
        }
        
        public void run() {
        	tempCapturedContent = new ByteArrayOutputStream();
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                    	tempCapturedContent.write(tempBuffer, 0, cnt);
                    }
                }
                tempCapturedContent.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];

        private AudioInputStream audioInputStream = null;
        private SourceDataLine sourceDataLine;
        
        public PlayThread(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) {
        	this.audioInputStream = audioInputStream;
        	this.sourceDataLine = sourceDataLine;
        }
        
        public void run() {
            try {
                int cnt;
                while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }
                }
                sourceDataLine.drain();
                sourceDataLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
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
    
    private class MyCamera {
		private CaptureDeviceInfo cam;
		private MediaLocator locator;
		DataSource ds = null;
		private Player player;

		private JFrame frame= null;
		private VideoRecorder recorder = null;
		private JPanel videoPanel = null;
		
		public MyCamera() {
			try {
				recorder = new VideoRecorder();
				cam = recorder.getCaptureVideoDevice();

				
				
					// Create a Player for Media Located by MediaLocator
				player = Manager.createRealizedPlayer(cam.getLocator());
				
				System.out.println("Hello");
				if (player != null) {
					player.start();
					frame = new JFrame();
					frame.setPreferredSize(new Dimension(640, 480));
					frame.setTitle("Test Webcam");
					frame.setLayout(new BorderLayout());
					Component c = player.getVisualComponent();
					c.setSize(100, 100);
					
					videoPanel = new JPanel();
					videoPanel.setLayout(new BorderLayout());
					videoPanel.add(c, BorderLayout.CENTER);
					
					frame.add(videoPanel, BorderLayout.CENTER);
					System.out.println("++++++++++++++++++++++" + frame.getComponentCount());
					frame.pack();
					frame.setVisible(true);
				}

			} catch (Exception e) {
				System.out.println("exception van");
				e.printStackTrace();
			}
		}
		
		protected void startCapture() {
			try {
				player.close();
				//player.deallocate();
				
				recorder.init();
				ds = ((SourceCloneable)recorder.getVideoDataSource()).createClone();
				player = Manager.createRealizedPlayer(ds);
				player.start();
				
				Component c = player.getVisualComponent();
				videoPanel.remove(0);
				videoPanel.add(c, BorderLayout.CENTER);
				//frame.add(videoPanel, BorderLayout.CENTER);
				//frame.pack();
				//frame.setVisible(true);

				
				recorder.startCapture();
			}catch(IOException e) {
				e.printStackTrace();
			} catch (CannotRealizeException e) {
				e.printStackTrace();
			}
			catch (NoPlayerException e) {
				e.printStackTrace();
			}

		}
		
		protected void stopCapture() {
			recorder.stopCapture();
		}
	}

}

