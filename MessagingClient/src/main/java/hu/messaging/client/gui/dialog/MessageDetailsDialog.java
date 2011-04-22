package hu.messaging.client.gui.dialog;

import java.awt.BorderLayout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.util.ImageUtil;
import hu.messaging.client.model.*;

public abstract class MessageDetailsDialog extends JFrame {
	
	private MessageInfoContainer messageInfoContainer;
	private byte[] content;
	private List<JFrame> children = new ArrayList<JFrame>();
	
	public MessageDetailsDialog(MessageInfoContainer messageInfoContainer, byte[] content) {
		this.messageInfoContainer = messageInfoContainer;
		this.content = content;
		
		createGUI();

	}
	
	protected JPanel createInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		
		final JLabel senderLabel = new JLabel(Resources.resources.get("detail.message.sender.label"));
		senderLabel.setFont(new Font("", Font.BOLD, 12));
		final JLabel s = new JLabel(messageInfoContainer.getSender().getName() + " <" + messageInfoContainer.getSender().getSipUri() + ">");
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(5,10,0,0);
		infoPanel.add(senderLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		infoPanel.add(s, c);
		
		JLabel j = new JLabel();
		c.gridx = 2;
		infoPanel.add(j, c);
		
		
		final JLabel subjectLabel = new JLabel(Resources.resources.get("detail.message.subject.label"));
		subjectLabel.setFont(new Font("", Font.BOLD, 12));
		final JLabel subj = new JLabel(messageInfoContainer.getSubject());
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		infoPanel.add(subjectLabel, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		infoPanel.add(subj, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		final JLabel messageTypeLabel = new JLabel(Resources.resources.get("detail.message.mimetype.label"));
		messageTypeLabel.setFont(new Font("", Font.BOLD, 12));
		infoPanel.add(messageTypeLabel, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		final JLabel type = new JLabel(messageInfoContainer.getContentDescription().getMimeType());
		infoPanel.add(type, c);
		
		return infoPanel;
	}
	
	private void createGUI() {
		setLayout(new BorderLayout());
		setTitle(Resources.resources.get("frame.message.details.title"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);             
        setIconImage(ImageUtil.createImage("logo.gif"));
		this.setSize(400, 150);
		
		this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		close();
        	}
        });
		
		final JPanel infoPanel = createInfoPanel();
		final JPanel buttonPanel = createButtonPanel();	
		
		add(BorderLayout.NORTH, infoPanel);
		add(BorderLayout.SOUTH, buttonPanel);
        
        setVisible(true);
	}
	
	protected abstract JPanel createButtonPanel();
	
	protected void addChild(JFrame child) {
		children.add(child);
	}
	
	public void close() {
		for (JFrame f : children) {
			f.setVisible(false);
			f.dispose();
		}
		
		MessageDetailsDialog.this.setVisible(false);
		MessageDetailsDialog.this.dispose();
	}
	
	public MessageInfoContainer getMessageInfoContainer() {
		return messageInfoContainer;
	}

	public byte[] getContent() {
		return content;
	}

}
