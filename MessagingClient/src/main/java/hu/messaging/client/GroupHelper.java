package hu.messaging.client;

import hu.messaging.client.model.GroupListStruct;

import java.util.Iterator;
import java.util.LinkedList;

import com.ericsson.icp.services.PGM.IRLSGroup;
import com.ericsson.icp.services.PGM.IRLSManager;
import com.ericsson.icp.services.PGM.PGMFactory;
import com.ericsson.icp.util.IBuddy;
import com.ericsson.icp.util.IIterator;

public class GroupHelper {

	private IRLSManager rlsManager = null;
	private LinkedList<GroupListStruct> groupList = new LinkedList<GroupListStruct>();
	
	public GroupHelper(IRLSManager rlsManager) {
		this.rlsManager = rlsManager;
		init();
	}
	
	private boolean init() {
		boolean ret = true;
		ret = getGroups();
		
		return ret;
	}
	
	/**
	 * Hozz�adja a buddyURI felhaszn�l�t a megadott csoporthoz �s friss�ti a PGM szervert.
	 * @param buddyURI
	 * @param groupName
	 */
	public void addBuddyToGroup(String buddyURI, String groupName){
		
		IIterator iterator;
		try {
			iterator = rlsManager.getGroupIterator();		
			iterator.reset();
			boolean groupNameIsValid = false;
			
			IRLSGroup group = PGMFactory.createGroup("temp");
			
			while( iterator.hasNext() )	{
				iterator.next();
				group = (IRLSGroup) iterator.getElement();
				if (group.getName().equals(groupName)) {
					groupNameIsValid = true;
					break;
				}
			}
			
			if (groupNameIsValid) {
				IBuddy buddy = PGMFactory.createBuddy(buddyURI);
				group.addMember(buddy);
				addBuddyToGroupList(buddyURI.toString(), groupName);
				
				System.out.println("---------------------------------------");
				System.out.println(group.getName() + " tulajdonos: " + group.getUri());
				IIterator it = group.getMembers();
				while (it.hasNext()) {
					it.next();
					buddy = (IBuddy) it.getElement();
					String str0 = buddy.getUri();
					String str = it.getElement().toString();
					System.out.println(str0 + " " + str);
				}
			
				rlsManager.modifyGroup(group);
			}
				
			iterator.release();
			
		} catch (Exception e) {
			if (e.getMessage().equals("The object already exists.")) {
				System.out.println(e.getMessage() + 
								   " Ez az felhaszn�l� URI m�r tagja a " + groupName + " csoportnak!");
			}
			else if (e.getMessage().equals("creat object error")) {
				System.out.println(e.getMessage() + "! " + "Formailag hib�s URI. pl: sip:name@domain");
			}
		}		
	}
	
	/**
	 * A buddyURI felhaszn�l�t hozz�adja a megadott csoporthoz a lok�lis csoportlist�ban.
	 * @param buddyURI
	 * @param groupName
	 */
	private void addBuddyToGroupList(String buddyURI, String groupName) {
		Iterator<GroupListStruct> i = groupList.iterator();
		while (i.hasNext()) {
			GroupListStruct element = new GroupListStruct();
			element = i.next();
    		if (groupName.equals(element.groupName)) {
    			element.members.add(buddyURI);
    			System.out.println(groupName + " csoporthoz " + buddyURI + " hozz�adva!");
    			
    		}
    	}
	}
	
	/**
	 * A buddyURI felhaszn�l�t t�rli a PGM szerverr�l az adott csoportb�l.
	 * @param buddyURI
	 * @param groupName
	 */
	public void delBuddyFromGroup(String buddyURI, String groupName){
		
		IIterator iterator;
		try {
			iterator = rlsManager.getGroupIterator();		
			iterator.reset();
			boolean groupNameIsValid = false;
			
			IRLSGroup group = PGMFactory.createGroup("temp");
			
			while( iterator.hasNext() )	{
				iterator.next();
				group = (IRLSGroup) iterator.getElement();
				if (group.getName().equals(groupName)) {
					groupNameIsValid = true;
					break;
				}
			}
			if (groupNameIsValid) {
				
				IBuddy buddy = PGMFactory.createBuddy(buddyURI);
				group.removeMember(buddy);
				delBuddyFromGroupList(buddyURI, groupName);
				
				System.out.println("---------------------------------------------");
				
				System.out.println(group.getName() + " tulajdonos: " + group.getUri());
				IIterator it = group.getMembers();
				while (it.hasNext()) {
					it.next();
					buddy = (IBuddy) it.getElement();
					String str0 = buddy.getUri();
					String str = it.getElement().toString();
					System.out.println(str0 + " " + str);
				}
			
				rlsManager.modifyGroup(group);
			}
		
			iterator.release();
			
		} catch (Exception e) {
			System.out.println(e.getMessage() + " Ez a felhaszn�l� URI nem tagja a csoportnak!");
		}
	}
	
	/**
	 * A buddyURI felhaszn�l�t t�rli a megadott csoportb�l a lok�lis csoportlist�ban.
	 * @param buddyURI
	 * @param groupName
	 */
	private void delBuddyFromGroupList(String buddyURI, String groupName) {
		Iterator<GroupListStruct> i = groupList.iterator();
		while (i.hasNext()) {
			GroupListStruct element = new GroupListStruct();
			element = i.next();
    		if (groupName.equals(element.groupName)) {
    			Iterator<String> i2 = element.members.iterator();
    			while (i2.hasNext()) {
    				String member = i2.next();
    				if (member.equals(buddyURI)) {
    					i2.remove();
    					System.out.println(groupName + " csoportb�l a " + buddyURI + " elt�vol�tva!");
    				}
    			}
    		}
    	}
	}
	
	/**
	 * Lek�ri a PGM szervert�l a csoportokat.
	 */
	public boolean getGroups() {
		
		groupList.clear();

		try {
			IIterator iterator;
			iterator = rlsManager.getGroupIterator();		
			iterator.reset();
			
			IRLSGroup group = PGMFactory.createGroup("temp");
			
			while( iterator.hasNext() )	{
				
				iterator.next();
				group = (IRLSGroup) iterator.getElement();
				
				GroupListStruct element = new GroupListStruct();
				element.groupName = group.getName();
				System.out.println("element.groupName: " + element.groupName);
				element.members = new LinkedList<String>();
				IIterator i = group.getMembers();
				while (i.hasNext()) {
					i.next();
					IBuddy member = (IBuddy) i.getElement();
					element.members.add(member.getUri());
				}
				groupList.add(element);
				
			}
			System.out.println("-------------------------****------------------------");
			Iterator<GroupListStruct> i = groupList.iterator();
			GroupListStruct element = new GroupListStruct();
	   		while (i.hasNext()) {
	   			element = i.next();
				System.out.println(element.groupName);
			}
	   		
	   		iterator.release();
	   		
	   		System.out.println("-------------------------------------------------");
		} catch (Exception e) {
			System.out.println("getGroups catch");
			return false;
		}
		return true;
	}
	
	/**
	 * Hozz�ad a PGM szerverhez egy �j csoportot.
	 * @param groupName
	 */
	public void addGroup(String groupName) {
		try {
			
			IRLSGroup group = PGMFactory.createGroup(groupName);
			System.out.println("checkout1");
			if (rlsManager == null) {
				System.out.println("rlsManager null");
			}
			
			rlsManager.addGroup(group);
			System.out.println("checkout2");
			GroupListStruct temp = new GroupListStruct();
			System.out.println("checkout3");
			temp.groupName = groupName;
			System.out.println("checkout4");
			temp.members = new LinkedList<String>();
			System.out.println("checkout5");
			groupList.add(temp);
			
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " +
					"Sikertelen csoport l�trehoz�s!");
			//e.printStackTrace();			
		}
	}
	
	/**
	 * T�rli a PGM szerverr�l a megadott csoportot.
	 * @param groupName
	 */
	public void delGroup(String groupName) {
		try {
			System.out.println("delete group from pgm: " + groupName);

			IIterator iterator = rlsManager.getGroupIterator();
			iterator.reset();
			IRLSGroup group = PGMFactory.createGroup("1");
			while( iterator.hasNext() )	{
				iterator.next();
				group = (IRLSGroup) iterator.getElement();
				if (group.getName().equals(groupName)) {
					rlsManager.removeGroup(group);
					break;
				}			
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " +
					"Sikertelen csoport t�rl�s!");	
		}
		delGroupFromList(groupName);
	}
	
	/**
	 * T�rli a lok�lis csoportlist�b�l a megadott csoportot.
	 * @param groupName
	 */
	private void delGroupFromList(String groupName) {
			
		GroupListStruct temp = new GroupListStruct();
		System.out.println("delete group from local list: " + groupName);
		Iterator i = groupList.iterator();
		while (i.hasNext()) {
			temp = (GroupListStruct) i.next();
			if (temp.groupName.equals(groupName)) {
				i.remove();
				break;
			}
		}
	}
	
	/**
	 * T�rli a felhaszn�l� �sszes csoportj�t a PGM szerverr�l.
	 */
	public void delAllGroupsFromPGM() {
		
		IIterator iterator;
		try {
			iterator = rlsManager.getGroupIterator();
			iterator.reset();
			IRLSGroup group = PGMFactory.createGroup("1");
			while( iterator.hasNext() )	{
				iterator.next();
				group = (IRLSGroup) iterator.getElement();
				rlsManager.removeGroup(group);
			}
			groupList.clear();
		} catch (Exception e) {
			System.out.println(" delAllGroupsFromPGM catch");
			//e.printStackTrace();
		}		
	}
	
	/**
	 * Visszaadja a lok�lis csoportlist�t.
	 * @return csoport list�ja
	 */
	public LinkedList<GroupListStruct> getGroupList() {
		return groupList;
	}
}
