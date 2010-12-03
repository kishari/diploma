package hu.messaging.client.icp.listener;

import hu.messaging.client.gui.controller.ICPController;
import com.ericsson.icp.util.ErrorReason;

public class BaseListener {

	protected boolean logEnabled = false;
	protected ICPController icpController;
    protected BaseListener(ICPController icpController){
    	this.icpController = icpController;
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
