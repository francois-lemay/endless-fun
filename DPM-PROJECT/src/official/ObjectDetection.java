package official;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * detection of objects in a scope of approx. 30 degrees towards the front of
 * the robot. Currently, the timer is stopped if a styrofoam block has been detected. This
 * allows the robot to deal with the block without the possibility of being
 * interrupted.
 * 
 * @author Francois Lemay
 * 
 */
public class ObjectDetection implements TimerListener {

	/**
	 * robot's navigation class
	 */
	private Navigation nav;
	/**
	 * array of us pollers
	 */
	private USPoller[] up;
	/**
	 * obstacle avoidance class
	 */
	private ObstacleAvoidance avoider;
	/**
	 * timer
	 */
	private Timer timer;
	/**
	 * timed out period
	 */
	private final int PERIOD = Constants.OBJ_DETECT_PERIOD;
	/**
	 * object detection boolean
	 */
	public static boolean objectDetected;

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
	 * constructor
	 * @param odo
	 *            - robot's odometry
	 * @param nav
	 *            - robot's navigation class
	 * @param up
	 *            - bottom and top us sensors
	 * @param avoider
	 *            - obstacle avoidance class
	 */
	public ObjectDetection(Odometer odo, Navigation nav, USPoller[] up,
			ObstacleAvoidance avoider) {

		this.nav = nav;
		this.up = up;
		this.avoider = avoider;

		// set up timer
		timer = new Timer(PERIOD, this);
	}

	/**
	 * main thread controlled by timer
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

			// get reading from bottom us sensor
			int bottomReading = up[Constants.bottomUSPollerIndex]
					.getLatestFilteredDataPoint();

			// get reading from top us sensor
			int topReading = up[Constants.topUSPollerIndex]
					.getLatestFilteredDataPoint();

			// check difference in readings
			if (Math.abs(bottomReading - topReading) < 15) {
				isBlock = false;
			} else {
				isBlock = true;
			}

			// if is a block, go pick up block
			if (isBlock) {

				// let user know that block has been found
				Sound.beepSequenceUp();

				// wait for control of navigation to be available
				while (Navigation.getIsNavigating()) {
				}

				// approach block if too far
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

				// tell Slave to pick up block
				NXTComm.write(Constants.CODE_CLOSE_CLAMP);

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
		} else {
			objectDetected = false;
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
