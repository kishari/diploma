/* **********************************************************************
 * Copyright (c) Ericsson 2006. All Rights Reserved.
 * Reproduction in whole or in part is prohibited without the 
 * written consent of the copyright owner. 
 * 
 * ERICSSON MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE, OR NON-INFRINGEMENT. ERICSSON SHALL NOT BE LIABLE FOR ANY 
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR 
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. 
 * 
 * **********************************************************************/
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
