package hu.messaging.msrp;

import hu.messaging.msrp.listener.MSRPEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Observable;

public class SenderConnection extends Observable implements Runnable {

	private MSRPStack msrpStack;
	private Session session = null;
	private String remoteSipUri = null;
	private InetAddress remoteAddress = null;
	private int remotePort = -1; //-1 = undefined
	private boolean running = false;
	private boolean connected = false;

	private SocketChannel senderChannel = null;
	private Selector selector = null;
		
	public SenderConnection(InetAddress remoteAddress, int remotePort, 
							String remoteSipUri, MSRPStack msrpStack) throws IOException {
		
		this.msrpStack = msrpStack;		
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.remoteSipUri = remoteSipUri;
		
		addObserver(msrpStack.getConnections());
		this.selector = initSelector();
	}


	protected String getRemoteSipUri() {
		return remoteSipUri;
	}

	protected void send(byte[] chunk) throws IOException {
		this.write(chunk);
	}
	
	public void run() {
		try {
			senderChannel = this.initiateConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
						this.read(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("senderConnection stopped: " + remoteSipUri);
		
		try {
			this.senderChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		connected = false;
		System.out.println("senderConnection closed: " + remoteSipUri);
		this.setChanged();
		notifyObservers(this);
			
	}
	
	protected void start() {
		System.out.println("SenderConnection start! (" + remoteSipUri + ")");
		running = true;
		new Thread(this).start();		
	}

	protected void stop() {
		running = false;
		System.out.println("senderConnection stop called: " + remoteSipUri);
		this.selector.wakeup();
	}
	
	private SocketChannel initiateConnection() throws IOException {
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
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			return;
		}	
		connected = true;
		socketChannel.register(this.selector, SelectionKey.OP_READ);
		this.msrpStack.notifyListeners(new MSRPEvent(MSRPEvent.MSRPEventType.sessionStarted, remoteSipUri));
	}
	
	private Selector initSelector() throws IOException {
		return SelectorProvider.provider().openSelector();
	}
	
	private void read(SelectionKey key) throws IOException {
		//Ez akkor hívódik csak meg, ha hibával áll le a távoli gép,
		//Ilyenkor ezt a connectiont meg kell ölni
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		int numRead;
	    try {
	    	ByteBuffer buff = ByteBuffer.allocate(10000);
	    	buff.clear();
	    	numRead = socketChannel.read(buff);
	    } catch (IOException e) {
	      key.cancel();
	      socketChannel.close();
	      msrpStack.stopSession(remoteSipUri);
	      return;
	    }
		 if (numRead == -1) {
		      key.channel().close();
		      key.cancel();
		      //Szabadítsuk fel az erõforrásokat
		      msrpStack.stopSession(remoteSipUri);
		      return;
		 }
	}
	
	private synchronized void write(byte[] data) throws IOException {
		//System.out.println("write");
		ByteBuffer b = ByteBuffer.allocate(data.length);
		b.clear();
		b = ByteBuffer.wrap(data);
		senderChannel.write(b);
	}
	
	protected Session getSession() {
		return session;
	}

	protected void setSession(Session session) {
		this.session = session;
	}
	
	private boolean isRunning() {
		return running;
	}
	
	public boolean isConnected() {
		return connected;
	}


}
