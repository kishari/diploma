package hu.messaging.msrp;

import hu.messaging.msrp.model.Message;
import hu.messaging.msrp.util.MSRPUtil;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

public class ReceiverConnection extends Observable implements Runnable {
	private boolean running = false;	
	private MSRPStack msrpStack = null;
	private Map<SocketChannel, String> saveBuffers = new HashMap<SocketChannel, String>();
	private InetAddress hostAddress;
	private int port = 0;	
	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;
	
	public ReceiverConnection(InetAddress localHostAddress, MSRPStack msrpStack) throws IOException {
		this.hostAddress = localHostAddress;
		this.msrpStack = msrpStack;
		this.selector = initSelector();
		this.addObserver(msrpStack.getConnections());
	}

	public void run() {
		running = true;
		
		while (isRunning()) {
			try {
				// Wait for an event one of the registered channels
				this.selector.select();

				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}
			        if (key.isAcceptable()) {
				        accept(key);
				    }
			        if (key.isReadable()) {
			        	read(key);
			        }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("receiverConnection stopped");
		
		try {
			this.serverSocketChannel.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("receiverConnection closed");
		this.setChanged();
		notifyObservers(this);
	}
	
	private synchronized int getUnboundPort() throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();
		ServerSocket s = channel.socket();
		int randomPort = 0;
		Random random = new Random();
		randomPort = random.nextInt(65535 - 1024) + 1024;
		InetSocketAddress addr = new InetSocketAddress(hostAddress, randomPort);
		while( !s.isBound() ) {			
			try {
				s.bind(addr);
			}
			catch(IOException e) {
				randomPort = random.nextInt(65535 - 1024) + 1024;
				addr = new InetSocketAddress(hostAddress, randomPort);
			}			
		}
		try {
			s.close();
		}
		catch(IOException e) { }
		
		return randomPort;
	}
	
	private void accept(SelectionKey key) throws IOException {
		  System.out.println("receiver accept!");
		    // For an accept to be pending the channel must be a server socket channel.
		    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		    // Accept the connection and make it non-blocking
		    SocketChannel socketChannel = serverSocketChannel.accept();
		    socketChannel.configureBlocking(false);

		    // Register the new SocketChannel with our Selector, indicating
		    // we'd like to be notified when there's data waiting to be read
		    saveBuffers.put(socketChannel, ""); 
		    socketChannel.register(this.selector, SelectionKey.OP_READ);
	}
	
	private void read(SelectionKey key) throws IOException {
	    SocketChannel socketChannel = (SocketChannel) key.channel();
	    ByteBuffer buff = ByteBuffer.allocate(15000000);
	    buff.clear();
		int numRead;
		try {
		   buff.clear();
		   numRead = socketChannel.read(buff);
		} catch (IOException e) {
		// The remote forcibly closed the connection, cancel
		   key.cancel();
		   socketChannel.close();
		   saveBuffers.remove(socketChannel);
		   return;
		}

		if (numRead == -1) {
		// Remote entity shut the socket down cleanly. Do the
		// same from our end and cancel the channel.
		   key.channel().close();
		   key.cancel();
		   saveBuffers.remove(socketChannel);
		   return;
	    }
		    
		byte[] data = new byte[numRead];
		    
		buff.rewind();
		buff.get(data, 0, numRead);
		    
		List<String> messages = preParse(data, socketChannel);
		for (String m : messages) {
		  	Message msg = MSRPUtil.createMessageFromString(m);
		 	try {
		 		Session s = msrpStack.findSession(msg.getToPath().toString()+msg.getFromPath().toString());
		 		if (s != null) {
		 			s.putMessageIntoIncomingMessageQueue(msg);
		 		}
		 		else {
		 			System.out.println("session is null");
		 		}		 		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }	    
	}
	  
	private Selector initSelector() throws IOException {
	    Selector socketSelector = SelectorProvider.provider().openSelector();

	    this.serverSocketChannel = ServerSocketChannel.open();
	    serverSocketChannel.configureBlocking(false);

	    this.port = getUnboundPort();
	    InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
	    serverSocketChannel.socket().bind(isa);

	    serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

	    return socketSelector;
	}
	
	private List<String> preParse(byte[] data, SocketChannel channel) {
		List<String> messages = new ArrayList<String>();
		int successProcessedByteCount = 0;
		String m = new String(data);
		String saveBuff = saveBuffers.get(channel);
		
		if (!"".equals(saveBuff)) {
			m = saveBuff + m;
			saveBuffers.put(channel, "");
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(m.getBytes(), 0, m.length());
		buffer.limit(buffer.capacity());
		
		int state = 0;
		int byteCounter = 0;
		while (buffer.hasRemaining()) {
			byte b = buffer.get();			
			byteCounter++;
			switch(state) {
				case 0 : 
						if (b == '\r') state = 1;
				
						break;
				case 1 :
						if (b == '\n') state = 2;
						else state = 0;
				
						break;
				case 2 :
						if (b == '-')	state = 3;
						else state = 0;

				 		break;
				case 3 :
						if (b == '-')	state = 4;
						else state = 0;
				
		 		 		break;
				case 4 :
						if (b == '-')	state = 5;
				 		else state = 0;

 		 		 		break;
				case 5 :
						if (b == '-')	state = 6;
				 		else state = 0;
				
 		 		 		break;
				case 6 : 
						if (b == '-') state = 7;
				 		 else state = 0;
 		 		 		 
						 break;
				case 7 : 
						if (b == '-')	state = 8;
				 		 else state = 0;
 		 		 
						 break;
				case 8 : 
						if (b == '-')	state = 9;
				 		 else state = 0;
 		 		 
						 break;
				case 9 :
						if (b == '$' || b == '#' || b == '+') state = 10;
						
				case 10 : 
						if (state == 10) {
							byte[] temp = new byte[byteCounter];
							buffer.position(buffer.position() - byteCounter);
							buffer.get(temp, 0, byteCounter);
							String chunk = new String(temp);
							messages.add(chunk);
							state = 0;
							successProcessedByteCount += byteCounter;
							byteCounter = 0;
						}
			    }			
		}
				
		if (byteCounter != 0) {
			byte[] save = new byte[byteCounter];
			buffer.position(buffer.position() - byteCounter);
			buffer.get(save, 0, byteCounter);
			saveBuff = new String(save);
			saveBuffers.put(channel, saveBuff);
		}
		
		return messages;
	}
	
	protected void start() {
		if (!isRunning()) {
			System.out.println("ReceiverConnection start! listen on: " + this.hostAddress.getHostAddress() + ":" + this.port);
			Thread t = new Thread(this);
			t.start();
		}
	}
	
	protected void stop() {
		running = false;
		this.selector.wakeup();
	}

	protected boolean isRunning() {
		return running;
	}

	protected InetAddress getHostAddress() {
		return hostAddress;
	}

	protected int getPort() {
		return port;
	}

}