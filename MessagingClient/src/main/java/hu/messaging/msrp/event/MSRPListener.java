package hu.messaging.msrp.event;

public interface MSRPListener {

	public void messageSentSuccess(MSRPEvent event);
	
	public void startTrasmission(MSRPEvent event);
	
	public void brokenTrasmission(MSRPEvent event);
	
}
