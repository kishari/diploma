package hu.messaging.client.msrp;

import java.net.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import hu.nio.test.*;

public class TransactionManager {

	private SocketChannel client = null;
	private Selector selector = null;
	private int port = 60301;
	
	private NioClient nioClient = null;
	private RspHandler handler = null;

	public TransactionManager() {
		try {
			nioClient = new NioClient(InetAddress.getLocalHost(), 9090);
			Thread t = new Thread(nioClient);
			t.setDaemon(true);
			t.start();
			handler = new RspHandler();
		}
		catch (Exception e) {}
	}
	
	public void sendData(String data) {
		try {
			nioClient.send(data.getBytes(), handler);
			//handler.waitForResponse();
		}
		catch (Exception e) {}
	}
	
	private InetAddress getLocalHost() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		}
		catch(Exception e) {
			return null;
		}
		return addr;
	}
	
}
