package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.msrp.util.MSRPUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

public class OutgoingMessageProcessor extends Observable implements Runnable {

	//TESZTHEZ START
	private File contentFile = null;
	private File recreatedContentFile = null;
	private String fileExtension = null;
	//TESZTHEZ END
	
	private static int counter = 0;
	private boolean running = false;
	private BlockingQueue<CompleteMessage> outgoingMessageQueue;
	private Session session;
	
	public OutgoingMessageProcessor(BlockingQueue<CompleteMessage> outgoingMessageQueue, 
									Session session,
									TransactionManager transactionManager ) {
		this.outgoingMessageQueue = outgoingMessageQueue;
		this.session = session;
		this.addObserver(transactionManager);
	}
	
	public void run() {
		while (running) {
			try {
				//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
				//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
				CompleteMessage data = this.outgoingMessageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 
				if (data != null) {
					processOutgoingMessage(data);
				}				
			}
			catch(InterruptedException e) {}
			
		}
	}
	
	private void processOutgoingMessage(CompleteMessage completeMessage) {
		this.printToFile(completeMessage.getContent(), false);
		System.out.println("OutgoingProcessor processOutgoingMessage... ");

		int chunkSize = Constants.chunkSize;
		
		List<byte[]> chunks = splitMessageToChunks(completeMessage, chunkSize);
		
		System.out.println("num of chunks: " + chunks.size());
		
		int offset = 1;
		char endToken = '+';
		String tId = "";
		String messageId = MSRPUtil.generateRandomString(Constants.messageIdLength);
		List<Request> requestList = new ArrayList<Request>();
		
		for (byte[] chunk : chunks) {			
			counter++;
			tId = MSRPUtil.generateRandomString(Constants.transactionIdLength);
			
			if (chunk.length < chunkSize) {
				endToken = '$';
			}
			
			Request req = MSRPUtil.createRequest(chunk, session.getLocalUri(), session.getRemoteUri(),
												  tId, messageId, 
												  offset, chunk.length, completeMessage.getContent().length, 
												  endToken);
			
			Message mTest = MSRPUtil.createMessageFromString(req.toString());
			Request r = (Request)mTest;
			this.printToFile(Base64.decodeBase64(r.getContent()), true);
			
			offset += chunk.length;
			requestList.add(req);
									
		}
		this.setChanged();
		this.notifyObservers(requestList);
	}
	
	private List<byte[]> splitMessageToChunks(CompleteMessage message, int chunkSize) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		
		byte[] base64EncodedContent = Base64.encodeBase64(message.getContent());
		
		double div = base64EncodedContent.length / (double) chunkSize;
		int numOfChunks = (int)Math.floor(div);
		
		int i = 0;
		int index = 0;
		while( i < numOfChunks ) {
			byte[] c = new byte[chunkSize];
			System.arraycopy(base64EncodedContent, index, c, 0, chunkSize);
			chunks.add(c);
			index += chunkSize;
			i++;
		}
		
		int lastChunkSize = base64EncodedContent.length - index;
		byte[] c = new byte[lastChunkSize];
		System.arraycopy(base64EncodedContent, index, c, 0, lastChunkSize);
		chunks.add(c);		
		
		return chunks;
	}
	
	public void start() {
		this.running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		this.running = false;
	}	
	
	//TESZT
	public void printToFile(byte[] data, boolean recreated) {
		try {
			OutputStream out = null;
			if (recreated) {
				contentFile = new File("c:\\serverContentFile." + fileExtension);
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			}
			else {
				recreatedContentFile = new File("c:\\serverRecreatedContentFile." + fileExtension);
				out = new BufferedOutputStream(new FileOutputStream(recreatedContentFile, true));
			}
			
			out.write(data);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
	
}
