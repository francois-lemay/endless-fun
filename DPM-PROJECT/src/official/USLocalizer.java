package official;

/** 
 * initial Localization using the Ultrasonic Sensor.
 * Data is taken from the US Poller class
 */
public class USLocalizer {	
	
	// class variables
	/**
	 * robot's odometer
	 */
	private Odometer odo;
	/**
	 * robot's navigation class
	 */
	private Navigation navigator;
	/**
	 * bottom-front ultrasonic sensor
	 */
	private USPoller us;
	/**
	 * type of localization to be performed
	 */
	private LocalizationType locType;
	
	/**
	 * implemented types of localization
	 *
	 */
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	// ****************************************************************************
	// these values should be determined experimentally
	
	/**
	 * robot's rotation speed used during us localization
	 */
	private final int ROTATION_SPEED = 70;
	
	/**
	 * distance considered as 'no wall present' (centimeters)
	 */
	private final int NO_WALL = 80;
	/**
	 * distance considered as 'wall detected' (centimeters)
	 */
	private final int THRESHOLD = 40;
	/**
	 * size of noise margin
	 */
	private final int NOISE_MARGIN = 5;
	/**
	 * tweaking value (in degrees) for deltaTheta (in Falling Edge)
	 */
	private final int FE_TWEAK = 0;
	/**
	 * tweaking value (in degrees) for deltaTheta (in Rising Edge)
	 */
	private final int RE_TWEAK = 0;
	
	// ******************************************************************************

	
	/**
	 * constructor
	 * @param odo - robot's odometer
	 * @param navigator - robot's navigation class
	 * @param usPoller - bottom-front us sensor
	 * @param locType - type of localization to be performed
	 */
	public USLocalizer(Odometer odo, Navigation navigator, USPoller usPoller, LocalizationType locType) {
		this.odo = odo;
		this.navigator = navigator;
		this.us = usPoller;
		this.locType = locType;
		
	}
	
	/**
	 * perform localization
	 */
	public void doLocalization() {
		double angleA = 0;
		double angleB = 0;

		if (locType == LocalizationType.FALLING_EDGE) {

			boolean noWall = false;

			// rotate the robot until it sees no wall
			navigator.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);

			while (!noWall) {
				if (getFilteredData() > NO_WALL) {
					// keep rotating and set noWall to true
					// to exit the loop
					noWall = true;
				}
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while (noWall) {
				// enter noise margin
				if (getFilteredData() <= THRESHOLD + NOISE_MARGIN) {
					
					//wait until go below noise margin
					while (noWall) {
						if (getFilteredData() <= THRESHOLD - NOISE_MARGIN) {
							angleA = odo.getAng();
							noWall = false; // exit both loops
						}
					}
				}
			}

			// switch direction and wait until it sees no wall
			navigator.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
			
			noWall = false;
			while (!noWall) {
				if (getFilteredData() > NO_WALL) {
					// keep rotating and set noWall to true
					// to exit the loop
					noWall = true;
				}
			}

			// keep rotating until the robot sees a wall, then latch the angle
			noWall = true;
			while (noWall) {
				// enter noise margin
				if (getFilteredData() <= THRESHOLD + NOISE_MARGIN) {
					
					//wait until go below noise margin
					while (noWall) {
						if (getFilteredData() <= THRESHOLD - NOISE_MARGIN) {
							angleB = odo.getAng();
							noWall = false; // exit both loops
						}
					}
				}
			}
			
			navigator.setSpeeds(0, 0);
			
			// update the odometer position
			double deltaTheta = 0;
			if(angleA>angleB){
				deltaTheta = 225 - (angleA + angleB)/2 - FE_TWEAK;
			}
			else{
				deltaTheta = 45 - (angleA + angleB)/2 - FE_TWEAK;
			}
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, odo.getAng() + deltaTheta }, new boolean [] {false, false, true});
			
			// turnTo(90)
			navigator.turnTo(90);
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			boolean noWall = false;

			// rotate the robot until it sees no wall
			navigator.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);

			while (!noWall) {
				if (getFilteredData() > NO_WALL) {
					// keep rotating and set noWall to true
					// to exit the loop
					noWall = true;
				}
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while (noWall) {
				// enter noise margin
				if (getFilteredData() <= THRESHOLD + NOISE_MARGIN) {
					
					//wait until go below noise margin
					while (noWall) {
						if (getFilteredData() <= THRESHOLD - NOISE_MARGIN) {
							angleA = odo.getAng();
							noWall = false; // exit both loops
						}
					}
				}
			}
			noWall = true; // set to true for the upcoming while loop
			
			// keep rotating until the robot detects a rising edge
			while (noWall) {
				// enter noise margin
				if (getFilteredData() >= THRESHOLD - NOISE_MARGIN) {
					
					//wait until goes above noise margin
					while (noWall) {
						if (getFilteredData() >= THRESHOLD + NOISE_MARGIN) {
							angleB = odo.getAng();
							noWall = false; // exit both loops
						}
					}
				}
			}
			
			// stop robot
			navigator.setSpeeds(0, 0);
			
			// update the odometer position
			double deltaTheta = 0;
			if(angleA>angleB){
				deltaTheta = 225 - (angleA + angleB)/2 - RE_TWEAK;
			}
			else{
				deltaTheta = 45 - (angleA + angleB)/2 - RE_TWEAK;
			}
			
			odo.setPosition(new double [] {0.0, 0.0, odo.getAng() + deltaTheta }, new boolean [] {false, false, true});
						
		}
	}
	
	/**
	 * get a median-filtered reading from the ultrasonic sensor.
	 * @return the latest filteredData point
	 */
	private int getFilteredData() {
				
		return us.getLatestFilteredDataPoint();
	}

}
