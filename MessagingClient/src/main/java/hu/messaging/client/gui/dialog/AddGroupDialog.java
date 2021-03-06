package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class AddGroupDialog extends SimpleTextDialog {

	private static final long serialVersionUID = -4805896329658795053L;

	public AddGroupDialog(JFrame frame) {
		super(frame, "dialog.group.name.label");
		setTitle(Resources.resources.get("dialog.group.add.title"));
		KeyAdapter adapter = new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				getOkButton().setEnabled(dataCompleted());
			}
		};
		getOkButton().setEnabled(false);
		textField.addKeyListener(adapter);
		pack();
	}

	private boolean dataCompleted() {
		return textField.getText().length() > 0;
	}
}
