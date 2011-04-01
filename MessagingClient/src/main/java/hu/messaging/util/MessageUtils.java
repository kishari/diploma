package hu.messaging.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import hu.messaging.client.model.*;
import hu.messaging.util.*;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.Constants;

public class MessageUtils {
	
	public static MessageContainer createMessageContainerFromCompleteMessage(CompleteMessage m, boolean isSent) {
		ObjectFactory f = new ObjectFactory();
		MessageContainer c = f.createMessageContainer();
		c.setContentAvailable(m.getContent() != null);
		c.setId(m.getMessageId());
		c.setMimeType(m.getExtension());
		if (isSent) {
			c.setStatus("SENT");
		}
		else {
			c.setStatus("NEW");
		}
		MessageContainer.Sender s = new ObjectFactory().createMessageContainerSender();
		s.setName(m.getSender() + "_name");
		s.setSipUri(m.getSender());
		c.setSender(s);
		
		c.setSubject(m.getSubject());
		
		return c;
	}

	public static void createMessageContainerFile(MessageContainer message, byte[] content) {			
		
		File dir = new File(Constants.messagesPath);
		dir.mkdirs();
		File messageFile = new File(dir, message.getId() + ".message");
		message.setContentAvailable(content != null);
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Constants.messagesPath + Constants.messagesContentsRelativePath;	
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId() + "." + message.getMimeType());
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			
				out.write(content);
				out.flush();					
				out.close();
			}
			catch(IOException e) { 
				e.printStackTrace();
			}	
		}
		XMLUtils.createXMLFileFromMessageContainer(message, messageFile);			
	}
	
	public static void updateMessageContainerFile(MessageContainer message, byte[] content) {							
		
		File dir = new File(Constants.messagesPath);
		dir.mkdirs();
		File messageFile = new File(dir, message.getId() + ".message");
		if (!messageFile.exists()) {
			System.out.println("Nincs ilyen containerFile: " + message.getId() + ".message");
		}
		
		MessageContainer m = XMLUtils.createMessageContainerFromFile(messageFile);
		m.setContentAvailable(true);
		
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Constants.messagesPath + Constants.messagesContentsRelativePath;	
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId() + "." + m.getMimeType());
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			
				out.write(content);
				out.flush();					
				out.close();
			}
			catch(IOException e) { 
				e.printStackTrace();
			}	
		}
		XMLUtils.createXMLFileFromMessageContainer(m, messageFile);			
	}
	
	public static MessageContainer readMessageContainerFromFile(String messageId) {
		File dir = new File(Constants.messagesPath + Constants.messagesContentsRelativePath);
		File messageFile = new File(dir, messageId + ".message");
		MessageContainer m = XMLUtils.createMessageContainerFromFile(messageFile);			
		
		return m;
	}
	
	public static List<MessageContainer> loadInboxMessages() {		
		List<MessageContainer> inbox = new ArrayList<MessageContainer>();
		File dir = new File(Constants.messagesPath);
		
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				System.out.println(f.getName());				
				MessageContainer c = XMLUtils.createMessageContainerFromFile(f);
				System.out.println("MessageContainer id: " + c.getId());
				if (!"SENT".equals(c.getStatus().toUpperCase())) {
					inbox.add(c);
				}
			}				
		}			
		return inbox;
	}
	
	public static List<MessageContainer> loadSentMessages() {		
		List<MessageContainer> sentMessages = new ArrayList<MessageContainer>();
		File dir = new File(Constants.messagesPath);
		
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				System.out.println(f.getName());				
				MessageContainer c = XMLUtils.createMessageContainerFromFile(f);
				System.out.println("MessageContainer id: " + c.getId());
				if ("SENT".equals(c.getStatus().toUpperCase())) {
					sentMessages.add(c);
				}
			}	
		}			 
		return sentMessages;
	}
	
	public static MessageContainer createMessageContainerFromNotifyInfoMessage(InfoMessage info) {		
		MessageContainer m = new ObjectFactory().createMessageContainer();
		MessageContainer.Sender s = new ObjectFactory().createMessageContainerSender();
		m.setId(info.getInfoDetail().getId());
		m.setStatus("NEW");
		m.setMimeType(info.getInfoDetail().getMimeType());
		m.setSubject(info.getInfoDetail().getSubject());
		m.setContentAvailable(false);
		
		s.setName(info.getInfoDetail().getSender().getName());
		s.setSipUri(info.getInfoDetail().getSender().getSipUri());
		m.setSender(s);		
					 
		return m;
	}
}