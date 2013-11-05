package official;

import lejos.nxt.NXTRegulatedMotor;

/**
 * basic controls to pick up blocks
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
	 * maximum block capacity
	 */
	public final int MAX_BLOCK = 3;

	/**
	 * number of blocks being carried by robot
	 */
	public int blocks;
	
	// constructor
	public BlockPickUp(NXTRegulatedMotor[] motors){
		lift = motors[0];
		clamp = motors[1];
	}
	
	/**
	 * position robot for picking up block
	 */
	private void positionRobot(){
		
	}
	
	/**
	 * open clamp
	 */
	private void openClamp(){
		
	}
	
	/**
	 * close clamp
	 */
	private void closeClamp(){
		
	}
	
	/**
	 * raise lift
	 */
	private void raise(){
		
	}
	
	/**
	 * lower lift
	 */
	private void lower(){
		
	}

}
