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
	 * obstacle avoidance class
	 */
	private ObstacleAvoidance avoider;
	/**
	 * block pick up class
	 */
	private BlockPickUp bp;

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
	 * booleans that confirm presence of object at specified sensors
	 */
	private boolean bottom, top, left, right;

	/**
	 * dist used for fine approach towards styro block
	 */
	private final int FINE_APPROACH = Constants.FINE_APPROACH;


	/**
	 * 
	 * @param nav
	 * @param lp
	 * @param up
	 * @param isMaster
	 */

	// constructor
	public ObjectDetection(Odometer odo, Navigation nav, USPoller[] up, ObstacleAvoidance avoider) {

		this.nav = nav;
		this.lp = lp;
		this.up = up;
		this.avoider = avoider;

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
				while (Navigation.getIsNavigating()) {
				}

				// approach block if necessary
				if (up[Constants.bottomUSPollerIndex]
						.getLatestFilteredDataPoint() > FINE_APPROACH) {

					// slowly move forward
					nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);

					// wait for robot to be at a good distance from the
					// block
					while (up[Constants.bottomUSPollerIndex]
							.getLatestFilteredDataPoint() > FINE_APPROACH) {
					}
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
				avoider.avoidObstacle();
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
