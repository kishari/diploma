package hu.messaging.msrp.util;

import hu.messaging.msrp.Constants;
import hu.messaging.msrp.Message;
import hu.messaging.msrp.Request;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSRPUtil {
	private static Pattern methodPattern =  Pattern.compile("(^MSRP) ([\\p{Alnum}]{8,20}) ([\\p{Upper}]{1,20})\r\n(.*)", Pattern.DOTALL);
	private static Pattern toPathPattern =  Pattern.compile("(To-Path:) (msrp://[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}:[\\p{Digit}]{4,5}/([\\p{Alnum}]{10,50});tcp)\r\n");
	private static Pattern fromPathPattern =  Pattern.compile("(From-Path:) (msrp://[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}:[\\p{Digit}]{4,5}/([\\p{Alnum}]{10,50});tcp)\r\n");
	private static Pattern messageIdPattern =  Pattern.compile("(Message-ID:) ([\\p{Alnum}]{10,50})\r\n");
	private static Pattern byteRangePattern =  Pattern.compile("(Byte-Range:) ([\\p{Digit}]{1,})-([\\p{Digit}]{1,})" +
															   "/([\\p{Digit}]{1,})\r\n");
	//private static Pattern contentTypePattern =  Pattern.compile("(Content-Type:) ([\\p{Alpha}]{1,}/[\\p{Alpha}]{1,})\r\n\r\n");
	private static Pattern contentPattern =  Pattern.compile("(Content-Type:) ([\\p{Alpha}]{1,}/[\\p{Alpha}]{1,})\r\n\r\n" +
															 "(.*)\r\n");
	private static Pattern endLinePattern =  Pattern.compile("\r\n([-]{7})([\\p{Alnum}]{8,20})([+$#]{1})");
	
	public static List<Message> createMessages(byte[] completeMessage) {
		System.out.println("createMessages...");
		int chunkSize = 100;
		int index = 0;
		List<Message> chunks = new ArrayList<Message>();
		double div = completeMessage.length / (double) chunkSize;
		System.out.println("Oszto (double)" + div);
		int numOfChunks = (int)Math.floor(div);
		System.out.println("numOfChunks: " + numOfChunks);
		//chunks.add(e)
		return null;
	}


	public static Message createMessage(String msg) {
		System.out.println("create message from: " + msg);
		Message m = new Message();
		Request req = new Request();
		long startTime = new Date().getTime();
		System.out.println("createMessage started: " + startTime);
		
		//System.out.println("MESSAGEUTIL inc msg: \n" + msg);
		
		Matcher matcher = methodPattern.matcher(msg);
		
		String method = null;
		if (matcher.find()) {
			method = matcher.group(3);
		}
		if ("SEND".equals(method)) {			
			req.setMethod(Constants.methodSEND);
			
			req.setTransactionId(matcher.group(2));
			System.out.println("nyito tId: <" + matcher.group(2) + ">");
			
			matcher = toPathPattern.matcher(msg);
			String toPath = null;
			if (matcher.find()) {
				toPath = matcher.group(2);
			}
									
			matcher = fromPathPattern.matcher(msg);
			String fromPath = null;
			if (matcher.find()) {
				fromPath = matcher.group(2);
			}
						
			try {
				if (toPath != null) {
					req.createToPath(toPath);				
				}
				if (fromPath != null) { 
					req.createFromPath(fromPath);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			matcher = messageIdPattern.matcher(msg);
			if (matcher.find()) {
				req.setMessageId(matcher.group(2));
			}				
			
			matcher = byteRangePattern.matcher(msg);
			if (matcher.find()) {
				req.setFirstByte(Integer.valueOf(matcher.group(2)));
				req.setLastByte(Integer.valueOf(matcher.group(3)));
				req.setSumByte(Integer.valueOf(matcher.group(4)));				
			}
			
			matcher = contentPattern.matcher(msg);
			if (matcher.find()) {
				req.setContentType(matcher.group(2));
				req.setContent(matcher.group(3).getBytes());
			}
						
			matcher = endLinePattern.matcher(msg);
			if (matcher.find()) {
				String tId = matcher.group(2);
				System.out.println("zaro tId: <" + tId + ">");
				if (!req.getTransactionId().equals(tId)) {
					System.out.println("A nyito tranzakcioId (" + req.getTransactionId() + ") " +
									   "nem egyezik a zaro tranzakcioId-val (" + tId + ")");
				}
				m.setEndToken(matcher.group(3).charAt(0));
			}
			
			long endTime = new Date().getTime();
			System.out.println("createMessage ended: " + endTime);
			System.out.println("duration: " + (endTime - startTime) );
			//System.out.println("message after create: \n"  + m.toString());			
		}
		
		return req;
	}
}