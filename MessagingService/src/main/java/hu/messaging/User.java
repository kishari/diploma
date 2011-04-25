package hu.messaging;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class User extends Observable{

	private String sipURI = null;	
	private Timer timer;
	private int expireDelay = 0;
	
	public User() {}
	
	public User(String sipURI, int expireDelay) {
		System.out.println("new User:" + sipURI + " expires: " + expireDelay);
		this.sipURI = sipURI;
		this.expireDelay = 1000 * expireDelay + 10000;
		this.timer = new Timer();
		this.timer.schedule(new TimeOutTask(), this.expireDelay);
	}
	
	public void updateTimer(int expireDelay) {
		this.expireDelay = 1000 * expireDelay + 10000;
		System.out.println("User updateTimer");
		this.timer.cancel();
		this.timer = new Timer();
		this.timer.schedule(new TimeOutTask(), this.expireDelay);
	}

	public Timer getTimer() {
		return timer;
	}

	public String getSipURI() {
		return sipURI;
	}

	public void setSipURI(String sipURI) {
		this.sipURI = sipURI;
	}
	
	private void doTimeOut() {
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setExpireDelay(int expireDelay) {
		this.expireDelay = expireDelay;
	}

	public int getExpireDelay() {
		return expireDelay;
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
