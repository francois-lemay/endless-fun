package testing;

import official.Constants;
import official.LightPoller;
import official.Navigation;
import official.Odometer;
import official.USPoller;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class goFwdCautionTest {
	
	
	
	public static void main(String[] args){
		
		// **************************************************************

		Constants.goodZone = new int[] { -1 * Constants.SQUARE_LENGTH,
				2 * Constants.SQUARE_LENGTH, 0 * Constants.SQUARE_LENGTH,
				3 * Constants.SQUARE_LENGTH };

		Constants.badZone = new int[] { -1 * Constants.SQUARE_LENGTH,
				4 * Constants.SQUARE_LENGTH, 0 * Constants.SQUARE_LENGTH,
				5 * Constants.SQUARE_LENGTH };

		// ***************************************************************
		
		/*
		 * hardware initialization
		 */

		// initialize block pick up first since it takes the longest
		// BlockPickUp.init();

		// set up robot motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(
				Constants.leftMotorPort);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(
				Constants.rightMotorPort);

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// color sensor
		//ColorSensor frontS = new ColorSensor(Constants.frontLightSensorPort);

		// two front us sensors
		UltrasonicSensor leftS = new UltrasonicSensor(Constants.leftSensorPort);
		UltrasonicSensor rightS = new UltrasonicSensor(
				Constants.rightSensorPort);

		// us pollers
		USPoller left = new USPoller(leftS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		USPoller right = new USPoller(rightS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		
		String status = "";
		for(int i=0;i<20;i++){
						
			status = nav.goFwdCaution(30, Navigation.FAST, left, right);
			
			
			if (status.equals("obstacle")) {
				
				LCD.drawString(status, 0, 0);
				Button.waitForAnyPress();

			} else if (status.equals("destination")) {

				LCD.drawString(status, 0, 0);
				Button.waitForAnyPress();
				
				// execute if too close to enemy zone
			} else if (status.equals("enemy")) {

				LCD.drawString(status, 0, 0);
				Button.waitForAnyPress();
			} else if(status.equals("success")){
				
				LCD.drawString(status, 0, 0);
				Button.waitForAnyPress();
			}
			
			LCD.clear();
			LCD.drawString("press button", 0, 0);
		}
		
		
	}

}
