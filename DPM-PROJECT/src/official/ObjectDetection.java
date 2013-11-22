package official;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * detection of objects in robot's surroundings. The timer is stopped if a
 * styrofoam block has been detected. This allows the robot to deal with the
 * block without the possibility of being interrupted, until it re-starts it
 * decides to re-start the timer.
 * 
 * @author Francois
 * 
 */
public class ObjectDetection implements TimerListener {

	// class variables

	/**
	 * robot's navigation class
	 */
	private Navigation nav;

	/**
	 * array of light pollers
	 */
	private LightPoller[] lp;

	/**
	 * array of us pollers
	 */
	private USPoller[] up;
	/**
	 * block pick up class
	 */
	BlockPickUp bp;
	/**
	 * motors
	 */
	NXTRegulatedMotor leftMotor, rightMotor, sensorMotor;

	/**
	 * timer
	 */
	private Timer timer;

	/**
	 * timed out period
	 */
	private final int PERIOD = Constants.OBJ_DETECT_PERIOD;

	/**
	 * detection status booleans
	 */
	public static boolean newObjectDetected;

	/**
	 * 
	 */
	public boolean objectDetected, sameObject;

	/**
	 * identification status booleans
	 */
	public static boolean isBlock, isDetecting;

	/**
	 * determines whether the Master or the Slave brick is using this class.
	 */
	private boolean isMaster;

	/**
	 * booleans that confirm presence of object at specified sensors
	 */
	private boolean bottom, top, left, right;

	/**
	 * dist used for fine approach towards styro block
	 */
	private final int FINE_APPROACH = Constants.FINE_APPROACH;
	/**
	 * dist used for obstacle avoidance approach
	 */
	private final int OBSTACLE_APPROACH = Constants.OBSTACLE_APPROACH;
	/**
	 * dist used for obstacle dodging
	 */
	private final int OBSTACLE_PRESENT = Constants.OBSTACLE_PRESENT;

	/**
	 * 
	 * @param nav
	 * @param lp
	 * @param up
	 * @param isMaster
	 */

	// constructor
	public ObjectDetection(Navigation nav, LightPoller[] lp, USPoller[] up,
			boolean isMaster, BlockPickUp bp, NXTRegulatedMotor leftMotor,
			NXTRegulatedMotor rightMotor) {

		this.nav = nav;
		this.lp = lp;
		this.up = up;
		this.isMaster = isMaster;
		this.bp = bp;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// initialize sensor motor
		this.sensorMotor = new NXTRegulatedMotor(
				Constants.sensorMotorPort);
		sensorMotor.setSpeed(Navigation.SLOW);
		sensorMotor.resetTachoCount();

		timer = new Timer(PERIOD, this);

	}

	/**
	 * main thread
	 */
	public void timedOut() {

		// stop timer to ensure completion of thread
		stop();

		// set isDetecting
		isDetecting = true;

		// check if an object is detected
		checkIfObjectDetected();

		// if yes, identify it
		if (objectDetected) {
			
			// identify object			
			int bottomReading = up[Constants.bottomUSPollerIndex]
					.getLatestFilteredDataPoint();
			int topReading = up[Constants.topUSPollerIndex]
					.getLatestFilteredDataPoint();
			
			// check difference in readings
			if (Math.abs(bottomReading - topReading) < 15) {
				isBlock = false;
			} else {
				isBlock = true;
			}

			// if block, do BlockPickUp
			if (isBlock) {

				// let user know that block has been found
				Sound.beepSequenceUp();

				// wait for control of navigation to be available
				while (Navigation.getIsNavigating()) {}

				// approach block if necessary
				if (up[Constants.bottomUSPollerIndex]
						.getLatestFilteredDataPoint() > FINE_APPROACH) {
					
					// slowly move forward
					nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);
					
					// wait for robot to be at a good distance from the
					// block
					while (up[Constants.bottomUSPollerIndex]
							.getLatestFilteredDataPoint() > FINE_APPROACH) {}
				}

				// stop moving
				nav.stopMotors();
				// grab block
				bp.closeClamp();
				// increment number of blocks
				Master.blocks++;

			} else {
				// let user know that is obstacle
				Sound.beepSequence();

				// otherwise, do ObstacleAvoidance
				avoidObstacle();
			}

		}

		// reset isDetecting
		isDetecting = false;

		// re-start timer only if no block found
		if (Master.blocks == 0 && !isBlock) {

			// reset all booleans
			resetBooleans();
			// re-start timer
			start();
		} else {
			// reset all booleans
			resetBooleans();
		}

	}

	/**
	 * determines if an object is detected. Will change corresponding class
	 * boolean accordingly. (e.g. if the bottom us sensor detects an object,
	 * then 'bottom' = true)
	 * 
	 * @return true or false
	 */
	private void checkIfObjectDetected() {

		// check data from every sensor to see if an object is detected

		// if Master
		if (isMaster) {
			try {
				if (up != null) {

					int dist = -1;

					// check us sensors
					for (int i = 0; i < up.length; i++) {

						// get latest filtered reading
						dist = up[i].getLatestFilteredDataPoint();

						// if object within range
						if (dist < Constants.US_OBJECT_THRESH) {

							// set corresponding boolean
							switch (i) {
							case Constants.bottomUSPollerIndex:
								bottom = true;
								break;
							case Constants.topUSPollerIndex:
								top = true;
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				LCD.clear();
				LCD.drawString("Object Detection", 0, 1);
				LCD.drawString("up null", 0, 1);
			}

		}
		// if Slave
		else {

			try {
				// check light sensors if there are any
				if (lp != null) {

					int light = 0;

					for (int i = 0; i < lp.length; i++) {

						// get latest filtered reading
						light = lp[i].getLatestFilteredDataPoint();

						// if object within range
						if (light > Constants.LIGHT_OBJECT_THRESH) {
							switch (i) {
							case Constants.leftLightPollerIndex:
								left = true;
								break;
							case Constants.rightLightPollerIndex:
								right = true;
								break;
							}

						}
					}
				}
			} catch (Exception e) {
				LCD.clear();
				LCD.drawString("Object Detection", 0, 1);
				LCD.drawString("lp null", 0, 1);
			}
		}

		// set value of objectDetected
		if (bottom || top || left || right) {
			objectDetected = true;
			newObjectDetected = true;
		} else {
			objectDetected = false;
			newObjectDetected = false;
		}
	}

	/**
	 * make robot avoid obstacle while staying on track to its destination
	 */
	private void avoidObstacle() {

		// initialize variable
		int dist = up[Constants.topUSPollerIndex].getLatestFilteredDataPoint();
		
		// approach obstacle if necessary
		if (dist > OBSTACLE_APPROACH) {
			
			// slowly approach obstacle
			nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);
			
			do {
				dist = up[Constants.topUSPollerIndex]
						.getLatestFilteredDataPoint();
			} while (dist > OBSTACLE_APPROACH);
		} else {
			
			// stop approaching obstacle
			nav.stopMotors();
		}

		// turn 90 degrees clockwise
		nav.rotateBy(-90, false);
		
		// turn us sensor towards obstacle
		sensorMotor.rotateTo(-90, false);
		try{
			Thread.sleep(500);
		}catch(Exception e){}
		
		// move forward until obstacle is not seen
		nav.setSpeeds(Navigation.FAST, Navigation.FAST);
		
		do{
			// check for obstacle (e.g. a robot passing in front) at the front at the same time
			if(up[Constants.bottomUSPollerIndex].getLatestFilteredDataPoint() < 20){
				
				// stop motors
				nav.stopMotors();
				
				// wait until robot passes by
				while(up[Constants.bottomUSPollerIndex].getLatestFilteredDataPoint() < 20){}
				
				// reset the motor speeds
				nav.setSpeeds(Navigation.FAST, Navigation.FAST);
			}
			
			// get reading from top us sensor
			dist = up[Constants.topUSPollerIndex]
					.getLatestFilteredDataPoint();
			
		}while(dist < Constants.OBSTACLE_PRESENT);
		
		// turn sensor back
		sensorMotor.rotateTo(0, false);
		
		// continue moving forward to contour the obstacle
		nav.moveForwardBy(20, Navigation.FAST);
		
		
		// turn 90 degrees counter-clockwise
		nav.rotateBy(90, false);
	}
	
	/**
	 * reset all booleans that are used to flag the presence of an object
	 */
	private void resetBooleans() {
		left = false;
		right = false;
		bottom = false;
		top = false;
		objectDetected = false;
		newObjectDetected = false;
		isBlock = false;
	}

	/**
	 * start timer
	 */
	public void start() {
		timer.start();
	}

	/**
	 * stop timer
	 */
	public void stop() {
		timer.stop();
	}

}
