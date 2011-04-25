package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class BaseDialog extends JDialog {
	private static final long serialVersionUID = -8124372770435414564L;

	private boolean okPressed = false;

	private Object data;

	private JButton okButton;

	public BaseDialog() {
		this(null, true, false);
	}

	public BaseDialog(JFrame frame) {
		this(frame, true, true);
	}

	public BaseDialog(JFrame frame, boolean isModal) {
		this(frame, isModal, true);
	}
	
	public BaseDialog(JFrame frame, boolean modal, boolean createCancelButton) {
		super(frame, modal);

		BorderLayout layout = new BorderLayout();
		layout.setVgap(5);
		layout.setHgap(5);
		setLayout(layout);
		JPanel bottomContainer = new JPanel();
		bottomContainer.setLayout(new GridBagLayout());

		JPanel buttonPanel = new JPanel();
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(1);
		gridLayout.setHgap(5);
		buttonPanel.setLayout(gridLayout);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 5, 5));

		GridBagConstraints pushConstraint = new GridBagConstraints();
		pushConstraint.weightx = 1;
		pushConstraint.anchor = GridBagConstraints.EAST;
		bottomContainer.add(new JLabel(), pushConstraint);
		bottomContainer.add(buttonPanel);

		okButton = createButton("button.ok");
		buttonPanel.add(okButton);
		okButton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					okPressed();
				}
			}
		});
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});

		if (createCancelButton) {
			JButton cancelButton = createButton("button.cancel");

			buttonPanel.add(cancelButton);
			cancelButton.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						cancelPressed();
					}
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
		}
		add(bottomContainer, BorderLayout.SOUTH);

		if (frame != null) {
			setLocation(frame.getLocation());
		}
	}

	protected void okPressed() {
		okPressed = true;
		save();
		dispose();
	}

	protected void cancelPressed() {
		dispose();
	}

	protected void save() {

	}

	public Object getData() {
		return data;
	}

	protected JButton getOkButton() {
		return okButton;
	}

	public void setData(Object data) {
		this.data = data;
	}

	protected void setContent(Container content) {
		add(content, BorderLayout.CENTER);
	}

	private JButton createButton(String resource) {
		JButton button = new JButton(Resources.resources.get(resource));
		button.setName(resource);
		return button;
	}

	public boolean isOkPressed() {
		return okPressed;
	}

	protected GridBagConstraints createConstraints(boolean fill) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;

		if (fill) {
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
		}

		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		return constraints;
	}
}
