package hu.messaging.client;

import hu.messaging.client.msrp.TransactionManager;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import java.util.*;

import com.ericsson.icp.ICPFactory;
import com.ericsson.icp.IPlatform;
import com.ericsson.icp.IProfile;
import com.ericsson.icp.IService;
import com.ericsson.icp.ISession;

import com.ericsson.icp.util.*;


public class Client {
	
	private IPlatform platform = null;
	private IProfile profile = null;
	private IService service = null;
	private ISession session = null;
	
	private TransactionManager manager = null;
	private String data = "";
	private TextArea logArea = new TextArea();
	 
	private boolean done;

	private void initICP() {
		try {
			platform = ICPFactory.createPlatform();
			platform.registerClient("MessagingClient");
			platform.addListener(new PlatformAdapter(logArea));
			profile = platform.createProfile("IMSSetting");
			profile.addListener(new ProfileAdapter(logArea));
			
			service = profile.createService("+g.messagingclient", "messageClientApp");
			service.addListener(new ServiceAdapter(logArea));
			
			session = service.createSession();
			System.out.println(session.getState());
			System.out.println(session.isValid());
			session.addListener(new SessionAdapter(logArea));

			manager = new TransactionManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disposeICP() {
		
		try {
			session.release();
			service.release();
			profile.release();
			platform.release();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private Frame createGui() {
		final Frame frame = new Frame();
       frame.setLayout(new GridBagLayout());
     
       Button messageButton = new Button("sendTestMessage");
       Button inviteButton = new Button("Invite");
       Button clearButton = new Button("Clear");
       
       messageButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
       			//sendMessage();
        	   data += " Géza";
        	   manager.sendData(data);
           }
       });
       
       inviteButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
       			sendInvite();
           }
       });
       
       clearButton.addActionListener(new ActionListener()   {
           public void actionPerformed(ActionEvent e) {
        	   logArea.setText("");
           }
       });
       
       Panel mainPanel = new Panel();
       mainPanel.setLayout(new BorderLayout());
       Panel buttonPanel = new Panel();
       buttonPanel.add(messageButton);
       buttonPanel.add(inviteButton);
       buttonPanel.add(clearButton);
       mainPanel.add(buttonPanel, BorderLayout.NORTH);
       mainPanel.add(logArea, BorderLayout.CENTER);
       frame.add(mainPanel);
     
       frame.setSize(new Dimension(500, 500));
       frame.addWindowListener(new WindowAdapter()
       {
           public void windowClosing(WindowEvent e)
           {
               done = true;
           }
       });
       
		return frame;
	}
	
	private ISessionDescription createLocalSdp()    {
       ISessionDescription localSdp = null;
       try
       {

    	   List<String> m = new ArrayList<String>();
    	   
//    	   m.add("0 PCMU/8000");
//    	   m.add("99 speex/8000");
//    	   m.add("100 telephone-event/8000");

    	   String address = new String("192.168.1.104");
    	   String port = new String("5123");
    	   m.add("text/plain");
    	   
    	   String path = new String();
    	   path = "msrp://" + address + ":" + port + "/xycsdfsfs" + ";tcp";
  
    	   String codes = new String();
    	   for (String mt : m) {
    		   //String code = mt.substring(0, mt.indexOf(" "));
    		   //codes = codes + code + " ";
    	   }
    	   
    	   codes = "*";
    	   codes = codes.trim();
    	   
           localSdp = SdpFactory.createSessionDescription("");
           localSdp.setField(ISessionDescription.FieldType.ProtocolVersion, "0");
           localSdp.setField(ISessionDescription.FieldType.Owner, "test 121123222 984773827 IN IP4 " + address);
           localSdp.setField(ISessionDescription.FieldType.SessionName, "-");
           localSdp.setField(ISessionDescription.FieldType.Connection, "IN IP4 " + address);
           ITimeDescription timeDescription = SdpFactory.createTimeDescription();
           timeDescription.setSessionActiveTime("0 0");
           localSdp.addTimeDescription(timeDescription);
           
           IMediaDescription mediaDescription = SdpFactory.createMediaDescription();
           String t = new String();
           mediaDescription.setField(IMediaDescription.FieldType.Name, "message " + port + " TCP/MSRP " + codes);
                    
       	   IAttribute attr = SdpFactory.createAttribute("accept-types", "text/plain");
       	   mediaDescription.appendAttribute(attr);
       	   attr = SdpFactory.createAttribute("path", path);
       	   mediaDescription.appendAttribute(attr);
       	   
           localSdp.addMediaDescription(mediaDescription);

       }
       catch (Exception e)
       {
       	e.printStackTrace();
       }
       return localSdp;
   }
   
	public void sendInvite() {        
		try {
		
			ISessionDescription desc = createLocalSdp();

			logArea.append("send INVITE to: " + "sip:weblogic103@192.168.1.103" + "\n");
			session.start("sip:weblogic103@192.168.1.103", desc, profile.getIdentity(), SdpFactory.createIMSContentContainer());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage() {
		String msg = new String("MSG FROM CLIENT");
		
		logArea.append("send MESSAGE to alice: " + msg + "\n");
		
		try {
			service.sendMessage("sip:alice@ericsson.com", "sip:alice@ericsson.com", "text/plain", msg.getBytes(), msg.length() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		initICP();
	//	sendMessage();
		Frame frame = createGui();
	    frame.setVisible(true);
	    
	    while (!done)
       {
           try
           {
               Thread.sleep(10);
           }
           catch (InterruptedException e1)
           {
           }
       }

	    try {
	    	if (session.getState() == ISession.State.Active)
	    		session.end();
	    }
	    catch(Exception e) {
	    	System.out.println("Excption");
	    	e.printStackTrace();
	    }
	    
	    frame.setVisible(false);
	    frame.dispose();
		disposeICP();
	}

	public static void main(String[] args) throws Exception {
		Client messagingClient = new Client();
		messagingClient.run();
	}

}
