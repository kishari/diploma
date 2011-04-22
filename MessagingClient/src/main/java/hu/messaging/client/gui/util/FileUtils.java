package hu.messaging.client.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {
	
	public static byte[] readFileToByteArray(File file) {
		return readFileToByteArray(file, false);
    }
	
	public static byte[] readFileToByteArray(File file, boolean deleteFileAfterRead) {		
		  byte[] fileAsByteArray = null;
		  FileInputStream fis = null;
		  try {
			  fis = new FileInputStream(file);
			  int numOfBytes = fis.available();	    		  
			  fileAsByteArray = new byte[numOfBytes];
			  fis.read(fileAsByteArray);
		  }
		  catch (IOException e) {
			  e.printStackTrace();
		  }
		  finally {	    		
			  if (fis != null)
				  try {
					  fis.close();
				  }
			  	catch (IOException e) {}
		  }
		  
		  if (deleteFileAfterRead) {
			  file.delete();
		  }
		  
		  return fileAsByteArray;
	  }
	
}
