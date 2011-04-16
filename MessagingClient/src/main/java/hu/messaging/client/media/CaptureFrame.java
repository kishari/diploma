package hu.messaging.client.media;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class CaptureFrame extends JFrame {

	  boolean stopCapture = false;
	  ByteArrayOutputStream byteArrayOutputStream;
	  AudioFormat audioFormat;
	  TargetDataLine targetDataLine;
	  AudioInputStream audioInputStream;
	  SourceDataLine sourceDataLine;

	  public CaptureFrame(){
	    final JButton captureBtn = new JButton("Capture");
	    final JButton stopBtn = new JButton("Stop");
	    final JButton playBtn = new JButton("Playback");

	    captureBtn.setEnabled(true);
	    stopBtn.setEnabled(false);
	    playBtn.setEnabled(false);

	    captureBtn.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e){
	        	captureBtn.setEnabled(false);
	        	stopBtn.setEnabled(true);
	        	playBtn.setEnabled(false);
	        	captureAudio();
	        }
	    });
	    
	    getContentPane().add(captureBtn);

	    stopBtn.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	captureBtn.setEnabled(true);
	        	stopBtn.setEnabled(false);
	        	playBtn.setEnabled(true);
	        	stopCapture = true;
	        }
	    });
	    
	    getContentPane().add(stopBtn);

	    playBtn.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	playAudio();
	        }
	    });
	    
	    getContentPane().add(playBtn);

	    getContentPane().setLayout(new FlowLayout());
	    setTitle("Capture/Playback Demo");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(250,70);
	    setVisible(true);
	  }

	  private void captureAudio(){
		  try{
			  audioFormat = getAudioFormat();
			  DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			  targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
			  targetDataLine.open(audioFormat);
			  targetDataLine.start();

			  Thread captureThread = new Thread(new CaptureThread());
			  captureThread.start();
		  } catch (Exception e) {
			  System.out.println(e);
			  System.exit(0);
		  }
	  }

	  private void playAudio() {
	    try{
	      byte audioData[] = byteArrayOutputStream.toByteArray();
	      InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
	      AudioFormat audioFormat = getAudioFormat();
	      audioInputStream = new AudioInputStream(byteArrayInputStream, 
	    		  								  audioFormat, 
	    		  								  audioData.length/audioFormat.getFrameSize());
	      DataLine.Info dataLineInfo =  new DataLine.Info(SourceDataLine.class, audioFormat);
	      sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
	      sourceDataLine.open(audioFormat);
	      sourceDataLine.start();

	      Thread playThread = new Thread(new PlayThread());
	      playThread.start();
	    } catch (Exception e) {
	      System.out.println(e);
	      System.exit(0);
	    }
	  }

	  private AudioFormat getAudioFormat(){
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

	class CaptureThread extends Thread{
	  byte tempBuffer[] = new byte[10000];
	  public void run(){
	    byteArrayOutputStream = new ByteArrayOutputStream();
	    stopCapture = false;
	    try{
	      while(!stopCapture){
	          int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
	        if(cnt > 0){
	          byteArrayOutputStream.write(tempBuffer, 0, cnt);
	        }
	      }
	      byteArrayOutputStream.close();
	    }catch (Exception e) {
	      System.out.println(e);
	      System.exit(0);
	    }
	  }
	}
	
	class PlayThread extends Thread{
	  byte tempBuffer[] = new byte[10000];

	  public void run(){
	    try{
	      int cnt;
	      while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
	        if(cnt > 0){
	          sourceDataLine.write(tempBuffer, 0, cnt);
	        }
	      }
	      sourceDataLine.drain();
	      sourceDataLine.close();
	    }catch (Exception e) {
	      System.out.println(e);
	      System.exit(0);
	    }
	  }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CaptureFrame();
	}

}
