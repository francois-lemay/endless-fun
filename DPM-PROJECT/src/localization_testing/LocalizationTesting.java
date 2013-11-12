package localization_testing;

import lejos.nxt.ColorSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import official.Constants;
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
		int PERIOD = Constants.M_PERIOD;
		
		/*
		 * sample sizes
		 */
		int BOTT_SAMPLE = Constants.BOTT_SAMPLE, BACK_SAMPLE = Constants.BACK_SAMPLE;
		
		/*
		 * derivative sample sizes
		 */
		int  BOTT_DIFF = Constants.BOTT_DIFF, BACK_DIFF = Constants.BACK_DIFF;
		
		
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
