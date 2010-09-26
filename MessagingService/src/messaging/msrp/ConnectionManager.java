package messaging.msrp;

import java.io.*;
import java.net.*;

import hu.nio.test.*;

public class ConnectionManager {

	private EchoWorker worker = null;
	
	public ConnectionManager(InetAddress address) {
			worker = new EchoWorker();
	}
	
	public void start() {
		try {
			new Thread(worker).start();
			new Thread(new NioServer(null, 9090, worker)).start();
		}
		catch(IOException e){}
	}

}
