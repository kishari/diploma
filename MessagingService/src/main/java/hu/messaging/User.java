package hu.messaging;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class User extends Observable{

	private boolean online = false;
	private String sipURI = null;
	private Timer timer;
	
	public User(String sipURI) {
		this.sipURI = sipURI;
		this.timer = new Timer();
		this.timer.schedule(new TimeOutTask(), Constants.onlineUserTimeOut);
	}
	
	public void updateTimer() {
		System.out.println("User updateTimer");
		this.timer.cancel();
		this.timer = new Timer();
		this.timer.schedule(new TimeOutTask(), Constants.onlineUserTimeOut);
	}

	public Timer getTimer() {
		return timer;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
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
	
	private class TimeOutTask extends TimerTask {
		public void run() {
			doTimeOut();
			timer.cancel();
		}		
	}
	
}
