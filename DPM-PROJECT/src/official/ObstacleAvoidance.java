package official;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * avoid obstacles while remain on travel course
 * 
 * @author Francois
 * 
 */
public class ObstacleAvoidance {

	// class variables
	/**
	 * robot's odometer
	 */
	private Odometer odo;
	/**
	 * navigator
	 */
	private Navigation nav;
	/**
	 * robot's motors
	 */
	private NXTRegulatedMotor leftMotor, rightMotor, sensorMotor;
	/**
	 * array of us pollers
	 */
	private USPoller[] up;

	private final int bandCenter = 50;
	private final int bandwith = 5;
	private final int motorHigh = 250;
	private final int motorLow = 100;

	// constructor
	public ObstacleAvoidance(Odometer odo, Navigation nav, USPoller[] up,
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {

		this.odo = odo;
		this.nav = nav;
		this.up = up;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

		// initialize sensor motor
		this.sensorMotor = new NXTRegulatedMotor(Constants.sensorMotorPort);
		sensorMotor.setSpeed(Navigation.SLOW);
		sensorMotor.resetTachoCount();

	}

	public void avoidObstacle() {

		// initialize variables
		double delta = 0;
		int dist = up[Constants.topUSPollerIndex].getLatestFilteredDataPoint();

		// get actual destination
		double x1 = Constants.greenZone[0];
		double y1 = Constants.greenZone[1];

		// approach obstacle if necessary
		if (dist > Constants.OBSTACLE_APPROACH) {

			// slowly approach obstacle
			nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);

			do {
				dist = up[Constants.topUSPollerIndex]
						.getLatestFilteredDataPoint();
			} while (dist > Constants.OBSTACLE_APPROACH);
		} else {

			// stop approaching obstacle
			nav.stopMotors();
		}

		// turn 90 degrees clockwise
		nav.rotateBy(-90, false);

		// turn us sensor towards obstacle
		sensorMotor.rotateTo(-90, false);
		
		Sound.buzz();

		// do bang bang wall follower

		do {
			// follow obstacle
			doBangBang();

			// check for obstacle (e.g. a robot passing in front) at the front
			// at the same time
			if (up[Constants.bottomUSPollerIndex].getLatestFilteredDataPoint() < 20) {

				// stop motors
				nav.stopMotors();

				// wait until robot passes by
				while (up[Constants.bottomUSPollerIndex]
						.getLatestFilteredDataPoint() < 20) {
				}

				// reset the motor speeds
				nav.setSpeeds(Navigation.FAST, Navigation.FAST);
			}

			// check if in-line with destination point. Stop bang bang if so.

			// calculate difference in heading
			delta = Math.abs(odo.getAng() - destAng(x1, y1));

		} while (delta!=100034);
		
		// turn sensor back
		sensorMotor.rotateTo(0, false);

		// continue moving forward to contour the obstacle
		nav.moveForwardBy(20, Navigation.FAST);

		// turn 90 degrees counter-clockwise
		nav.rotateBy(100, false);
	}

	private void doBangBang() {

		// get reading from top us sensor
		int distance = up[Constants.topUSPollerIndex]
				.getLatestRawDataPoint();

		// calculate error
		int error = bandCenter - distance;

		// Speeds of motors are varied depending on bias (error)

		if (Math.abs(error) <= bandwith) { // Within range of error
			leftMotor.forward(); // Set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(Navigation.FAST); // Maintain same speed for
			rightMotor.setSpeed(Navigation.FAST); // both motors
		} else if (error < 0) { // Too far from the wall
			leftMotor.forward(); // Set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh); // Speed up right motor
		} else if (error > 0) { // Too close to the wall
			leftMotor.forward(); // Set left motor to spin forward
			leftMotor.setSpeed(Navigation.FAST); // and right motor to backward
												// (for greater compensation
			rightMotor.backward(); // in concave corners)
			rightMotor.setSpeed(50);
		}

	}

	private double destAng(double x1, double y1) {

		double x0, y0, theta1;

		// get robot's current position
		x0 = odo.getX();
		y0 = odo.getY();

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

		return theta1;
	}

}
