package hu.messaging.client.icp.listener;

import com.ericsson.icp.IProfileListener;
import com.ericsson.icp.IProfile.State;
import com.ericsson.icp.util.ErrorReason;

public class ProfileListener extends BaseListener implements IProfileListener {

	public ProfileListener() {
		super();
	}

	public void processEvent(String event, String source, ErrorReason reasonCode) {
		log(getClass().getSimpleName() + ": processEvent");
	}

	public void processStateChanged(State state) {
		log(getClass().getSimpleName() + ": processStateChanged");
	}
}
