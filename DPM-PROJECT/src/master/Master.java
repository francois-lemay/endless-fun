package master;

import official.LightPoller;
import official.NXTComm;
import official.Odometer;
import official.OdometryCorrection;
import official.SensorController;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RS485;

/**
 * master brick's main class
 */
public class Master {
	
	// class variables
	
	public static void main(String[] args){
		
		/*
		 * inter-brick communication initialization
		 */
		
		// set master brick's friendly name
		RS485.setName("Master");
		
		String receiver = "Slave";
		
		LCD.drawString("Press to begin", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();
		
		// connect with slave brick
		NXTComm.connect(receiver, true);

		// write to DOS
		LCD.clear();
		LCD.drawString("Press to write to DOS", 0, 0);
		Button.waitForAnyPress();
		NXTComm.write(9);
		
		/*
		 * localization initialization
		 */
		
		
		
		
		/*
		 * main program initialization
		 */
		
		
		
		// polling frequency for SensorController 
		int PERIOD = 50;
		
		// sample sizes
		int BACK_SAMPLE = 7;
		
		// derivative sample sizes
		int BACK_DIFF = 6;
				
		// set up motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);
		
		// set up odometry
		Odometer odo = new Odometer(leftMotor,rightMotor);
		OdometryCorrection odoCorr = new OdometryCorrection(odo);
		
		// back light sensor
		ColorSensor backS = new ColorSensor(SensorPort.S1);
		
		// light poller
		LightPoller back = new LightPoller(backS, BACK_SAMPLE, BACK_DIFF);
		LightPoller[] lp = {back};

		
		// sensor controller
		//SensorController cont = new SensorController(odoCorr, lp, null, PERIOD, detector);
		
		
		
		
		
		
		
		/*
		 * end communication with Slave
		 */
		
		LCD.drawString("Press to disconnect", 0, 0);
		Button.waitForAnyPress();
		NXTComm.disconnect();
		
		Button.waitForAnyPress();
	}

}
