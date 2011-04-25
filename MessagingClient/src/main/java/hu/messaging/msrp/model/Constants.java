package hu.messaging.msrp.model;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static final int queuePollTimeout = 300; //ms
	public static final int chunkSize = 2048; //byte
	public static final int sessionIdLength = 20;
	public static final int transactionIdLength = 15;
	public static final int messageIdLength = 15;
	public static final int receiverBufferSize = 1000000;	
	public static final int unAcknoledgedChunksLimit = 25;
	public static final int senderThreadSleepTime = 50; //ms
	
    public static final Map<Message.MethodType, String> methods = new HashMap<Message.MethodType, String>(){
		{
            put(Message.MethodType.Send, "SEND");
            put(Message.MethodType._200OK, "200 OK");
            
        }
    };
}
