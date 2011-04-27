package hu.messaging.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.util.StringUtil;
import hu.messaging.client.media.MimeHelper;
import hu.messaging.client.model.*;

public class MessageUtils {

	public static MessageInfoContainer createMessageContainerFromCompleteMessage(
			CompleteMessage m, boolean isSent) {
		ObjectFactory f = new ObjectFactory();
		MessageInfoContainer c = f.createMessageInfoContainer();
		c.setContentDescription(f.createContentDescription());

		c.getContentDescription().setContentAvailable(m.getContent() != null);
		c.setId(m.getMessageId());
		c.getContentDescription().setMimeType(m.getMimeType());
		if (isSent) {
			c.setStatus("SENT");
		} else {
			c.setStatus("NEW");
		}
		UserInfo s = f.createUserInfo();
		if (m.getSender() != null) {
			s.setName(m.getSender().getName());
			s.setSipUri(m.getSender().getSipUri());
		}
		c.setSender(s);

		c.setSubject(m.getSubject());

		return c;
	}

	public static void createMessageContainerFile(MessageInfoContainer message,
			byte[] content) {

		File dir = new File(Resources.messagesDirectory);
		dir.mkdirs();
		File messageFile = new File(dir, message.getId() + ".message");
		message.getContentDescription().setContentAvailable(content != null);
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Resources.messageContentsDirectory;
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId()
						+ "."
						+ MimeHelper.getExtensionByMIMEType(message
								.getContentDescription().getMimeType()));
				out = new BufferedOutputStream(new FileOutputStream(
						contentFile, true));

				out.write(content);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		XMLUtils.createXMLFileFromMessageInfoContainer(message, messageFile);
	}

	public static void createContentFileToMessageInfoContainer(
			MessageInfoContainer message, byte[] content) {
		if (content != null) {
			try {
				OutputStream out = null;
				String contentDirPath = Resources.messageContentsDirectory;
				File contentDir = new File(contentDirPath);
				contentDir.mkdir();
				File contentFile = new File(contentDir, message.getId()
						+ "."
						+ MimeHelper.getExtensionByMIMEType(message
								.getContentDescription().getMimeType()));
				out = new BufferedOutputStream(new FileOutputStream(
						contentFile, true));

				out.write(content);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateMessageContainerFile(MessageInfoContainer message) {

		File dir = new File(Resources.messagesDirectory);
		dir.mkdirs();
		System.out.println("MessageUtils messageId: " + message.getId());
		File messageFile = new File(dir, message.getId() + ".message");
		if (!messageFile.exists()) {
			System.out.println("Nincs ilyen containerFile: " + message.getId()
					+ ".message");
		}

		XMLUtils.createXMLFileFromMessageInfoContainer(message, messageFile);
	}

	public static MessageInfoContainer readMessageContainerFromFile(
			String messageId) {
		File dir = new File(Resources.messagesDirectory);
		File messageFile = new File(dir, messageId + ".message");
		MessageInfoContainer m = XMLUtils
				.createMessageInfoContainerFromFile(messageFile);

		return m;
	}

	public static List<MessageInfoContainer> loadInboxMessages() {
		List<MessageInfoContainer> inbox = new ArrayList<MessageInfoContainer>();
		File dir = new File(Resources.messagesDirectory);

		for (File f : dir.listFiles()) {
			if (f.isFile()
					&& StringUtil.getFileExtension(f.getName()).equals(
							"message")) {
				System.out.println(f.getName());
				MessageInfoContainer c = XMLUtils
						.createMessageInfoContainerFromFile(f);
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
		File dir = new File(Resources.messagesDirectory);

		for (File f : dir.listFiles()) {
			if (f.isFile()
					&& StringUtil.getFileExtension(f.getName()).equals(
							"message")) {
				System.out.println(f.getName());
				MessageInfoContainer c = XMLUtils
						.createMessageInfoContainerFromFile(f);
				System.out.println("MessageContainer id: " + c.getId());
				if ("SENT".equals(c.getStatus().toUpperCase())) {
					sentMessages.add(c);
				}
			}
		}
		return sentMessages;
	}

	public static List<MessageInfoContainer> createMessageInfoContainerListFromNotifyInfoMessage(
			InfoMessage info) {
		ObjectFactory f = new ObjectFactory();
		List<MessageInfoContainer> cList = new ArrayList<MessageInfoContainer>();

		for (InfoDetail d : info.getDetailList().getDetail()) {
			System.out.println("csinálom a dolgom");
			MessageInfoContainer m = f.createMessageInfoContainer();
			m.setContentDescription(f.createContentDescription());
			UserInfo s = f.createUserInfo();

			m.setId(d.getId());
			m.setStatus("NEW");
			m.getContentDescription().setMimeType(d.getContent().getMimeType());
			m.setSubject(d.getSubject());
			m.setSentAt(d.getSentAt());
			m.getContentDescription().setContentAvailable(false);
			m.getContentDescription().setSize(d.getContent().getSize());

			s.setName(d.getSender().getName());
			s.setSipUri(d.getSender().getSipUri());
			m.setSender(s);

			cList.add(m);
		}

		return cList;
	}

	public static void deleteMessageWithMessageId(String messageId) {
		File dir = new File(Resources.messagesDirectory);
		File contentDir = new File(Resources.messageContentsDirectory);

		for (File f : dir.listFiles()) {
			if (f.isFile()
					&& StringUtil.getFileExtension(f.getName()).equals(
							"message")) {
				if (messageId.equals(StringUtil.getFileNameWithoutExtension(f
						.getName()))) {
					f.delete();
					for (File c : contentDir.listFiles()) {
						if (messageId.equals(StringUtil
								.getFileNameWithoutExtension(c.getName()))) {
							c.delete();
							break;
						}
					}
					break;
				}
			}
		}
	}

}