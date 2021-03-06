package official;

import lejos.nxt.Sound;

/**
 * Localization using the light sensor. The robot travels to a location from
 * where he can clock all four gridlines (while rotating at a fixed position).
 * According to the angles at which the gridlines are clocked, the robot
 * corrects its position and its heading.
 * 
 * @author Fran�ois Lemay
 * 
 */
public class LightLocalizer {
	
	/**
	 * robot's odometry class
	 */
	private Odometer odo;
	/**
	 * robot's navigation class
	 */
	private Navigation navigator;
	/**
	 * back light sensor
	 */
	private LightPoller lp;

	/**
	 * distance from light sensor to middle of wheel base
	 */
	private final double SENSOR_DISTANCE = Constants.BACK_SENSOR_DIST;
	/**
	 *  derivative threshold for light
	 *  polling (white to black)
	 */
	private final int THRESH_BLACK = Constants.GRIDLINE_THRES;
	/**
	 * tweaking value (in degrees) for final
	 * correction in heading
	 */
	private final int ANG_TWEAK = Constants.ANG_TWEAK;
	/**
	 * tweaking value to compensate for line detection lag
	 */
	private final int ANG_DELAY = Constants.ANG_DELAY;

	/**
	 * constructor
	 * @param odo
	 * @param navigator
	 * @param lp
	 */
	public LightLocalizer(Odometer odo, Navigation navigator, LightPoller lp) {
		this.odo = odo;
		this.navigator = navigator;
		this.lp = lp;

	}

	/**
	 * do light sensor localization taking into account the starting corner
	 */
	public void doLocalization() {
		
		double[] angles = new double[4];
/*
		// move forward until the x-axis is detected.
		// Only update Y position to 0.0
		navigator.setSpeeds(Constants.US_LOC_SPEED, Constants.US_LOC_SPEED);
		
		while (true) {
			if (lp.getLatestDerivative()<THRESH_BLACK) {
				odo.setPosition(new double[] { 0, 0 + SENSOR_DISTANCE, 0 },
						new boolean[] { false, true, false });
				Sound.beep();
				break;
			}
		}
		navigator.moveForwardBy(-2*SENSOR_DISTANCE, Constants.US_LOC_SPEED);

		// turn to 0 degrees & move along +ve x-axis until y-axis is crossed.
		// Only update X position to 0.

		navigator.turnTo(0,Constants.US_LOC_SPEED);
		navigator.setSpeeds(Constants.US_LOC_SPEED, Constants.US_LOC_SPEED);

		while (true) {
			if (lp.getLatestDerivative()<THRESH_BLACK) {
				odo.setPosition(new double[] { 0 + SENSOR_DISTANCE, 0, 0 },
						new boolean[] { true, false, false });
				Sound.beep();
				break;
			}
		}
		navigator.moveForwardBy(-2*SENSOR_DISTANCE,Constants.US_LOC_SPEED);
		
		// position robot to clock gridlines
		navigator.travelTo(-5,-5, Constants.US_LOC_SPEED);		
		navigator.turnTo(90,Constants.US_LOC_SPEED);
*/
		// start rotating and clock all 4 gridlines.
		// angles = { X- , Y+ , X+ , Y- }
		navigator.setSpeeds(Constants.LIGHT_LOC_SPEED, -Constants.LIGHT_LOC_SPEED);
		
		//*****************************************
		//RConsole.println("Start clocking");
		//*****************************************
		
		for (int i = 0; i < 4; i++) {

			// detect a line and store heading
			while (true) {
				if (lp.getLatestDerivative()<THRESH_BLACK) {
					angles[i] = odo.getAng() - ANG_DELAY;
					Sound.beep();
					break;
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

		}

		// halt motion of robot
		navigator.stopMotors();

		// do trig to compute (0,0) and 0 degrees
		double x = -1 * SENSOR_DISTANCE
				* Math.cos(Math.toRadians((angles[1] - angles[3]) / 2));
		double y = -1 * SENSOR_DISTANCE
				* Math.cos(Math.toRadians((angles[0] - angles[2]) / 2));
		double theta = odo.getAng() - angles[1]
				+ ((angles[1] + 360 - angles[3]) / 2) + ANG_TWEAK;

		odo.setPosition(new double[] { x, y, theta }, new boolean[] { true,
				true, true });

		// when done travel to (0,0) and turn to 90 degrees
		navigator.travelTo(0, 0, Constants.LIGHT_LOC_SPEED);
		navigator.turnTo(90,Constants.LIGHT_LOC_SPEED);

		// change origin to starting corner
		correctCorner();
	}

	/**
	 * update position according to the starting corner
	 * This assumes the grid to be 12 by 12.
	 */
	private void correctCorner() {
		
		int id = Constants.corner.getId();
		double x = Constants.corner.getX();
		double y = Constants.corner.getY();
		double heading = 90;	
		
		// calculate new heading
		
		// bottom left corner
		if(id==1){
			heading = 90;
		}
		// bottom right corner
		if(id==2){
			heading = 180;
		}
		// top right corner
		if(id==3){
			heading = 270;
		}
		// top left corner
		if(id==4){
			heading = 0;
		}

		
		// convert x,y to centimeters
		x = x*Constants.SQUARE_LENGTH;
		y = y*Constants.SQUARE_LENGTH;

		// update position and heading
		odo.setPosition(new double[] { x, y, heading }, new boolean[] { true,
				true, true });
	}

}
