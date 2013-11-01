/* Group 22
 * Franois Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 *
 * DESCRIPTION
 *
 * Initial Localization using the Ultrasonic Sensor.
 * Data is taken from the US Poller class
 * 
 */

package main;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 70;
	
	// these values should be determined experimentally
	private final int NO_WALL = 80; // distance considered as no wall present
	private final int THRESHOLD = 40;
	private final int NOISE_MARGIN = 5;
	
	private final int FE_TWEAK = 7; // tweaking value (in degrees) for deltaTheta (in Falling Edge)
	private final int RE_TWEAK = 2;  // tweaking value (in degrees) for deltaTheta (in Rising Edge)

	
	// other class variables
	private Odometer odo;
	private Navigation navigator;
	private USPoller usPoller;
	private LocalizationType locType;
	
	// us readings filter variables
	private final int FILTER_OUT = 10;
	private int filterCtl;
	
	public USLocalizer(Odometer odo, Navigation navigator, USPoller usPoller, LocalizationType locType) {
		this.odo = odo;
		this.navigator = navigator;
		this.usPoller = usPoller;
		this.locType = locType;
		
		filterCtl = 0;
	}
	
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
	
	private int getFilteredData() {
		int distance = 0;
		boolean badValue = true;
		
		// this while loop is intended for filtering of 255 values
		while(badValue){
			
			// there will be a delay here
			distance = usPoller.getLatestMedian();
			
			// the following if statement filters inconsistent readings of 255
			if (distance == 255 && filterCtl < FILTER_OUT) {  
				// bad value, do not set the distance variable, however do increment the filter value
				filterCtl ++;
						
			// the distance variable is set to 255 if the 255 value has been read FILTER_OUT times or more
			} else if (distance == 255){
				// true 255, therefore set distance to 255 and exit while loop
				badValue = false;
				
			} else {
				// distance went below 255, therefore reset filterControl and exit while loop
				filterCtl = 0;
				badValue = false;
			}
		}
				
		return distance;
	}

}
