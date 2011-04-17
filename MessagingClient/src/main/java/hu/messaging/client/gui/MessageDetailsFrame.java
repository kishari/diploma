package hu.messaging.client.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import hu.messaging.client.Resources;
import hu.messaging.client.model.*;

public abstract class MessageDetailsFrame extends JFrame {
	
	private MessageContainer message;
	private byte[] content;
	private List<JFrame> children = new ArrayList<JFrame>();
	
	public MessageDetailsFrame(MessageContainer message, byte[] content) {
		this.message = message;
		this.content = content;
		
		createGUI();

	}
	
	protected JPanel createInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		
		final JLabel senderLabel = new JLabel("Felad�: ");
		senderLabel.setFont(new Font("", Font.BOLD, 12));
		final JLabel s = new JLabel(message.getSender().getName() + " <" + message.getSender().getSipUri() + ">");
		
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
		
		
		final JLabel subjectLabel = new JLabel("T�rgy: ");
		subjectLabel.setFont(new Font("", Font.BOLD, 12));
		final JLabel subj = new JLabel(message.getSubject());
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
		final JLabel messageTypeLabel = new JLabel("�zenet t�pusa: ");
		messageTypeLabel.setFont(new Font("", Font.BOLD, 12));
		infoPanel.add(messageTypeLabel, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		final JLabel type = new JLabel(message.getMimeType());
		infoPanel.add(type, c);
		
		return infoPanel;
	}
	
	private void createGUI() {
		setLayout(new BorderLayout());
		setTitle(Resources.resources.get("frame.message.details.title"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);             
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("hu/messaging/client/gui/logo.gif")));
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
		System.out.println("addChild");
		children.add(child);
	}
	
	public void close() {
		System.out.println("close: " + children.size());
		for (JFrame f : children) {
			f.setVisible(false);
			f.dispose();
		}
		
		MessageDetailsFrame.this.setVisible(false);
		MessageDetailsFrame.this.dispose();
	}
	
	public MessageContainer getMessage() {
		return message;
	}

	public byte[] getContent() {
		return content;
	}

}
