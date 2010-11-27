package hu.messaging.msrp.event;

public class MSRPEvent {

	public static final int messageSentSuccessCode = 1;
	public static final int startTrasmissionCode = 2;
	public static final int brokenTrasmissionCode = 3;
	
	private String description;
	private int code;
	
	public MSRPEvent(String description, int code) {
		this.description = description;
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
