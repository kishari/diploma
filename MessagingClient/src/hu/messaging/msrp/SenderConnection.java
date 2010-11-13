package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Random;

public class SenderConnection implements Runnable {

	private String sipUri = null;
	private InetAddress remoteAddress = null;
	private int remotePort = -1; //-1 = undefined
	private boolean started = false;
	private boolean running = false;
	
	private Connections parent;
	private SocketChannel senderChannel = null;
	private Selector selector = null;
		
	public SenderConnection(InetAddress remoteAddress, int remotePort, Connections parent, String sipUri) throws IOException {
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.parent = parent;
		this.sipUri = sipUri;
		//System.out.println("SenderConnection konstruktor!");
		//System.out.println("remoteAddr: " + remoteAddress.getHostAddress());
		//System.out.println("port: " + remotePort);
		this.selector = initSelector();
	}
	
	public void send(byte[] data) throws IOException {
		System.out.println("Sending bytes: " + data.length);
		ByteBuffer b = ByteBuffer.allocate(data.length);
		b.clear();
		b = ByteBuffer.wrap(data);
    	senderChannel.write(b);
	}
	
	public void run() {
		try {
			senderChannel = this.initiateConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setStarted(true);
		
		while (isRunning()) {
			try {
				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}
					// Check what event is available and deal with it
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
						//itt kellene olvasni
					} else if (key.isWritable()) {
						//itt meg írni
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("hehehehe");
		try {
			this.senderChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("senderChannel close után");
	}
	
	private SocketChannel initiateConnection() throws IOException {
		System.out.println("senderconnection initiateConnection");
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
	
		InetSocketAddress addr = new InetSocketAddress(this.remoteAddress, this.remotePort);
		System.out.println("sender initiateConnection: " + addr.getAddress().getHostAddress() + " : " + addr.getPort());
		socketChannel.connect(new InetSocketAddress(this.remoteAddress, this.remotePort));
		
		socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
		
		return socketChannel;
	}
	
	private void finishConnection(SelectionKey key) throws IOException {
		//System.out.println("sender finishconnection");
		SocketChannel socketChannel = (SocketChannel) key.channel();
	
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			return;
		}		
		//key.interestOps(SelectionKey.OP_READ);	
		//System.out.println(this.sipUri);
	}
	
	private Selector initSelector() throws IOException {
		return SelectorProvider.provider().openSelector();
	}
	
	public void start() {
		System.out.println("SenderConnection start! (" + this.remoteAddress.getHostAddress() + ":" + this.remotePort + ")");
		setRunning(true);
		new Thread(this).start();
	}

	public void stop() {
		System.out.println("senderConnection.stop");
		setRunning(false);
		this.selector.wakeup();
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isStarted() {
		return started;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public String getSipUri() {
		return sipUri;
	}

	public void setSipUri(String sipUri) {
		this.sipUri = sipUri;
	}

}
