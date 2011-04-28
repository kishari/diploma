package hu.messaging.client.media.video;

import javax.media.datasink.*;


public class DataSinkAdapter implements DataSinkListener {
	boolean endOfStream = false;

	public void dataSinkUpdate(DataSinkEvent event)	{
		if (event instanceof javax.media.datasink.EndOfStreamEvent) {
			endOfStream = true;
		}
	}

	public void waitEndOfStream(long checkTimeMs) {
		while (!endOfStream) {
			System.out.println("datasink: waiting for end of stream ...");
			try { 
				Thread.currentThread().sleep(checkTimeMs); 
			} 
			catch (InterruptedException e) {}
		}
		System.out.println("datasink: ... end of stream reached.");
	}
}
