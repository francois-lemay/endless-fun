package official;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

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
	 * navigator
	 */
	private Navigation navigator;

	/**
	 * lift motor
	 */
	private NXTRegulatedMotor lift;

	/**
	 * clamp motor
	 */
	private NXTRegulatedMotor clamp;
	
	/**
	 * lift motor speed
	 */
	private final int LIFT_SPEED = 200;
	
	/**
	 * clamp motor speed
	 */
	private final int CLAMP_SPEED = 100;
	
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
	public static final int MAX_HEIGHT = 2600;
	
	/**
	 * min allowed height for lift
	 */
	public static final int MIN_HEIGHT = 0;
	
	/**
	 * height at which to keep lift while this class is not in use. (the given
	 * value is in degrees of motor rotation that translate in vertical
	 * displacement of the lift)
	 */
	public static final int IDLE = 2000;

	/**
	 * incremental height that corresponds to the height of one styrofoam block.
	 * (the given value is in degrees of motor rotation that translate in
	 * vertical displacement of the lift)
	 */
	public static final int BLOCK_HEIGHT = 720;

	/**
	 * limit angle of clamp motor considered as the open position
	 */
	public static final int OPEN = 0;

	/**
	 * limit angle of clamp motor considered as the closed position
	 */
	public static final int CLOSED = 100;

	/**
	 * constructor
	 * @param motors - motors being used for the lift and the clamp
	 */
	public BlockPickUp(NXTRegulatedMotor[] motors) {
		lift = motors[0];
		clamp = motors[1];

		lift.setSpeed(LIFT_SPEED);
		clamp.setSpeed(CLAMP_SPEED);
		
		// reset lift and clamp tacho counts
		lift.resetTachoCount();
		clamp.resetTachoCount();
		
		// reset blocks
		blocks = 0;
	}

	/**
	 * position robot for picking up block
	 */
	public void positionRobot() {

	}

	// lift methods

	/**
	 * position lift at specified height above its lowest point. (blocking)
	 */
	public void raiseTo(int height) {
		
		if(height<=MAX_HEIGHT && height>=MIN_HEIGHT){
			
			LCD.clear();
			LCD.drawString("changing height", 0, 5);
			
			int deltaH = height - (-1*lift.getTachoCount());
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
	public void raiseBy(int deltaH) {

		int height = -1*lift.getTachoCount() + deltaH;
		raiseTo(height);
	}

	/**
	 * lower lift. A positive deltaH results in negative vertical displacement
	 * 
	 * @param deltaH
	 *            - negative vertical displacement (degrees)
	 */
	public void lowerBy(int deltaH) {
		int height = -1*lift.getTachoCount() - deltaH;
		raiseTo(height);
	}
	
	
	// clamp methods
	
	/**
	 * open clamp. (blocking)
	 */
	public void openClamp() {
		clamp.rotateTo(OPEN, false);
	}

	/**
	 * close clamp. (blocking)
	 */
	public void closeClamp() {
		clamp.rotateTo(-CLOSED, false);
	}

}
