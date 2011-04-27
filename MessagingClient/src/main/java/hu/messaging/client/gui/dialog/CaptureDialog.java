package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.media.MimeHelper;
import hu.messaging.client.media.audio.AudioConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class CaptureDialog extends JFrame {

    private boolean stopCapture = false;
    private ByteArrayOutputStream tempCapturedContent;
    private byte[] capturedContent = null;
    private String capturedContentMimeType = "";

    public CaptureDialog() {
        final JButton captureBtn = new JButton(Resources.resources.get("button.capture"));
        final JButton stopBtn = new JButton(Resources.resources.get("button.stop"));
        final JButton playBtn = new JButton(Resources.resources.get("button.play"));
        final JButton okButton = new JButton(Resources.resources.get("button.ok"));

        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        okButton.setEnabled(false);

        setIconImage(ImageUtil.createImage("record_24x24.png"));
        
        captureBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                captureBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                playBtn.setEnabled(false);
                captureAudio();
            }
        });

        getContentPane().add(captureBtn);

        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                captureBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                okButton.setEnabled(true);
                stopCapture = true;
                AudioConverter converter = new AudioConverter();                
                try {
                	capturedContent = converter.encodeStream(tempCapturedContent.toByteArray(), Resources.getMessagesDirectoryPath() + "captured.mp3");
                	capturedContentMimeType = MimeHelper.getMIMETypeByExtension("mp3");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        getContentPane().add(stopBtn);

        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	okButton.setEnabled(true);
                playAudio();
            }
        });                

        getContentPane().add(playBtn);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {            	
                CaptureDialog.this.setVisible(false);
            }
        }); 
        
        getContentPane().add(okButton);
        
        getContentPane().setLayout(new FlowLayout());
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

}

