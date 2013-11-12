package localization_testing;

import lejos.nxt.ColorSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import official.LightPoller;
import official.Navigation;
import official.Odometer;
import official.SensorController;
import official.USLocalizer;
import official.USPoller;

public class LocalizationTesting {
	
	public static void main(String[] args){
		
		// *********************************************
		// DO NOT TWEAK THESE VALUES 

		/*
		 * polling frequency for SensorController
		 */
		int PERIOD = 20;
		
		/*
		 * sample sizes
		 */
		int BOTT_SAMPLE = 9, BACK_SAMPLE = 9;
		
		/*
		 * derivative sample sizes
		 */
		int  BOTT_DIFF = 8, BACK_DIFF = 8;
		
		
		// **********************************************
		
		
		// motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);
		
		// odometry
		Odometer odo = new Odometer(leftMotor,rightMotor);
		
		// navigation
		Navigation nav = new Navigation(odo);
		
		// bottom us sensor
		UltrasonicSensor bottomS = new UltrasonicSensor(SensorPort.S1);
		
		// back light sensor
		ColorSensor backS = new ColorSensor(SensorPort.S2);

		// light poller
		LightPoller back = new LightPoller(backS, BACK_SAMPLE, BACK_DIFF);
		LightPoller[] lp = {back};
		
		// us poller
		USPoller bottom = new USPoller(bottomS, BOTT_SAMPLE, BOTT_DIFF);
		USPoller[] up = {bottom};
		
		// sensor controller
		// no need for OdometryCorrection and ObjectDetection
		SensorController cont = new SensorController(null, lp, up, PERIOD, null);
		
		// start controller
		cont.startPolling();
		
		/*
		 * US LOCALIZATION
		 */
		
		// set up us localization
		USLocalizer usLoc = new USLocalizer(odo, nav, bottom, USLocalizer.LocalizationType.RISING_EDGE);
		
		// do us localization
		usLoc.doLocalization();
		
		/*
		 * LIGHT LOCALIZATION
		 */
		
		// set up light localization
		
		
		// do light localization
		
		
	}

}
