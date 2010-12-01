package hu.messaging.client.gui.data;

import hu.messaging.client.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContactList {
    private final List<Group> groupList = new ArrayList<Group>();
    public static final String DEFAULT_GROUP_NAME = Resources.resources.get("buddy.group.default");

    public void addGroup(Group group) {
    	if(getGroup(group.getName()) == null) {
    		groupList.add(group);
    	}
    }

    public void sortGroupAscending() {
    	Group[] groupArray = groupList.toArray(new Group[groupList.size()]);
    	Arrays.sort(groupArray);
    	groupList.clear();
    	for(int i = 0; i < groupArray.length; i++) {
    		groupList.add(groupArray[i]);
    	}
    }

    public void sortGroupDescending() {
    	Group[] groupArray = groupList.toArray(new Group[groupList.size()]);
    	Arrays.sort(groupArray);
    	groupList.clear();
    	for(int i = groupArray.length - 1; i >= 0; i--) {
    		groupList.add(groupArray[i]);
    	}
    }

    public boolean removeGroup(Group group) {   
    	boolean success = false;
    	if(!group.getName().equals(DEFAULT_GROUP_NAME))	{
	        groupList.remove(group);
	        success = true;
    	}
    	
    	return success;
    }
    
    public int getGroupCount() {
        return groupList.size();
    }

    public void addBuddy(Buddy contact) {
        getDefaultGroup().addBuddy(contact);
    }
    
    public void addBuddy(Group group, Buddy contact) {
        Group realGroup = getGroup(group.getName());
        if(realGroup == null) {
            throw new IllegalArgumentException("Group name does not currently exist!");
        }
        realGroup.addBuddy(contact);
    }

    public Group getGroup(String name) {
        Group group = null;
        for(int i=0; i < groupList.size();i++){
            Group currentGroup = groupList.get(i);
            if(currentGroup.getName().equals(name)) {
                group = currentGroup;
                break;
            }
        }
        return group;
    }
    /**
     * Retrieve a group by its display name
     * @param name The group display name
     * @return The group
     */
    public Group getGroupNamed(String displayName)
    {
        Group group = null;
        for(int i=0; i<groupList.size();i++)
        {
            Group currentGroup = groupList.get(i);
            if(currentGroup.getDisplayName().equals(displayName))
            {
                group = currentGroup;
                break;
            }
        }
        return group;
    }
    /**
     * Get a group at a given index
     * @param i The index
     * @return The group at this index
     */
    public Group getGroup(int i)
    {
        return groupList.get(i);
    }

    /**
     * Get the total number of contacts
     * @return The total number of contacts
     */
    public int getBuddyCount()
    {
        int nbContacts = 0;
        for(int groupIndex=0; groupIndex<groupList.size(); groupIndex++)
        {
            Group group = groupList.get(groupIndex);
            nbContacts += group.getBuddiesCount();
        }
        return nbContacts;
    }
    /**
     * Get ta contact by its name
     * @param buddyName The name to find
     * @return The contact
     */
	public Buddy getBuddy(String buddyName)
	{
		Buddy buddy = null;
        for(Group group : groupList)
        {
            buddy = group.getBuddy(buddyName);
            if (buddy != null)
            {
                break;
            }
        }
        return buddy;
	}
    /**
     * Get ta contact by its name
     * @param buddyName The name to find
     * @return The contact
     */
    public Buddy getBuddyNamed(String buddyName)
    {
        Buddy buddy = null;
        for(Group group : groupList)
        {
            buddy = group.getBuddy(buddyName);
            if (buddy != null)
            {
                break;
            }
        }
        return buddy;
    }
    /**
     * Retirve the default group
     * @return The default group
     */
	public Group getDefaultGroup()
    {
      return getGroup(DEFAULT_GROUP_NAME);
    }
    /**
     * Retrieve all the buddies in the contact list
     * @return A list of <code>Buddy</code>
     */
	public List<Buddy> getBuddies()
    {
        ArrayList<Buddy> buddies = new ArrayList<Buddy>();
        
        for(int groupIndex=0; groupIndex<groupList.size(); groupIndex++)
        {
            Group group = groupList.get(groupIndex);
            buddies.addAll(group.getBuddies());
        }
        return buddies;
    }
}
