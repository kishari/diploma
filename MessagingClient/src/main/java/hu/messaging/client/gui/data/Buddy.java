package hu.messaging.client.gui.data;

import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.gui.util.StringUtil;

import javax.swing.ImageIcon;


public class Buddy implements Comparable<Object> {
	
    /**
     * sip contact address. eg: sip:bob@ericsson.com
     */
    private String contactAddress;
    
    /**
     * Display name for the user. If null, the contact address is used
     */
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
		return displayName.compareTo(((Buddy)o).getDisplayName());
	}
    
    public String toString() {
        return getDisplayName();
    }
}
