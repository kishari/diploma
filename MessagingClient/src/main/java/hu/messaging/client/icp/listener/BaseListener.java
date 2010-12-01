package hu.messaging.client.icp.listener;

import com.ericsson.icp.util.ErrorReason;

public class BaseListener {

    protected BaseListener(){

    }

    public void processError(ErrorReason aError){
        System.out.println("processError: " + aError.getReasonString());
    }
    
    public void log(String message)  {
    	System.out.println(message);
    }
    
}
