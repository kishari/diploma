package hu.messaging.client.gui.bean;

import hu.messaging.client.gui.controller.ContactListController;

import javax.swing.JPanel;


public class BaseBean extends JPanel {

    private static final long serialVersionUID = 853216640750041696L;
    
    private Object data;

    protected ContactListController contactListController;
    
    public BaseBean(ContactListController controller)
    {
        this.contactListController = controller;
    }
    
    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }
}
