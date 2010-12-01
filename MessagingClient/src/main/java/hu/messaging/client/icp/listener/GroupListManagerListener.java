package hu.messaging.client.icp.listener;

import com.ericsson.icp.services.PGM.IRLSMListener;


public class GroupListManagerListener extends BaseListener implements IRLSMListener
{
	/**
     * Wait a maximum of 5 seconds before for the callback to be called
     */
    private static final long TIMEOUT = 5000;

    /**
     * Indicates if the listener was called;
     */
    private boolean called = false;

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


    /**
     * Wait until the listener is called
     */
    public void waitForCallback() throws Exception
    {
        long start = System.currentTimeMillis();
        while (!called && (start + TIMEOUT > System.currentTimeMillis()))
        {
            // Wait to be called
            try
            {
                Thread.sleep(20);
            }
            // Ignore
            catch (InterruptedException e)
            {
            }
        }
        if(!called)
        {
            throw new Exception("Timeout waiting for listener to be called");
        }
        called = false;
    }

    /**
     * Clear the called flag
     */
    public void clear()
    {
        called = false;
    }
}
