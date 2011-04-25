package hu.messaging.service;

import hu.messaging.model.*;
import hu.messaging.User;
import hu.messaging.dao.MessagingDAO;
import hu.messaging.msrp.*;
import hu.messaging.msrp.listener.MSRPEvent;
import hu.messaging.msrp.listener.MSRPListener;
import hu.messaging.msrp.model.CompleteMSRPMessage;
import hu.messaging.msrp.util.SessionDescription;
import hu.messaging.util.XMLUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MessagingService implements Observer, MSRPListener{
	
	public List<User> onlineUsers = new ArrayList<User>();
	private MessagingDAO messagingDao = new MessagingDAO();
	private MSRPStack msrpStack = new MSRPStack();
	private Map<String, Map<String, SessionDescription>> sdpContainer = null;
	
	public MessagingService() {		
		sdpContainer = new HashMap<String, Map<String, SessionDescription>>();
	}
	
	public Map<String, Map<String, SessionDescription>> getSdpContainer() {
		return sdpContainer;
	}

	public void setSdpContainer(
			Map<String, Map<String, SessionDescription>> sdpContainer) {
		this.sdpContainer = sdpContainer;
	}

	public void stopSession(String remoteSipUri) {
		getMsrpStack().stopSession(remoteSipUri);
	}
	
	public void sendMessages(List<CompleteMessage> messages, String remoteSipUri) {
		for (CompleteMessage m : messages) {
			sendMessage(m, remoteSipUri);
		}
	}
	
	public void sendMessage(CompleteMessage completeMessage, String remoteSipUri) {
		getMsrpStack().sendMessage(new CompleteMSRPMessage(completeMessage.getMessageId(), completeMessage.getContent()), remoteSipUri);
	}
	
	public MSRPStack getMsrpStack() {
		return msrpStack;
	}

	private synchronized void removeUserFromOnlineList(User user) {
		int index = 0;
		for (User u : this.onlineUsers) {
			if (user.equals(u)) {
				u.getTimer().cancel();
				this.onlineUsers.remove(index);
				
				break;
			}
			index++;
		}
	}
	
	private synchronized User findUserInOnlineList(User user) {
		for (User u : this.onlineUsers) {
			if (user.equals(u)) {
				return u;
			}
		}
		return null;
	}
	
	public synchronized boolean addUserToOnlineList(User user) {
		boolean userIsAddedFirst = false;
		User u = findUserInOnlineList(user);
		if(u == null) {
			this.onlineUsers.add(user);	
			userIsAddedFirst = true;
		}
		else {
			this.removeUserFromOnlineList(user);
			this.onlineUsers.add(user);			
		}
		
		return userIsAddedFirst;
	}
	
	public void update(Observable o, Object arg) {
		User user = (User)o;
		System.out.println("User timeOut. Removing from online list: " + user.getSipURI());
		
		removeUserFromOnlineList(user);		
	}
	
	public void addMSRPListener(MSRPListener listener) {
		getMsrpStack().addMSRPListener(listener);
	}
	
	public void removeMSRPListener(MSRPListener listener) {
		getMsrpStack().removeMSRPListener(listener);
	}
	
	public String createNotifyMessageContent(InfoMessage info) {
		ObjectFactory factory = new ObjectFactory();
		InfoMessage infoMsg = factory.createInfoMessage();
		
		infoMsg.setInfoType(InfoMessage.notifyUser);
		InfoMessage.DetailList detailList = factory.createInfoMessageDetailList();
			
		for (InfoDetail d : info.getDetailList().getDetail()) {
			InfoDetail detail = factory.createInfoDetail();
			detail.setId(d.getId());
			detail.setContent(factory.createContentDescription());
			detail.getContent().setMimeType(d.getContent().getMimeType());
			detail.getContent().setSize(d.getContent().getSize());
				
			UserInfo sender = factory.createUserInfo();
			sender.setName(d.getSender().getName());
			sender.setSipUri(d.getSender().getSipUri());			
			detail.setSender(sender);
			
			detail.setSentAt(d.getSentAt());
			detail.setSubject(d.getSubject());
			
			if (d.getRecipientList() != null) {
				InfoDetail.RecipientList recipientList = factory.createInfoDetailRecipientList();
				for (UserInfo r : d.getRecipientList().getRecipient()) {
					UserInfo recipient = factory.createUserInfo();
					recipient.setName(r.getName());
					recipient.setSipUri(r.getSipUri());
					recipientList.getRecipient().add(recipient);
				}
			}
			
			detailList.getDetail().add(detail);
		}
			
		infoMsg.setDetailList(detailList);
				
		
		
		String xml = XMLUtils.createStringXMLFromInfoMessage(infoMsg);
		return xml;
	}

	public void fireMsrpEvent(MSRPEvent event) {
		switch(event.getEventType()) {
			case messageReceivingSuccess:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: messageReceivingSuccess");
				CompleteMessage c = new CompleteMessage();
				c.setContent(event.getCompleteMessage().getContent());
				c.setMessageId(event.getMessageId());
				
				this.messagingDao.insertMessage(c);
				//printToFile(event.getCompleteMessage().getContent(), event.getCompleteMessage().getMimeType());
				break;
			case brokenTrasmission:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: brokenTrasmission");
				break;
			case messageSentSuccess:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: messageSentSuccess");
				break;
			case sessionStarted:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: sessionStarted");
				break;
		}
	}
	
	public MessagingDAO getMessagingDao() {
		return messagingDao;
	}
	
//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, String mimeType) {
		try {
			OutputStream out = null;
			File recreatedContentFile = new File("c:\\diploma\\testing\\serverRecreatedContentFile." + mimeType);
			out = new BufferedOutputStream(new FileOutputStream(recreatedContentFile, true));
			
			out.write(data);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
//<<<<<<<<<<<<<<TESZT	

}
