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

    public Group getGroup(int i){
        return groupList.get(i);
    }
    
	public Group getDefaultGroup()
    {
      return getGroup(DEFAULT_GROUP_NAME);
    }
}
