package master;

import lejos.nxt.Button;
import lejos.nxt.comm.RS485;

public class Master {
	
	// class variables
	static String name = "Master";
	
	
	public static void main(String[] args){
		
		// set master brick's friendly name
		RS485.setName(name);
		
		// connect with slave brick
		NXTComm.connect(name, true);
		
		
		Button.waitForAnyPress();
	}

}
