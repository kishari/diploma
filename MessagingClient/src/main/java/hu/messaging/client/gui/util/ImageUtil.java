package hu.messaging.client.gui.util;

import hu.messaging.client.gui.Main;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class ImageUtil {

	public static ImageIcon createImageIcon(String path) {
		ImageIcon icon = null;
		try {
			InputStream stream = Main.class.getResourceAsStream(path);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
			icon = new ImageIcon(bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return icon;
	}

	public static Image createImage(String path) {
		ImageIcon icon = createImageIcon(path);
		return icon.getImage();
	}
}
