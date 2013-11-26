package official;

import java.io.IOException;

import deprecated.NXTComm;
import bluetooth.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;

/**
 * master brick's main class.
 * <p>
 * initiates communication with Slave brick. Controls lifting and clamping
 * mechanism via commands given to Slave brick.
 * 
 * @author Francois Lemay
 */
public class Master {

	/**
	 * number of blocks being carried by robot at a given time
	 */
	public static int blocks = 0;

	/**
	 * main program thread
	 * 
	 * @param args
	 *            - default argument
	 */
	public static void main(String[] args) throws IOException {

		/*
		 * bluetooth initialization
		 */

		// bluetoothInit();

		// **************************************************************

		Constants.goodZone = new int[] { -1 * Constants.SQUARE_LENGTH,
				6 * Constants.SQUARE_LENGTH, 0 * Constants.SQUARE_LENGTH,
				7 * Constants.SQUARE_LENGTH };

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

		// initialize sensor motor
		NXTRegulatedMotor sensorMotor = new NXTRegulatedMotor(
				Constants.sensorMotorPort);
		sensorMotor.setSpeed(Navigation.SLOW);
		sensorMotor.resetTachoCount();

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, Constants.LIGHT_SAMPLE,
				Constants.M_PERIOD);

		// three front us sensors
		UltrasonicSensor bottomS = new UltrasonicSensor(
				Constants.bottomSensorPort);
		UltrasonicSensor leftS = new UltrasonicSensor(Constants.leftSensorPort);
		UltrasonicSensor rightS = new UltrasonicSensor(
				Constants.rightSensorPort);

		// us pollers
		USPoller bottom = new USPoller(bottomS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		USPoller left = new USPoller(leftS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		USPoller right = new USPoller(rightS, Constants.US_SAMPLE,
				Constants.M_PERIOD);

		// odometry correction
		// OdometryCorrection odoCorr = new OdometryCorrection(odo, back);

		/*
		 * US LOCALIZATION
		 */
		/*
		 * // set up us localization USLocalizer usLoc = new USLocalizer(odo,
		 * nav, bottom, USLocalizer.LocalizationType.RISING_EDGE);
		 * 
		 * // do us localization usLoc.doLocalization();
		 */
		/*
		 * LIGHT LOCALIZATION
		 */
		/*
		 * // set up light localization LightLocalizer lightLoc = new
		 * LightLocalizer(odo, nav, back);
		 * 
		 * // do light localization lightLoc.doLocalization();
		 */

		/*
		 * main program loop
		 */

		// start odometry correction
		try {
			// odoCorr.start();
		} catch (Exception e) {
			LCD.clear();
			LCD.drawString("problem", 0, 0);

		}

		Button.waitForAnyPress();

		// set robot's destination
		Constants.robotDest[0] = Constants.goodZone[0];
		Constants.robotDest[1] = Constants.goodZone[1];

		// initialize boolean
		String status = "";

		// travel to construction zone while detecting objects
		do {

			// turn towards destination point
			nav.turnTowards(Constants.robotDest[0], Constants.robotDest[1]);

			// move forward with goFwdUS
			status = nav.goFwdCaution(30, Navigation.FAST, bottom, left, right);

			// check various scenarios

			if (status.equals("obstacle")) {

				Sound.beepSequence();
				// identify object

				// avoid obstacle

				// check if right is clear
				nav.rotateBy(-90, false, Navigation.FAST);
				if (left.getLatestFilteredDataPoint() > 10
						&& right.getLatestFilteredDataPoint() > 10
						&& bottom.getLatestFilteredDataPoint() > 10) {
					nav.goFwdCaution(30, Navigation.FAST, bottom, left, right);
				}else{
					nav.rotateBy(180,false, Navigation.FAST);
					nav.goFwdCaution(30, Navigation.FAST, bottom, left, right);
				}
				
				System.exit(0);

				// check if left is clear

			}
			else if (status.equals("destination")) {

				Sound.beepSequenceUp();
				System.exit(0);

				// exit loop
				break;
			}
			else if (status.equals("enemy")) {

				// avoid enemy's zone
				Sound.buzz();
				//System.exit(0);

			}

			// no obstacle encountered. repeat until destination reached

		} while (true);

		System.exit(0);

		// find one block if none found yet
		if (blocks >= 1) {
			while (blocks < 1) {

				nav.rotateBy(-180, false, Navigation.SLOW);

			}
		}

		/*
		 * end program
		 */
		Sound.beepSequence();
		System.exit(0);
	}

	/**
	 * method for bluetooth client connection
	 */
	public static void bluetoothInit() {

		BluetoothConnection conn = new BluetoothConnection();
		// as of this point the bluetooth connection is closed again, and you
		// can pair to another NXT (or PC) if you wish

		// example usage of Tranmission class
		Transmission t = conn.getTransmission();

		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {

			Constants.corner = t.startingCorner;
			Constants.role = t.role;

			if (Constants.role == PlayerRole.BUILDER) {

				// green zone is defined by these (bottom-left and top-right)
				// corners:
				Constants.goodZone = new int[4];
				Constants.goodZone[0] = t.greenZone[0]
						* Constants.SQUARE_LENGTH;
				Constants.goodZone[1] = t.greenZone[1]
						* Constants.SQUARE_LENGTH;
				Constants.goodZone[2] = t.greenZone[2]
						* Constants.SQUARE_LENGTH;
				Constants.goodZone[3] = t.greenZone[3]
						* Constants.SQUARE_LENGTH;

				// red zone is defined by these (bottom-left and top-right)
				// corners:
				Constants.badZone = new int[4];
				Constants.badZone[0] = t.redZone[0] * Constants.SQUARE_LENGTH;
				Constants.badZone[1] = t.redZone[1] * Constants.SQUARE_LENGTH;
				Constants.badZone[2] = t.redZone[2] * Constants.SQUARE_LENGTH;
				Constants.badZone[3] = t.redZone[3] * Constants.SQUARE_LENGTH;

			} else {

				// red zone is defined by these (bottom-left and top-right)
				// corners:
				Constants.badZone = new int[4];
				Constants.badZone[0] = t.greenZone[0] * Constants.SQUARE_LENGTH;
				Constants.badZone[1] = t.greenZone[1] * Constants.SQUARE_LENGTH;
				Constants.badZone[2] = t.greenZone[2] * Constants.SQUARE_LENGTH;
				Constants.badZone[3] = t.greenZone[3] * Constants.SQUARE_LENGTH;

				// green zone is defined by these (bottom-left and top-right)
				// corners:
				Constants.goodZone = new int[4];
				Constants.goodZone[0] = t.redZone[0] * Constants.SQUARE_LENGTH;
				Constants.goodZone[1] = t.redZone[1] * Constants.SQUARE_LENGTH;
				Constants.goodZone[2] = t.redZone[2] * Constants.SQUARE_LENGTH;
				Constants.goodZone[3] = t.redZone[3] * Constants.SQUARE_LENGTH;

			}

			// print out the transmission information to the LCD
			conn.printTransmission();
		}
	}
}
