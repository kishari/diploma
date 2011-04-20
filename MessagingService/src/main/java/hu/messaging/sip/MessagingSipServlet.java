package hu.messaging.sip;

import java.io.IOException;
import java.net.InetAddress;
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

import hu.messaging.*;
import hu.messaging.msrp.CompleteMessage;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.service.*;
import hu.messaging.util.*;
import hu.messaging.model.*;

public class MessagingSipServlet extends SipServlet {

	private SipFactory sipFactory;
	private MessagingService messagingService = null;
	private static final long serialVersionUID = 1L;
	
	private String getCleanSipUri(String incomingUri) {
		Pattern sipUriPattern =  Pattern.compile("(sip:[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}@" +
												 "[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,}.?[\\p{Alnum}]{1,})");	
		Matcher m = sipUriPattern.matcher(incomingUri);
		m.find();
		return m.group(1);
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		sipFactory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		messagingService = new MessagingService();
		messagingService.addMSRPListener(messagingService);
		
		System.out.println("MessagingSipServlet inited!");
	}
	
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		System.out.println("doRegister");
		//System.out.println(req.toString());
		req.createResponse(200).send();
		User user = new User(this.getCleanSipUri(req.getTo().toString()), req.getExpires());
		
		user.addObserver(this.messagingService);
		if (this.messagingService.addUserToOnlineList(user)) {
			//notifyUserFromNewMessages(req, user);
		}
		
	}
	
	protected void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		super.doErrorResponse(resp);
		
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
			this.messagingService.createNewMSRPSession(localSdp.getPath(), remoteSdp.getPath(), getCleanSipUri(req.getFrom().toString()));

			resp.send();			
		}
		else {
			req.createResponse(403).send();
		}
		
	}

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
		 
		if (info != null && info.getInfoType().equals(InfoMessage.downloadContent)) {
			System.out.println("download messages");
			CompleteMessage message = messagingService.getMessagingDao().getMessageToMessageId(info.getDetailList().getDetail().get(0).getId());
			List<CompleteMessage> messages = new ArrayList<CompleteMessage>();
			messages.add(message);
			String sipURI = this.getCleanSipUri(req.getFrom().toString());
			this.messagingService.sendMessages(messages, sipURI);
		}
		
		if (info != null && InfoMessage.messageData.equals(info.getInfoType().toUpperCase())) {		
			InfoMessage.DetailList detailList = info.getDetailList();
			InfoDetail detail = detailList.getDetail().get(0);
			
			String messageId = detail.getId();
			String mimeType = detail.getContent().getMimeType();
			String sender = detail.getSender().getSipUri();
			
			List<Recipient> recipients = new ArrayList<Recipient>();
			
			for (UserInfo r : detail.getRecipientList().getRecipient()) {				
				Recipient recipient = new Recipient(r.getName(), r.getSipUri());
				System.out.println("recipient name : " + recipient.getName() + " ****** uri: " + recipient.getSipURI());
				recipients.add(recipient);
			}
			
			messagingService.getMessagingDao().insertRecipients(messageId, recipients);
			messagingService.getMessagingDao().updateMessage(messageId, mimeType, sender); 
			notifyOnlineRecipientsFromNewMessage(req, info);
		}
	}

	private void notifyUserFromNewMessages(SipServletRequest req, User user) throws ServletParseException, IOException {
		System.out.println("notifyUserFromItsNewMessages");
		SipServletRequest r = sipFactory.createRequest(sipFactory.createApplicationSession(), "MESSAGE", sipFactory.createAddress("sip:weblogic@ericsson.com") , req.getTo());
		r.setRequestURI(sipFactory.createURI(req.getTo().toString()));
		r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
		//r.setContent(messagingService.createNotifyMessageContent("sender", "messageId", "extension"), "text/plain");	        
		r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
		System.out.println(r.toString());
		r.send();
	}
	
	private void notifyOnlineRecipientsFromNewMessage(SipServletRequest req, 
									    InfoMessage info) throws ServletParseException, IOException {
		
		System.out.println("notifyOnlineRecipients");
		InfoDetail detail = info.getDetailList().getDetail().get(0);
		for (UserInfo recipient : detail.getRecipientList().getRecipient()) {
			for (User user : this.messagingService.onlineUsers ) {
				if (recipient.getSipUri().equals(user.getSipURI())) {					
					SipServletRequest r = sipFactory.createRequest(req, false);
					r.setRequestURI(sipFactory.createURI(user.getSipURI()));
					r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
					r.setContent(messagingService.createNotifyMessageContent(info), "text/plain");	        
					r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
					
					r.send();
				}
			}			
		}
	}
	
	
}
