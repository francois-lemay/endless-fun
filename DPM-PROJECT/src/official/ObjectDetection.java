package official;

/**
 * detection of objects in robot's surroundings
 * 
 * @author Francois
 * 
 */
public class ObjectDetection extends Thread {

	// class variables

	/**
	 * array of all light pollers
	 */
	private LightPoller[] lp;

	/**
	 * array of us pollers
	 */
	private USPoller[] up;

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
	 * strings used in LCDInfo
	 */
	private final String[] objectType = { "Block", "Not Block" };

	// constructor
	public ObjectDetection(LightPoller[] lp, USPoller[] up, boolean isMaster) {

		this.lp = lp;
		this.up = up;
		this.isMaster = isMaster;

	}

	/**
	 * main thread
	 */
	public void run() {

		// set isDetecting
		isDetecting = true;

		// check if an object is detected
		checkIfObjectDetected();

		// if yes, identify it
		if (objectDetected) {
			// go identify object

			// if block, do BlockPickUp

			// otherwise, do ObstacleAvoidance

		}

		// reset detection booleans
		resetBooleans();

		// reset isDetecting
		isDetecting = false;
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

		}
		// if Slave
		else {

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
		}
	}

	/**
	 * set object detection status booleans according to which sensors have
	 * detected an object
	 * 
	 * @return
	 */
	private void whichSensor() {

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
		isBlock = false;
	}

	/**
	 * make robot avoid obstacle while staying on track to its destination
	 */
	private void avoidObstacle() {

	}

}
