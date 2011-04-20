package hu.messaging.client.gui.util;

import com.ericsson.icp.IBase;
/**
 * Collection orf utility methods for the ICP
 */
public class IcpUtils
{
    /**
     * Relase an ICP base object
     * @param object The object to release
     */
    public static void release(IBase object) 
    {
        try
        {
            // Ensure it is not null
            if (object != null)
            {
                object.release();
            }
        }
        catch (Exception e)
        {
            // Could not release, nothing we can do
            System.out.println("Could not release ICP object: " + e);
            e.printStackTrace();
        }
    }

}
