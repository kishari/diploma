package hu.messaging.client.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class SwingUtil
{
    private SwingUtil()
    {
        // prevent instantiation of utility class
    }
    
    public static JFrame getFrame(Container component)
    {
        JFrame frame = null;

        if(component != null)
        {
            if(component instanceof JFrame)
            {
                frame = (JFrame) component;
            }
            else
            {
                frame = getFrame(component.getParent());
            }
        }

        return frame;
    }
    
    /**
     * OPen a "choose file" dialog
     * @param parent The parent GUI component
     * @param fileName The initial filename selection
     * @param extensions The file extensions to filter (without the ., (i.e. JPEG, GIF)) 
     * @return The selected file or null if none selected 
     */
    public static File openChooseFileDialog(Component parent, String fileName, String[] extensions, String description)
   {
    	System.out.println("SwingUtil openChooseFileDialog(...)");
/*
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        
        fileChooser.setName("dialog.select");
        fileChooser.setMultiSelectionEnabled(false);
        if (fileName != null)
        {
            File selectedFile = new File(fileName);
            fileChooser.setSelectedFile(selectedFile);
        }
        if (extensions != null)
        {
            FileExtensionFilter filter = new FileExtensionFilter(extensions, description);
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        fileChooser.setDialogTitle(Resources.getInstance().get("dialog.select.title"));
        int retValue = fileChooser.showDialog(parent, Resources.getInstance().get("dialog.select"));
        if(retValue == JFileChooser.APPROVE_OPTION)
        {
            file = fileChooser.getSelectedFile();
        }
        return file;
*/
    	return null;
    }
    
        
}
