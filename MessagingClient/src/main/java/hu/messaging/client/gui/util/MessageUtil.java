package hu.messaging.client.gui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hu.messaging.msrp.CompleteMessage;

public class MessageUtil {

	public static void createMessageFile(CompleteMessage message, boolean sentMessage) {
		try {
			String path = "c:\\";
			if (sentMessage) {
				path += "message\\sent";
				
			}
			else {
				path += "message\\inbox";
			}
			
			File dir = new File(path);
			dir.mkdirs();
			File messageFile = new File(dir, message.getMessageId() + ".message");
			BufferedWriter out = new BufferedWriter(new FileWriter(messageFile));
			out.write("Sender:\n" + message.getSender() + "\n");
			out.write("Extension:\n" + message.getExtension() + "\n");
			out.write("Content:\n " + new String(message.getContent()));
			
			out.flush();			
			out.close();			
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
	
	public static CompleteMessage readMessageFile(String messageId) {
		
		return null;
	}
}