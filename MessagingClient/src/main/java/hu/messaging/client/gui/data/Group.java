package hu.messaging.client.gui.data;

import hu.messaging.client.gui.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Group implements Comparable<Object> {

	private String groupName;
	private String groupDisplayName;

	private final List<Buddy> buddies = new ArrayList<Buddy>();

	public Group(String name) {
		setName(name);
		setDisplayName(name);
	}

	public String getName() {
		return groupName;
	}

	public String getDisplayName() {
		return groupDisplayName;
	}

	public void setName(String name) {
		if (StringUtil.isEmpty(name)) {
			throw new IllegalArgumentException("Group name cannot be null or empty");
		}
		groupName = name;
	}

	public void setDisplayName(String name) {
		if (StringUtil.isEmpty(name)) {
			throw new IllegalArgumentException("Group name cannot be null or empty");
		}
		groupDisplayName = name;
	}

	public void addBuddy(Buddy buddy) {
		buddies.add(buddy);
	}

	public void removeBuddy(Buddy buddy) {
		buddies.remove(buddy);
	}

	public int getBuddiesCount() {
		return buddies.size();
	}

	public Buddy getBuddy(int contactIndex) {
		return buddies.get(contactIndex);
	}

	public int compareTo(Object o) {
		return groupName.compareTo(((Group) o).getDisplayName());
	}

	public void sortAscending() {
		Buddy[] buddyArray = buddies.toArray(new Buddy[buddies.size()]);
		Arrays.sort(buddyArray);
		buddies.clear();
		for (int i = 0; i < buddyArray.length; i++) {
			buddies.add(buddyArray[i]);
		}
	}

	public void sortDescending() {
		Buddy[] buddyArray = buddies.toArray(new Buddy[buddies.size()]);
		Arrays.sort(buddyArray);
		buddies.clear();
		for (int i = buddyArray.length - 1; i >= 0; i--) {
			buddies.add(buddyArray[i]);
		}
	}
	
	public List<Buddy> getBuddies() {
		return buddies;
	}
}
