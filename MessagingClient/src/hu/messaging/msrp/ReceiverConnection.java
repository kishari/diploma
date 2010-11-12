package hu.messaging.msrp;

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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class ReceiverConnection implements Runnable {

	private int sumOfReadedByte = 0;
	private int countOfRead = 0;
	
	private boolean running = false;
	
	private String saveBuffer = new String();
	
	private ByteBuffer buff = null;
	
	private Connections parent;
	
	private InetAddress hostAddress;
	private int port = 0;
	
	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;
	
	public ReceiverConnection(InetAddress localHostAddress, Connections parent) throws IOException {
		this.hostAddress = localHostAddress;
		this.parent = parent;
		this.selector = initSelector();
		buff = ByteBuffer.allocate(1000000);
		buff.clear();
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
		//System.out.println("getUnBoundPort: " + randomPort);
		return randomPort;
	}
	public void run() {
		setRunning(true);
		
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
		try {
			this.serverSocketChannel.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
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
		    socketChannel.register(this.selector, SelectionKey.OP_READ);
	}
	
	private void read(SelectionKey key) throws IOException {
		//System.out.println("receiver read from channel!");
	    SocketChannel socketChannel = (SocketChannel) key.channel();
		    
		    // Clear out our read buffer so it's ready for new data
		    
		    // Attempt to read off the channel
		    int numRead;
		    try {
		      buff.clear();
		      numRead = socketChannel.read(buff);
		      sumOfReadedByte += numRead;
		      countOfRead++;
		      //System.out.println("number of readed byte: " + numRead);
		    } catch (IOException e) {
		      // The remote forcibly closed the connection, cancel
		      // the selection key and close the channel.
		      key.cancel();
		      socketChannel.close();
		      return;
		    }

		    if (numRead == -1) {
		      // Remote entity shut the socket down cleanly. Do the
		      // same from our end and cancel the channel.
		      key.channel().close();
		      key.cancel();
		      return;
		    }
		    //System.out.println(readBuffer.position());
		    //this.readBuffer.rewind();
		    
		    byte[] data = new byte[numRead];
		   // System.out.println(readBuffer.position());
		    
		    buff.rewind();
		    //System.out.println(b.toString());
		    buff.get(data, 0, numRead);
		    //System.out.println(b.toString());
		    
		    List<String> messages = preParse(data);
		    int i = 0;
		    for (String m : messages) {
		    	i++;
		    	//System.out.println("Preparser után az adat " + i + ":\n" + m + " x");
		    	MSRPUtil.createMessage(m);
		    }
	    
	}
	  
	private Selector initSelector() throws IOException {
	    Selector socketSelector = SelectorProvider.provider().openSelector();

	    this.serverSocketChannel = ServerSocketChannel.open();
	    serverSocketChannel.configureBlocking(false);

	    setPort(getUnboundPort());
	    InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
	    serverSocketChannel.socket().bind(isa);

	    serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

	    return socketSelector;
	}
	
	private List<String> preParse(byte[] data) {
		List<String> messages = new ArrayList<String>();
		int successProcessedByteCount = 0;
		String m = new String(data);
		if (!"".equals(saveBuffer)) {
			m = saveBuffer + m;
			saveBuffer = "";
		}
		
		//System.out.println("preparser üzenet: " + m);
		
		ByteBuffer buffer = ByteBuffer.wrap(m.getBytes(), 0, m.length());
		//System.out.println("preparser data length: " + buffer.capacity());
		buffer.limit(buffer.capacity());
		//System.out.println("data limit: " + buffer.limit());
		
		int state = 0;
		int byteCounter = 0;
		int messageCounter = 0;
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
						//System.out.println("case 9, b: " + (char)b);
						//System.out.println(data.remaining());
						if (b == '$' || b == '#' || b == '+') state = 10;
						
				case 10 : 
						if (state == 10) {
							//System.out.println("case 10");
							byte[] temp = new byte[byteCounter];
							buffer.position(buffer.position() - byteCounter);
							//System.out.println("Lekérjük a bufferból a " + buffer.position() + ". bytetól " + byteCounter + " byteot");
							buffer.get(temp, 0, byteCounter);
							String chunk = new String(temp);
							messages.add(chunk);
							state = 0;
							messageCounter++;
							successProcessedByteCount += byteCounter;
							byteCounter = 0;
						}
			    }			
		}
		
		//System.out.println("success processed byte: " + succesProcessedByteCount);		
		if (byteCounter != 0) {
			byte[] save = new byte[byteCounter];
			buffer.position(buffer.position() - byteCounter);
			buffer.get(save, 0, byteCounter);
			saveBuffer = new String(save);
			//System.out.println("Maradék adat: " + saveBuffer);
		}
		return messages;
	}
	
	public void start() {
		if (!isRunning()) {
			System.out.println("ReceiverConnection start! listen on: " + this.hostAddress.getHostAddress() + ":" + this.port);
			Thread t = new Thread(this);
			//t.setDaemon(true);
			t.start();
		}
	}
	
	public void stop() {
		setRunning(false);
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		System.out.println("setrunning " + running);
		this.running = running;
	}

	public InetAddress getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(InetAddress hostAddress) {
		this.hostAddress = hostAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}