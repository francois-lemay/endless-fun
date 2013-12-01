package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import official.Constants;
import official.LightLocalizer;
import official.LightPoller;
import official.Navigation;
import official.Odometer;
import official.USLocalizer;
import official.USPoller;

/**
 * code used for testing us localization and light localization
 * @author Francois
 *
 */
public class LocalizationTest {

	public static void main(String[] args) {

		//RConsole.openUSB(0);

		// motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// left us sensor
		UltrasonicSensor leftS = new UltrasonicSensor(
				Constants.leftSensorPort);

		// us poller
		USPoller bottom = new USPoller(leftS, Constants.US_SAMPLE, Constants.M_PERIOD);

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, Constants.US_SAMPLE,Constants.M_PERIOD);

		// display menu
		LCD.clear();
		LCD.drawString("Choose task", 3, 0);
		LCD.drawString("< Left | Right > ", 0, 2);
		LCD.drawString("       |         ", 0, 3);
		LCD.drawString(" USLoc | LightLoc", 0, 4);
		LCD.drawString("       |         ", 0, 5);

		Button.waitForAnyPress();
		int button = Button.readButtons();

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
		
		if(button == Button.ID_RIGHT){
		// set up light localization
		LightLocalizer lightLoc = new LightLocalizer(odo, nav, back);

		// do light localization
		lightLoc.doLocalization();
		}
	}
}
