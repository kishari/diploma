package hu.messaging.service;

import hu.messaging.msrp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MessagingService {
	
	private static Map<String, String> localSDPs = new HashMap<String, String>();
	
	private static MSRPStack msrpStack = new MSRPStack();
	
	public static void createSenderConnection(InetAddress host, int port, String sipUri) {
		try {
			System.out.println("MSRPService createSenderConnection");
			getMsrpStack().createSenderConnection(host, port, sipUri);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createReceiverConnection(InetAddress host) {
		try {
			getMsrpStack().createReceiverConnection(host);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMessage(byte[] completeMessage, String sipUri) throws IOException {
		getMsrpStack().sendMessage(completeMessage, sipUri);
	}
	
	public static boolean isRunningReceiverConnection() {
		return getMsrpStack().getConnections().isRunningReceiverConnection();
	}
	
	public static boolean isReceiverConnection() {
		return getMsrpStack().getConnections().isReceiverConnection();
	}

	public static MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	public static void addLocalSDP(String sessionId, String sdp) {
		localSDPs.put(sessionId, sdp);
	}
	
	public static String getLocalSDP(String sessionId) {
		System.out.println("MessagingService.getLocalSDP: " + sessionId);
		String sdp = localSDPs.get(sessionId);
		System.out.println("sdp: " + sdp );
		return sdp;
	}
	
	public static Session createNewSession(URI localURI, URI remoteURI, String sipUri) {
		SenderConnection s = getMsrpStack().getConnections().findSenderConnection(sipUri);
		System.out.println("MessagingService createNewSession");
		
		if (s == null) {
			System.out.println("nem találtunk a sessionhoz sendert");
			return null;
		}
		
		System.out.println(localURI);
		System.out.println(remoteURI);
		Session newSession = new Session(localURI, remoteURI, s);
		getMsrpStack().putNewSession(newSession);
		
		s.setSession(newSession);
		
		return newSession;
	}
}
