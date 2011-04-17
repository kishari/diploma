package hu.messaging.client.media.audio;

import java.io.*;
import hu.messaging.client.media.audio.Player;

public class AudioPlayer implements Player {
	
    private javazoom.jl.player.Player mp3Player;
    private static boolean isPlaying = false;

    private void close() { 
    	if (mp3Player != null) 
    		mp3Player.close();
    }

    public void playMp3(byte[] media) {
        try {
            InputStream is = new ByteArrayInputStream(media);
            BufferedInputStream bis = new BufferedInputStream(is);
            mp3Player = new javazoom.jl.player.Player(bis);
        }
        catch (Exception e) {         
            System.out.println(e);
        }
        new Thread() {
            public void run() {
                try {            
                	System.out.println("run");
                	mp3Player.play();
                	mp3Player.close();
                	isPlaying = false;
                }
                catch (Exception e) { 
                	System.out.println(e); 
                }
            }
        }.start();        
    }

    @Override
	public void play(byte[] media) {
    	isPlaying = true;    	
    	playMp3(media);
	}

	@Override
	public void stop() {
		close();
	}

    // test client
    public static void main(String[] args) {
        String filename = "c:\\diploma\\testing\\clientContentFile.mp3";
        File file = new File(filename);
        byte fileContent[] = new byte[(int)file.length()];
        try
        {          
          FileInputStream fin = new FileInputStream(file);
          fin.read(fileContent);         
        }
        catch(FileNotFoundException e)
        {
          System.out.println("File not found" + e);
        }
        catch(IOException ioe)
        {
          System.out.println("Exception while reading the file " + ioe); 
        }
        
        //BufferedInputStream bis = new BufferedInputStream(is);
        AudioPlayer mp3 = new AudioPlayer();
        mp3.play(fileContent);
       
        while(isPlaying) {
        	try {
        		Thread.sleep(10);
        	}
        	catch(Exception e) {}        	
        }
        mp3.close();

    }
    


}
