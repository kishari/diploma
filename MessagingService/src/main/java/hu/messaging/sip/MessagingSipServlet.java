package hu.messaging.sip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import hu.messaging.*;
import hu.messaging.dao.MessagingDAO;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.service.*;
import hu.messaging.util.*;

public class MessagingSipServlet extends SipServlet {

	private MessagingService messagingService = null;
	private static Pattern sipUriPattern =  Pattern.compile("(sip:[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}@" +
															"[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})");
	private static Pattern recipientsMessagePattern =  Pattern.compile("^RECIPIENTS\r\n" + 
																	   "Message-ID: ([\\p{Alnum}]{10,50})\r\n\r\n" + 
																	   "(.*)" + 
																	   "\r\n\r\n-----END", Pattern.DOTALL);
	
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
		this.messagingService = new MessagingService();
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
		this.messagingService.disposeSenderConnection(getCleanSipUri(req.getFrom().toString()));
		
		
//TESZT
		MessagingDAO dao = new MessagingDAO();
		//dao.getMessages();
//TESZT
	}

	protected void doAck(SipServletRequest req)	throws ServletException, IOException {
		System.out.println("doAck calling...");
		System.out.println("Ack from: " + req.getFrom() );
	}

	protected void doMessage(SipServletRequest req)	throws ServletException, IOException {		
		
		System.out.println("doMessage calling...");
		System.out.println("Incoming message: " + req.getContent() );
		req.createResponse(200).send();
		
		if ("TESTINVITE".equals(req.getContent())) {
			System.out.println("keres from: " + req.getFrom());
			System.out.println("keres to: " + req.getTo());
			SipServletRequest messageRequest = sipFactory.createRequest(req, false);
			
			/*
			SipServletRequest messageRequest = sipFactory.createRequest(req.getSession().getApplicationSession(),
																		"MESSAGE",
																		"sip:weblogic@ericsson.com",
																		"sip:alice@ericsson.com"); 
			 */
			messageRequest.setRequestURI(sipFactory.createSipURI("alice", "ericsson.com"));
			//messageRequest.pushRoute(sipFactory.createSipURI(null, "192.168.1.102:5082"));
            messageRequest.setContent("Hello " + req.getContent(), "text/plain");
            messageRequest.addHeader("p-asserted-identity", "sip:helloworld@ericsson.com");
                       
            System.out.println("valasz from: " + messageRequest.getFrom());
            System.out.println("valasz to: " + messageRequest.getTo());
            messageRequest.send();
		}
		if ("UPDATESTATUS".equals(req.getContent())) {
			User user = new User(this.getCleanSipUri(req.getFrom().toString()));
			user.addObserver(this.messagingService);
			this.messagingService.addUser(user);
		}
		
		if (req.getContent().toString().startsWith("RECIPIENTS")) {
			Matcher m = recipientsMessagePattern.matcher(req.getContent().toString());
			m.find();
			
			String messageId = m.group(1);
			String[] rTemp = m.group(2).split("\r\n");
			
			List<Recipient> recipients = new ArrayList<Recipient>();
			
			for (String r : rTemp) {
				String[] t = r.split("#");
				Recipient recipient = new Recipient(t[0], t[1]);
				recipients.add(recipient);
			}
			
			MessagingDAO dao = new MessagingDAO();
			dao.insertRecipients(messageId, recipients);
			notifyOnlineRecipients(req, recipients);
		}

		

/*		SipServletRequest messageRequest = sipFactory.createRequest(req, false);
		messageRequest.setRequestURI(sipFactory.createSipURI("alice", "ericsson.com"));
			
		messageRequest.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
	    messageRequest.addHeader("Accept-Contact", req.getHeader("Accept-Contact"));
	    messageRequest.addHeader("User-Agent", req.getHeader("User-Agent"));

        messageRequest.setContent("Hello " + req.getContent(), "text/plain");	        
        messageRequest.addHeader("p-asserted-identity", "sip:helloworld@ericsson.com");

	    messageRequest.send();
	    */
	}

	private void notifyOnlineRecipients(SipServletRequest req, 
										List<Recipient> recipients) throws ServletParseException, IOException {

		System.out.println("notifyOnlineRecipients");
		for (Recipient recipient : recipients) {
			for (User user : this.messagingService.onlineUsers ) {
				System.out.println(recipient.getSipURI() + "--" + user.getSipURI() );
				if (recipient.getSipURI().equals(user.getSipURI())) {
					System.out.println("send notify message");
					SipServletRequest r = sipFactory.createRequest(req, false);
					//r.setRequestURI(sipFactory.createSipURI("alice", "ericsson.com"));
					r.setRequestURI(sipFactory.createURI(user.getSipURI()));
					//r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
					r.setContent("Uzeneted jott basszameg ", "text/plain");	        
					r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
					
					r.send();
				}
			}			
		}
	}
	
	protected void doInvite(SipServletRequest req) throws ServletException, IOException {
		System.out.println("doInvite!...");
		System.out.println("Invite from: " + req.getFrom() );
		System.out.println("Invite from (regex): " + getCleanSipUri(req.getFrom().toString()) );
		
//		System.out.println("content Type: " + req.getContentType());
//		System.out.println("content: " + req.getContent());
		
		if (req.isInitial()) {
			SipServletResponse resp = req.createResponse(200);
			
			if (!this.messagingService.isReceiverConnection()) {				
				this.messagingService.createReceiverConnection(InetAddress.getLocalHost()); 
				this.messagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			} 
			else if (!this.messagingService.isRunningReceiverConnection()) {
				this.messagingService.getMsrpStack().getConnections().getReceiverConnection().start();
			}
			
			ParsedSDP remoteSdp = new SDPUtil().parseSessionDescription(req.getContent().toString());						
			
			InetAddress localReceiverAddress = this.messagingService.getMsrpStack().getConnections().getReceiverConnection().getHostAddress();
			int localReceiverPort = this.messagingService.getMsrpStack().getConnections().getReceiverConnection().getPort();
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

			this.messagingService.createSenderConnection(remoteSdp.getHost(), remoteSdp.getPort(), getCleanSipUri(req.getFrom().toString()));
			this.messagingService.getMsrpStack().getConnections().findSenderConnection(getCleanSipUri(req.getFrom().toString())).start();
			this.messagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), getCleanSipUri(req.getFrom().toString()));

			resp.send();			
		}
		else {
			req.createResponse(403).send();
		}
		
	}
}
