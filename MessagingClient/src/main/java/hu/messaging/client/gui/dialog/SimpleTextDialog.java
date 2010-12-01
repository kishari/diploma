package hu.messaging.client.gui.dialog;

import hu.messaging.client.Resources;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class SimpleTextDialog extends BaseDialog
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 7069836578360954010L;

    protected JTextField textField;

    public SimpleTextDialog(JFrame frame, String label)
    {
        super(frame);
        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        panel.setName(label);
        JLabel labelText = new JLabel(Resources.resources.get(label));
        textField = new JTextField();
        textField.setColumns(20);
        textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                okPressed();
            }
        });
        GridBagConstraints constraint = createConstraints(false);
        panel.add(labelText, constraint);
        constraint = createConstraints(true);
        panel.add(textField, constraint);
        setContent(panel);
    }

    public void setTextValue(String text)
    {
        textField.setText(text);
    }
    @Override
    protected void save()
    {
        setData(textField.getText());
    }
}
