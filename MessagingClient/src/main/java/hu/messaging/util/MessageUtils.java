package hu.messaging.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import hu.messaging.client.model.*;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.Constants;

public class MessageUtils {
	
	public static MessageInfoContainer createMessageContainerFromCompleteMessage(CompleteMessage m, boolean isSent) {
		ObjectFactory f = new ObjectFactory();
		MessageInfoContainer c = f.createMessageInfoContainer();
		c.setContentDescription(f.createContentDescription());
		
		c.getContentDescription().setContentAvailable(m.getContent() != null);
		c.setId(m.getMessageId());
		c.getContentDescription().setMimeType(m.getExtension());
		if (isSent) {
			c.setStatus("SENT");
		}
		else {
			c.setStatus("NEW");
		}
		UserInfo s = f.createUserInfo();
		s.setName(m.getSender() + "_name");
		s.setSipUri(m.getSender());
		c.setSender(s);
		
		c.setSubject(m.getSubject());
		
		return c;
	}

	public static void createMessageContainerFile(MessageInfoContainer message, byte[] content) {			
		
		File dir = new File(Constants.messagesPath);
		dir.mkdirs();
		File messageFile = new File(dir, message.getId() + ".message");
		message.getContentDescription().setContentAvailable(content != null);
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Constants.messagesPath + Constants.messagesContentsRelativePath;	
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId() + "." + message.getContentDescription().getMimeType());
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			
				out.write(content);
				out.flush();					
				out.close();
			}
			catch(IOException e) { 
				e.printStackTrace();
			}	
		}
		XMLUtils.createXMLFileFromMessageInfoContainer(message, messageFile);			
	}
	
	public static void updateMessageContainerFile(MessageInfoContainer message, byte[] content) {							
		
		File dir = new File(Constants.messagesPath);
		dir.mkdirs();
		File messageFile = new File(dir, message.getId() + ".message");
		if (!messageFile.exists()) {
			System.out.println("Nincs ilyen containerFile: " + message.getId() + ".message");
		}
		
		MessageInfoContainer m = XMLUtils.createMessageInfoContainerFromFile(messageFile);
		m.getContentDescription().setContentAvailable(true);
		
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Constants.messagesPath + Constants.messagesContentsRelativePath;	
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId() + "." + m.getContentDescription().getMimeType());
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			
				out.write(content);
				out.flush();					
				out.close();
			}
			catch(IOException e) { 
				e.printStackTrace();
			}	
		}
		XMLUtils.createXMLFileFromMessageInfoContainer(m, messageFile);			
	}
	
	public static MessageInfoContainer readMessageContainerFromFile(String messageId) {
		File dir = new File(Constants.messagesPath + Constants.messagesContentsRelativePath);
		File messageFile = new File(dir, messageId + ".message");
		MessageInfoContainer m = XMLUtils.createMessageInfoContainerFromFile(messageFile);			        
		
		return m;
	}
	
	public static List<MessageInfoContainer> loadInboxMessages() {		
		List<MessageInfoContainer> inbox = new ArrayList<MessageInfoContainer>();
		File dir = new File(Constants.messagesPath);
		
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				System.out.println(f.getName());				
				MessageInfoContainer c = XMLUtils.createMessageInfoContainerFromFile(f);
				System.out.println("MessageContainer id: " + c.getId());
				if (!"SENT".equals(c.getStatus().toUpperCase())) {
					inbox.add(c);
				}
			}				
		}			
		return inbox;
	}
	
	public static List<MessageInfoContainer> loadSentMessages() {		
		List<MessageInfoContainer> sentMessages = new ArrayList<MessageInfoContainer>();
		File dir = new File(Constants.messagesPath);
		
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				System.out.println(f.getName());				
				MessageInfoContainer c = XMLUtils.createMessageInfoContainerFromFile(f);
				System.out.println("MessageContainer id: " + c.getId());
				if ("SENT".equals(c.getStatus().toUpperCase())) {
					sentMessages.add(c);
				}
			}	
		}			 
		return sentMessages;
	}
	
	public static MessageInfoContainer createMessageInfoContainerFromNotifyInfoMessage(InfoMessage info) {	
		ObjectFactory f = new ObjectFactory();
		MessageInfoContainer m = f.createMessageInfoContainer();
		m.setContentDescription(f.createContentDescription());
		
		UserInfo s = f.createUserInfo();
		
		InfoDetail detail = info.getDetailList().getDetail().get(0);
		m.setId(detail.getId());
		m.setStatus("NEW");
		m.getContentDescription().setMimeType(detail.getContent().getMimeType());
		m.setSubject(detail.getSubject());
		m.getContentDescription().setContentAvailable(false);
		
		s.setName(detail.getSender().getName());
		s.setSipUri(detail.getSender().getSipUri());
		m.setSender(s);		
					 
		return m;
	}
}