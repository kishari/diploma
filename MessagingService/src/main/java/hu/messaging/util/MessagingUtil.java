package hu.messaging.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MessagingUtil {
	
	public static String getLocalIPAddress() {
		InetAddress internetAddress = null;
		try {
			internetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// Default to loopback
			return "127.0.0.1";
		}
		
		return internetAddress.getHostAddress();
	
	}
}
