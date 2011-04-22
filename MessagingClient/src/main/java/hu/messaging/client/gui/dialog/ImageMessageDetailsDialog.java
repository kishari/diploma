package hu.messaging.client.gui.dialog;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import hu.messaging.client.Resources;
import hu.messaging.client.model.*;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageMessageDetailsDialog extends MessageDetailsDialog {

	public ImageMessageDetailsDialog(MessageInfoContainer messageInfoContainer, byte[] content) {
		super(messageInfoContainer, content);
	}

	@Override
	protected JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
					
		final JButton okButton = new JButton(Resources.resources.get("button.ok"));
		
		okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

		buttonPanel.add(okButton);
        
        final JButton playButton = new JButton(Resources.resources.get("button.show"));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFrame f = new JFrame();            	
            	ImagePanel ip = new ImagePanel(getContent());
            	f.getContentPane().add(ip);
            	f.setSize(ip.getImageWidth(), ip.getImageHeight());
            	f.setVisible(true);
            	addChild(f);
            }
        });

        buttonPanel.add(playButton);
                
		return buttonPanel;
	}
	
	private class ImagePanel extends JPanel {
		private BufferedImage image;
		
		public ImagePanel(byte[] imageContent) {
	    	InputStream is = new ByteArrayInputStream(imageContent);
	    	try {
	    		image = ImageIO.read(is);
	    	}
	    	catch(IOException e) {e.printStackTrace();}
		    
		  }

		  public void paint(Graphics g) {
		    g.drawImage( image, 0, 0, null);
		  }
		  
		  public int getImageWidth() {
			  return image.getWidth();
		  }
		  
		  public int getImageHeight() {
			  return image.getHeight();
		  }
	}

}
