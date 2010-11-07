package hu.messaging.msrp;

import java.util.*;

public class Constants {

	public static final int methodSEND = 1;
	public static final int method200OK = 2;
	
    public static final Map<Integer, String> methods = new HashMap<Integer, String>(){
		{
            put(methodSEND, "SEND");
            put(method200OK, "200 OK");
        }
    };
}

