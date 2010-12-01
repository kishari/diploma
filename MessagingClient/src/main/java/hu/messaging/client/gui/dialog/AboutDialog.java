
package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Client about dialog 
 */
public class AboutDialog extends BaseDialog
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3838704212861903672L;

    public AboutDialog(JFrame frame)
    {
        super(frame, true, false);
        setTitle(Resources.resources.get("dialog.help.about.title"));
        setName(Resources.resources.get("dialog.help.about"));
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        JLabel nameLabel = new JLabel();
        nameLabel.setText(Resources.resources.get("application.name"));
        content.add(nameLabel, createConstraints());
        GridBagConstraints constraint = createConstraints();
        constraint.gridy = 1;
        JLabel versionLabel = new JLabel();
        versionLabel.setText(Resources.resources.get("application.version"));
        content.add(versionLabel, constraint);
        setContent(content);
        pack();
    }


    /**
     * Create constraints for the dialog component
     * @return The constaint
     */
    private GridBagConstraints createConstraints()
    {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.weightx = 1;
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.insets = new Insets(5, 5, 5, 5);
        return constraint;
    }
}
