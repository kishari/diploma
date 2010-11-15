package hu.messaging.msrp;

import hu.messaging.msrp.util.MSRPUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Runnable {

	private BlockingQueue<Message> incomingMessageQueue;
	private BlockingQueue<byte[]> outgoingMessageQueue;
	private SenderConnection senderConnection;
	private Session session;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<byte[]> outgoingMessageQueue,
			  				  SenderConnection senderConnection,
			  				  Session session) {
		
			this.incomingMessageQueue = incomingMessageQueue;
			this.outgoingMessageQueue = outgoingMessageQueue;
			this.senderConnection = senderConnection;
			this.session = session;
	}
	
	public void run() {
		while(true) {
				Message mIn = incomingMessageQueue.poll();
				if (mIn != null) {
					System.out.println("TransactionManager kivett egy üzenetet a session incomingMessageQueue-bol:");
					System.out.println(mIn.toString());
					System.out.println();
					
					processIncomingMessage(mIn);
				}
				byte[] mOut = outgoingMessageQueue.poll();
				if (mOut != null) {
					processOutgoingMessage(mOut);
				}
		}
	}
	
	private void processIncomingMessage(Message chunk) {
		System.out.println("TransactionManager.processIncomingMessage... ");
		
		if (chunk.getMethod() == Constants.methodSEND) {
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is 'send' message!");
			Request req = (Request) chunk;
			System.out.println(req.toString());
			Response ack = createAcknowledgement(req);
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is 'send' message! Ack created: \n" + ack.toString() );
		}
		else if ( chunk.getMethod() == Constants.method200OK ){
			System.out.println("TransactionManager.processIncomingMessage. Incoming message is '200 OK' message!");
			Response resp = (Response) chunk;
			System.out.println(resp.toString());
		}		
	}
	
	private void processOutgoingMessage(byte[] completeMessage) {
		System.out.println("TransactionManager.processOutgoingMessage... ");
		System.out.println(new String(completeMessage));

		int chunkSize = 10;
		
		List<byte[]> chunks = splitMessageToChunks(completeMessage, chunkSize);
		
		int offset = 1;
		char endToken = '+';
		String tId = "transactionId";
		String messageId = "messageId";
		for (byte[] chunk : chunks) {
			if (chunk.length < chunkSize) {
				endToken = '$';
			}
			
			Request mOut = MSRPUtil.createRequest(chunk, session.getLocalUri(), session.getRemoteUri(),
												  tId, messageId, 
												  offset, chunk.length, completeMessage.length, 
												  endToken);
			offset += chunk.length;
			System.out.println("TransactionManager.processOutgoingMessage: after message create: \n" + mOut.toString());
			try {
				this.senderConnection.sendChunk(mOut.toString().getBytes());
			}
			catch (IOException e) {}
			
		}
		
		
	}
	
	private Response createAcknowledgement(Request incomingMessage) {
		Response ack = new Response();
		
		ack.setMethod(Constants.method200OK);
		ack.setToPath(incomingMessage.getFromPath());
		ack.setFromPath(incomingMessage.getToPath());
		ack.setTransactionId(incomingMessage.getTransactionId());
		ack.setEndToken('$');
				
		return ack;
	}
	
	private List<byte[]> splitMessageToChunks(byte[] message, int chunkSize) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		//System.out.println("message length: " + message.length);
		double div = message.length / (double) chunkSize;
		//System.out.println("Oszto (double)" + div);
		int numOfChunks = (int)Math.floor(div);
		//System.out.println("numOfChunks: (floor) " + numOfChunks);
		
		int i = 0;
		int index = 0;
		while( i < numOfChunks ) {
			byte[] c = new byte[chunkSize];
			//System.out.println("copy to chunk from: " + index + " length " + chunkSize);
			
			for (int j = 0; j < chunkSize; j++) {
				 char ch = (char)message[index + j];
				//System.out.print(ch);
			}
			//System.out.println();
			System.arraycopy(message, index, c, 0, chunkSize);
			chunks.add(c);
			index += chunkSize;
			i++;
		}
		
		int lastChunkSize = message.length - index;
		byte[] c = new byte[lastChunkSize];
		System.arraycopy(message, index, c, 0, lastChunkSize);
		chunks.add(c);
		//System.out.println("last:");
		for (int j = 0; j < lastChunkSize; j++) {
			 char ch = (char)message[index + j];
			//System.out.print(ch);
		}
		
		return chunks;
	}

}
