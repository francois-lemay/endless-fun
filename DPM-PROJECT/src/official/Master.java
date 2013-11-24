package official;

import bluetooth.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;

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
	public static void main(String[] args) {
						
		/*
		 * bluetooth initialization
		 */

		// bluetoothInit();

		/*
		 * inter-brick communication initialization
		 */
		//commInit();

		// **************************************************************

		Constants.goodZone = new int[] { -1 * Constants.SQUARE_LENGTH,
				2 * Constants.SQUARE_LENGTH };
		
		// ***************************************************************

		/*
		 * hardware initialization
		 */

		// set up robot motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(
				Constants.leftMotorPort);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(
				Constants.rightMotorPort);
		
		// initialize sensor motor
		NXTRegulatedMotor sensorMotor = new NXTRegulatedMotor(Constants.sensorMotorPort);
		sensorMotor.setSpeed(Navigation.SLOW);
		sensorMotor.resetTachoCount();

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, Constants.BACK_SAMPLE,
				Constants.M_PERIOD);

		// two front us sensors
		UltrasonicSensor bottomS = new UltrasonicSensor(
				Constants.bottomSensorPort);
		UltrasonicSensor topS = new UltrasonicSensor(Constants.topSensorPort);

		// us pollers
		USPoller bottom = new USPoller(bottomS, Constants.BOTT_SAMPLE,
				Constants.M_PERIOD);
		USPoller top = new USPoller(topS, Constants.TOP_SAMPLE,
				Constants.M_PERIOD);
		
		// us poller array
		USPoller[] up = new USPoller[2];
		up[Constants.bottomUSPollerIndex] = bottom;
		up[Constants.topUSPollerIndex] = top;

		// odometry correction
		// OdometryCorrection odoCorr = new OdometryCorrection(odo, back);
		
		// sensor sweep
		SensorSweep sweeper = new SensorSweep(sensorMotor);

		// obstacle avoidance
		ObstacleAvoidance avoider = new ObstacleAvoidance(odo, nav, up,
				leftMotor, rightMotor, sensorMotor);

		// object detection
		ObjectDetection detector = new ObjectDetection(odo, nav, up, avoider, sensorMotor);
		
		Button.waitForAnyPress();


		/*
		 * US LOCALIZATION
		 */
/*
		  // set up us localization USLocalizer usLoc = new USLocalizer(odo,
		  nav, bottom, USLocalizer.LocalizationType.RISING_EDGE);
		  
		  // do us localization
		  usLoc.doLocalization();
*/		 
		 /*
		  * LIGHT LOCALIZATION
		  */
/*		 
		  // set up light localization LightLocalizer lightLoc = new
		  LightLocalizer(odo, nav, back);
		  
		  // do light localization
		  lightLoc.doLocalization();
*/
		
		/*
		 * main program loop
		 */

		// start odometry correction, object detection and sensor sweep
		try {
			sweeper.start();
			detector.start();
			//odoCorr.start();
		} catch (Exception e) {
			LCD.clear();
			LCD.drawString("problem", 0, 0);

		}
		
		// set robot's destination
		Constants.robotDest[0] = Constants.goodZone[0];
		Constants.robotDest[1] = Constants.goodZone[1];
		
		// travel to construction zone while detecting objects
		do {
			
			// travel if no object detected
			if (!ObjectDetection.objectDetected) {
				nav.travelTo(Constants.robotDest[0], Constants.robotDest[1],
						Navigation.FAST);
			}

		} while (!Navigation.destinationReached);

		
		System.exit(0);

		// find one block if none found yet
		if (blocks >= 1) {
			while (blocks < 1) {

				nav.rotateBy(-180, false);

			}
		}

		// will need to re-start ObjectDetection before re-starting search. Also
		// reset 'blocks' to 0.
		// ObjectDetection is stopped when a styrofoam block has been found
		// (i.e. when blocks is not equal to zero)

		// start searching thread.

		// go deposit block(s) to construction zone
		// builder.deliverTower();

		/*
		 * end communication with Slave. & end program
		 */
		Sound.beepSequence();
		NXTComm.disconnect();
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
			
			if(Constants.role == PlayerRole.BUILDER){
				
			// green zone is defined by these (bottom-left and top-right)
			// corners:
			Constants.goodZone = new int[2];
			Constants.goodZone[0] = t.greenZone[0] * Constants.SQUARE_LENGTH;
			Constants.goodZone[1] = t.greenZone[1] * Constants.SQUARE_LENGTH;

			// red zone is defined by these (bottom-left and top-right) corners:
			Constants.badZone = new int[2];
			Constants.badZone[0] = t.redZone[0] * Constants.SQUARE_LENGTH;
			Constants.badZone[1] = t.redZone[1] * Constants.SQUARE_LENGTH;

			}else{
				
				// red zone is defined by these (bottom-left and top-right)
				// corners:
				Constants.badZone = new int[2];
				Constants.badZone[0] = t.greenZone[0] * Constants.SQUARE_LENGTH;
				Constants.badZone[1] = t.greenZone[1] * Constants.SQUARE_LENGTH;

				// green zone is defined by these (bottom-left and top-right) corners:
				Constants.goodZone = new int[2];
				Constants.goodZone[0] = t.redZone[0] * Constants.SQUARE_LENGTH;
				Constants.goodZone[1] = t.redZone[1] * Constants.SQUARE_LENGTH;
				
			}

			// print out the transmission information to the LCD
			conn.printTransmission();
		}
	}

	/**
	 * initialization of inter-brick communication
	 */
	public static void commInit() {
		// set master brick's friendly name
		RS485.setName("Master");

		String receiver = "Slave";

		LCD.drawString("Press to begin", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();

		// connect with slave brick
		NXTComm.connect(receiver, true);
	}

}
