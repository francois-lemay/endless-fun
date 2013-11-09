package master;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RS485;

public class Master {
	
	// class variables
	
	
	public static void main(String[] args){
		
		// set master brick's friendly name
		RS485.setName("Master");
		
		String receiver = "Slave";
		
		LCD.drawString("Press to begin", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();
		
		// connect with slave brick
		NXTComm.connect(receiver, true);

		// write to DOS
		LCD.clear();
		LCD.drawString("Press to write to DOS", 0, 0);
		Button.waitForAnyPress();
		NXTComm.write(9);

		
		// do whatever needs to be done
		
		
		
		
		
		
		
		// end communication with slave
		LCD.drawString("Press to disconnect", 0, 0);
		Button.waitForAnyPress();
		NXTComm.disconnect();
		
		Button.waitForAnyPress();
	}

}
