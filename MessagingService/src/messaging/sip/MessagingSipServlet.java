package messaging.sip;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import messaging.util.*;
import messaging.msrp.*;

import org.apache.log4j.Logger;


public class MessagingSipServlet extends SipServlet {

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

	private ConnectionManager manager = null;
	/**
	 * @inheritDoc
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		sipFactory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		
		manager = new ConnectionManager(null);
		//manager.run();
		//MessagingUtil.init(getInitParameter("vms-sip-uri"));
		System.out.println("MessagingSipServlet inited!");
	}

	/**
	 * @inheritDoc
	 */
	protected void doBye(SipServletRequest req) throws ServletException, IOException {
		//TODO: Implement this method
		req.createResponse(200).send();
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
		System.out.println("session callId: " + req.getCallId());
		
		System.out.println("content Type: " + req.getContentType());
		System.out.println("content: " + req.getContent());
		
		if (req.isInitial()) {
			SipServletResponse resp = req.createResponse(200);
			String address = "192.168.1.104";
			String port = "1234";
			String content = "v=0\n" +
							 "o=weblogic 2890844526 2890844527 IN IP4 " + address + "\n" +
							 "s=-\n" +							 
							 "c=IN IP4 " + address + "\n" +
							 "t=0 0\n" +
							 "m=message " + port + " TCP/MRSP *\n" +
							 "a=accept-types:text/plain\n" +
							 "a=path:msrp://" + address + ":" + port + "/kjhd37s2s20w2a;tcp";
			resp.setContent(content, "application/sdp");
			resp.send();
			//log.debug("INVITE is initial! Sending 200 OK:" + req.getFrom());
			//System.out.println("INVITE is initial! Sending 200 OK:" + req.getFrom());
		}
		else {
			req.createResponse(403).send();
			//log.debug("INVITE is not initial! Sending 403:" + req.getFrom());
		}
		manager.start();
		
	}
}
