package official;

import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * robot's odometer.
 * <p>
 * This class counts the robots' motor rotations
 * and translates them into displacement in the
 * x-y plane. This class is being helped by
 * OdometryCorrection to help compensate for error
 * that occurs at the physical/software interface
 * of the robot.
 * <p>
 * The robot's position is update every ODOMETER_PERIOD ms
 * <p>
 * Angles increase counter clockwise.
 * A vector pointing along the positive y-axis is at 90 degrees
 * <p>
 * Translation of the robot's heading to "compass" heading is done in getPosition()
 * @author Francois
 *
 */
public class Odometer implements TimerListener {
	
	/**
	 * robot's motors
	 */
	private NXTRegulatedMotor leftMotor, rightMotor;
	
	/**
	 * robot's position and heading
	 */
	private double x, y, theta;
	
	/**
	 * tachometer readings
	 */
	private int lastTachoLeft, lastTachoRight;
	
	/**
	 * left wheel radius
	 */
	public double leftRadius = Constants.leftRadius;
	/**
	 * right wheel radius;
	 */
	public double rightRadius = Constants.rightRadius;
	/**
	 * wheelbase width
	 */
	public double width = Constants.width;

	/**
	 * odometer's update period (in miliseconds)
	 */
	private final int ODOMETER_PERIOD = Constants.ODOMETER_PERIOD;

	/**
	 * lock object for mutual exclusion
	 */
	private Object lock;
	
	/**
	 * timer
	 */
	private Timer timer;

	/**
	 * constructor
	 * @param leftMotor
	 * @param rightMotor
	 */
	public Odometer(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// initialize all position & tachometer values
		x = 0.0;
		y = 0.0;
		theta = 90.0; // initial theta (in degrees)
		
		lastTachoLeft = leftMotor.getTachoCount();
		lastTachoRight = rightMotor.getTachoCount();
		
		this.leftRadius = 2.15;
		this.rightRadius = 2.15;
		this.width = 20.4;
		
		lock = new Object();
		
		timer = new Timer(ODOMETER_PERIOD, this);
		timer.start(); // start odometry
		
	}


	/**
	 * Odometer's main thread. Keeps track of robot's displacements and rotations by counting tachometer
	 * readings at the specified frequency ODOMETER_PERIOD.
	 * 
	 * @return void
	 */
	public void timedOut() {
		
		// tachometer values
		int tachoLeft, tachoRight, thetaLeft, thetaRight;
		// displacement values
		double dLeft, dRight, dDiff, displacement;
		// changes in orientation and position
		double deltaTheta, deltaX, deltaY;

		while (true) {
			
			// read left and right TachoCounts
			tachoLeft = leftMotor.getTachoCount();
			tachoRight = rightMotor.getTachoCount();
			
			// calculate change in rotation of both wheels (in degrees)
			thetaLeft = tachoLeft - lastTachoLeft;
			thetaRight = tachoRight - lastTachoRight;
			
			// update last tachometer readings
			lastTachoLeft = tachoLeft;
			lastTachoRight = tachoRight;
			
			// Calculate distance traveled by leftWheel & rightWheel
			// (thetaLeft/thetaRight are converted to rads in the calculation)
			dLeft = (leftRadius * Math.PI * thetaLeft) / 180;
			dRight = (rightRadius * Math.PI * thetaRight) / 180;
			
			// Calculate difference between dRight and dLeft
			dDiff = dRight - dLeft;
			
			// Calculate change in orientation and convert to degrees
			deltaTheta = ( dDiff * 180 ) / ( width * Math.PI ) ;
						
			// Calculate displacement
			displacement = (dLeft + dRight) / 2 ;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				
				// Calculate change in X and Y
				deltaX = displacement * Math.cos( ( theta + .5 * deltaTheta ) * Math.PI / 180 );
				deltaY = displacement * Math.sin( ( theta + .5 * deltaTheta) * Math.PI / 180 );
				
				// Update value of theta
				theta = (theta + deltaTheta) ;
						
				// Make the value of theta2 loop around at 360
				if(theta >= 360){
					theta = theta - 360;
				}
				else if(theta < 0){
					theta = 360 + theta;
				}
				
				// Update value of X & Y
				x = x + deltaX;
				y = y + deltaY;
				
			}
		}
	}

	
	// accessors
	
	/**
	 * get position and heading
	 * 
	 * @param position - array containing x-y coordinates(cm) and heading(degrees)
	 * @return void
	 */
	public void getPosition(double[] position) {
		
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
				position[0] = x;
				position[1] = y;
				
				// convert 'math' angle to 'compass' angle
				double temp = theta;
				if(theta <= 90){
					temp = 90 - theta;
				}
				else{
					temp = 450 - theta;
				}
				
				position[2] = temp;
		}
	}

	/**
	 * get x coordinate of robot's position
	 * @return x coordinate
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * set y coordinate of robot's position
	 * @return y coordinate
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * set robot's heading.
	 * @return heading (in degrees)
	 */
	public double getAng() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	
	/**
	 * get NXTRegulatedMotors used by Navigation.
	 * @return robot's left and right motors
	 */
	public NXTRegulatedMotor [] getMotors() {
		return new NXTRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}

	// mutators
	
	/**
	 * set robot's x-y position and its heading.
	 * @param position - contains robot's x-y coords. (cm) and heading (degrees)
	 * @return void
	 */
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}
	
	/**
	 * set robot's x coordinate
	 * @param x
	 * @return void
	 */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}
	
	/**
	 * set robot's y coordinate
	 * @param y
	 * @return void
	 */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	/**
	 * set robot's heading
	 * @param theta
	 * @return void
	 */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}