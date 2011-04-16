package hu.messaging.client.media.audio;

import com.sun.jna.Platform;
import net.sf.lamejb.*;

import java.io.*;

public class AudioConverter {


    public void encodeStream(byte[] wav,String mp3File) throws Exception
    {
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
    }
}
