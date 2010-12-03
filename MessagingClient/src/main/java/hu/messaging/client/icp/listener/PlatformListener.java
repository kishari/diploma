package hu.messaging.client.icp.listener;

import hu.messaging.client.gui.controller.ICPController;

import com.ericsson.icp.IPlatformListener;
import com.ericsson.icp.util.ErrorReason;

public class PlatformListener extends BaseListener implements IPlatformListener {
	
	public PlatformListener(ICPController icpController) {
		super(icpController);
	}

	public void processApplicationData(String application, byte[] data,	int length) {
		log(getClass().getSimpleName() + ": processApplicationData");
	}

	public void processIncomingProfile(String profileName) {
		log(getClass().getSimpleName() + ": processIncomingProfile");
	}

	public void processPlatformTerminated(ErrorReason reasonCode) {
		log(getClass().getSimpleName() + ": processPlatformTerminated");
	}
}
