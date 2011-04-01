package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.msrp.util.MSRPUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReceiverConnection extends Observable implements Runnable {
	private boolean running = false;	
	private MSRPStack msrpStack = null;
	private Map<SocketChannel, String> saveBuffers = new HashMap<SocketChannel, String>();
	private InetAddress hostAddress;
	private int port = 0;	
	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;
	private Parser preParser;
	private Router router;
	private ByteBuffer buff;
	
	private Date testDate;
	
	int parsedCounter = 0;
	
	public ReceiverConnection(InetAddress localHostAddress, MSRPStack msrpStack) throws IOException {
		this.hostAddress = localHostAddress;
		this.msrpStack = msrpStack;
		this.selector = initSelector();
		this.preParser = new Parser();
		this.router = new Router();
		this.addObserver(msrpStack.getConnections());
		buff = ByteBuffer.allocate(Constants.receiverBufferSize);
		buff.clear();
		
	}

	public void run() {
		setRunning(true);
		
		this.preParser.start();
		this.router.start();
		
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
		
		this.preParser.stop();
		this.router.stop();
		
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
		  testDate = new Date();
		  parsedCounter = 0;
		  
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
	    //ByteBuffer buff = ByteBuffer.allocate(100000);
	    buff.clear();
		int numRead = 0;
		try {
		   buff.clear();
		   numRead = socketChannel.read(buff);
		   //System.out.println("numReadByte:" + numRead);
		} catch (IOException e) {
			System.out.println("Exception van!");
			e.printStackTrace();
		// The remote forcibly closed the connection, cancel
		   key.cancel();
		   socketChannel.close();
		   saveBuffers.remove(socketChannel);
		   return;
		} catch (Exception e) {
			e.printStackTrace();
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
		    
		byte[] rawData = new byte[numRead];
		    
		buff.rewind();
		buff.get(rawData, 0, numRead);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Keys.socketChannel, socketChannel);
		map.put(Keys.rawData, rawData);
		
		this.preParser.getMessageQueue().add(map);
   
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
	
	public void start() {
		if (!isRunning()) {
			System.out.println("ReceiverConnection start! listen on: " + this.hostAddress.getHostAddress() + ":" + this.port);
			Thread t = new Thread(this);
			t.start();
		}
	}
	
	public void stop() {
		setRunning(false);
		this.selector.wakeup();
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
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

	public MSRPStack getMsrpStack() {
		return msrpStack;
	}
	
	public Map<SocketChannel, String> getSaveBuffers() {
		return saveBuffers;
	}
	
	private class Parser implements Runnable {
				
		private ByteBuffer parserBuffer;
		private BlockingQueue<Map<String, Object>> messageQueue = new LinkedBlockingQueue<Map<String, Object>>();
		private boolean isRunning = false;
		public void run() {
			while (isRunning) {
				try {
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
					Map<String, Object> map = messageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 
					if (map != null) {
						//System.out.println("parser: parse msg");
						byte[] rawData = (byte[]) map.get(Keys.rawData);
						SocketChannel channel = (SocketChannel) map.get(Keys.socketChannel);
						List<String> chunks = preParse(rawData, channel);
						//System.out.println("Parser rawData != null");
						//System.out.println("Parser chunks: " + chunks.size());
						//parsedCounter += chunks.size();
						//System.out.println("all parsed: " + parsedCounter);
						ReceiverConnection.this.router.getParsedMessageQueue().addAll(chunks);
					}				
				}
				catch(InterruptedException e) {}				
			}
		}

		public void start() {
			isRunning = true;
			Thread t = new Thread(this);
			//t.setDaemon(true);
			t.start();
		}
		public void stop() {
			isRunning = false;
		}

		public BlockingQueue<Map<String, Object>> getMessageQueue() {
			return messageQueue;
		}
		
		private List<String> preParse(byte[] rawData, SocketChannel channel) {
			List<String> messages = new ArrayList<String>();
			int successProcessedByteCount = 0;
			String m = new String(rawData);
			//printToFile(m.getBytes());	
			
			//ByteBuffer buffer = ByteBuffer.wrap(m.getBytes(), 0, m.length());
			//parserBuffer = ByteBuffer.wrap(m.getBytes(), 0, m.length());
			//parserBuffer.limit(parserBuffer.capacity());
			
			String saveBuff = saveBuffers.get(channel);
			
			if (!"".equals(saveBuff)) {
				System.out.println("saveBuffer nem ures");
				saveBuff += m;
				m = new String(saveBuff);
				//System.out.println("saveBuffer utan msg: " + m);
				saveBuffers.put(channel, new String(""));
			}
			
			parserBuffer = ByteBuffer.wrap(m.getBytes(), 0, m.length());
			parserBuffer.limit(parserBuffer.capacity());
			
			int state = 0;
			int byteCounter = 0;
			while (parserBuffer.hasRemaining()) {
				byte b = parserBuffer.get();			
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
								parserBuffer.position(parserBuffer.position() - byteCounter);
								parserBuffer.get(temp, 0, byteCounter);
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
				parserBuffer.position(parserBuffer.position() - byteCounter);
				parserBuffer.get(save, 0, byteCounter);
				saveBuff = new String(save);
				System.out.println("savebuff length: " + saveBuff.length());
				printToFile(saveBuff.getBytes());	
				saveBuffers.put(channel, saveBuff);
			}
			
			return messages;
		}
	}
	
	
	private class Router implements Runnable {

		private BlockingQueue<String> parsedMessageQueue = new LinkedBlockingQueue<String>();
		private boolean isRunning = false;
		public void run() {
			String parsedMsg = "";
			while (isRunning) {
					//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
					//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
				
				String prevMsg = null;
				if (parsedMsg != null) {
					//prevMsg = new String(parsedMsg);
				}
					
					parsedMsg = null;
					try {
						parsedMsg = parsedMessageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} 
					if (parsedMsg != null) {
						//System.out.println("router: create msg from parsed string msg");
						Message msg = MSRPUtil.createMessageFromString(parsedMsg);
						if (msg == null) {
							System.out.println("msg null");
							System.out.println(parsedMsg);
							//System.out.println(prevMsg);
						}
					 	try {
					 		Session s = getMsrpStack().findSession(msg.getToPath().toString()+msg.getFromPath().toString());
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
		}

		public void start() {
			isRunning = true;
			Thread t1 = new Thread(this);
			//t.setDaemon(true);
			t1.start();
		}
		public void stop() {
			isRunning = false;
		}

		public BlockingQueue<String> getParsedMessageQueue() {
			return parsedMessageQueue;
		}
		
	}
	
	//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data) {
		try {
			OutputStream out = null;
			File recreatedContentFile = new File("c:\\diploma\\testing\\saveBufferData-" + testDate.getTime() + ".txt");
			out = new BufferedOutputStream(new FileOutputStream(recreatedContentFile, true));
			
			out.write(data);
			out.write("\r\n**********************************************************************\r\n".getBytes());
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
//<<<<<<<<<<<<<<TESZT
	

}