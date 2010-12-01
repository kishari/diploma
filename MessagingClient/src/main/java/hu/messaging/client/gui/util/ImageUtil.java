package hu.messaging.client.gui.util;

import hu.messaging.client.gui.Main;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
public class ImageUtil
{
	private ImageUtil()
    {
        // prevent instantiation of utility class 
    }
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) 
    {
        ImageIcon icon = null;
        try
        {
        	//System.out.println(path);
            InputStream stream = Main.class.getResourceAsStream(path);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            icon = new ImageIcon(bytes);
            
        }
        catch (IOException e)
        {
            // Should not occur, print the stack race for debugginf purpose
            e.printStackTrace();
        }
        return icon;
    }

	public static Image createImage(String path)
	{
        ImageIcon icon = createImageIcon(path);
        return icon.getImage();
	}
}
