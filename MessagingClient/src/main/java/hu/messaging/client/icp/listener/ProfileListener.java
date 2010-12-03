package hu.messaging.client.icp.listener;

import hu.messaging.client.gui.controller.ICPController;

import com.ericsson.icp.IProfileListener;
import com.ericsson.icp.IProfile.State;
import com.ericsson.icp.util.ErrorReason;

public class ProfileListener extends BaseListener implements IProfileListener {

	public ProfileListener(ICPController icpController) {
		super(icpController);
	}

	public void processEvent(String event, String source, ErrorReason reasonCode) {
		log(getClass().getSimpleName() + ": processEvent");
	}

	public void processStateChanged(State state) {
		log(getClass().getSimpleName() + ": processStateChanged");
	}
}
