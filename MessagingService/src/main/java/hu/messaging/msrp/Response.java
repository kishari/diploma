package hu.messaging.msrp;

public class Response extends Message {

	@Override
	public String toString() {
		String msg = new String();
		msg += "MSRP " + getTransactionId() + " " + Constants.methods.get( getMethod() ) + "\r\n";
		msg += "To-Path: " + getToPath() + "\r\n";
		msg += "From-Path: " + getFromPath() + "\r\n";
		msg += "-------" + getTransactionId() + getEndToken();
		return msg;
	}
	
}
