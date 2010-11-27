package hu.messaging;

public class Recipient {

	private String name;
	private String sipURI;
	
	public Recipient(String name, String sipURI) {
		this.name = name;
		this.sipURI = sipURI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSipURI() {
		return sipURI;
	}

	public void setSipURI(String sipURI) {
		this.sipURI = sipURI;
	}
	
	
}
