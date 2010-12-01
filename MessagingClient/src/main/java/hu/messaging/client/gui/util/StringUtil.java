package hu.messaging.client.gui.util;

public class StringUtil
{
    private StringUtil()
    {
        // prevent instantiation of utility class 
    }
    
    /**
     * Returns <code>true</code> if the string is null or empty.
     * @param str
     * @return
     */
    public static boolean isEmpty(String str)
    {
        return str == null || str.equals("");
    }
    /**
     * Retirve a file extension
     * @param fileName The file name from which to retirve the extension
     * @return The extension
     */
    public static String getFileExtension(String fileName)
    {
        String extension = null; 
    
        int extensionIndex = fileName.indexOf(".");
        if (extensionIndex >= 0)
        {
            extension = fileName.substring(extensionIndex + 1);
        }
        return extension;

    }
}
