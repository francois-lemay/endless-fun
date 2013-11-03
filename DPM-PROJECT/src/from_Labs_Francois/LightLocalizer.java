/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 *
 * DESCRIPTION
 *
 * Localization using the light sensor. The robot
 * travels to a location from where he can clock all
 * four gridlines (while rotating at a fixed position).
 * According to the angles at which the gridlines are
 * clocked, the robot corrects its position and its
 * heading.
 * 
 */

package from_Labs_Francois;

import lejos.nxt.Sound;

public class LightLocalizer {
	private Odometer odo;
	private Navigation navigator;
	private LightPoller lp;
	
	private final double SENSOR_DISTANCE = 12.5; // distance from light sensor to middle of wheel base
	private final int THRESH_BLACK = -50; // derivative threshold for light polling (white to black)
	private final int ANG_TWEAK = -1; // tweaking value (in degrees) for final correction in heading
	
	public LightLocalizer(Odometer odo, Navigation navigator, LightPoller lp) {
		this.odo = odo;
		this.navigator = navigator;
		this.lp = lp;

	}
	
	public void doLocalization() {
		double[] angles = new double[4];
		
		// //drive to location listed in tutorial.
		
		// call turnTo(), orient robot to 90 degrees
		navigator.turnTo(90);
		
		// move forward until the x-axis is detected.
		// Only update Y position to 0.0
		navigator.setSpeeds(Navigation.FAST, Navigation.FAST);
		
		boolean lineDetected = false;
		
		while(!lineDetected){
			if(this.lp.searchForSmallerDerivative(THRESH_BLACK)){
				odo.setPosition(new double[] { 0 , 0 + SENSOR_DISTANCE, 0} , new boolean[] {false,true,false});
				lineDetected = true;
				Sound.beep();
			}
		}
///*		
		// turn to 0 degrees & move along +ve x-axis until y-axis is crossed.
		// Only update X position to 0.
		
		navigator.turnTo(0);
		navigator.setSpeeds(Navigation.FAST,Navigation.FAST);
		
		lineDetected = false;
		
		while(!lineDetected){
			if(this.lp.searchForSmallerDerivative(THRESH_BLACK)){
				odo.setPosition(new double[] { 0 + SENSOR_DISTANCE , 0, 0} , new boolean[] {true,false,false});
				lineDetected = true;
				Sound.beep();
			}
		}
		
		// travelTo(-6,0) & turnTo(270degrees)
		// in order to clock all 4 gridlines
		navigator.travelTo(-6, 0);
		navigator.turnTo(270);
		
		// start rotating and clock all 4 gridlines.
		// int[] angles = { Y+ , X+ , Y- , X- }
		navigator.setSpeeds(Navigation.SLOW, -Navigation.SLOW);
		
		for (int i = 0; i < 4; i++) {

			// reset boolean
			lineDetected = false;

			// detect a line and store heading
			while (!lineDetected) {
				if (this.lp.searchForSmallerDerivative(THRESH_BLACK)) {
					angles[i] = odo.getAng();
					lineDetected = true; // exit while loop
					Sound.beep();
				}
			}
			try{Thread.sleep(500);}
			catch(InterruptedException e){}
			
		}

		// halt motion of robot
		navigator.setSpeeds(0,0);		
		
		// do trig to compute (0,0) and 0 degrees
		double x = -1 * SENSOR_DISTANCE * Math.cos(Math.toRadians((angles[0] - angles[2])/2));
		double y = -1 * SENSOR_DISTANCE * Math.cos(Math.toRadians((angles[3] - angles[1])/2));
		double theta = odo.getAng() - angles[0] + ((angles[0] + 360 - angles[2])/2) + ANG_TWEAK;
		
		odo.setPosition(new double[] {x,y,theta} , new boolean[] {true,true,true});
		
		// when done travel to (0,0) and turn to 0 degrees
		navigator.travelTo(0, 0);
		navigator.turnTo(90);
//*/
	}

}
