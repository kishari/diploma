package hu.messaging.client.gui.util;

import com.ericsson.icp.IBase;

public class IcpUtils {

	public static void release(IBase object) {
		try {
			if (object != null) {
				object.release();
			}
		} catch (Exception e) {
			System.out.println("Could not release ICP object: " + e);
			e.printStackTrace();
		}
	}

}
