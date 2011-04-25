package hu.messaging.client.icp.listener;

import hu.messaging.client.gui.controller.ICPController;

import com.ericsson.icp.services.PGM.IRLSMListener;

public class GroupListManagerListener extends BaseListener implements IRLSMListener {

	private static final long TIMEOUT = 5000;

	private boolean called = false;

	public GroupListManagerListener(ICPController icpController) {
		super(icpController);
	}

	public void processAddGroupResult(boolean success, String name) {
		called = true;
		log(getClass().getSimpleName() + ": processAddGroupResult");
	}

	public void processDeleteGroupResult(boolean success, String name) {
		called = true;
		log(getClass().getSimpleName() + ": processDeleteGroupResult");
	}

	public void processEditGroupResult(boolean success, String name) {
		called = true;
		log(getClass().getSimpleName() + ": processEditGroupResult");
	}

	public void processListRefreshed(boolean success) {
		called = true;
		log(getClass().getSimpleName() + ": processListRefreshed");
	}

	public void waitForCallback() throws Exception {
		long start = System.currentTimeMillis();
		while (!called && (start + TIMEOUT > System.currentTimeMillis())) {
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException e) {
			}
		}
		if (!called) {
			throw new Exception("Timeout waiting for listener to be called");
		}
		called = false;
	}

	public void clear() {
		called = false;
	}
}
