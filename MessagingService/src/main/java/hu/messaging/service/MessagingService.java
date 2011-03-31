package hu.messaging.service;

import hu.messaging.User;
import hu.messaging.dao.MessagingDAO;
import hu.messaging.msrp.*;
import hu.messaging.msrp.event.MSRPEvent;
import hu.messaging.msrp.event.MSRPListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessagingService implements Observer, MSRPListener{
	
	public List<User> onlineUsers = new ArrayList<User>();
	private MessagingDAO messagingDao = new MessagingDAO();
	private MSRPStack msrpStack = new MSRPStack();
	
	public void createSenderConnection(InetAddress host, int port, String sipUri) {
		try {
			getMsrpStack().createSenderConnection(host, port, sipUri);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disposeSenderConnection(String sipUri) {
		getMsrpStack().getConnections().deleteSenderConnection(sipUri);
	}
	
	public void createReceiverConnection(InetAddress host) {
		try {
			getMsrpStack().createReceiverConnection(host);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessages(List<CompleteMessage> messages, String sipUri) {
		for (CompleteMessage m : messages) {
			sendMessage(m, sipUri);
		}
	}
	
	public void sendMessage(CompleteMessage completeMessage, String sipUri) {
		getMsrpStack().sendMessage(completeMessage, sipUri);
	}
	
	public boolean isRunningReceiverConnection() {
		return getMsrpStack().getConnections().isRunningReceiverConnection();
	}
	
	public boolean isReceiverConnection() {
		return getMsrpStack().getConnections().isReceiverConnection();
	}

	public Session createNewSession(URI localURI, URI remoteURI, String sipUri) {
		SenderConnection s = getMsrpStack().getConnections().getSenderConnection(sipUri);
		System.out.println("MessagingService createNewSession");
		
		if (s == null) {
			System.out.println("nem találtunk a sessionhoz sendert");
			return null;
		}
		
		Session newSession = new Session(localURI, remoteURI, s, msrpStack);
		getMsrpStack().putNewSession(newSession);
		
		s.setSession(newSession);
		
		return newSession;
	}
	
	public MSRPStack getMsrpStack() {
		return msrpStack;
	}

	public synchronized void removeUserFromOnlineList(User user) {
		int index = 0;
		for (User u : this.onlineUsers) {
			if (user.getSipURI().equals(u.getSipURI())) {
				u.getTimer().cancel();
				this.onlineUsers.remove(index);
				
				break;
			}
			index++;
		}
	}
	
	public synchronized User findUserInOnlineList(String sipURI) {
		for (User u : this.onlineUsers) {
			if (sipURI.equals(u.getSipURI())) {
				return u;
			}
		}
		return null;
	}
	
	public synchronized boolean addUserToOnlineList(User user) {
		boolean userIsAddedFirst = false;
		if(findUserInOnlineList(user.getSipURI()) == null) {
			this.onlineUsers.add(user);	
			userIsAddedFirst = true;
		}
		else {
			removeUserFromOnlineList(user);
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
	
	public String createNotifyMessageContent(String sender, String messageId, String extension) {
		String msg = "MESSAGENOTIFY\r\n\r\n";
		msg += "Message-ID: " + messageId + "\r\n";
		msg += "Extension: " + extension + "\r\n";
		msg += "Sender: " + sender + "\r\n";
		msg += "Subject: "; 
		
		System.out.println("createNotifyMessageContent:");
		System.out.println(msg);
		return msg;
	}

	public void fireMsrpEvent(MSRPEvent event) {
		switch(event.getCode()) {
			case MSRPEvent.messageReceivingSuccess:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: messageReceivingSuccess");
				this.messagingDao.insertMessage(event.getCompleteMessage());
				printToFile(event.getCompleteMessage().getContent(), event.getCompleteMessage().getExtension());
				break;
			case MSRPEvent.brokenTrasmission:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: brokenTrasmission");
				break;
			case MSRPEvent.messageSentSuccess:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: messageSentSuccess");
				break;
			case MSRPEvent.sessionStarted:
				System.out.println(getClass().getSimpleName() + " fireMsrpEvent: sessionStarted");
				break;
		}
	}
	
	public MessagingDAO getMessagingDao() {
		return messagingDao;
	}
	
//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, String fileExtension) {
		try {
			OutputStream out = null;
			File recreatedContentFile = new File("c:\\diploma\\testing\\serverRecreatedContentFile." + fileExtension);
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
