package official;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;

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
	 * boolean status
	 */
	public static boolean isAvoiding;
	/**
	 * distance to keep between robot and obstacle/wall
	 */
	private final int bandCenter = 25;
	/**
	 * error bandwidth before bangbang control kicks in
	 */
	private final int bandwith = 5;
	/**
	 * high motor speed for bangbang controller
	 */
	private final int motorHigh = 220;
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
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, NXTRegulatedMotor sensorMotor) {

		this.odo = odo;
		this.nav = nav;
		this.up = up;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sensorMotor = sensorMotor;
		
		isAvoiding = false;

	}

	/**
	 * avoid incoming obstacles at front of the robot
	 */
	public void avoidObstacle() {
		
		// set status
		isAvoiding = true;
		
		// initialize variables
		double delta = 0;
		double fwdError = 0;
		long time1=0;
		long time2=0;

		// get reading from top us sensor
		int dist = up[Constants.topUSPollerIndex].getLatestFilteredDataPoint();

		// get actual destination
		double x1 = Constants.robotDest[0];
		double y1 = Constants.robotDest[1];

		// approach obstacle if too far
		if (dist > 20) {

			// slowly approach obstacle
			nav.setSpeeds(Navigation.SLOW, Navigation.SLOW);
			
			// start time keeping for timeout
			time1 = System.currentTimeMillis();

			do {
				
				dist = up[Constants.topUSPollerIndex]
						.getLatestFilteredDataPoint();
				
				time2 = System.currentTimeMillis();
				
				// stop approach if timed out
				if(time2 - time1 > 2000){
					break;
				}
				
			} while (dist > 23);

			// stop approaching obstacle
			nav.stopMotors();
		}

		// turn robot 90 degrees clockwise
		nav.rotateBy(-90, false);
				
		// turn us sensor towards obstacle
		sensorMotor.rotateTo(-110, false);

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
					LCD.drawString("Waiting for obstacle",0,0);
					LCD.drawString("at front to move",0,1);
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
			
			// calculate position error between position and destination
			fwdError = calcFwdError(x1, y1);

		} while (delta > 10  && fwdError > 10);
		
		// stop robot
		nav.stopMotors();
		
		// turn sensor back
		sensorMotor.rotateTo(0, false);
		
		// reset status
		isAvoiding = false;
		
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
		
	}

	/**
	 * do wall/obstacle following (with bangbang controller) using one us sensor
	 */
	private void doBangBang() {

		// initialize variables
		int FILTER_OUT = 10;
		int filterControl = 0;
		int distance = 0;
		boolean foo = true;
		
		do {
			// get reading from top us sensor
			distance = up[Constants.topUSPollerIndex]
					.getLatestRawDataPoint();

			// the following if statement filters inconsistent readings of 255
			if (distance == 255 && filterControl < FILTER_OUT) {
				// bad value, do not set the distance variable, however do
				// increment
				// the filter value
				filterControl++;
				foo = true;

			} else if (distance == 255) {
				// true 255, therefore set distance to 255
				foo = false;
			} else {
				// distance went below 255, therefore reset filterControl
				filterControl = 0;
				foo = false;
			}
		} while (foo);

		// calculate error
		int error = bandCenter - distance;

		// Speeds of motors are varied depending on bias (error)

		if (Math.abs(error) <= bandwith) { // Within range of error
			leftMotor.forward(); // Set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(Navigation.FAST); // maintain same speed for
			rightMotor.setSpeed(Navigation.FAST); // both motors
		} else if (error < 0) { // too far from the wall
			leftMotor.forward(); // set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh); // speed up right motor
		} else if (error > 0) { // too close to the wall
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorLow);
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
	
	private double calcFwdError(double x1, double y1) {
		// // Forward Error Calculation

		double[] displacementVector = new double[2];

		// get update robot's current position and heading
		double x0 = odo.getX();
		double y0 = odo.getY();

		// calculation of displacement vector
		displacementVector[0] = x1 - x0;
		displacementVector[1] = y1 - y0;

		// find magnitude of displacement vector to obtain forward error
		return Math.sqrt((displacementVector[0] * displacementVector[0])
				+ (displacementVector[1] * displacementVector[1]));
	}

}
