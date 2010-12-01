/* **********************************************************************
 * Copyright (c) Ericsson 2006. All Rights Reserved.
 * Reproduction in whole or in part is prohibited without the 
 * written consent of the copyright owner. 
 * 
 * ERICSSON MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE, OR NON-INFRINGEMENT. ERICSSON SHALL NOT BE LIABLE FOR ANY 
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR 
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. 
 * 
 * **********************************************************************/
package hu.messaging.client.gui.data;

import hu.messaging.client.gui.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Group containing contacts that we subscribe to
 *
 */
public class Group implements Comparable<Object>
{
    /**
     * Group name. Cannot be null/empty.
     */
    private String groupName;
    /**
     * Group Display name .
     */
    private String groupDisplayName;
    /**
     * Contacts associated with this group
     */
    private final List<Buddy> buddies = new ArrayList<Buddy>();

    /**
     * Build the group object
     * @param name  The group name
     */
    public Group(String name)
    {
        setName(name);
        setDisplayName(name);
    }

    /**
     * Retrieve the group name
     * @return The group name
     */
    public String getName()
    {
        return groupName;
    }
    /**
     * Retrieve the group name
     * @return The group name
     */
    public String getDisplayName()
    {
        return groupDisplayName;
    }

    /**
     * Sets the name of the group. Cannot be null or empty. This will chaneg the group
     * name for the GUI only, not on the server
     * @param name The new group name
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setName(String name)
    {
        if(StringUtil.isEmpty(name))
        {
        	System.out.println(name);
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        groupName = name;
    }
    /**
     * Sets the name of the group. Cannot be null or empty. This will chaneg the group
     * name for the GUI only, not on the server
     * @param name The new group name
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setDisplayName(String name)
    {
        if(StringUtil.isEmpty(name))
        {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        groupDisplayName = name;
    }
    /**
     * Add a buddy to the group
     * @param buddy The buddy to add to the group
     */
    public void addBuddy(Buddy buddy)
    {
        buddies.add(buddy);
    }
    /**
     * Remove a buddy from the group. Removing a body not part of the group has no effect. 
     * @param buddy The buddy to remove
     */
    public void removeBuddy(Buddy buddy)
    {
        buddies.remove(buddy);
    }
    /**
     * Get the number of buddies in the group
     * @return The number of buddies in the group
     */
    public int getBuddiesCount()
    {
        return buddies.size();
    }

    /**
     * Get a buddy at a given index
     * @param contactIndex The index
     * @return The buddy
     */
    public Buddy getBuddy(int contactIndex)
    {
        return buddies.get(contactIndex);
    }
    /**
     * @inheritDoc
     */
	public int compareTo(Object o)
	{
		return groupName.compareTo(((Group)o).getDisplayName());
	}

	public void sortAscending()
	{
		Buddy[] buddyArray = buddies.toArray(new Buddy[buddies.size()]);
    	Arrays.sort(buddyArray);
    	buddies.clear();
    	for(int i = 0; i < buddyArray.length; i++)
    	{
    		buddies.add(buddyArray[i]);
    	}
	}
	public void sortDescending()
	{
		Buddy[] buddyArray = buddies.toArray(new Buddy[buddies.size()]);
    	Arrays.sort(buddyArray);
    	buddies.clear();
    	for(int i = buddyArray.length-1; i >= 0; i--)
    	{
    		buddies.add(buddyArray[i]);
    	}
	}
	/**
     * Retrieve a <code>Buddy</code> 
     * @param buddyName The name of the buddy to get
     * @return The <code>Buddy</code>, or <code>null</code> if not found
	 */
	public Buddy getBuddy(String buddyName)
	{
		Buddy buddy = null;
		Iterator<Buddy> iterator = buddies.iterator();
		while(iterator.hasNext() && (buddy == null))
		{
			buddy = (Buddy) iterator.next();
			if(!buddy.getDisplayName().equals(buddyName))
			{
                buddy = null;
			}
		}
		return buddy;
	}
    /**
     * Return all the buddies for this group
     * @return A list of <code>Buddy</code>
     */
    public List<Buddy> getBuddies()
    {
        return buddies;
    }
}
