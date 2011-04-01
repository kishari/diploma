package hu.messaging.sip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
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

import hu.messaging.*;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.service.*;
import hu.messaging.util.*;

public class MessagingSipServlet extends SipServlet {

	private MessagingService messagingService = null;
	private static Pattern sipUriPattern =  Pattern.compile("(sip:[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}@" +
															"[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})");
	private static Pattern recipientsMessagePattern =  Pattern.compile("^RECIPIENTS\r\n" + 
																	   "Message-ID: ([\\p{Alnum}]{10,50})\r\n" +
																	   "Extension: ([\\p{Alnum}]{2,10})\r\n" +
																	   "Sender: (sip:[\\p{Alnum}]{1,}\\.?[\\p{Alnum}]{1,}\\.?[\\p{Alnum}]{1,}" +
																	   "@[\\p{Alnum}]{1,}\\.?[\\p{Alnum}]{1,}\\.?[\\p{Alnum}]{1,}\\.?[\\p{Alnum}]{1,})\r\n" +
																	   "Subject: (.*)\r\n\r\n" +
																	   "(.*)" + 
																	   "\r\n\r\n-----END", Pattern.DOTALL);

	private static Pattern getMessageMessageIdsPattern =  Pattern.compile("^GETMESSAGES\r\n" + 
			   															 	"Message-IDs:\r\n(.*)" + 
			   															 	"\r\n\r\n-----END", Pattern.DOTALL);

	
	private String[] getMessageIds(String incomingSIPMessage) {
		System.out.println("getMessageIds");
		List<String> ids = new ArrayList<String>();
		Matcher m = getMessageMessageIdsPattern.matcher(incomingSIPMessage);
		m.find();
		String temp = m.group(1);
		
		String[] mIds = temp.split("\r\n");
		
		for (int i = 0; i < mIds.length; i++) {
			ids.add(mIds[i]);
			System.out.println(mIds[i]);
		}
		
		return mIds;
	}
	
	private String getCleanSipUri(String incomingUri) {
		Matcher m = sipUriPattern.matcher(incomingUri);
		m.find();
		return m.group(1);
	}
	
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		System.out.println("doRegister");
		//System.out.println(req.toString());
		req.createResponse(200).send();
		User user = new User(this.getCleanSipUri(req.getTo().toString()), req.getExpires());
		
		user.addObserver(this.messagingService);
		if (this.messagingService.addUserToOnlineList(user)) {
			//notifyUserFromItsNewMessages(req, user);
		}
		
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
		messagingService = new MessagingService();
		messagingService.addMSRPListener(messagingService);
		
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
	}

	protected void doAck(SipServletRequest req)	throws ServletException, IOException {
		System.out.println("doAck calling...");
		System.out.println("Ack from: " + req.getFrom() );
	}

	protected void doMessage(SipServletRequest req)	throws ServletException, IOException {		
		InfoMessage info = null;
		System.out.println("doMessage calling...");
		//System.out.println("Incoming message: " + req.getContent() );
		req.createResponse(200).send();
		
		if (req.getContent().toString().startsWith("<?xml")) {
			info = (InfoMessage)XMLUtils.createInfoMessageFromStringXML(req.getContent().toString());
			System.out.println(info.getInfoType());
		}
		 
		if (req.getContent().toString().startsWith("GETMESSAGES")) {
			//System.out.println(req.getContent().toString());
			List<CompleteMessage> messages = messagingService.getMessagingDao().getMessagesToMessageIds(getMessageIds(req.getContent().toString()));
			System.out.println(getClass().getSimpleName() + " messages size: " + messages.size());
			String sipURI = this.getCleanSipUri(req.getFrom().toString());
			this.messagingService.sendMessages(messages, sipURI);
		}
		
		if ("UPDATESTATUS".equals(req.getContent())) {
			
		}
		
		if (info != null && "MESSAGE_DATA".equals(info.getInfoType().toUpperCase())) {
			InfoMessage.InfoDetail detail = info.getInfoDetail();
			String messageId = detail.getId();
			String extension = detail.getMimeType();
			String sender = detail.getSender().getSipUri();
			
			List<Recipient> recipients = new ArrayList<Recipient>();
			
			for (InfoMessage.InfoDetail.Recipients.Recipient r : detail.getRecipients().getRecipient()) {				
				Recipient recipient = new Recipient(r.getName(), r.getSipUri());
				System.out.println("recipient name : " + recipient.getName() + " ****** uri: " + recipient.getSipURI());
				recipients.add(recipient);
			}
			
			messagingService.getMessagingDao().insertRecipients(messageId, recipients);
			messagingService.getMessagingDao().updateMessage(messageId, extension, sender);
			notifyOnlineRecipients(req, recipients, messageId, sender, extension);
		}
	}

	private void notifyUserFromItsNewMessages(SipServletRequest req, User user) throws ServletParseException, IOException {
		System.out.println("notifyUserFromItsNewMessages");
		SipServletRequest r = sipFactory.createRequest(sipFactory.createApplicationSession(), "MESSAGE", sipFactory.createAddress("sip:weblogic@ericsson.com") , req.getTo());
		r.setRequestURI(sipFactory.createURI(req.getTo().toString()));
		r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
		r.setContent(messagingService.createNotifyMessageContent("sender", "messageId", "extension"), "text/plain");	        
		r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
		System.out.println(r.toString());
		r.send();
	}
	
	private void notifyOnlineRecipients(SipServletRequest req, 
									    List<Recipient> recipients,	
										String messageId,
										String sender,
										String extension) throws ServletParseException, IOException {

		System.out.println("notifyOnlineRecipients");
		for (Recipient recipient : recipients) {
			for (User user : this.messagingService.onlineUsers ) {
				if (recipient.getSipURI().equals(user.getSipURI())) {					
					SipServletRequest r = sipFactory.createRequest(req, false);
					r.setRequestURI(sipFactory.createURI(user.getSipURI()));
					r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
					r.setContent(messagingService.createNotifyMessageContent(sender, messageId, extension), "text/plain");	        
					r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
					
					r.send();
				}
			}			
		}
	}
	
	protected void doInvite(SipServletRequest req) throws ServletException, IOException {
		System.out.println("doInvite!...");
		System.out.println("Invite from: " + req.getFrom() );
		
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
			this.messagingService.getMsrpStack().getConnections().getSenderConnection(getCleanSipUri(req.getFrom().toString())).start();
			this.messagingService.createNewSession(localSdp.getPath(), remoteSdp.getPath(), getCleanSipUri(req.getFrom().toString()));

			resp.send();			
		}
		else {
			req.createResponse(403).send();
		}
		
	}
}
