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

import hu.messaging.Constants;
import hu.messaging.dao.MessagingDAO;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.service.*;
import hu.messaging.util.*;

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
		super.doErrorResponse(resp);
		
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * The SIP Factory. Can be used to create URI and requests.
	 */
	private SipFactory sipFactory;

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
		System.out.println(getCleanSipUri(req.getFrom().toString()));
		MessagingService.disposeSenderConnection(getCleanSipUri(req.getFrom().toString()));
		
		
//TESZT
		MessagingDAO dao = new MessagingDAO();
		dao.getMessages();
//TESZT
	}

	protected void doAck(SipServletRequest req)	throws ServletException, IOException {
		System.out.println("doAck calling...");
		System.out.println("Ack from: " + req.getFrom() );
	}

	protected void doMessage(SipServletRequest req)	throws ServletException, IOException {
		
		System.out.println("doMessage calling...");
		System.out.println("Incoming message: " + req.getContent() );
		System.out.println("from: " + req.getFrom());
		req.createResponse(200).send();

		SipServletRequest messageRequest = sipFactory.createRequest(req, false);
		messageRequest.setRequestURI(sipFactory.createSipURI("alice", "ericsson.com"));
			
		messageRequest.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
//	    messageRequest.addHeader("Accept-Contact", req.getHeader("Accept-Contact"));
//	    messageRequest.addHeader("User-Agent", req.getHeader("User-Agent"));

        messageRequest.setContent("Hello " + req.getContent(), "text/plain");	        
        messageRequest.addHeader("p-asserted-identity", "sip:helloworld@ericsson.com");

	    messageRequest.send();
	}

	protected void doInvite(SipServletRequest req) throws ServletException, IOException {
		System.out.println("doInvite!...");
		System.out.println("Invite from: " + req.getFrom() );
		System.out.println("Invite from (regex): " + getCleanSipUri(req.getFrom().toString()) );
		
//		System.out.println("content Type: " + req.getContentType());
//		System.out.println("content: " + req.getContent());
		
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
			
			InetAddress localReceiverAddress = MessagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress();
			int localReceiverPort = MessagingService.getMsrpStack().getConnections().getReceiverConnection().getPort();
			String address = localReceiverAddress.getHostAddress();
			String port = Integer.toString(localReceiverPort);
			
			String sessionId = MSRPUtil.generateRandomString(Constants.sessionIdLength);
			
			String content = "v=0\n" +
							 "o=weblogic 2890844526 2890844527 IN IP4 " + address + "\n" +
							 "s=-\n" +							 
							 "c=IN IP4 " + address + "\n" +
							 "t=0 0\n" +
							 "m=message " + port + " TCP/MSRP *\n" +
							 "a=accept-types:text/plain\n" +
							 "a=path:msrp://" + address + ":" + port + "/" + sessionId + ";tcp";
			resp.setContent(content, "application/sdp");
			
			ParsedSDP localSdp = new SDPUtil().parseSessionDescription(content);

			MessagingService.createSenderConnection(remoteSdp.getHost(), remoteSdp.getPort(), getCleanSipUri(req.getFrom().toString()));
			MessagingService.getMsrpStack().getConnections().findSenderConnection(getCleanSipUri(req.getFrom().toString())).start();
			MessagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), getCleanSipUri(req.getFrom().toString()));

			resp.send();			
		}
		else {
			req.createResponse(403).send();
		}
		
	}
}
