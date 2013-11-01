/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 *
 * DESCRIPTION
 * 
 * LCD printing method.
 * 
 * Displayed heading is in the 'compass' format.
 * (Conversion is done in Odometer.java)
 * 
 */

package main;

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener {
	public static final int LCD_REFRESH = 100; // display is updated every 100
												// ms
	private Odometer odo;
	private Timer lcdTimer;
	private ObjectDetection objectDetector;
	private ObjectIdentification objectID;


	// arrays for displaying data
	private double[] pos;

	public LCDInfo(Odometer odo , ObjectDetection objectDetector, ObjectIdentification objectID) {
		this.odo = odo;
		this.objectDetector = objectDetector;
		this.objectID = objectID;
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// initialize the arrays for displaying data
		pos = new double[3];

		// start the timer
		lcdTimer.start();
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
		
		// print object detection status
		LCD.drawString(objectDetector.getStatus(), 0, 4);
		
		// if object detected, print type
		if(objectDetector.isObjectDetected()){
		LCD.drawString(objectID.getStatus(), 0, 5);
		}else{
			LCD.drawString("                        ", 0, 5);

		}
		

	}
}
