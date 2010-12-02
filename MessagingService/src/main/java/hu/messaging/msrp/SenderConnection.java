package hu.messaging.msrp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SenderConnection implements Runnable {

	private Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();
	private MSRPStack msrpStack;
	private Session session = null;
	private String sipUri = null;
	private InetAddress remoteAddress = null;
	private int remotePort = -1; //-1 = undefined
	private boolean running = false;
	private boolean connected = false;
	
	private SocketChannel senderChannel = null;
	private Selector selector = null;
		
	public SenderConnection(InetAddress remoteAddress, int remotePort, 
							String sipUri, MSRPStack msrpStack) throws IOException {
		
		this.msrpStack = msrpStack;
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.sipUri = sipUri;
		this.selector = initSelector();
	}

	public void sendChunk(byte[] chunk) throws IOException {
		synchronized(this.pendingChanges) {
			this.pendingChanges.add( new ChangeRequest(this.senderChannel, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE) );
			ByteBuffer b = ByteBuffer.allocate(chunk.length);
			b.clear();
			b = ByteBuffer.wrap(chunk);
			synchronized(pendingData) {
				List<ByteBuffer> queue = this.pendingData.get(senderChannel);
				
				if (queue == null) {
					queue = new ArrayList<ByteBuffer>();					
					this.pendingData.put(senderChannel, queue);
				}
				queue.add(b);				
			}
		}
		//System.out.println("senderConnection send data! selector wakeup.");
		this.selector.wakeup();
		//System.out.println("Sending bytes: " + data.length);
		//ByteBuffer b = ByteBuffer.allocate(data.length);
		//b.clear();
		//b = ByteBuffer.wrap(data);
    	//senderChannel.write(b);
	}
	
	public void run() {
		try {
			senderChannel = this.initiateConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("run eleje (senderchannel init utan)");
		while (isRunning()) {
			//System.out.println("senderConnection run");
			try {				
				synchronized (this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						//System.out.println(this.pendingChanges.size());
						//System.out.println("Iteralunk a pendingChanges listan...");
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case ChangeRequest.REGISTER:
				              change.socket.register(this.selector, change.ops);
				              break;
						}
					}
					//System.out.println("clear elott: " + this.pendingChanges.size());
					//System.out.println(this.pendingChanges.size());
					this.pendingChanges.clear();
					//System.out.println("clean utan: " + this.pendingChanges.size());
				}
				// Wait for an event one of the registered channels
				this.selector.select();
				//System.out.println("select utan");
				//System.out.println("senderConnection run");
				

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					//System.out.println("iteralunk a selector.selectedKeys listan");
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}
					// Check what event is available and deal with it
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("senderConnection stopped: " + this.sipUri);
		
		try {
			this.senderChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setConnected(false);
		
		System.out.println("senderConnection closed: " + this.sipUri);
			
	}
	
	public void start() {
		//System.out.println("SenderConnection start! (" + this.remoteAddress.getHostAddress() + ":" + this.remotePort + ")");
		System.out.println("SenderConnection start! (" + this.sipUri + ")");
		setRunning(true);
		new Thread(this).start();		
	}

	public void stop() {
		setRunning(false);
		System.out.println("senderConnection stop: " + this.sipUri);
		this.selector.wakeup();
		
		//Törlöm az MSRPStackbõl a sessiont
		this.session.getTransactionManager().stop();
		getMsrpStack().removeSession(session.getId());
		
	}
	
	private SocketChannel initiateConnection() throws IOException {
		System.out.println("senderconnection initiateConnection");
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
	
		InetSocketAddress addr = new InetSocketAddress(this.remoteAddress, this.remotePort);
		System.out.println("sender initiateConnection: " + addr.getAddress().getHostAddress() + " : " + addr.getPort());
		socketChannel.connect(new InetSocketAddress(this.remoteAddress, this.remotePort));
		
		//socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
		synchronized(this.pendingChanges) {
		      this.pendingChanges.add( new ChangeRequest( socketChannel, 
		    		  				  					 ChangeRequest.REGISTER, 
		    		  				  					 SelectionKey.OP_CONNECT ) );
		    }
		
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
		socketChannel.register(this.selector, SelectionKey.OP_READ);
		setConnected(true);
	}
	
	private Selector initSelector() throws IOException {
		return SelectorProvider.provider().openSelector();
	}
	
	private void read(SelectionKey key) throws IOException {
		//Ha hibával áll le a távoli gép, akkor ezt a connectiont meg kell ölni
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		int numRead;
	    try {
	    	ByteBuffer buff = ByteBuffer.allocate(10000);
	    	buff.clear();
	    	numRead = socketChannel.read(buff);
	    } catch (IOException e) {
	      key.cancel();
	      socketChannel.close();
	      System.out.println("Sender IOException");
	      getMsrpStack().getConnections().deleteSenderConnection(sipUri);
	      return;
	    }
		 if (numRead == -1) {
			 System.out.println("-1 adatot olvastam");
		      key.channel().close();
		      key.cancel();
		      //Szabadítsuk fel az erõforrásokat
		      getMsrpStack().getConnections().deleteSenderConnection(sipUri);
		      return;
		 }
	}
	
	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		//System.out.println("sender write");
		synchronized (this.pendingData) {
			List<ByteBuffer> queue = this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
				//System.out.println("queue.remove");
			}

			if (queue.isEmpty()) {
				//System.out.println("Nincs kuldendo adat!");
				socketChannel.register(this.selector, SelectionKey.OP_READ);
			}
		}
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public MSRPStack getMsrpStack() {
		return msrpStack;
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

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

}
