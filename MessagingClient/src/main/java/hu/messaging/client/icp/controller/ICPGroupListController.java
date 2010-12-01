package hu.messaging.client.icp.controller;

import hu.messaging.client.gui.controller.ContactListController;
import hu.messaging.client.gui.controller.ICPController;
import hu.messaging.client.gui.data.Buddy;
import hu.messaging.client.gui.data.ContactList;
import hu.messaging.client.gui.data.Group;
import hu.messaging.client.gui.listener.ui.ContactListEvent;
import hu.messaging.client.gui.util.IcpUtils;
import hu.messaging.client.gui.util.StringUtil;
import hu.messaging.client.icp.listener.GroupListManagerListener;

import com.ericsson.icp.IProfile;
import com.ericsson.icp.services.PGM.IRLSGroup;
import com.ericsson.icp.services.PGM.IRLSMListener;
import com.ericsson.icp.services.PGM.IRLSManager;
import com.ericsson.icp.services.PGM.PGMFactory;
import com.ericsson.icp.util.IBuddy;
import com.ericsson.icp.util.IIterator;
import com.ericsson.icp.util.IUri;
import com.ericsson.icp.util.ITuple.Basic;


/**
 * Handles the creation and updates of groups and buddies
 */
public class ICPGroupListController
{

    /**
     * The ICP group list manager
     */
    private IRLSManager groupListManager;

    /**
     * The application contact list controller
     */
    private ContactListController contactListController;


    /**
     * The user identity
     */
    private String identity;

    /**
     * The group list manager listener that received the callback
     */
    private GroupListManagerListener glmListener;

    public ICPGroupListController(IProfile profile, ContactListController contactListController) throws Exception
    {
    	System.out.println(getClass().getSimpleName() + " konstuktor called");
        contactListController.setIcpGroupListController(this);
        groupListManager = PGMFactory.createRLSManager(profile);

        identity = profile.getIdentity();
        glmListener = new GroupListManagerListener();

        groupListManager.addListener((IRLSMListener) glmListener);
        this.contactListController = contactListController;

        loadGroups();        
        
        createDefaultGroup();
    }

    /**
     * Add contact to the specified group and create the group if it doesn't exist.
     * 
     * @throws Exception
     * @see hu.messaging.client.gui.listener.ui.ContactListListener#buddyAdded(hu.messaging.client.gui.data.Group, hu.messaging.client.gui.data.Buddy)
     */
    public void addBuddy(Group group, Buddy contact) throws Exception
    {
    	System.out.println(getClass().getSimpleName() + "addBuddy: " + group.getDisplayName() + " buddy: " + contact.getDisplayName());
        IRLSGroup icpGroup = findGroup(group.getName());
        IBuddy newBuddy = PGMFactory.createBuddy(contact.getContact());
        newBuddy.setDisplayName(contact.getDisplayName());
        icpGroup.addMember(newBuddy);
        glmListener.clear();
        groupListManager.modifyGroup(icpGroup);
        glmListener.waitForCallback();
    }

    /**
     * @throws Exception
     * @see hu.messaging.client.gui.listener.ui.ContactListListener#buddyRemoved(hu.messaging.client.gui.data.Group, hu.messaging.client.gui.data.Buddy)
     */
    public void removeBuddy(Group group, Buddy contact) throws Exception
    {
        IRLSGroup icpGroup = findGroup(group.getName());
        if(icpGroup != null)
        {
            IBuddy buddyToRemove = findBuddy(icpGroup, contact.getContact());
            if(buddyToRemove != null)
            {
                icpGroup.removeMember(buddyToRemove);
                groupListManager.modifyGroup(icpGroup);
            }
        }
    }

    /**
     * @throws Exception 
     * @see hu.messaging.client.gui.listener.ui.ContactListListener#groupAdded(hu.messaging.client.gui.data.Group)
     */
    public void addGroup(Group group) throws Exception
    {
        IRLSGroup icpGroup = PGMFactory.createGroup(group.getName());
        icpGroup.setDisplayName(group.getName());
        glmListener.clear();
        groupListManager.addGroup(icpGroup);
        glmListener.waitForCallback();
    }

    /**
     * @throws Exception
     * @see hu.messaging.client.gui.listener.ui.ContactListListener#groupRemoved(hu.messaging.client.gui.data.Group)
     */
    public void removeGroup(Group group) throws Exception
    {
        IRLSGroup icpGroup = findGroup(group.getName());
        if(icpGroup != null)
        {
            groupListManager.removeGroup(icpGroup);
            icpGroup.release();
        }
    }

    public void modifyBuddy(Group group, Buddy contact) throws Exception
    {
        IRLSGroup icpGroup = findGroup(group.getName());
        if(icpGroup != null)
        {
            IBuddy buddyToUpdate = findBuddy(icpGroup, contact.getContact());
            if(buddyToUpdate != null)
            {
                buddyToUpdate.setDisplayName(contact.getDisplayName());
                icpGroup.modifyMember(buddyToUpdate);
                groupListManager.modifyGroup(icpGroup);
            }
        }
    }

    public void modifyGroup(Group group) throws Exception
    {
        IRLSGroup icpGroup = findGroup(group.getName());
        if(icpGroup != null)
        {
            icpGroup.setDisplayName(group.getDisplayName());
            groupListManager.modifyGroup(icpGroup);
        }
    }

    /**
     * Find the group indentified by name.
     * 
     * @param name
     * @return The group finded or null if no group with the identifier name is finded.
     */
    private IRLSGroup findGroup(String name)
    {
        IRLSGroup icpGroup = null;
        try
        {
            icpGroup = groupListManager.searchGroup(name);
        }
        catch (Exception e)
        {
            //ICPController.error("Could not find group", e);
        }
        return icpGroup;
    }

    /**
     * Find a user
     * 
     * @param iterator A user iterator
     * @param contactUri The URI to find
     * @return The buddy if found
     */
    private IBuddy findBuddy(IRLSGroup group, String contactUri)
    {
        IBuddy icpBuddy = null;
        try
        {
            IIterator itr = group.getMembers();
            while (itr.hasNext() && (icpBuddy == null))
            {
                itr.next();
                icpBuddy = (IBuddy) itr.getElement();
                if(!icpBuddy.getUri().equals(contactUri))
                {
                    icpBuddy = null;
                }
            }
        }
        catch (Exception e)
        {
            //ICPController.error("Error searching for buddy", e);
        }
        return icpBuddy;
    }

    /**
     * Get the groups and the buddies
     * 
     * @throws Exception
     */
    private void loadGroups() throws Exception
    {
    	System.out.println(getClass().getSimpleName() + " : loadGroups()");
        IIterator groupList = groupListManager.getGroupIterator();
        while (groupList.hasNext())        	
        {
        	System.out.print("csoport: ");
            groupList.next();
            IRLSGroup icpGroup = (IRLSGroup) groupList.getElement();

            System.out.println(icpGroup.getName());
            if(!StringUtil.isEmpty(icpGroup.getDisplayName()))
            {
            	System.out.println("displayName nem ures: " + icpGroup.getDisplayName());
                Group group = new Group(icpGroup.getName());
                group.setDisplayName(icpGroup.getDisplayName());
                contactListController.addGroupToGui(group);

                IIterator buddyList = icpGroup.getMembers();
                while (buddyList.hasNext())
                {
                    buddyList.next();
                    IBuddy icpBuddy = (IBuddy) buddyList.getElement();
                    Buddy buddy = new Buddy(icpBuddy.getUri());
                    buddy.setDisplayName(icpBuddy.getDisplayName());
                    contactListController.addBuddy(group, buddy, false);
                }
            }
        }
    }

    /**
     * Create the default gr4oup as required
     */
    private void createDefaultGroup()
    {
        if(contactListController.getDefaultGroup() == null)
        {
            // Create a default group
            Group group = new Group(ContactList.DEFAULT_GROUP_NAME);
            contactListController.addGroup(group);
        }
    }

    /**
     * Release this instance
     */
    public void release()
    {
        try
        {
            IcpUtils.release(groupListManager);
        }
        catch (Exception e)
        {
            //ICPController.error("Error releasing group controller", e);
        }
    }

}
