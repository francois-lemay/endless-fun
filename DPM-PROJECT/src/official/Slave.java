package official;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RS485;


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
		
		// set up clamp and lift motors
		NXTRegulatedMotor clamp = new NXTRegulatedMotor(Constants.clampMotorPort);
		NXTRegulatedMotor lift = new NXTRegulatedMotor(Constants.liftMotorPort);
		NXTRegulatedMotor[] motors = {clamp,lift};
		
		// set up brick pick up
		BlockPickUp bp = new BlockPickUp(motors);
		
		// read from DIS
		while(true){
			int n = NXTComm.read();
			if(n==9){
				LCD.clear();
				LCD.drawString("Succesful transfer", 0, 7);
				Button.waitForAnyPress();
				break;
			}
			
			
			switch (n){
			
			// Block pick up orders
			case 49:
				bp.closeClamp();
				break;
			case 50:
				bp.openClamp();
				break;
			case 51:
				bp.raiseTo(BlockPickUp.IDLE);
				break;
			case 52:
				bp.raiseBy(BlockPickUp.BLOCK_HEIGHT);
				break;
			case 53:
				bp.lowerBy(BlockPickUp.BLOCK_HEIGHT);
				break;
			case 54:
				bp.raiseTo(BlockPickUp.MIN_HEIGHT);
			case 55:
				bp.raiseTo(BlockPickUp.MAX_HEIGHT);
			case 56:
				bp.straightenBlock();
				break;
			
			// end program orders
			case 99:
				System.exit(0);
			}
		}
	}
}
