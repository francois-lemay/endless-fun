package master;

import bluetooth.*;
import official.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RS485;

/**
 * master brick's main class
 * @author François Lemay
 */
public class Master {
	
	// class variables
	public static int blocks = 0;

	/**
	 * main program thread
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * bluetooth initialization
		 */

		bluetoothInit();

		/*
		 * inter-brick communication initialization
		 */
		commInit();

		// write to DOS
		/*
		 * LCD.clear(); LCD.drawString("Press to write to DOS", 0, 0);
		 * Button.waitForAnyPress(); NXTComm.write(9);
		 */

		/*
		 * hardware initialization
		 */

		// set up motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(Constants.leftMotorPort);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(Constants.rightMotorPort);

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, Constants.BACK_SAMPLE, Constants.M_PERIOD);
		LightPoller[] lp = { back };

		// two front us sensors
		UltrasonicSensor bottomS = new UltrasonicSensor(Constants.bottomSensorPort);
		UltrasonicSensor topS = new UltrasonicSensor(Constants.topSensorPort);

		// us pollers
		USPoller bottom = new USPoller(bottomS, Constants.BOTT_SAMPLE,Constants.M_PERIOD);
		USPoller top = new USPoller(topS, Constants.TOP_SAMPLE, Constants.M_PERIOD);
		USPoller[] up = new USPoller[2];
		up[Constants.bottomUSPollerIndex] = bottom;
		up[Constants.topUSPollerIndex] = top;

		// odometry correction
		OdometryCorrection odoCorr = new OdometryCorrection(odo, back);

		// object detection
		ObjectDetection detector = new ObjectDetection(null, up, true);
		
		// obstacle avoidance

		// sensor controller
		SensorController cont = new SensorController(odoCorr, lp, up,
				Constants.M_PERIOD, detector);

		// start controller
		cont.startPolling();

		/*
		 * us localization
		 */

		// set up us localization
		USLocalizer usLoc = new USLocalizer(odo, nav, bottom,
				USLocalizer.LocalizationType.RISING_EDGE);

		// do us localization
		usLoc.doLocalization();

		/*
		 * LIGHT LOCALIZATION
		 */

		// set up light localization
		LightLocalizer lightLoc = new LightLocalizer(odo, nav, back);

		// do light localization
		lightLoc.doLocalization();
		
		/*
		 * TOWER BUILDING
		 */
		TowerBuilding builder = new TowerBuilding(nav);
		
		/*
		 * 
		 */

		
		/*
		 * main program loop
		 */
		
		// find one block
		while(blocks<1){
			
			// insert code
			
		}
		// go deposit block(s) to construction zone
		builder.deliverTower();
		

		/*
		 * end communication with Slave.
		 * &
		 * end program
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
			// green zone is defined by these (bottom-left and top-right)
			// corners:
			Constants.greenZone = t.greenZone;

			// red zone is defined by these (bottom-left and top-right) corners:
			Constants.redZone = t.redZone;

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
