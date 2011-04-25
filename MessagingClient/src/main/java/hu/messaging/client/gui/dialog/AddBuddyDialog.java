package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.data.Buddy;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddBuddyDialog extends BaseDialog {

	private static final long serialVersionUID = -6276676700561848746L;

	private JComboBox groupCombo;

	private JTextField uriTextField;

	private JTextField nameTextField;

	public AddBuddyDialog(JFrame frame, String[] groupList) {
		super(frame);
		setTitle(Resources.resources.get("dialog.buddy.add.title"));

		JPanel panel = new JPanel(new GridBagLayout());

		JLabel groupLabel = new JLabel();
		groupLabel.setText(Resources.resources.get("dialog.buddy.group.label"));
		GridBagConstraints constraints = createConstraints(false);
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(groupLabel, constraints);

		groupCombo = new JComboBox(groupList);
		groupCombo.setSelectedIndex(0);
		constraints = createConstraints(true);
		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(groupCombo, constraints);

		JLabel nameLabel = new JLabel();
		nameLabel.setText(Resources.resources.get("dialog.buddy.name.label"));
		constraints = createConstraints(false);
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(nameLabel, constraints);

		nameTextField = new JTextField();
		constraints = createConstraints(true);
		constraints.gridx = 1;
		constraints.gridy = 1;
		panel.add(nameTextField, constraints);

		KeyAdapter adapter = new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				getOkButton().setEnabled(dataCompleted());
			}
		};
		ActionListener enterListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataCompleted()) {
					okPressed();
				}
			}
		};
		nameTextField.addKeyListener(adapter);
		nameTextField.addActionListener(enterListener);
		JLabel uriLabel = new JLabel();
		uriLabel.setText(Resources.resources.get("dialog.buddy.uri.label"));
		constraints = createConstraints(false);
		constraints.gridx = 0;
		constraints.gridy = 2;
		nameTextField.setColumns(20);
		panel.add(uriLabel, constraints);

		uriTextField = new JTextField();
		uriTextField.addKeyListener(adapter);
		uriTextField.addActionListener(enterListener);
		constraints = createConstraints(true);
		constraints.gridx = 1;
		constraints.gridy = 2;
		panel.add(uriTextField, constraints);

		getOkButton().setEnabled(false);
		setContent(panel);
		pack();
	}

	@Override
	protected void save() {
		Buddy buddy = new Buddy(uriTextField.getText());
		buddy.setDisplayName(nameTextField.getText());
		setData(buddy);
	}

	public void setSelectedGroup(String group) {
		groupCombo.setSelectedItem(group);
	}

	public String getGroup() {
		return (String) groupCombo.getSelectedItem();
	}

	private boolean dataCompleted() {
		boolean completed = nameTextField.getText().length() > 0;
		Pattern pattern = Pattern.compile("sip:.*@.*");
		Matcher matcher = pattern.matcher(uriTextField.getText());
		completed &= matcher.matches();
		return completed;
	}
}
