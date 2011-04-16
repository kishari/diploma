package hu.messaging.client.media.audio;

import com.sun.jna.Platform;
import net.sf.lamejb.*;

import java.io.*;

public class AudioConverter {


    public byte[] encodeStream(byte[] wav,String mp3File) throws Exception {
    	LamejbCodecFactory codecFactory;
    	if ( Platform.isWindows() )
    		codecFactory = new BladeCodecFactory();
    	else
    		codecFactory = new LameCodecFactory();

    	LamejbCodec codec = codecFactory.createCodec();

    	LamejbConfig config = new LamejbConfig(44100, 128, LamejbConfig.MpegMode.JOINT_STEREO, true);

    	InputStream is = new BufferedInputStream(new ByteArrayInputStream(wav));
    	OutputStream os = new BufferedOutputStream(new FileOutputStream(mp3File));

    	codec.encodeStream(is, os, config);

    	os.flush();
    	os.close();
    	
    	File mp3 = new File(mp3File);
    	FileInputStream is2 = null;
    	if (mp3.exists() && mp3.isFile()) {
    		is2 = new FileInputStream(mp3);
    	}
    	
    	long length = mp3.length();
    	System.out.println("length: " + length);
    	
    	byte[] bytes = new byte[(int)length];
        
    	int l = is2.read(bytes);
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
//        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//            offset += numRead;
//        }
    
        // Ensure all the bytes have been read in
        if (l < length) {
//            throw new IOException("Could not completely read file "+mp3.getName());
        	System.out.println("haha");
        }
    
        // Close the input stream and return bytes
        is.close();
    	
    	
    	return bytes;
    }
}
