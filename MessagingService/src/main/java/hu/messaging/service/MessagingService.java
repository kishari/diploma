package hu.messaging.service;

import hu.messaging.msrp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

public class MessagingService {
	
	public static String serverURI = "sip:weblogic@192.168.1.103";
	
	private static MSRPStack msrpStack = new MSRPStack();
	
	public static void createSenderConnection(InetAddress host, int port, String sipUri) {
		try {
			getMsrpStack().createSenderConnection(host, port, sipUri);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void disposeSenderConnection(String sipUri) {
		getMsrpStack().getConnections().deleteSenderConnection(sipUri);
	}
	
	public static void createReceiverConnection(InetAddress host) {
		try {
			getMsrpStack().createReceiverConnection(host);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(byte[] completeMessage, String sipUri) {
		getMsrpStack().sendMessage(completeMessage, sipUri);
	}
	
	public static boolean isRunningReceiverConnection() {
		return getMsrpStack().getConnections().isRunningReceiverConnection();
	}
	
	public static boolean isReceiverConnection() {
		return getMsrpStack().getConnections().isReceiverConnection();
	}

	public static Session createNewSession(URI localURI, URI remoteURI, String sipUri) {
		SenderConnection s = getMsrpStack().getConnections().findSenderConnection(sipUri);
		System.out.println("MessagingService createNewSession");
		
		if (s == null) {
			System.out.println("nem találtunk a sessionhoz sendert");
			return null;
		}
		
		Session newSession = new Session(localURI, remoteURI, s);
		getMsrpStack().putNewSession(newSession);
		
		s.setSession(newSession);
		
		return newSession;
	}
	
	public static MSRPStack getMsrpStack() {
		return msrpStack;
	}
}
