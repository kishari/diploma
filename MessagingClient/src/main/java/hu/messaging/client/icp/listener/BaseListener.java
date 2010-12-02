package hu.messaging.client.icp.listener;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.icp.util.ErrorReason;

public class BaseListener {

	protected boolean logEnabled = true;
	
    protected BaseListener(){

    }

    public void processError(ErrorReason aError){
        System.out.println("processError: " + aError.getReasonString());
    }
    
    public void log(String message)  {
    	if (logEnabled) {
    		System.out.println(message);
    	}
    	
    }
    
}
