package official;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;
import master.Master;

/**
 * detection of objects in robot's surroundings
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
	 * 
	 */
	NXTRegulatedMotor leftMotor,rightMotor;
	
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
	public static boolean isBlock, isDetecting, isObstacle;

	/**
	 * determines whether the Master or the Slave brick is using this class.
	 */
	private boolean isMaster;

	/**
	 * booleans that confirm presence of object at specified sensors
	 */
	private boolean bottom, top, left, right;

	/**
	 * strings used in LCDInfo
	 */
	// private final String[] objectType = { "Block", "Not Block" };

	/**
	 * dist used for fine approach towards styro block
	 */
	private final int FINE_APPROACH = Constants.FINE_APPROACH;
	/**
	 * dist used for obstacle avoidance approach
	 */
	private final int OBSTACLE_APPROACH = 30;

	/**
	 * 
	 * @param nav
	 * @param lp
	 * @param up
	 * @param isMaster
	 */

	// constructor
	public ObjectDetection(Navigation nav, LightPoller[] lp, USPoller[] up,
			boolean isMaster, BlockPickUp bp, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {

		this.nav = nav;
		this.lp = lp;
		this.up = up;
		this.isMaster = isMaster;
		this.bp = bp;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

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
			// go identify object
			// identifyObject();

			if (Math.abs(up[Constants.bottomUSPollerIndex]
					.getLatestFilteredDataPoint()
					- up[Constants.topUSPollerIndex]
							.getLatestFilteredDataPoint()) < 15) {
				isBlock = false;
			} else {
				isBlock = true;
			}

			// if block, do BlockPickUp
			if (isBlock) {

				// let user know that block has been found
				Sound.beepSequenceUp();

				// approach block
				while (up[Constants.bottomUSPollerIndex]
						.getLatestFilteredDataPoint() > FINE_APPROACH) {
					nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);
				}
				// stop moving
				nav.setSpeeds(0, 0);

				bp.closeClamp();
				
				isObstacle = false;

				Master.blocks++;

			} else {
				// let user know that is obstacle
				Sound.beepSequence();
				
				// set status
				//isObstacle = true;
				
				// otherwise, do ObstacleAvoidance
				//avoidObstacle();
			}

		}

		// reset isDetecting
		isDetecting = false;

		// re-start timer only if no block found
		if (Master.blocks == 0 && !isObstacle) {
			
			// reset all booleans
			resetBooleans();
			// re-start timer
			start();
		}else{
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
	 * identify the detected object. Method of identification will vary
	 * according to the sensor that detected the object and the position of the
	 * object relative to the robot
	 */
	private void identifyObject() {

		// if Master
		if (isMaster) {

			if (bottom && top) {
				isBlock = false;
			} else if (bottom && !top) {
				isBlock = true;
			}
		}
		// if Slave
		else {
			/*
			 * do object identification with the appropriate light sensor as in
			 * lab 5
			 */
		}
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
		isObstacle = false;
	}

	/**
	 * make robot avoid obstacle while staying on track to its destination
	 */
	private void avoidObstacle() {

		// get distance from top us sensor
		int dist = 0;

		// approach obstacle
		do {
			nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);
			dist = up[Constants.topUSPollerIndex].getLatestFilteredDataPoint();
		} while (dist > OBSTACLE_APPROACH);

	}

	// start timer
	public void start() {
		timer.start();
	}

	// stop timer
	public void stop() {
		timer.stop();
	}
	
}
