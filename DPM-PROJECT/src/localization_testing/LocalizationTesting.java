package localization_testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import official.Constants;
import official.LightLocalizer;
import official.LightPoller;
import official.Navigation;
import official.Odometer;
import official.SensorController;
import official.USLocalizer;
import official.USPoller;

public class LocalizationTesting {

	public static void main(String[] args) {

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
		int BOTT_DIFF = Constants.BOTT_DIFF, BACK_DIFF = Constants.BACK_DIFF;

		// **********************************************

		// motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// bottom us sensor
		UltrasonicSensor bottomS = new UltrasonicSensor(
				Constants.bottomSensorPort);

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, BACK_SAMPLE, BACK_DIFF);
		LightPoller[] lp = { back };

		// us poller
		USPoller bottom = new USPoller(bottomS, BOTT_SAMPLE, BOTT_DIFF);
		USPoller[] up = { bottom };

		// sensor controller
		// no need for OdometryCorrection and ObjectDetection
		SensorController cont = new SensorController(null, lp, up, PERIOD, null);
		
		// LCD display
		LCDInfo lcd = new LCDInfo(odo);
		
		// display menu
		LCD.clear();
		LCD.drawString("Choose task", 3, 0);
		LCD.drawString("< Left | Right > ", 0, 2);
		LCD.drawString("       |         ", 0, 3);
		LCD.drawString(" USLoc | USLoc & ", 0, 4);
		LCD.drawString("       | LightLoc ", 0, 5);
		
		Button.waitForAnyPress();
		int button = Button.readButtons();

		// start controller
		cont.startPolling();

		/*
		 * US LOCALIZATION
		 */

		// set up us localization
		USLocalizer usLoc = new USLocalizer(odo, nav, bottom,
				USLocalizer.LocalizationType.RISING_EDGE);

		// do us localization
		usLoc.doLocalization();

		/*
		 * LIGHT LOCALIZATION
		 */
		if (button == Button.ID_RIGHT) {

			// set up light localization
			LightLocalizer lightLoc = new LightLocalizer(odo, nav, back);

			// do light localization
			lightLoc.doLocalization();
		}
	}

}
