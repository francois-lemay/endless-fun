package official;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/**
 * avoid obstacles while remaining on travel course. Employs a
 * BangBang-controlled wall follower to go around obstacles. The wall follower
 * is stopped once the robot is aligned with its destination point.
 * 
 * @author Francois Lemay
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
	/**
	 * distance to keep between robot and obstacle/wall
	 */
	private final int bandCenter = 50;
	/**
	 * error bandwidth before bangbang control kicks in
	 */
	private final int bandwith = 5;
	/**
	 * high motor speed for bangbang controller
	 */
	private final int motorHigh = 250;
	/**
	 * low motor speed for bangbang controller
	 */
	private final int motorLow = 100;

	/**
	 * constructor
	 * 
	 * @param odo
	 *            - robot's odometry class
	 * @param nav
	 *            - robot's navigation
	 * @param up
	 *            - bottom and top us sensors
	 * @param leftMotor
	 *            - left motor
	 * @param rightMotor
	 *            - right motor
	 */
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

	/**
	 * avoid incoming obstacles at front of the robot
	 */
	public void avoidObstacle() {

		// intialize variables
		double delta = 0;

		// get reading from top us sensor
		int dist = up[Constants.topUSPollerIndex].getLatestFilteredDataPoint();

		// get actual destination
		double x1 = Constants.robotDest[0];
		double y1 = Constants.robotDest[1];

		// approach obstacle if too far
		if (dist > Constants.OBSTACLE_APPROACH) {

			// slowly approach obstacle
			nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);

			do {
				dist = up[Constants.topUSPollerIndex]
						.getLatestFilteredDataPoint();
			} while (dist > Constants.OBSTACLE_APPROACH);

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
			}

			// check if in-line with destination point
			// by calculating difference in heading.
			// if so, stop bang bang

			// get destination heading
			double destAng = destAng(x1, y1);
			double robotAng = odo.getAng();

			// calculate difference in heading in two ways
			double delta1 = Math.abs(robotAng - destAng);
			double delta2 = Math.abs(robotAng - destAng + 360);

			// choose smallest difference in heading
			delta = Math.min(delta1, delta2);

		} while (delta < 10);

		// turn sensor back
		sensorMotor.rotateTo(0, false);

	}

	/**
	 * do wall/obstacle following (with bangbang controller) using one us sensor
	 */
	private void doBangBang() {

		// get reading from top us sensor
		int distance = up[Constants.topUSPollerIndex].getLatestRawDataPoint();

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
