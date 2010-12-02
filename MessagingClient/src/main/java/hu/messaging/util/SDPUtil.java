package hu.messaging.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.*;

import com.ericsson.icp.util.IAttribute;
import com.ericsson.icp.util.IMediaDescription;
import com.ericsson.icp.util.ISessionDescription;
import com.ericsson.icp.util.ITimeDescription;
import com.ericsson.icp.util.SdpFactory;

public class SDPUtil {
	private static Pattern attributePattern =  Pattern.compile("a=([\\p{Alnum}]{1,}-?[\\p{Alnum}]{1,}):([\\p{Graph}]{1,})");
	private static Pattern mediaPattern =  Pattern.compile("m=([\\p{Alpha}]{1,}) ([\\p{Digit}]{4,5}) TCP/MSRP \\*");
	private static Pattern connectionPattern =  Pattern.compile("c=IN IP4 " + 
																"([\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})\r\n");
	
	
	public static SessionDescription parseSessionDescription(String s) throws UnknownHostException {
		SessionDescription sdp = new SessionDescription();
		
		Matcher m = attributePattern.matcher(s);
		while (m.find()) {
			sdp.addAttribute(m.group(1).toUpperCase(), m.group(2));
		}
		
		m = mediaPattern.matcher(s);
		while (m.find()) {
			sdp.setPort(Integer.parseInt(m.group(2)));
		}
		
		m = connectionPattern.matcher(s);
		while (m.find()) {
			sdp.setHost(InetAddress.getByName(m.group(1)));
		}
	
		return sdp;
	}
	
	public static ISessionDescription createSDP(InetAddress host, int port, String sessionId) {
	    ISessionDescription localSdp = null;
	    
		try {
			String address = host.getHostAddress();
			
			String path = "msrp://" + address + ":" + port + "/" + sessionId + ";tcp";

			String codes = "*";
			codes = codes.trim();

			localSdp = SdpFactory.createSessionDescription("");
			localSdp.setField(ISessionDescription.FieldType.ProtocolVersion, "0");
			localSdp.setField(ISessionDescription.FieldType.Owner, "client 121123222 984773827 IN IP4 " + address);
			localSdp.setField(ISessionDescription.FieldType.SessionName, "-");
			localSdp.setField(ISessionDescription.FieldType.Connection, "IN IP4 " + address);
			ITimeDescription timeDescription = SdpFactory.createTimeDescription();
			timeDescription.setSessionActiveTime("0 0");
			localSdp.addTimeDescription(timeDescription);

			IMediaDescription mediaDescription = SdpFactory.createMediaDescription();
			mediaDescription.setField(IMediaDescription.FieldType.Name, "message " + port + " TCP/MSRP " + codes);

			IAttribute attr = SdpFactory.createAttribute("accept-types", "text/plain");
			mediaDescription.appendAttribute(attr);
			attr = SdpFactory.createAttribute("path", path);
			mediaDescription.appendAttribute(attr);

			localSdp.addMediaDescription(mediaDescription);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localSdp;
	}

}
