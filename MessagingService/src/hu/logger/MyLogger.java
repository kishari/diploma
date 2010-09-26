package hu.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;



public class MyLogger {
	
	private static String logFilePath = "c:\\log.txt";
	
	private static boolean inited = false;
	
	private static Writer writer = null;
	
	private static void init() {
		try {
				
			writer = new BufferedWriter(new FileWriter(logFilePath));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		inited = true;
	}
	
	public static void debug(String className, int row, String message) {		
		if (!inited) {
			init();
		}
		try {
			
			writer.append("DEBUG : " + className + " : " + row + " > " + message + "\n");					
			writer.flush();			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

