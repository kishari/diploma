package hu.messaging.client;

import com.ericsson.icp.services.PGM.IRLSMListener;
import com.ericsson.icp.util.ErrorReason;
import java.awt.TextArea;

public class RLSMAdapter extends BaseAdapter implements IRLSMListener {

	public RLSMAdapter(TextArea logArea) {
		super(logArea);
	}
	
	public void processAddGroupResult(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		log("processAddGroupResult: ");
		log(arg1 + " ");
		log(Boolean.toString(arg0));
		
	}

	public void processDeleteGroupResult(boolean arg0, String arg1) {
		log("processDeleteGroupResult: ");
		log(arg1 + " ");
		log(Boolean.toString(arg0));
	}

	public void processEditGroupResult(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		log("processEditGroupResult: ");
		log(arg1 + " ");
		log(Boolean.toString(arg0));
	}

	public void processListRefreshed(boolean arg0) {
		log("processListRefreshed: ");
		log(Boolean.toString(arg0));

	}

	public void processError(ErrorReason arg0) {
		log("processError: ");
		log(arg0.getReasonString());
	}

}
