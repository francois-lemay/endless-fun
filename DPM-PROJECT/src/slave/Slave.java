package slave;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RS485;
import official.NXTComm;


/**
 * slave brick's main class
 * @author Francois
 *
 */
public class Slave {

	// class variables
	
	public static void main(String[] args){
		
		// set slave brick's friendly name
		RS485.setName("Slave");
		
		// connect with master brick
		NXTComm.connect("It doesnt matter what name goes here", false);			
		
		// read from DIS
		while(true){
			int n = NXTComm.read();
			if(n==9){
				LCD.clear();
				LCD.drawString("Succesful transfer", 0, 7);
				Button.waitForAnyPress();
				break;
			}
		}
		
		// do whatever needs to be done
		
		
		
		Button.waitForAnyPress();
	}
}
