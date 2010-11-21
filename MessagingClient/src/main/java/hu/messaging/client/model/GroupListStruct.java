package hu.messaging.client.model;

import java.util.LinkedList;

/**
 * A lok�lis csoportlista elemeinek t�pus�t le�r� oszt�ly.
 * @author Harangoz� Csaba
 *
 */
public class GroupListStruct {
	/**
	 * A csoport neve.
	 */
	public String groupName;
	
	/**
	 * A csoport tagjainak list�ja.
	 */
	public LinkedList<String> members;
	
	public GroupListStruct() {
		members = new LinkedList<String>();
		groupName = new String();
	}
}
