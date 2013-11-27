package official;

import java.io.IOException;

import deprecated.NXTComm;
import bluetooth.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
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
		
		LCD.drawString("Press to start", 0, 0);
		Button.waitForAnyPress();

		/*
		 * bluetooth initialization
		 */

		// bluetoothInit();

		// **************************************************************

		Constants.goodZone = new int[] { -1 * Constants.SQUARE_LENGTH,
				8 * Constants.SQUARE_LENGTH, 1 * Constants.SQUARE_LENGTH,
				9 * Constants.SQUARE_LENGTH };

		Constants.badZone = new int[] { 2 * Constants.SQUARE_LENGTH,
				4 * Constants.SQUARE_LENGTH, 3 * Constants.SQUARE_LENGTH,
				5 * Constants.SQUARE_LENGTH };

		// ***************************************************************

		/*
		 * hardware initialization
		 */

		// initialize block pick up first since it takes the longest
		LCD.clear();
		LCD.drawString("Connected to Slave", 0, 0);
		BlockPickUp.init();
		BlockPickUp.openClamp();
		BlockPickUp.raiseTo(BlockPickUp.IDLE);
		LCD.clear();

		// set up robot motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(
				Constants.leftMotorPort);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(
				Constants.rightMotorPort);

		// odometry
		Odometer odo = new Odometer(leftMotor, rightMotor);

		// navigation
		Navigation nav = new Navigation(odo);

		// light sensors
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);
		ColorSensor frontS = new ColorSensor(Constants.frontLightSensorPort);
		
		// light poller
		LightPoller back = new LightPoller(backS, Constants.LIGHT_SAMPLE,
				Constants.M_PERIOD);

		// three front us sensors
		UltrasonicSensor leftS = new UltrasonicSensor(Constants.leftSensorPort);
		UltrasonicSensor rightS = new UltrasonicSensor(
				Constants.rightSensorPort);

		// us pollers
		USPoller left = new USPoller(leftS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		USPoller right = new USPoller(rightS, Constants.US_SAMPLE,
				Constants.M_PERIOD);

		// odometry correction
		// OdometryCorrection odoCorr = new OdometryCorrection(odo, back);

		/*
		 * US LOCALIZATION
		 */
				
		  //set up us localization 
//		USLocalizer usLoc = new USLocalizer(odo,
//		  nav, left, USLocalizer.LocalizationType.RISING_EDGE);
		  
		  // do us localization
//		usLoc.doLocalization();
		
//		Button.waitForAnyPress();
		
		/*
		 * LIGHT LOCALIZATION
		 */
		
		  // set up light localization
//		LightLocalizer lightLoc = new
//		  LightLocalizer(odo, nav, back);
		  
		  // do light localization
//		lightLoc.doLocalization();
		 

//		Button.waitForAnyPress();
		
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

		// set robot's destination
		Constants.robotDest[0] = Constants.goodZone[0];
		Constants.robotDest[1] = Constants.goodZone[1];

		// initialize boolean
		String status = "";

		// travel to construction zone while detecting objects
		do {

			// turn towards destination point
			nav.turnTowards(Constants.robotDest[0]+15, Constants.robotDest[1]+15);

			// move forward with goFwdUS
			status = nav.goFwdCaution(50, Navigation.FAST, left, right);

			// check various scenarios

			if (status.equals("obstacle")) {

				Sound.beepSequence();

				// locate object and turn towards it
				sweep(odo, nav,left,right);
				
				// identify object
				int code = identify(nav,frontS);
				
				// act upon result of identification
				switch(code){
				case 1:
					avoidObstacle(nav,left,right);
					break;
				case 2:
					getFoam(nav);
					break;
				case 3:
					//do nothing
					break;
				}	

				// execute if robot has reached its destination
			} else if (status.equals("destination")) {

				Sound.beepSequenceUp();
				LCD.clear();
				LCD.drawString("destination reached", 0, 0);
				LCD.drawString("...", 0, 2);
				LCD.drawString("press to exit", 0, 3);
				
				Button.waitForAnyPress();
				exit();
				
				// execute if too close to enemy zone
			} else if (status.equals("enemy")) {

				// avoid enemy's zone
				Sound.buzz();
				LCD.clear();
				LCD.drawString("too close to", 0, 0);
				LCD.drawString("enemy", 0, 1);
				LCD.drawString("press to exit", 0, 3);
				Button.waitForAnyPress();
				exit();
				
				//avoidEnemyZone();
			}

			// no obstacle encountered. repeat until destination reached

		} while (blocks<5);

		/*
		 * end program
		 */
		Sound.beepSequence();
		exit();
	}
	
	/**
	 * identify obstacle
	 * @param censor - color sensor at front of robot
	 * @return result of identification (1=obstacle, 2=foam, 3=no obstacle)
	 */
	public static int identify(Navigation nav, ColorSensor censor){
		
		int redlightValue = 0;
	    int bluelightValue = 0;
	    long startTime = 0;

		// approach object
		nav.setSpeeds(Navigation.FAST, Navigation.FAST);
		
		startTime = System.currentTimeMillis();
		
		do{
			ColorSensor.Color vals = censor.getColor();
		    redlightValue = vals.getRed();
		    if(System.currentTimeMillis()-startTime > 3000){
		    	break;
		    }
		}while(redlightValue < 34);
		
		// stop motors
		nav.stopMotors();
		
		// proceed to identification
		ColorSensor.Color vals = censor.getColor();
	    bluelightValue = vals.getBlue();
	    redlightValue = vals.getRed();
		
		if(redlightValue >= 35){
			int error = Math.abs(bluelightValue - redlightValue);
			
			if(error >25){
				// is obstacle
				return 1;
			}else{
				// is foam
				return 2;
			}
			
		}else{
			// no object detected
			return 3;
		}
	}
	
	/**
	 * avoid obstacle
	 */
	public static void avoidObstacle(Navigation nav, USPoller left, USPoller right){
		
		// back up a little bit
		nav.moveForwardBy(-10, Navigation.FAST);
		
		// check if right is clear
		nav.rotateBy(-90, false, Navigation.FAST);
		// give time to update readings
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		if (left.getLatestFilteredDataPoint() > 40
				&& right.getLatestFilteredDataPoint() > 40) {
			nav.moveForwardBy(35, Navigation.FAST);
		} 
		// otherwise rotate to the left
		else{
			nav.rotateBy(180, false, Navigation.FAST);
			nav.moveForwardBy(35, Navigation.FAST);
		}
		
		// turn towards destination
		nav.turnTowards(Constants.robotDest[0], Constants.robotDest[1]);

	}
	
	/**
	 * locate object and turn towards it
	 * @param nav - navigation class
	 * @param left - us sensor
	 * @param right - us sensor
	 */
	public static void sweep(Odometer odo, Navigation nav, USPoller left, USPoller right) {

		double angL = 0;
		double angR = 0;

		if (left.getLatestFilteredDataPoint() < 30
				&& right.getLatestFilteredDataPoint() < 30) {
			
			// object is centered, do nothing
			// do nothing
			
		} else{
			
			// rotate to left and latch angle
			nav.setSpeeds(-Navigation.FAST, Navigation.FAST);
			
			do {
				// do nothing
			} while (left.getLatestFilteredDataPoint() < 30);
			nav.stopMotors();
			angL = odo.getAng();
			
			// rotate to right and latch angle
			nav.setSpeeds(Navigation.FAST, -Navigation.FAST);
			do {
				// do nothing
			} while (right.getLatestFilteredDataPoint() < 30);
			nav.stopMotors();
			angR = odo.getAng();
		}
		
		// find average of the two angles
		double ang1 = Math.abs(angL-angR)/2;
		double ang2 = Math.abs(angL-angR+360)/2;
		
		nav.rotateBy(Math.min(ang1,ang2), false, Navigation.FAST);
		
	}
	
	/**
	 * get foam block
	 */
	public static void getFoam(Navigation nav){
		
		// back up a little to pick up block
		nav.moveForwardBy(-5, Navigation.FAST);
		
		// do stuff to pick up foam block
		BlockPickUp.openClamp();
		BlockPickUp.raiseTo(BlockPickUp.MIN_HEIGHT);
		BlockPickUp.closeClamp();
		BlockPickUp.raiseTo(BlockPickUp.IDLE);
		
		// increment number of foam blocks
		blocks++;
		
	}
	
	/**
	 * avoid enemy's zone
	 */
	public static void avoidEnemyZone(){
		
		// do stuff to avoid enemy's zone
		
	}
	
	/**
	 * exit program. lower lift before exiting, however.
	 */
	public static void exit(){
		BlockPickUp.raiseTo(BlockPickUp.MIN_HEIGHT);
		BlockPickUp.openClamp();
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
				Constants.badZone[0] = t.redZone[0] * Constants.SQUARE_LENGTH; //x1
				Constants.badZone[1] = t.redZone[1] * Constants.SQUARE_LENGTH; //y1
				Constants.badZone[2] = t.redZone[2] * Constants.SQUARE_LENGTH; //x2
				Constants.badZone[3] = t.redZone[3] * Constants.SQUARE_LENGTH; //y2

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
