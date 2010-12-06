package hu.messaging.msrp;

import hu.messaging.Constants;
import hu.messaging.dao.MessagingDAO;
import hu.messaging.msrp.event.MSRPEvent;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

public class TransactionManager implements Observer {

	private static int reqCounter = 0;
	private boolean isSentMessageChunk = false;
	private Session session;
	private Map<String, Request> acknowledgedMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> sentMessages = Collections.synchronizedMap(new HashMap<String, Request>());
	private Map<String, Request> incomingMessages = Collections.synchronizedMap(new HashMap<String, Request>());


	private File outTempBinFile = new File("c:\\outTemp.png");
	private File outTemptxtFile = new File("c:\\OutTemp.txt");	
	
	private OutgoingMessageProcessor outgoingMessageProcessor;
	private IncomingMessageProcessor incomingMessageProcessor;
	
	
	public TransactionManager(BlockingQueue<Message> incomingMessageQueue, 
			  				  BlockingQueue<CompleteMessage> outgoingMessageQueue,
			  				  Session session) {
		
			this.session = session;
			outgoingMessageProcessor = new OutgoingMessageProcessor(outgoingMessageQueue, session, this);
			outgoingMessageProcessor.start();
			
			incomingMessageProcessor = new IncomingMessageProcessor(incomingMessageQueue, session, this);
			incomingMessageProcessor.start();
			
	}
	
	public void stop() {
		this.outgoingMessageProcessor.stop();
		this.incomingMessageProcessor.stop();
	}

	public void update(Observable o, Object obj) {
		//System.out.println("TManager update: " + o.toString());
		//System.out.println(o.toString());
		if (o.toString().contains("OutgoingMessageProcessor")) {
			Request r = (Request) obj;
			this.sentMessages.put(r.getTransactionId(), r);		
			isSentMessageChunk = true;
		}
		else if (o.toString().contains("IncomingMessageProcessor")) {
			Message m = (Message) obj;
			if (m.getMethod() == Constants.methodSEND) {
				Request req = (Request) m;
				this.incomingMessages.put(req.getTransactionId(), req);		
				reqCounter++;
				//System.out.println("TManager reqCounter: " + reqCounter);
				if (req.getEndToken() == '$') {
					System.out.println("Utolso csomag is megjott");
					List<Request> chunks = new ArrayList<Request>();
					
					int byteCount = 0;
					for (String key : this.incomingMessages.keySet()) {
						chunks.add(this.incomingMessages.get(key));
						byteCount += this.incomingMessages.get(key).getContent().length;
					}
										
					Collections.sort(chunks);	
					
					for (Request r : chunks) {
						printToFile(r.getFirstByte() + "-" + r.getLastByte() + "\r\n");
					}
					
					ByteBuffer b = ByteBuffer.allocate(byteCount);
					b.clear();
					byte[] content = new byte[byteCount];
					
					for (Request r : chunks) {
						b.put(r.getContent());
					}
					
					b.rewind();
					b.get(content);
					CompleteMessage msg = new CompleteMessage(req.getMessageId(), 
															  content,
															  null);
					new MessagingDAO().insertMessage(msg);
					
					printToFile(msg.getContent());
					
					
				}
			}
			else if (m.getMethod() == Constants.method200OK) {
				Response resp = (Response) m;
				Request ackedReq = this.sentMessages.remove(resp.getTransactionId());
				this.acknowledgedMessages.put(ackedReq.getTransactionId(), ackedReq);
				
				if (isAckedTotalSentMessage()) {
					System.out.println("minden csomag nyugtazva");
					this.isSentMessageChunk = false;
					MSRPEvent event = new MSRPEvent("sentSuccess", MSRPEvent.messageSentSuccessCode);
					event.setMessageId(ackedReq.getMessageId());
					this.session.getMsrpStack().notifyListeners(event);
					this.acknowledgedMessages.clear();
				}
			}
		}		
	}
	
	private boolean isAckedTotalSentMessage() {
		boolean t = this.sentMessages.isEmpty() && this.isSentMessageChunk;
		return t;
	}
	
	public void printToFile(byte[] data) {
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(outTempBinFile, true));
			out.write(data);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}

	public void printToFile(String text) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outTemptxtFile, true));
			out.append(text);
			out.flush();					
			out.close();
		}
		catch(IOException e) { 
			e.printStackTrace();
		}		
	}
	
}
