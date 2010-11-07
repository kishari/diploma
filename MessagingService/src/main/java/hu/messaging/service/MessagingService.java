package hu.messaging.service;

import hu.messaging.msrp.*;

import java.io.IOException;
import java.net.InetAddress;

public class MessagingService {
	
	private static MSRPStack msrpStack = new MSRPStack();
	
	public static void createSenderSession(InetAddress host, int port) {
		try {
			getMsrpStack().createSenderConnection(host, port);	
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
