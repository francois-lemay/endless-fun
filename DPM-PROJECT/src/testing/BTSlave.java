/*package testing;

import deprecated.NXTCommBT;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.Bluetooth;
import official.BlockPickUp;
import official.Constants;

public class BTSlave {
	
public static void main(String[] args){
		
		// set slave brick's friendly name
		Bluetooth.setFriendlyName("Slave");
		
		// connect with master brick
		NXTCommBT.connect("It doesnt matter what name goes here", false);
		
		// set up clamp and lift motors
		NXTRegulatedMotor clamp = new NXTRegulatedMotor(Constants.clampMotorPort);
		NXTRegulatedMotor lift = new NXTRegulatedMotor(Constants.liftMotorPort);
		NXTRegulatedMotor[] motors = {clamp,lift};
		
		// set up brick pick up
		BlockPickUp bp = new BlockPickUp(motors);
		
		// read from DIS
		while(true){
			int n = NXTCommBT.read();
			if(n==9){
				LCD.clear();
				LCD.drawString("Succesful transfer", 0, 7);
				Button.waitForAnyPress();
				break;
			}
			
			
			switch (n){
			
			// Block pick up orders
			case Constants.CODE_CLOSE_CLAMP:
				bp.closeClamp();
				break;
			case Constants.CODE_OPEN_CLAMP:
				bp.openClamp();
				break;
			case Constants.CODE_RAISE_IDLE:
				bp.raiseTo(BlockPickUp.IDLE);
				break;
			case Constants.CODE_RAISE_BLOCK_HEIGHT:
				bp.raiseBy(BlockPickUp.BLOCK_HEIGHT);
				break;
			case Constants.CODE_LOWER_BLOCK_HEIGHT:
				bp.lowerBy(BlockPickUp.BLOCK_HEIGHT);
				break;
			case Constants.CODE_LOWER_MIN:
				bp.raiseTo(BlockPickUp.MIN_HEIGHT);
			case Constants.CODE_RAISE_MAX:
				bp.raiseTo(BlockPickUp.MAX_HEIGHT);
			case 56:
				bp.straightenBlock();
				break;
			
			// end program orders
			case Constants.CODE_EXIT:
				System.exit(0);
			}
		}
	}

}
*/