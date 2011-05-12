package hu.messaging;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class User extends Observable{

	private String sipURI = null;	
	private Timer timer;
	private int expireDelay = 0;
	
	public User() {}
	
	public User(String sipURI) {
		this.sipURI = sipURI;
	}
	
	public User(String sipURI, int expireDelay) {
		System.out.println("new User:" + sipURI + " expires: " + expireDelay);
		this.sipURI = sipURI;
		this.expireDelay = 1000 * expireDelay;
		this.timer = new Timer();
		this.timer.schedule(new TimeOutTask(), this.expireDelay);
	}

	public String getSipURI() {
		return sipURI;
	}
	
	private void doTimeOut() {
		this.setChanged();
		this.notifyObservers();
	}
	
	private class TimeOutTask extends TimerTask {
		public void run() {
			doTimeOut();
			timer.cancel();
		}		
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User o = (User) obj;
			return this.sipURI.equals(o.sipURI);
		}
		return false;
	}
	
}
