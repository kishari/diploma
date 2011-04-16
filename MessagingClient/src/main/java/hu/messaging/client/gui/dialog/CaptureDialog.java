package hu.messaging.client.gui.dialog;

import hu.messaging.client.media.audio.AudioConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class CaptureDialog extends JFrame {

    private boolean stopCapture = false;
    private ByteArrayOutputStream baos;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    public CaptureDialog() {
        final JButton captureBtn = new JButton("Capture");
        final JButton stopBtn = new JButton("Stop");
        final JButton playBtn = new JButton("Playback");

        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);

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
                stopCapture = true;
                AudioConverter converter = new AudioConverter();
                byte[] convertedData = null;
                try {
                	convertedData = converter.encodeStream(baos.toByteArray(), "c:\\diploma\\testing\\Mp3.mp3");
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                
                File f = new File("c:\\diploma\\testing\\Mp3_2.mp3");
                try {
                	FileOutputStream fos = new FileOutputStream(f);
                	fos.write(convertedData);
                	fos.flush();
                	fos.close();
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                
            }
        });

        getContentPane().add(stopBtn);

        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playAudio();
            }
        });

        getContentPane().add(playBtn);

        getContentPane().setLayout(new FlowLayout());
        setTitle("Capture window");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 100);
        setVisible(true);
    }

    private void captureAudio() {
        try {
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
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
        try {
            byte audioData[] = baos.toByteArray();
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioFormat audioFormat = getAudioFormat();
            audioInputStream = new AudioInputStream(byteArrayInputStream,
                    audioFormat,
                    audioData.length / audioFormat.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            Thread playThread = new Thread(new PlayThread());
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

        public void run() {
            baos = new ByteArrayOutputStream();
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        baos.write(tempBuffer, 0, cnt);
                    }
                }
                baos.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];

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

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new CaptureDialog();
    }

}

