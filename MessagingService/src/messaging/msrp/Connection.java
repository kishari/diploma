package messaging.msrp;

import java.nio.channels.SocketChannel;
import java.net.*;
import java.nio.channels.spi.SelectorProvider;
import java.io.*;
import java.nio.*;

public class Connection {

	public static final int OUTPUTBUFFERLENGTH = 2048;

	private SocketChannel socketChannel = null;
	
	private URI localURI = null;
	
	public Connection() {}
	
	public Connection(SocketChannel newSocketChannel) throws URISyntaxException {
		this.socketChannel = newSocketChannel;
		Socket socket = socketChannel.socket();
		URI newLocalURI = new URI("msrp", null, 
								  socket.getInetAddress().getHostAddress(),
								  socket.getLocalPort(),
								  null, null, null);
		localURI = newLocalURI;
	}
	
	public Connection(InetAddress address) throws URISyntaxException, IOException {
		socketChannel = SelectorProvider.provider().openSocketChannel();
		Socket socket = socketChannel.socket();
		InetSocketAddress socketAddr = new InetSocketAddress(address, 5123);
		
		try{
			socket.bind(socketAddr);
		}
		catch(IOException e){ 
			System.err.println("IOException in class Connection!");
			e.printStackTrace();
		}
		
		URI newLocalURI = new URI("msrp", null, 
				  				  address.getHostAddress(),
				  				  socket.getLocalPort(),
				  				  null, null, null);
		localURI = newLocalURI;
	}
	
	protected boolean isEstablished() {		
		return socketChannel == null ? false : socketChannel.isConnected();
	}
	
	protected boolean isBound() {
		return socketChannel == null ? false : socketChannel.socket().isBound();
	}
	
	protected int writeDataToSocket(byte[] outData) {
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
}
