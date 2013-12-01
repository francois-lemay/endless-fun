package official;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/**
 * robot's navigation class. Movement control class (turnTo, travelTo, flt, goFwdCaution, setSpeeds...)
 * 
 * @author Francois
 * 
 */
public class Navigation {

	/**
	 * fast speed
	 */
	public final static int FAST = 200;
	/**
	 * slow speed
	 */
	public final static int SLOW = 100;
	/**
	 * super slow speed
	 */
	public final static int SUPER_SLOW = 50;
	/**
	 * motor acceleration
	 */
	public final static int ACCELERATION = 2000;
	/**
	 * permitted error in heading (when reaching a destination)
	 */
	private final double DEG_ERR = 0.5;
	/**
	 * permitted error in position (when reaching a destination)
	 */
	private final double POSITION_ERR = 1.0;
	/**
	 * robot's odometry class
	 */
	private Odometer odometer;
	/**
	 * robot's left and right motor
	 */
	private NXTRegulatedMotor leftMotor, rightMotor;
	/**
	 * indicates if travelTo is being used
	 */
	private static boolean isNavigating;
	/**
	 * boolean indicating whether set destination has been reached or not
	 */
	public static boolean destinationReached;

	/**
	 * 
	 * @param odo
	 *            - robot's odometry class
	 */
	public Navigation(Odometer odo) {
		this.odometer = odo;

		NXTRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/**
	 * function to set the motor speed jointly
	 * 
	 * @param lSpd
	 *            - left motor speed
	 * @param rSpd
	 *            - right motor speed
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * set the motor speeds jointly
	 * 
	 * @param lSpd
	 *            - left motor speed
	 * @param rSpd
	 *            - right motor speed
	 */
	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop(true);
		this.rightMotor.stop(false);
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/**
	 * stop the two motors jointly
	 */
	public void stopMotors() {
		this.leftMotor.stop(true);
		this.rightMotor.stop(false);
	}

	/**
	 * make robot travel to specified position. Can get interrupted by
	 * ObjectDetection.isNewObjectDetected
	 * 
	 * @param x1
	 *            - x coordinate
	 * @param y1
	 *            - y coordinate
	 */
	public void travelTo(double x1, double y1, int speed) {

		// instantiate variables
		double x0, y0; // current position
		double theta1; // new heading
		double forwardError; // distance from destination coordinates
		double[] displacementVector = new double[2]; // vector connecting actual
														// position to
														// destination
														// coordinates
		// set isNavigating to true
		setIsNavigating(true);

		// set destinationReached = false
		setDestinationReached(false);

		// stop motors
		setSpeeds(0, 0);

		while (!destinationReached && !ObjectDetection.objectDetected) {

			// set isNavigating to true
			setIsNavigating(true);

			// get robot's current position
			x0 = odometer.getX();
			y0 = odometer.getY();

			// calculate new heading (ie. towards destination point)
			theta1 = Math.atan((y1 - y0) / (x1 - x0)) * 180 / Math.PI;

			// adjust value of theta1 according to deltaX and deltaY
			if ((x1 - x0) < 0 && (y1 - y0) > 0) { // if heading should be in 2nd
													// quadrant
				theta1 = theta1 + 180;
			}
			if ((x1 - x0) < 0 && (y1 - y0) < 0) { // if heading should be in 3rd
													// quadrant
				theta1 = theta1 + 180;
			}
			if ((x1 - x0) > 0 && (y1 - y0) < 0) { // if heading should be in 4th
													// quadrant
				theta1 = theta1 + 360;
			}

			turnTo(theta1, speed);

			// // Forward Error Calculation

			// get update robot's current position and heading
			x0 = odometer.getX();
			y0 = odometer.getY();

			// calculation of displacement vector
			displacementVector[0] = x1 - x0;
			displacementVector[1] = y1 - y0;

			// find magnitude of displacement vector to obtain forward error
			forwardError = Math
					.sqrt((displacementVector[0] * displacementVector[0])
							+ (displacementVector[1] * displacementVector[1]));

			// decide whether destination has been reached or not. If yes, stop
			// motors.
			if (Math.abs(forwardError) <= POSITION_ERR) {
				setDestinationReached(true);
				leftMotor.stop(true);
				rightMotor.stop(true);
			} else {

				setDestinationReached(false);

				// make robot move forward
				leftMotor.forward();
				rightMotor.forward();
				leftMotor.setSpeed(speed);
				rightMotor.setSpeed(speed);

			}

			// set isNavigating to false
			setIsNavigating(false);

		}

	}

	/**
	 * turn robot to specified heading
	 * 
	 * @param theta1
	 *            - heading
	 */
	public void turnTo(double theta1, int speed) {

		double theta0; // current heading
		double deltaTheta; // change in heading

		// // Angular Error Calculation

		// get actual heading
		theta0 = odometer.getAng();

		// calculate required change in heading
		deltaTheta = theta1 - theta0;

		// determine minimal change in heading
		if (Math.abs(deltaTheta) <= 180) {
			// do nothing
		}
		if (deltaTheta < -180) {
			deltaTheta = deltaTheta + 360;
		}
		if (deltaTheta > 180) {
			deltaTheta = deltaTheta - 360;
		}

		// rotate if deltaTheta is >= permitted heading error (i.e. robot
		// has not reached desired heading)
		if (Math.abs(deltaTheta) >= DEG_ERR) {

			// set motor speeds
			leftMotor.setSpeed(speed);
			rightMotor.setSpeed(speed);

			// begin rotation and wait for completion
			leftMotor.rotate(
					-convertAngle(odometer.leftRadius, odometer.width,
							deltaTheta), true);
			rightMotor.rotate(
					convertAngle(odometer.rightRadius, odometer.width,
							deltaTheta), false);

		}
	}

	/**
	 * turn robot towards a specified position
	 * 
	 * @param x1
	 *            - x coordinate
	 * @param y1
	 *            - y coordinate
	 */
	public void turnTowards(double x1, double y1) {

		double x0, y0, theta1;

		// get robot's current position
		x0 = odometer.getX();
		y0 = odometer.getY();

		// calculate new heading (ie. towards destination point)
		theta1 = Math.atan((y1 - y0) / (x1 - x0)) * 180 / Math.PI;

		// adjust value of theta1 according to deltaX and deltaY
		if ((x1 - x0) < 0 && (y1 - y0) > 0) { // if heading should be in 2nd
												// quadrant
			theta1 = theta1 + 180;
		}
		if ((x1 - x0) < 0 && (y1 - y0) < 0) { // if heading should be in 3rd
												// quadrant
			theta1 = theta1 + 180;
		}
		if ((x1 - x0) > 0 && (y1 - y0) < 0) { // if heading should be in 4th
												// quadrant
			theta1 = theta1 + 360;
		}

		turnTo(theta1, SLOW);
	}

	/**
	 * go forward by a certain distance while checking front us sensors for
	 * incoming obstacles. If an obstacle is incoming, stop moving and return
	 * "obstacle". If destination is reached return "destination". If too close
	 * to enemy's zone, return "enemy". Otherwise return "success".
	 * 
	 * @param distance
	 *            - traveling distance
	 * @param speed
	 *            - speed of left and right motors
	 * @param left
	 *            - robot's left hand side us sensor
	 * @param right
	 *            - robot's right hand side us sensor
	 */
	public String goFwdCaution(double distance, int speed,
			USPoller left, USPoller right) {

		// initialize vars
		double disp = 0;
		double fwdError = 0;

		// record starting position
		double x = odometer.getX();
		double y = odometer.getY();

		// set motor speeds
		setSpeeds(speed, speed);
		
		try{
			Thread.sleep(500);
		}catch(Exception e){}
		
		do {
			// stop traveling if obstacle is at front
			if (left.getLatestFilteredDataPoint() < 22
					|| right.getLatestFilteredDataPoint() < 22) {
				stopMotors();
				Sound.beep();
				return "obstacle";
			}

			// calculated how close to destination
			fwdError = Math.sqrt((Math.pow(
					(odometer.getX() - Constants.robotDest[0]), 2) + Math.pow(
					(odometer.getY() - Constants.robotDest[1]), 2)));

			// if arrived at position
			if (fwdError <= 1) {
				stopMotors();
				return "destination";
			}

			// check if too close to enemy zone
			if (tooClose2Enemy()) {
				stopMotors();
				return "enemy";
			}

			// calculate distance traveled
			disp = Math.sqrt((Math.pow((odometer.getX() - x), 2) + Math.pow(
					(odometer.getY() - y), 2)));

		} while (disp < distance);

		// distance traveled without interruption
		return "success";
	}

	/**
	 * check if robot is too close to enemy zone. return true if too close.
	 * return false otherwise.
	 * 
	 * @return true or false
	 */
	private boolean tooClose2Enemy() {
		double x0 = odometer.getX();
		double y0 = odometer.getY();
		double dist = 0;

		// bottom left corner
		double x1 = Constants.badZone[0];
		double y1 = Constants.badZone[1];

		// top right corner
		double x2 = Constants.badZone[2];
		double y2 = Constants.badZone[3];

		// bottom right corner
		double x3 = x2;
		double y3 = y1;

		// top left corner
		double x4 = x1;
		double y4 = y2;

		// calculate min. distance from enemy zone
		if (x1 < x0 && x0 < x2) {

			dist = Math.min(Math.abs(y1 - y0), Math.abs(y2 - y0));
			
		} else if (y1 < y0 && y0 < y2) {

			dist = Math.min(Math.abs(x1 - x0), Math.abs(x2 - x0));
		} else {
			// min distance is to one of the four corners

			// calc. distance to bottom left
			double dist1 = Math.sqrt((Math.pow((x0 - x1), 2) + Math.pow(
					(y0 - y1), 2)));
			// calc distance to top right
			double dist2 = Math.sqrt((Math.pow((x0 - x2), 2) + Math.pow(
					(y0 - y2), 2)));
			// calc distance to bottom right
			double dist3 = Math.sqrt((Math.pow((x0 - x3), 2) + Math.pow(
					(y0 - y3), 2)));
			// calc distance to top left
			double dist4 = Math.sqrt((Math.pow((x0 - x4), 2) + Math.pow(
					(y0 - y4), 2)));

			dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
		}

		if (dist < 5) {
			return true;
		} else {
			return false;
		}
	}

	// ---- helper methods ---- //

	/**
	 * make robot rotate by specified angle. A positive angle results in
	 * counter-clockwise rotation
	 * 
	 * @param deltaTheta
	 * @param nonBlocking
	 */
	public void rotateBy(double deltaTheta, boolean immediateReturn, int speed) {

		// set motor speeds
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		// begin rotation and wait for completion
		leftMotor.rotate(
				-convertAngle(odometer.leftRadius, odometer.width, deltaTheta),
				true);
		rightMotor.rotate(
				convertAngle(odometer.rightRadius, odometer.width, deltaTheta),
				immediateReturn);

	}

	//
	/**
	 * move forward by specified distance (-ve distance corresponds to moving
	 * backwards)
	 * 
	 * @param distance
	 */
	public void moveForwardBy(double distance, int speed) {

		// set motor speeds
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		// begin rotation and wait for completion
		leftMotor.rotate(convertDistance(odometer.leftRadius, distance), true);
		rightMotor.rotate(convertDistance(odometer.rightRadius, distance),
				false);
	}

	// helper methods

	/**
	 * convert desired traveling distance to angular wheel rotation
	 * 
	 * @param radius
	 *            - wheel radius
	 * @param distance
	 *            - distance of travel
	 * @return angle (in degrees)
	 */
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * convert desired change of orientation to angular wheel rotation
	 * 
	 * @param radius
	 *            - wheel radius
	 * @param width
	 *            - wheelbase width
	 * @param angle
	 *            - change in orientation
	 * @return angle (in degrees)
	 */
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	// ---- accessors ---- //

	/**
	 * returns true if robot is navigating. returns false otherwise
	 * 
	 * @return navigation status
	 */
	public static boolean getIsNavigating() {
		return isNavigating;
	}

	// ---- mutators ---- //

	/**
	 * set value of boolean isNavigating
	 * 
	 * @param foo
	 *            - status
	 */
	private void setIsNavigating(boolean foo) {
		isNavigating = foo;
	}

	/**
	 * set value of destinationReached
	 * 
	 * @param foo
	 *            - status
	 */
	private void setDestinationReached(boolean foo) {
		destinationReached = foo;
	}

}
