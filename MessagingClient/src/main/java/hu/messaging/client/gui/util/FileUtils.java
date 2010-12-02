package hu.messaging.client.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {
	  
	public static byte[] readFileToByteArray(File file) {
		  byte[] fileAsByteArray = null;
		  FileInputStream fis = null;
		  try {
			  fis = new FileInputStream(file);
			  int numOfBytes = fis.available();	    		  
			  fileAsByteArray = new byte[numOfBytes];
			  fis.read(fileAsByteArray);
		  }
		  catch (IOException e) {
			  
		  }
		  finally {	    		
			  if (fis != null)
				  try {
					  fis.close();
				  }
			  	catch (IOException e) {}
		  }
		  return fileAsByteArray;
	  }
	
}
