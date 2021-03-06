/*
 *
 * DESCRIPTION
 * 
 * LCD printing method.
 * 
 * Displayed heading is in the 'compass' format.
 * (Conversion is done in Odometer.java)
 * 
 */

package testing;

import official.Odometer;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * class borrowed from the labs (given by the TA's) to print odometry information to LCD screen. Helps for odometry testing
 * @author Francois
 *
 */
public class LCDInfo implements TimerListener {
	public static final int LCD_REFRESH = 100; // display is updated every 100
												// ms
	private Odometer odo;
	private Timer lcdTimer;

	// arrays for displaying data
	private double[] pos;

	public LCDInfo(Odometer odo) {
		this.odo = odo;
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// initialize the arrays for displaying data
		pos = new double[3];
		
	}

	public void timedOut() {
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int) (pos[0]), 3, 0);
		LCD.drawInt((int) (pos[1]), 3, 1);
		LCD.drawInt((int) (pos[2]), 3, 2);

	}
	
	/**
	 * start timer
	 */
	public void start(){
		this.lcdTimer.start();
	}
}
