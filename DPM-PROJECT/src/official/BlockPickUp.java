package official;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteNXT;

/**
 * basic controls to pick up blocks. The lift is positioned at absolute heights
 * and relies on the absolute values of the lift motor's tachometer. The lift
 * must be at its lowest point before instantiating. The clamp must be in its
 * OPEN position before instantiation of the class.
 * 
 * @author Francois
 * 
 */
public class BlockPickUp {

	// class variables

	/**
	 * remote nxt object
	 */
	private static RemoteNXT nxt;
	
	/**
	 * lift motor
	 */
	private static RemoteMotor lift;

	/**
	 * clamp motor
	 */
	private static RemoteMotor clamp;
	
	/**
	 * lift motor speed
	 */
	private static final int LIFT_SPEED = 200;
	
	/**
	 * clamp motor speed
	 */
	private static final int CLAMP_SPEED = 100;
	
	/**
	 * motor acceleration
	 */
	private final static int ACCELERATION = 2000;
	
	/**
	 * maximum block capacity
	 */
	public static final int MAX_BLOCK = 3;

	/**
	 * number of blocks being carried by robot
	 */
	public static int blocks;
	
	/**
	 * max allowed height for lift
	 */
	public static final int MAX_HEIGHT = 1200;
	
	/**
	 * min allowed height for lift
	 */
	public static final int MIN_HEIGHT = 0;
	
	/**
	 * height at which to keep lift while this class is not in use. (the given
	 * value is in degrees of motor rotation that translate in vertical
	 * displacement of the lift)
	 */
	public static final int IDLE = 1100;

	/**
	 * incremental height that corresponds to the height of one styrofoam block.
	 * (the given value is in degrees of motor rotation that translate in
	 * vertical displacement of the lift)
	 */
	public static final int BLOCK_HEIGHT = 300;

	/**
	 * limit angle of clamp motor considered as the open position
	 */
	public static final int OPEN = 0;

	/**
	 * limit angle of clamp motor considered as the closed position
	 */
	public static final int CLOSED = 70;

	/**
	 * constructor
	 * @param motors - motors being used for the lift and the clamp
	 */
	public static void init() throws IOException{
		
		nxt = new RemoteNXT("Slave", Bluetooth.getConnector());
		clamp = nxt.A;
		lift = nxt.B;
		
		// set speeds
		lift.setSpeed(LIFT_SPEED);
		clamp.setSpeed(CLAMP_SPEED);
		
		//set accelerations
		lift.setAcceleration(ACCELERATION);
		clamp.setAcceleration(ACCELERATION);
		
		// reset lift and clamp tacho counts
		lift.resetTachoCount();
		clamp.resetTachoCount();
		
		// reset blocks
		blocks = 0;
	}


	// lift methods

	/**
	 * position lift at specified height above its lowest point. (blocking)
	 */
	public static void raiseTo(int height) {
		
		if(height<=MAX_HEIGHT && height>=MIN_HEIGHT){
			
			LCD.clear();
			LCD.drawString("changing height", 0, 5);
			
			int deltaH = height - (-lift.getTachoCount());
			// raise or lower depending on relative positioning
			lift.rotate(-deltaH, false);
		}
		else{
			Sound.beep();
			LCD.clear();
			LCD.drawString("prohibited height", 0, 5);
			Button.waitForAnyPress();
		}
	}

	/**
	 * raise lift. A positive deltaH results in positive vertical displacement
	 * 
	 * @param deltaH
	 *            - positive vertical displacement (degrees)
	 */
	public static void raiseBy(int deltaH) {

		int height = lift.getTachoCount() + deltaH;
		raiseTo(height);
	}

	/**
	 * lower lift. A positive deltaH results in negative vertical displacement
	 * 
	 * @param deltaH
	 *            - negative vertical displacement (degrees)
	 */
	public static void lowerBy(int deltaH) {
		int height = lift.getTachoCount() - deltaH;
		raiseTo(height);
	}
	
	
	// clamp methods
	
	/**
	 * open clamp. (blocking)
	 */
	public static void openClamp() {
		clamp.rotateTo(OPEN, false);
	}

	/**
	 * close clamp. (blocking)
	 */
	public static void closeClamp() {
		clamp.rotateTo(-CLOSED, false);
	}
	
	/**
	 * straighten block with clamp
	 */
	public static void straightenBlock(){
		// if clamp is closed, open before first
		if(!isClampOpen()){
			openClamp();
		}
		
		// repeatedly close and open clamp
		for(int i=0;i<3;i++){
			closeClamp();
			openClamp();
		}
	}
	
	/**
	 * get is clamp open boolean
	 * @return true or false
	 */
	public static boolean isClampOpen(){
		boolean isOpen = false;
		
		if(Math.abs(clamp.getTachoCount()-OPEN) <= 10 ){
			isOpen = true;
		}
		else{
			isOpen = false;
		}
		return isOpen;
	}

}
