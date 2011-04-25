package hu.messaging.util;

import hu.messaging.client.model.*;
import javax.xml.bind.*;
import java.io.*;

public class XMLUtils {
	private static JAXBContext infoMessageContext;
	private static JAXBContext messageContext;
	
	static {
        try {
        	infoMessageContext = JAXBContext.newInstance(InfoMessage.class);
        	messageContext = JAXBContext.newInstance(MessageInfoContainer.class);
        } catch (JAXBException e) {
            //throw new RuntimeException(e);
        	System.out.println("Exception van");
        	e.printStackTrace();
        }
    }
	
	public static InfoMessage createInfoMessageFromStringXML(String xml) {	      	    
	    ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
	    InfoMessage iMsg = null;
	    try {
	    	Unmarshaller um = infoMessageContext.createUnmarshaller();
	    	iMsg = (InfoMessage)um.unmarshal(bais);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
		return iMsg;
	}
	
	public static String createStringXMLFromInfoMessage(InfoMessage infoMessage) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Marshaller m = infoMessageContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(infoMessage, baos);
		}
	    catch (JAXBException e) {
	    	
	    }
	    	 
	    System.out.println("XMLUtils createStringXMLFromInfoMessage return:");
	    System.out.println(baos.toString());
	    
		return baos.toString();
	}
	
	public static MessageInfoContainer createMessageInfoContainerFromStringXML(String xml) {	      	    
	    ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
	    MessageInfoContainer msg = null;
	    try {
	    	Unmarshaller um = messageContext.createUnmarshaller();
	    	msg = (MessageInfoContainer)um.unmarshal(bais);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
		return msg;
	}
	
	public static MessageInfoContainer createMessageInfoContainerFromFile(File xmlFile) {	      	    
		MessageInfoContainer msg = null;
		if (xmlFile == null) {
			System.out.println("XMLUtils createMessageInfoContainerFromFile file null");
		}
	    try {
	    	Unmarshaller um = messageContext.createUnmarshaller();
	    	msg = (MessageInfoContainer)um.unmarshal(xmlFile);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
	    System.out.println("createMessageInfoContainerFromFile return");
	    if (msg == null)
	    	System.out.println("baj van baj");
	    
		return msg;
	}
	
	public static String createStringXMLFromMessageInfoContainer(MessageInfoContainer message) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Marshaller m = messageContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(message, baos);
		}
	    catch (JAXBException e) {
	    	
	    }
	    	    	    	    
		return baos.toString();
	}
	
	public static void createXMLFileFromMessageInfoContainer(MessageInfoContainer message, File outputFile) {
		try {
			Marshaller m = messageContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(message, outputFile);
		}
	    catch (JAXBException e) {
	    	
	    }	    	    	    	    
	}
	    
}
