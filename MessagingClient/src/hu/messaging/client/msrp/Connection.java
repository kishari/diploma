package hu.messaging.client.msrp;

import java.nio.channels.SocketChannel;
import java.net.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.Selector;
import java.io.*;
import java.nio.*;

public class Connection {

	public static final int OUTPUTBUFFERLENGTH = 2048;

	private SocketChannel socketChannel = null;
	
	private Selector selector = null;
	
	private URI localURI = null;
	
	private int port = 60301;
	public Connection() {}
	
	public Connection(String address) throws URISyntaxException, IOException {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		this.selector = SelectorProvider.provider().openSelector();
		
		InetSocketAddress socketAddr = new InetSocketAddress(address, port);
		
		socketChannel.connect(socketAddr);
		
		socketChannel.configureBlocking(false);
		
	}
	

	protected boolean isEstablished() {		
		return socketChannel == null ? false : socketChannel.isConnected();
	}
	
	protected boolean isBound() {
		return socketChannel == null ? false : socketChannel.socket().isBound();
	}
	
	protected int writeDataToSocket(byte[] outData) {
		System.out.println("writeDataToSocket");
		int outDataLength = outData.length;
		byte[] output = new byte[OUTPUTBUFFERLENGTH];
		ByteBuffer outByteBuffer = ByteBuffer.wrap(output);
		outByteBuffer.clear();
		outByteBuffer.limit(outDataLength);
		
		int wroteNrBytes = 0;
		try {
			while (wroteNrBytes != outDataLength)
	            wroteNrBytes += socketChannel.write(outByteBuffer);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return wroteNrBytes;
	}

	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public URI getLocalURI() {
		return localURI;
	}

	public void setLocalURI(URI localURI) {
		this.localURI = localURI;
	}

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}
}
