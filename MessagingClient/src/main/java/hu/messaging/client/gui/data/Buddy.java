package hu.messaging.client.gui.data;

import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.gui.util.StringUtil;

import javax.swing.ImageIcon;


public class Buddy implements Comparable<Object> {
	
    private String contactAddress;
    private String displayName;    
    private ImageIcon userIcon = ImageUtil.createImageIcon("userIcon.gif");
 
    public Buddy(String contactAddress) {
        this.contactAddress = contactAddress;
    }
    
    public String getDisplayName() {
        String name = displayName;
        if(StringUtil.isEmpty(displayName)) {
            name = contactAddress;
        }
        return name;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }
    
	public ImageIcon getUserImage() {
		ImageIcon image = userIcon;
        return image;
	}

    public String getContact() {
        return contactAddress;
    }

    public int compareTo(Object o) {
		return getDisplayName().compareTo(((Buddy)o).getDisplayName());
	}
    
    public String toString() {
        return getDisplayName();
    }
}
