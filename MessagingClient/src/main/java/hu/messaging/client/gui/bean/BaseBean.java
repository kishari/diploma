package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ContactListController;

import javax.swing.JPanel;


public class BaseBean extends JPanel {

    private static final long serialVersionUID = 853216640750041696L;
    
    /**
     * Data object
     */
    private Object data;

    /**
     * Controller of the bean
     */
    protected ContactListController contactListController;
    
    public BaseBean(ContactListController controller)
    {
        this.contactListController = controller;
    }
    
    /**
     * Returns the current data object. Can be <code>null</code>.
     * @return The data object
     */
    public Object getData()
    {
        return data;
    }
    /**
     * Sets new data object.
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }
}
