package hu.messaging.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.*;

public class SDPUtil {
	private static Pattern attributePattern =  Pattern.compile("a=([\\p{Alnum}]{1,}-?[\\p{Alnum}]{1,}):([\\p{Graph}]{1,})");
	private static Pattern mediaPattern =  Pattern.compile("m=([\\p{Alpha}]{1,}) ([\\p{Digit}]{4,5}) TCP/MSRP \\*");
	private static Pattern connectionPattern =  Pattern.compile("c=IN IP4 " + 
																"([\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})\r\n");
	
	
	public ParsedSDP parseSessionDescription(String s) throws UnknownHostException {
		ParsedSDP sdp = new ParsedSDP();
		
		Matcher m = attributePattern.matcher(s);
		while (m.find()) {
			//System.out.println("SDPUtil parse " + m.group(1));
			//System.out.println("SDPUtil parse " + m.group(2));
			sdp.addAttribute(m.group(1), m.group(2));
			//System.out.println();
		}
		
		m = mediaPattern.matcher(s);
		while (m.find()) {
			//System.out.println("SDPUtil parse " + m.group(1));
			//System.out.println("SDPUtil parse ." + m.group(2) + ".");
			sdp.setPort(Integer.parseInt(m.group(2)));
			//System.out.println();
		}
		
		m = connectionPattern.matcher(s);
		while (m.find()) {
			//System.out.println("SDPUtil parse " + m.group(1));
			sdp.setHost(InetAddress.getByName(m.group(1)));
			System.out.println();
		}
		
		//System.out.println("sdp parse után: "  + sdp.getHost().getHostAddress() + ":" + sdp.getPort());
		return sdp;
	}
	
	public static void main(String[] args) {
		SDPUtil u = new SDPUtil();
		String content = 
		 "v=0\r\n" +
		 "o=weblogic 2890844526 2890844527 IN IP4 " + "192.168.1.120" + "\r\n" +
		 "s=-\r\n" +							 
		 "c=IN IP4 " + "192.168.1.103" + "\r\n" +
		 "t=0 0\r\n" +
		 "m=message " + 1000 + " TCP/MRSP *\r\n" +
		 "a=accept-types:text/plain\r\n" +
		 "a=path:MSRP://" + "www.exam.com" + ":" + 1000 + "/serversessionid;tcp";
		try {
			u.parseSessionDescription(content);			
		}
		catch(UnknownHostException e) { }
		
	}
}
