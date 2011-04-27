package hu.messaging.client.media.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.dialog.CaptureDialog;

public class AudioRecorder extends java.util.Observable{

	private ByteArrayOutputStream tempCapturedContent;
	private boolean stopCapture = false;
	private AudioConverter converter = new AudioConverter(); 
	
	public AudioRecorder(CaptureDialog cDialog) {
		this.addObserver(cDialog);
	}
	
	public void startCapture() {
		stopCapture = false;
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
	
	public void stopCapture() {
		stopCapture = true;
		byte[] capturedContent = null;
		try {
        	capturedContent = converter.encodeStream(tempCapturedContent.toByteArray(), Resources.getMessagesDirectoryPath() + "captured.mp3");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        this.setChanged();
        this.notifyObservers(capturedContent);
	}
	
	public AudioFormat getAudioFormat() {
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
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
	
	public ByteArrayOutputStream getTempCapturedContent() {
		return tempCapturedContent;
	}
	
	public void playAudio() {
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

            Thread playThread = new Thread(new PlayAudioThread(audioInputStream, sourceDataLine));
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
	
	private class PlayAudioThread extends Thread {
        byte tempBuffer[] = new byte[10000];

        private AudioInputStream audioInputStream = null;
        private SourceDataLine sourceDataLine;
        
        public PlayAudioThread(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) {
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
	
	private class CaptureThread extends Thread {
        byte tempBuffer[] = new byte[10000];
        
        private TargetDataLine targetDataLine = null;
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

}
