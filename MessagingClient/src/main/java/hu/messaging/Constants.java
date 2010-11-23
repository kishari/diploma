package hu.messaging;

import java.util.*;

public class Constants {

	public static final String serverURI = "sip:weblogic@192.168.1.102";
	
	
	public static final int methodSEND = 1;
	public static final int method200OK = 2;
	public static final int queuePollTimeout = 500; //ms
	public static final int chunkSize = 200; //byte
	public static final int sessionIdLength = 20;
	public static final int transactionIdLength = 10;
	public static final int messageIdLength = 10;
	public static final int receiverBufferSize = 1000000;	
	
    public static final Map<Integer, String> methods = new HashMap<Integer, String>(){
		{
            put(methodSEND, "SEND");
            put(method200OK, "200 OK");
        }
    };
}

