package hu.messaging.sip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import hu.messaging.*;
import hu.messaging.msrp.util.MSRPUtil;
import hu.messaging.msrp.util.SessionDescription;
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
			
			SessionDescription remoteSDP = new SDPUtil().parseSessionDescription(req.getContent().toString());						
			
			System.out.println(getClass().getSimpleName() + " remoteAddress: " + remoteSDP.getHost().getHostAddress());
			System.out.println(getClass().getSimpleName() + " remotePort: " + remoteSDP.getPort());
			
			InetAddress localReceiverAddress = messagingService.getMsrpStack().getReceiverConnectionHostAddress();
			int localReceiverPort = messagingService.getMsrpStack().getReceiverConnectionPort();
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
			
			SessionDescription localSDP = new SDPUtil().parseSessionDescription(content);			

			Map<String, SessionDescription> sdps = new HashMap<String, SessionDescription>();
			sdps.put(Keys.localSDP, localSDP);
			sdps.put(Keys.remoteSDP, remoteSDP);
			
			messagingService.getSdpContainer().put(getCleanSipUri(req.getFrom().toString()), sdps);
			resp.send();			
			this.messagingService.getMsrpStack().startReceiverConnection();			
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
		this.messagingService.stopSession(getCleanSipUri(req.getFrom().toString()));
		this.messagingService.getSdpContainer().remove(getCleanSipUri(req.getFrom().toString()));
	}

	protected void doAck(SipServletRequest req)	throws ServletException, IOException {
		System.out.println("doAck calling...");
		System.out.println("Ack from: " + req.getFrom() );
		
		String remoteSipUri = getCleanSipUri(req.getFrom().toString());
		SessionDescription localSDP = messagingService.getSdpContainer().get(remoteSipUri).get(Keys.localSDP);
		SessionDescription remoteSDP = messagingService.getSdpContainer().get(remoteSipUri).get(Keys.remoteSDP);;
		
		System.out.println();
		this.messagingService.getMsrpStack().createMSRPSession(localSDP, remoteSDP, remoteSipUri);
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
			this.messagingService.sendMessages(messages, getCleanSipUri(req.getFrom().toString()));
		}
		
		if (info != null && InfoMessage.messageData.equals(info.getInfoType().toUpperCase())) {		
			InfoMessage.DetailList detailList = info.getDetailList();
			InfoDetail detail = detailList.getDetail().get(0);
			
			String messageId = detail.getId();
			String mimeType = detail.getContent().getMimeType();
			String senderSipUri = detail.getSender().getSipUri();
			String senderName = detail.getSender().getName();
			String subject = detail.getSubject();			
			
			List<Recipient> recipients = new ArrayList<Recipient>();
			
			for (UserInfo r : detail.getRecipientList().getRecipient()) {				
				Recipient recipient = new Recipient(r.getName(), r.getSipUri());
				System.out.println("recipient name : " + recipient.getName() + " ****** uri: " + recipient.getSipURI());
				recipients.add(recipient);
			}
			
			messagingService.getMessagingDao().insertRecipients(messageId, recipients);
			Date sentAt = messagingService.getMessagingDao().updateMessage(messageId, mimeType, senderName, senderSipUri, subject);
			
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(sentAt);
			XMLGregorianCalendar date2 = null;
			try {
				date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			
			info.getDetailList().getDetail().get(0).setSentAt(date2);
			notifyOnlineRecipientsFromNewMessage(req, info);
		}
		
		if (info != null && InfoMessage.pullNewMessageInfos.equals(info.getInfoType().toUpperCase())) {
			System.out.println("kéne az infó mi?");
			
			User user = new User();
			user.setSipURI(this.getCleanSipUri(req.getFrom().toString()));

			SipServletRequest r = sipFactory.createRequest(req, false);
			r.setRequestURI(sipFactory.createURI(user.getSipURI()));
			r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
			r.setContent(XMLUtils.createStringXMLFromInfoMessage(createInfoMessageFromNewMessages(user)), "text/plain");	        
			r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
			
			r.send();

		}
	}

	private InfoMessage createInfoMessageFromNewMessages(User u) {
		ObjectFactory f = new ObjectFactory();
		
		InfoMessage m = f.createInfoMessage();
		m.setDetailList(f.createInfoMessageDetailList());
		m.setInfoType(InfoMessage.notifyUser);
		
		
		InfoDetail d = f.createInfoDetail();
		d.setSubject("test");
		d.setContent(f.createContentDescription());
		d.setSender(f.createUserInfo());
		InfoDetail d2 = f.createInfoDetail();
		d2.setSubject("teszt2");
		d2.setSender(f.createUserInfo());
		d2.setContent(f.createContentDescription());
		
		m.getDetailList().getDetail().add(d);
		m.getDetailList().getDetail().add(d2);
		
		return m;
	}
	
	private void notifyOnlineRecipientsFromNewMessage(SipServletRequest req, 
									    InfoMessage info) throws ServletParseException, IOException {
		
		System.out.println("notifyOnlineRecipients");
		InfoDetail detail = info.getDetailList().getDetail().get(0);
		for (UserInfo recipient : detail.getRecipientList().getRecipient()) {
			for (User user : this.messagingService.onlineUsers ) {				
				if (recipient.getSipUri().equals(user.getSipURI())) {
					SipServletRequest r = null;
					if (true) {
						r = sipFactory.createRequest(req, false);
						r.setRequestURI(sipFactory.createURI(user.getSipURI()));
						r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
						r.setContent(messagingService.createNotifyMessageContent(info), "text/plain");
						r.addHeader("p-asserted-identity", "sip:wl@ericsson.com");
					}
					else {
						r = sipFactory.createRequest(sipFactory.createApplicationSession(), "INVITE", "sip:weblogic@ericsson.com",
								user.getSipURI());
						r.setRequestURI(sipFactory.createURI(user.getSipURI()));
						r.pushRoute(sipFactory.createSipURI(null, InetAddress.getLocalHost().getHostAddress() + ":5082"));
						r.setContent(messagingService.createNotifyMessageContent(info), "text/plain");
						r.addHeader("p-asserted-identity", "sip:weblogic@ericsson.com");
					}
										
					System.out.println(r);
					r.send();
				}
			}			
		}
	}
	
	
}
