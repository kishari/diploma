package hu.messaging.service;

import hu.messaging.msrp.*;

import java.io.IOException;
import java.net.InetAddress;

public class MessagingService {
	
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
	
	public static boolean isRunningReceiverConnection() {
		return getMsrpStack().getConnections().isRunningReceiverConnection();
	}
	
	public static boolean isReceiverConnection() {
		return getMsrpStack().getConnections().isReceiverConnection();
	}

	public static MSRPStack getMsrpStack() {
		return msrpStack;
	}
}
