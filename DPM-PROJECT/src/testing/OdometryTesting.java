package testing;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import official.Navigation;
import official.Odometer;

/**
 * Odometry testing program.  makes robot drive to specified locations on x,y maps
 * 
 * robot assumes to begin exactly at the (0,0) location
 * facing "north", i.e. 90 degrees
 * @author Francois
 *
 */
public class OdometryTesting {
	
	
	public static void main(String[] args){
		
		NXTRegulatedMotor left = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor right = new NXTRegulatedMotor(MotorPort.B);

		Odometer odo = new Odometer(left,right);
		Navigation nav = new Navigation(odo);
		LCDInfo lcd = new LCDInfo(odo);
		
		lcd.start();
		
		/*
		 * Insert destinations here!!!
		 * {x,y}
		 */
		double[][] destinations = {{0,30},{60,30},{60,60},{0,0}};
		double x,y;
		
		Button.waitForAnyPress();
		
		/*
		 * will travel at every destination in the above array.
		 * Will stop at every destination until a button is pressed.
		 * This will allow you to measure its actual position and compare
		 * it to its intended destination.
		 */
		for(int i=0; i<destinations.length; i++){
			
			// set destination
			x = destinations[i][0];
			y = destinations[i][1];
			
			// travel to x,y
			nav.travelTo(destinations[i][0], destinations[i][1], Navigation.FAST);
		
		}
		
		nav.turnTo(90, Navigation.FAST);
		Button.waitForAnyPress();
		
	}

}
