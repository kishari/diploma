package hu.messaging.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import hu.messaging.client.Resources;
import hu.messaging.client.media.audio.AudioPlayer;
import hu.messaging.client.model.MessageContainer;

public class AudioMessageDetailsFrame extends MessageDetailsFrame {
	
	private AudioPlayer player;		

	public AudioMessageDetailsFrame(MessageContainer message, byte[] content) {
		super(message, content);
		player = new AudioPlayer();
	}
	
	@Override
	protected JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
					
		final JButton okButton = new JButton(Resources.resources.get("button.ok"));
		
		okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	player.stop();
            	close();
            }
        });

		buttonPanel.add(okButton);
        
        final JButton playButton = new JButton(Resources.resources.get("button.play"));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.play(getContent());
            }
        });

        buttonPanel.add(playButton);
        
        final JButton stopButton = new JButton(Resources.resources.get("button.stop"));
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.stop();
            }
        });

        buttonPanel.add(stopButton);
        
		return buttonPanel;
	}

}
