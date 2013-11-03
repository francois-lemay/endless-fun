/* François Lemay  260 465 492
 * 
 * NOTE: most of this code is from Sean Lawlor's file, however
 * 		I inserted my own copy of travelTo() and turnTo(), as well
 * 		as additional helper methods
 *  
 *  TravelTo() gets interrupted when ObjectDetection declares that
 *  a new object has been detected
 * 
 * -----------------------------------------
 * 
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
package official;

import lejos.nxt.NXTRegulatedMotor;

/**
 * robot's navigation class.
 * @author Francois
 *
 */
public class Navigation {
	
	/**
	 * fast speed
	 */
	final static int FAST = 150;
	/**
	 * slow speed
	 */
	final static int SLOW = 100;
	/**
	 * super slow speed
	 */
	final static int SUPER_SLOW = 50;
	/**
	 * motor acceleration
	 */
	final static int ACCELERATION = 2000;
	/**
	 * permitted error in heading
	 */
	final static double DEG_ERR = 0.5;
	/**
	 * permitted error in position
	 */
	final static double POSITION_ERR = 1.0;
	
	private Odometer odometer;
	private NXTRegulatedMotor leftMotor, rightMotor,sensorMotor;

	private static boolean isNavigating;
	private boolean destinationReached;

	/**
	 * 
	 * @param odo
	 * @param sensorMotor
	 */
	public Navigation(Odometer odo, NXTRegulatedMotor sensorMotor) {
		this.odometer = odo;

		NXTRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.sensorMotor = sensorMotor;
		
		// set us sensor motor's setting
		sensorMotor.resetTachoCount();
		sensorMotor.setSpeed(SLOW);

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	/**
	 * 
	 * @param lSpd
	 * @param rSpd
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
	 * 
	 * @param lSpd
	 * @param rSpd
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

	/*
	 * Float the two motors jointly
	 */
	/**
	 * 
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * My own travelTo method from Lab3
	 */
	/**
	 * make robot travel to specified position.
	 * @param x1 - x coordinate
	 * @param y1 - y coordinate
	 */
	public void travelTo(double x1, double y1) {

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

		while (!destinationReached /*&& !ObjectDetection.isNewObjectDetected()*/ ) {
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

			turnTo(theta1);
			
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
				leftMotor.stop();
				rightMotor.stop();
			} else {

				setDestinationReached(false);

				// make robot move forward
				leftMotor.forward();
				rightMotor.forward();
				leftMotor.setSpeed(FAST);
				rightMotor.setSpeed(FAST);

			}

		}
		// set isNavigating to false
		setIsNavigating(false);

	}


	/**
	 * turn robot to specified heading
	 * @param theta1 - heading
	 */
	public void turnTo(double theta1) {

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
			leftMotor.setSpeed(SLOW);
			rightMotor.setSpeed(SLOW);

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
	 * @param x1 - x coordinate
	 * @param y1 - y coordinate
	 */
	public void turnTowards(double x1,double y1){
		
		double x0,y0,theta1;
		
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

		turnTo(theta1);
	}	
	
	// ---- helper methods ---- //

	/**
	 * make robot rotate by specified angle.
	 * A positive angle results in counter-clockwise rotation
	 * @param deltaTheta
	 * @param nonBlocking
	 */
	public void rotateBy(double deltaTheta, boolean nonBlocking) {

		// set motor speeds
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);

		// begin rotation and wait for completion
		leftMotor.rotate(
				-convertAngle(odometer.leftRadius, odometer.width, deltaTheta),
				true);
		rightMotor.rotate(
				convertAngle(odometer.rightRadius, odometer.width, deltaTheta),
				nonBlocking);

	}
	
	//
	/**
	 * move forward by specified distance
	 * @param distance
	 */
	public void moveForwardBy(double distance){
		
		// set motor speeds
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);

		// begin rotation and wait for completion
		leftMotor.rotate(
				convertDistance(odometer.leftRadius, distance),
				true);
		rightMotor.rotate(
				convertDistance(odometer.rightRadius, distance),
				false);
	}

	/**
	 * convert desired traveling distance to angular wheel rotation
	 * @param radius - wheel radius
	 * @param distance - distance of travel
	 * @return angle (in degrees)
	 */
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * convert desired change of orientation to angular wheel rotation
	 * @param radius - wheel radius
	 * @param width - wheelbase width
	 * @param angle - change in orientation
	 * @return angle (in degrees)
	 */
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	
	
	// ---- accessors ---- //

	/**
	 * returns true if robot is navigating. returns false otherwise
	 * @return navigation status
	 */
	public static boolean getIsNavigating() {
		return isNavigating;
	}

	/**
	 * get value of destionationReached
	 * @return destination-reached status
	 */
	public boolean getDestinationReached() {
		return this.destinationReached;

	}

	// ---- mutators ---- //

	/**
	 * set value of boolean isNavigating
	 * @param foo - status
	 */
	private void setIsNavigating(boolean foo) {
		isNavigating = foo;
	}

	/**
	 * set value of destinationReached
	 * @param foo - status
	 */
	private void setDestinationReached(boolean foo) {
		this.destinationReached = foo;
	}

}
