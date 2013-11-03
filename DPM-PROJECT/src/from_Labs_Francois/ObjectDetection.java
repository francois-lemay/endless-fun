/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 * 
 * DESCRIPTION
 * 
 * This class will check every PERIOD ms for
 * the presence of an object in proximity to
 * the robot.
 * 
 * If a new object is positively detected,
 * ObjectIdentification is used to identify
 * the type of object.
 * 
 * ObjectDetection will stop detecting objects
 * once a styrofoam block is found.
 * 
 */

package from_Labs_Francois;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Object detection
 * @author Francois
 *
 */
public class ObjectDetection implements TimerListener {
	
	private final String[] status = { "Object Detected" , "No Object Detected"};

	private USPoller usPoller;
	private Timer timer;
	private final int PERIOD = 500; // period of checking for object presence

	private final int DISTANCE = 40; // dist. that detects object
	
	private static boolean newObjectDetected;
	private boolean objectDetected;
	private boolean sameObject;
	
	//position of robot just before detecting a new object
	
	//NOT USED
/*	// defining the boundaries of the platform for isWall()
	private final int X_MIN = -30;
	private final int X_MAX = 90;
	private final int Y_MIN = -30;
	private final int Y_MAX = 210;
	
	private final int TWEAK = 5;
*/

	// constructor
	/**
	 * constructor
	 * @param usPoller - object acquiring an filtering us sensor data
	 */
	public ObjectDetection(USPoller usPoller) {

		this.usPoller = usPoller;

		newObjectDetected = false;
		objectDetected = false;
		sameObject = false;

		// set up timer
		timer = new Timer(PERIOD, this);
		startTimer();
	}

	/**
	 * main thread
	 */
	public void timedOut() {

		// ensure that timedOut() isn't called while in use
		stopTimer();
		
		// perform object detection only if:
		if (!ObjectIdentification.isIdentifying() && ObjectSearching.getIsReady() && !ObjectIdentification.isBlock()) {

			// get latest distance reading from usPoller
			double reading = usPoller.getLatestMedian();

			// check if within threshold
			if (reading <= DISTANCE) {
					objectDetected = true;
			} else {
				objectDetected = false;

				// if no object is detected, the next object to be detected is
				// potentially different
				sameObject = false;
			}

			// use objectIdentification to identify object
			// if an object has been positively detected and it is a different
			// object from the one previously detected
			if (objectDetected && !sameObject) {

				// declare that a new object has been detected
				newObjectDetected = true;

				// prevent multiple detections of same object
				sameObject = true;
				
			} else {
				newObjectDetected = false;
			}
		}
		
		// do not re-enable object detection if styrofoam block is found
		if (!ObjectIdentification.isBlock()) {
			startTimer();
		} else {
			// will prevent object identification
			newObjectDetected = false;
		}
		
	}

	/* NOT USED
	 * 
	 * determine whether the "detected object" is one
	 * of the platform walls
	 */
/*
	public boolean isWall() {

		double x = odo.getX();
		double y = odo.getY();
		double theta = odo.getAng();

		// robot is close to west wall
		if (x < X_MIN + DISTANCE && theta > 90 + TWEAK && theta < 270 - TWEAK) {
			return true;
		}
		// robot is in south-west corner
		if (x < X_MIN + DISTANCE && y < Y_MIN + DISTANCE && theta > 90 + TWEAK
				&& theta < 360 - TWEAK) {
			return true;
		}
		// robot is close to south wall
		if (y < Y_MIN + DISTANCE && theta > 180 + TWEAK && theta < 360 - TWEAK) {
			return true;
		}
		// robot is in south-east corner
		if (y < Y_MIN + DISTANCE && x > X_MAX - DISTANCE
				&& (theta < 90 - TWEAK || theta > 180 + TWEAK)) {
			return true;
		}
		// robot is close to east wall
		if (x > X_MAX - DISTANCE && (theta < 90 - TWEAK || theta > 270 + TWEAK)) {
			return true;
		}
		// robot is in north-east corner
		if (x > X_MAX - DISTANCE && y > Y_MAX - DISTANCE
				&& (theta < 180 - TWEAK || theta > 270 + TWEAK)) {
			return true;
		}
		// robot is close to north wall
		if (y > Y_MAX - DISTANCE && theta < 180 - TWEAK && theta > 0 + TWEAK) {
			return true;
		}
		// robot is in north-west corner
		if (x < X_MIN + DISTANCE && y > Y_MAX - DISTANCE && theta > 0 + TWEAK
				&& theta < 270 - TWEAK) {
			return true;
		}

		return false;
	}
*/	
	
	//----- accesors -----//
	
	/**
	 * 
	 * @returns whether a new object has been detected
	 */
	public static boolean isNewObjectDetected(){
		return newObjectDetected;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isObjectDetected() {
		return objectDetected;
	}
	
	public String getStatus(){
		if(objectDetected){
			return status[0];
		}
		else{
			return status[1];
		}
	}
	
	//------ mutators -------//

	// start timer
	private void startTimer() {
		timer.start();
	}

	// stop timer
	private void stopTimer() {
		timer.stop();
	}

}
