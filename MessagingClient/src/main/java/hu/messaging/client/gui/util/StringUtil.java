package hu.messaging.client.gui.util;

public class StringUtil {
    
    public static boolean isEmpty(String str)
    {
        return str == null || str.equals("");
    }

    public static String getFileExtension(String fileName){
        String extension = null; 
    
        int extensionIndex = fileName.indexOf(".");
        if (extensionIndex >= 0)
        {
            extension = fileName.substring(extensionIndex + 1);
        }
        return extension;

    }
}
