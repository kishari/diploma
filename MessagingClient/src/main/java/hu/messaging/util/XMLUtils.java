package hu.messaging.util;

import hu.messaging.client.model.InfoMessage;
import hu.messaging.client.model.MessageContainer;
import javax.xml.bind.*;
import java.io.*;

public class XMLUtils {
	private static JAXBContext infoMessageContext;
	private static JAXBContext messageContext;
	
	static {
        try {
        	infoMessageContext = JAXBContext.newInstance(InfoMessage.class);
        	messageContext = JAXBContext.newInstance(MessageContainer.class);
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
	    	    	    	    
		return baos.toString();
	}
	
	public static MessageContainer createMessageContainerFromStringXML(String xml) {	      	    
	    ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
	    MessageContainer msg = null;
	    try {
	    	Unmarshaller um = messageContext.createUnmarshaller();
	    	msg = (MessageContainer)um.unmarshal(bais);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
		return msg;
	}
	
	public static MessageContainer createMessageContainerFromFile(File xmlFile) {	      	    
		MessageContainer msg = null;
	    try {
	    	Unmarshaller um = messageContext.createUnmarshaller();
	    	msg = (MessageContainer)um.unmarshal(xmlFile);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
		return msg;
	}
	
	public static String createStringXMLFromMessageContainer(MessageContainer message) {
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
	
	public static void createXMLFileFromMessageContainer(MessageContainer message, File outputFile) {
		try {
			Marshaller m = messageContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(message, outputFile);
		}
	    catch (JAXBException e) {
	    	
	    }	    	    	    	    
	}
	    
}
