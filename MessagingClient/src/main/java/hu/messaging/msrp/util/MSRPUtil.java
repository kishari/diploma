package hu.messaging.msrp.util;

import hu.messaging.Constants;
import hu.messaging.msrp.Message;
import hu.messaging.msrp.Request;
import hu.messaging.msrp.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSRPUtil {
	private static Pattern methodPattern =  Pattern.compile("(^MSRP) ([\\p{Alnum}]{8,50}) ([\\p{Alnum}]{3,5}[\\p{Blank}]{0,1}[\\p{Alnum}]{0,2})\r\n(.*)", Pattern.DOTALL);
	private static Pattern toPathPattern =  Pattern.compile("(To-Path:) (msrp://[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}:[\\p{Digit}]{4,5}/([\\p{Alnum}]{10,50});tcp)\r\n");
	private static Pattern fromPathPattern =  Pattern.compile("(From-Path:) (msrp://[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}:[\\p{Digit}]{4,5}/([\\p{Alnum}]{10,50});tcp)\r\n");
	private static Pattern messageIdPattern =  Pattern.compile("(Message-ID:) ([\\p{Alnum}]{10,50})\r\n");
	private static Pattern byteRangePattern =  Pattern.compile("(Byte-Range:) ([\\p{Digit}]{1,})-([\\p{Digit}]{1,})" +
															   "/([\\p{Digit}]{1,})\r\n");
	private static Pattern contentPattern =  Pattern.compile("(Content-Type:) ([\\p{Alpha}]{1,}/[\\p{Alpha}]{1,})\r\n\r\n" +
															 "(.*)\r\n-------", Pattern.DOTALL);
	private static Pattern endLinePattern =  Pattern.compile("\r\n([-]{7})([\\p{Alnum}]{8,50})([+$#]{1})");
	
	public static Request createRequest(byte[] content, URI localURI,
										URI remoteURI, String transactionId, String messageId, int offset,
										int chunkSize, int completeMessageSize, char endToken) {
		Request req = new Request();
		req.setMethod(Constants.methodSEND);
		try {
			req.createFromPath(localURI.toString());
			req.createToPath(remoteURI.toString());
		} catch (URISyntaxException e) { }

		req.setMessageId(messageId);
		req.setTransactionId(transactionId);

		req.setFirstByte(offset);
		req.setLastByte(offset + chunkSize - 1);
		req.setSumByte(completeMessageSize);
		req.setContentType("text/plain");
		req.setContent(content);

		req.setEndToken(endToken);

		return req;
	}
	
	public static Message createMessageFromString(String message) {
		
		Matcher matcher = methodPattern.matcher(message);
		
		String method = null;
		if (matcher.find()) {
			method = matcher.group(3);
		}
		if ("SEND".equals(method)) {
			Request req = new Request();
			
			req.setMethod(Constants.methodSEND);			
			req.setTransactionId(matcher.group(2));
			
			matcher = toPathPattern.matcher(message);
			String toPath = null;
			if (matcher.find()) {
				toPath = matcher.group(2);
			}
									
			matcher = fromPathPattern.matcher(message);
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
			
			matcher = messageIdPattern.matcher(message);
			if (matcher.find()) {
				req.setMessageId(matcher.group(2));
			}				
			
			matcher = byteRangePattern.matcher(message);
			if (matcher.find()) {
				req.setFirstByte(Integer.valueOf(matcher.group(2)));
				req.setLastByte(Integer.valueOf(matcher.group(3)));
				req.setSumByte(Integer.valueOf(matcher.group(4)));				
			}
			
			matcher = contentPattern.matcher(message);
			if (matcher.find()) {
				req.setContentType(matcher.group(2));
				req.setContent(matcher.group(3).getBytes());
			}
						
			matcher = endLinePattern.matcher(message);
			if (matcher.find()) {
				String tId = matcher.group(2);
				if (!req.getTransactionId().equals(tId)) {
					System.out.println("A nyito tranzakcioId (" + req.getTransactionId() + ") " +
									   "nem egyezik a zaro tranzakcioId-val (" + tId + ")");
				}
				req.setEndToken(matcher.group(3).charAt(0));
			}	
			
			return req;
		}
		else if("200 OK".equals(method)) {
			Response resp = new Response();
			
			resp.setMethod(Constants.method200OK);			
			resp.setTransactionId(matcher.group(2));
			
			matcher = toPathPattern.matcher(message);
			String toPath = null;
			if (matcher.find()) {
				toPath = matcher.group(2);
			}
									
			matcher = fromPathPattern.matcher(message);
			String fromPath = null;
			if (matcher.find()) {
				fromPath = matcher.group(2);
			}
						
			try {
				if (toPath != null) {
					resp.createToPath(toPath);				
				}
				if (fromPath != null) { 
					resp.createFromPath(fromPath);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			matcher = endLinePattern.matcher(message);
			if (matcher.find()) {
				String tId = matcher.group(2);
				if (!resp.getTransactionId().equals(tId)) {
					System.out.println("A nyito tranzakcioId (" + resp.getTransactionId() + ") " +
									   "nem egyezik a zaro tranzakcioId-val (" + tId + ")");
				}
				resp.setEndToken(matcher.group(3).charAt(0));
			}	
			
			
			return resp;
		}
		
		return null;				
	}
	
	public static String generateRandomString(int length) {		
		double divisor = length / 32.0; //32 karakter egy UUID '-' jelek nélkül
		int cycle = (int) Math.ceil(divisor);
		
		String random = "";
		for(int i = 0; i < cycle; i++) {
			random += UUID.randomUUID().toString().replaceAll("-", "");
		}
		
		return random.substring(0, length);
	}
	
	public static byte[] createMessageContentFromChunks(List<Request> chunks) {
		Collections.sort(chunks);
		byte[] content = new byte[chunks.get(chunks.size() - 1).getLastByte()];
		int offset = 0;
		System.out.println("createMessageContentFromChunks: " + (chunks.get(chunks.size() - 1).getLastByte()));
		for (Request chunk : chunks) {
			System.arraycopy(chunk.getContent(), 0, content, offset, chunk.getContent().length);
			offset += chunk.getContent().length;
		}

		return content;
	}
}