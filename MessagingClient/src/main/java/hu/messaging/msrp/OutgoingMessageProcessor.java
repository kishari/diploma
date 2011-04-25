package hu.messaging.msrp;

import hu.messaging.msrp.model.*;
import hu.messaging.msrp.util.MSRPUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.OutputStream;

import java.util.Map;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

public class OutgoingMessageProcessor extends Observable implements Runnable {

//TESZTHEZ START
	private File contentFile = null;
	private File recreatedContentFile = null;
//TESZTHEZ END
	
	private boolean running = false;
	private BlockingQueue<CompleteMSRPMessage> outgoingMessageQueue;
	private TransactionManager transactionManager;
	
	public OutgoingMessageProcessor(BlockingQueue<CompleteMSRPMessage> outgoingMessageQueue, 
									TransactionManager transactionManager) {
		this.outgoingMessageQueue = outgoingMessageQueue;
		this.transactionManager = transactionManager;
		this.addObserver(transactionManager);
	}
	
	public void run() {
		while (running) {
			try {
				//Vár 300 ms-ot adatra, ha nincs adat, akkor továbblép
				//Ez azért kell, hogy a stop metódus meghívása után fejezze be a ciklus a futást (ne legyen take() miatt blokkolva)
				CompleteMSRPMessage data = this.outgoingMessageQueue.poll(Constants.queuePollTimeout, TimeUnit.MILLISECONDS); 
				if (data != null) {
					processOutgoingMessage(data);
				}				
			}
			catch(InterruptedException e) {}
			
		}
		this.setChanged();
		this.notifyObservers();
	}
	
	@SuppressWarnings("unchecked")
	private void processOutgoingMessage(CompleteMSRPMessage fullMessage) {

		this.printToFile(fullMessage.getContent(), false);
		System.out.println(getClass().getSimpleName() + " processOutgoingMessage...");

		int chunkSize = Constants.chunkSize;
		
		Map<String, Object> map = splitMessageToChunks(fullMessage, chunkSize);
		
		List<byte[]> chunks = (List<byte[]>)map.get(Keys.chunkList);
		int totalContentLength = (Integer)map.get(Keys.contentLength);
		
		System.out.println(getClass().getSimpleName() + " num of chunks: " + chunks.size());
		System.out.println(getClass().getSimpleName() + " content total length: " + totalContentLength);
		
		int offset = 1;
		char endToken = '+';
		String tId = "";
		String messageId = MSRPUtil.generateRandomString(Constants.messageIdLength);
		List<Request> requestList = new ArrayList<Request>();
		
		for (byte[] chunk : chunks) {			
			tId = MSRPUtil.generateRandomString(Constants.transactionIdLength);
			
			if (chunk.length < chunkSize) {
				endToken = '$';
			}
			
			Request req = MSRPUtil.createRequest(chunk, transactionManager.getSession().getLocalUri(), transactionManager.getSession().getRemoteUri(),
												  tId, messageId, 
												  offset, chunk.length, totalContentLength,
												  endToken);
			
//>>>> TESZT
			Message mTest = MSRPUtil.createMessageFromString(req.toString());
			Request r = (Request)mTest;
			//System.out.println(r.getContent().length);
			this.printToFile(Base64.decodeBase64(r.getContent()), true);
//<<<< TESZT
			
			offset += chunk.length;
			requestList.add(req);									
		}
		
		Collections.sort(requestList);
		this.setChanged();
		this.notifyObservers(requestList); //Átküldjük a TransactionManagernek a requestList-et
	}
	
	private Map<String, Object> splitMessageToChunks(CompleteMSRPMessage message, int chunkSize) {
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
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Keys.chunkList, chunks);
		map.put(Keys.contentLength, new Integer(base64EncodedContent.length));
		
		return map;
	}
	
	protected void start() {
		this.running = true;
		new Thread(this).start();
	}
	
	protected void stop() {
		this.running = false;
	}	
	

//>>>>>>>>>>>TESZT
	public void printToFile(byte[] data, boolean recreated) {
		try {
			OutputStream out = null;
			if (!recreated) {
				contentFile = new File("c:\\diploma\\testing\\clientContentFile.mp3");
				out = new BufferedOutputStream(new FileOutputStream(contentFile, true));
			}
			else {
				recreatedContentFile = new File("c:\\diploma\\testing\\clientRecreatedContentFile.mp3");
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
//<<<<<<<<<<<<<<TESZT	
}
