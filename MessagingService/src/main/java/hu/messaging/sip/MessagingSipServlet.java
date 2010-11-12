package hu.messaging.sip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import hu.messaging.service.*;
import hu.messaging.util.*;

import org.apache.log4j.Logger;


public class MessagingSipServlet extends SipServlet {

	private static Pattern sipUriPattern =  Pattern.compile("sip:([\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}@" +
															"[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})");
	
	private String getCleanSipUri(String incomingUri) {
		Matcher m = sipUriPattern.matcher(incomingUri);
		m.find();
		return m.group(1);
	}
	
	protected void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doErrorResponse(resp);
	}

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(MessagingSipServlet.class);
	/**
	 * The SIP Factory. Can be used to create URI and requests.
	 */
	private SipFactory sipFactory;

	/**
	 * @inheritDoc
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		sipFactory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		
		System.out.println("MessagingSipServlet inited!");
	}

	/**
	 * @inheritDoc
	 */
	protected void doBye(SipServletRequest req) throws ServletException, IOException {
		//TODO: Implement this method
		System.out.println("Server doBye");
		req.createResponse(200).send();
		MessagingService.disposeSenderConnection(getCleanSipUri(req.getFrom().toString()));
	}

	/**
	 * @inheritDoc
	 */
	protected void doAck(SipServletRequest req)	throws ServletException, IOException {
		//log.info("doAck calling...");
		System.out.println("doAck calling...");
		System.out.println("Ack from: " + req.getFrom() );
		System.out.println("ack to: " + req.getTo());
		System.out.println("session callId: " + req.getCallId());

	}

	/**
	 * @inheritDoc
	 */
	protected void doMessage(SipServletRequest req)	throws ServletException, IOException {
		
		System.out.println("doMessage calling...");
		log.info("Incoming message: " + req.getContent() );
		log.info("to: " + req.getTo());
		log.info("from: " + req.getFrom());
		log.info("User-Agent: " + req.getHeader("User-Agent"));
		log.info("Accept-Contact: " + req.getHeader("Accept-Contact"));
		req.createResponse(200).send();

/*		if (true) {
			final SipSession session = req.getSession();
*/			
			SipServletRequest messageRequest = sipFactory.createRequest(req, false);
			messageRequest.setRequestURI(sipFactory.createSipURI("alice", "ericsson.com"));
			
			messageRequest.pushRoute(sipFactory.createSipURI(null, MessagingUtil.getLocalIPAddress() + ":5082"));
//	                messageRequest.addHeader("Accept-Contact", req.getHeader("Accept-Contact"));
//	                messageRequest.addHeader("User-Agent", req.getHeader("User-Agent"));

	        // Set the message content.
	        messageRequest.setContent("Hello " + req.getContent(), "text/plain");
	        
	        messageRequest.addHeader("p-asserted-identity", "sip:helloworld@ericsson.com");

	       	// Send the request
	       	messageRequest.send();
	}

	/**
	 * @inheritDoc
	 */
	protected void doInvite(SipServletRequest req) throws ServletException, IOException {
//		log.info("doInvite calling...");
//		log.info("Invite from: " + req.getFrom() );
		
		System.out.println("doInvite!");
		System.out.println("Invite from: " + req.getFrom() );
		System.out.println("Invite from (regex): " + getCleanSipUri(req.getFrom().toString()) );
		System.out.println("session callId: " + req.getCallId());
		
		System.out.println("content Type: " + req.getContentType());
		System.out.println("content: " + req.getContent());
		
		if (req.isInitial()) {
			SipServletResponse resp = req.createResponse(200);
			
			if (!MessagingService.isReceiverConnection()) {				
				MessagingService.createReceiverConnection(InetAddress.getLocalHost()); 
				MessagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			} 
			else if (!MessagingService.isRunningReceiverConnection()) {
					MessagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			}
			
			ParsedSDP remoteSdp = new SDPUtil().parseSessionDescription(req.getContent().toString());
			
			boolean isConnectionToRemoteHost = false; //Meg kell vizsgálni, hogy a távoli géphez van-e már élõ senderConnection
			if (!isConnectionToRemoteHost) {
				MessagingService.createSenderConnection(remoteSdp.getHost(), remoteSdp.getPort(), getCleanSipUri(req.getFrom().toString()));
				//MessagingService.getMsrpStack().getConnections().findSenderConnection(sdp.getHost(), sdp.getPort()).start();
				MessagingService.getMsrpStack().getConnections().findSenderConnection(getCleanSipUri(req.getFrom().toString())).start();
			}
			
			InetAddress localReceiverAddress = MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress();
			int localReceiverPort = MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort();
			String address = localReceiverAddress.getHostAddress();
			String port = Integer.toString(localReceiverPort);
			
			String content = "v=0\n" +
							 "o=weblogic 2890844526 2890844527 IN IP4 " + address + "\n" +
							 "s=-\n" +							 
							 "c=IN IP4 " + address + "\n" +
							 "t=0 0\n" +
							 "m=message " + port + " TCP/MRSP *\n" +
							 "a=accept-types:text/plain\n" +
							 "a=path:MSRP://" + address + ":" + port + "/serversessionid;tcp";
			resp.setContent(content, "application/sdp");
			
			ParsedSDP localSdp = new SDPUtil().parseSessionDescription(content);
			
			MessagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), getCleanSipUri(req.getFrom().toString()));
			resp.send();			
		}
		else {
			req.createResponse(403).send();
		}
		
	}
}
