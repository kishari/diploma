package hu.messaging.client.model;

import java.util.LinkedList;

/**
 * A lokális csoportlista elemeinek típusát leíró osztály.
 * @author Harangozó Csaba
 *
 */
public class GroupListStruct {
	/**
	 * A csoport neve.
	 */
	public String groupName;
	
	/**
	 * A csoport tagjainak listája.
	 */
	public LinkedList<String> members;
	
	public GroupListStruct() {
		members = new LinkedList<String>();
		groupName = new String();
	}
}
