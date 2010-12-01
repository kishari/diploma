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


public abstract class BaseDialog extends JDialog
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8124372770435414564L;

    private boolean okPressed = false;
    /**
     * Data object of the dialog.
     */
    private Object data;

    private JButton okButton;
    
    public BaseDialog()
    {
        this(null, true, false);
    }
    public BaseDialog(JFrame frame)
    {
        this(frame, true, true);
    }

    public BaseDialog(JFrame frame, boolean isModal)
    {
        this(frame, isModal, true);
    }

    /**
     * Creates a new BaseDialog
     * @param frame parent frame
     * @param modal modality of the dialog
     * @param createCancelButton when <code>true</code>, Cancel will be added to the dialog
     */
    public BaseDialog(JFrame frame, boolean modal, boolean createCancelButton)
    {
        super(frame, modal);
        
        //setResizable(false);
        BorderLayout layout = new BorderLayout();
        layout.setVgap(5);
        layout.setHgap(5);
        setLayout(layout);
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new GridBagLayout());
        
        // Create ok/cancel button panel
        JPanel buttonPanel = new JPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(1);
        gridLayout.setHgap(5);
        buttonPanel.setLayout(gridLayout);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 5, 5));
        
        // Add an empty component to push the button panel to the right
        GridBagConstraints pushConstraint = new GridBagConstraints();
        pushConstraint.weightx = 1;
        pushConstraint.anchor = GridBagConstraints.EAST;
        bottomContainer.add(new JLabel(), pushConstraint);
        bottomContainer.add(buttonPanel);
        
        okButton = createButton("button.ok");
        buttonPanel.add(okButton);
        okButton.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					okPressed();
				}
			}});
        okButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
            	okPressed();
            }
        });

        if(createCancelButton)
        {
            // Create cancel button
            JButton cancelButton = createButton("button.cancel");

            buttonPanel.add(cancelButton);
            cancelButton.addKeyListener(new KeyAdapter(){
    			public void keyPressed(KeyEvent e)
    			{
    				if(e.getKeyCode() == KeyEvent.VK_ENTER)
    				{
    	                cancelPressed();
    				}
    			}});
            cancelButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    cancelPressed();
                }
            });
        }
        add(bottomContainer, BorderLayout.SOUTH);
        
        // change location...
        if (frame != null)
        {
            setLocation(frame.getLocation());
        }
    }
    /**
     * OK button was pressed
     *
     */
    protected void okPressed()
	{
    	okPressed = true;
        save();
        dispose();
	}
    /**
     * Cancel button was pressed
     */
	protected void cancelPressed()
	{
    	dispose();
	}

	/**
     * All children should implement this method. When OK is pressed, this
     * function will be called. setData() should be called in this function.
     */
    protected void save()
    {
       
    }

    /**
     * Returns data object for the dialog.
     * 
     * @return
     */
    public Object getData()
    {
        return data;
    }
    
    protected JButton getOkButton()
    {
        return okButton;
    }

    /**
     * Sets data object for the dialog
     * 
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }

    protected void setContent(Container content)
    {
        add(content, BorderLayout.CENTER);
    }

    private JButton createButton(String resource)
    {
        JButton button = new JButton(Resources.resources.get(resource));
        button.setName(resource);
        return button;
    }
    
	public boolean isOkPressed()
	{
		return okPressed;
	}
    /**
     * Create the GUI constraint 
     * @param fill <code>true</code> if it should fill horizontal
     * @return The constraint object
     */
    protected GridBagConstraints createConstraints(boolean fill)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        
        if (fill)
        {
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;    
        }
        
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }}
