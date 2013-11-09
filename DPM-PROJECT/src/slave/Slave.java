package slave;

import lejos.nxt.Button;
import lejos.nxt.comm.RS485;

public class Slave {

	// class variables
	static String name = "Slave";
	
	
	public static void main(String[] args){
		
		// set slave brick's friendly name
		RS485.setName(name);
		
		// connect with master brick
		NXTComm.connect("foo", false);			
		
		Button.waitForAnyPress();
	}
	
}
