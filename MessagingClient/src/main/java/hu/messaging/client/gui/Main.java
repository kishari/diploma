package hu.messaging.client.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color; //import javax.swing.plaf.ColorUIResource;
import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
			UIManager.put("Tree.rowHeight", 25);
			UIManager.put("Tree.paintLines", false);
			UIManager.put("Tree.leftChildIndent", 0);
			UIManager.put("Tree.selectionBackground", new Color(245, 223, 165));
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		} catch (UnsupportedLookAndFeelException ulafe) {
			System.out.println("Substance failed to set");
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Substance not found");
		} catch (InstantiationException ie) {
			System.out.println("Substance failed to instantiate");
		} catch (IllegalAccessException iae) {
			System.out.println("Access denied");
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.getLookAndFeelDefaults().put("ClassLoader",
						getClass().getClassLoader());
				MessagingClient client = new MessagingClient();
				client.setSize(300, 450);
				client.setVisible(true);
			}
		});
	}

}
