/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 * 
 * 
 * Main class of program.
 * 
 * Modification of robot parameters are done in Odometer.java
 *  
 */

package from_Labs_Francois;

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab5 {
	public static final int TIMEOUT_INTERVAL = 30;

	public static void main(String[] args) {

		//RConsole.openUSB(0); // Open usb, wait for ever (int = 0)

		// Print menu on screen
		LCD.drawString("        |        ", 0, 0);
		LCD.drawString("        |        ", 0, 1);
		LCD.drawString("        |        ", 0, 2);
		LCD.drawString(" OBJECT | OBJECT ", 0, 3);
		LCD.drawString("GUESSING|  HUNT  ", 0, 4);
		LCD.drawString("        |        ", 0, 5);
		LCD.drawString("        |        ", 0, 6);
		LCD.drawString("        |        ", 0, 7);

		// set up the odometer and the navigator
		Odometer odo = new Odometer(Motor.A, Motor.B);
		Navigation navigator = new Navigation(odo, Motor.C);

		// set up sensors
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		USPoller usPoller = new USPoller(us); // start timer in constructor

		// set up classes used in object recognition and searching
		ObjectIdentification objectID = new ObjectIdentification(odo,
				navigator, usPoller, ls);
		ObjectDetection objectDetector = new ObjectDetection(usPoller);
		ObjectSearching objectHunter = new ObjectSearching(odo,navigator);


		// wait for button press
		Button.waitForAnyPress();
		int button = Button.readButtons();

		// set up display
		LCDInfo lcd = new LCDInfo(odo, objectDetector,objectID); // timer is started in the
																// constructor

		// object guessing
		if (button == Button.ID_LEFT) {
						
			// enable objectDetection and objectIdentification
			// but do not start timer
			objectHunter.setIsReady(true);

		} 
		// object searching
		else if(button == Button.ID_RIGHT){

			// perform the ultrasonic localization with FALLING_EDGE
			USLocalizer usl = new USLocalizer(odo, navigator, usPoller,
					USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			
			// go hunt for a styrofoam block
			objectHunter.beginHunt();
					
		}
		else if(button == Button.ID_ESCAPE){
			RConsole.close();
			System.exit(0);
		}

		Button.waitForAnyPress();
		RConsole.close();
		System.exit(0);
	}

}
