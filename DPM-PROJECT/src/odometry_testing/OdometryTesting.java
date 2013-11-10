package odometry_testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import official.Navigation;
import official.Odometer;

public class OdometryTesting {
	
	
	public static void main(String[] args){
		
		NXTRegulatedMotor left = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor right = new NXTRegulatedMotor(MotorPort.B);

		Odometer odo = new Odometer(left,right);
		Navigation nav = new Navigation(odo);
		
		/*
		 * Insert destinations here!!!
		 * {x,y}
		 */
		double[][] destinations = {{0,30},{30,30},{30,60},{60,0},{0,30},{0,0}};
		double x,y;
		int button=99;
		
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
			
			// print destination to screen
			LCD.drawString("x = "+x,0,0);
			LCD.drawString("y = "+y,0,0);
			
			// travel to x,y
			nav.travelTo(destinations[i][0], destinations[i][1]);
			
			// wait for button press
			LCD.clear();
			LCD.drawString("Press escape to",0,0);
			LCD.drawString("exit, or any",0,1);
			LCD.drawString("other button to",0,2);
			LCD.drawString("continue",0,3);
			
			Button.waitForAnyPress();
			if(button==Button.ID_ESCAPE){
				System.exit(0);
			}
		}
		
	}

}
