package hu.messaging.client;

import java.awt.TextArea;

import com.ericsson.icp.IProfileListener;
import com.ericsson.icp.IProfile.State;
import com.ericsson.icp.util.ErrorReason;

public class ProfileAdapter extends BaseAdapter implements IProfileListener {

	public ProfileAdapter(TextArea logArea) {
		super(logArea);
	}

	@Override
	public void processEvent(String event, String source, ErrorReason reasonCode) {
		log("ProfAdapter processEvent");

	}

	@Override
	public void processStateChanged(State state) {
		log("ProfAdapter processStateChanged");

	}

	@Override
	public void processError(ErrorReason error) {
		log("ProfAdapter processError");

	}

}
