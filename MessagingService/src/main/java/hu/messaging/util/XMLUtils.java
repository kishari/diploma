package hu.messaging.util;

import hu.messaging.model.*;
import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class XMLUtils {
	private static JAXBContext context;
	
	static {
        try {
        	context = JAXBContext.newInstance(InfoMessage.class);
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
	    	Unmarshaller um = context.createUnmarshaller();
	    	iMsg = (InfoMessage)um.unmarshal(bais);
	    }
	    catch (JAXBException e) {
	    	
	    }
	    
		return iMsg;
	}
	
	public static String createStringXMLFromInfoMessage(InfoMessage infoMessage) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(infoMessage, baos);
		}
	    catch (JAXBException e) {
	    	
	    }
	    	    	    	    
		return baos.toString();
	}
	    
}
