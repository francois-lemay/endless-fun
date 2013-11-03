package official;

import lejos.nxt.ColorSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * contains main method
 * @author Francois
 *
 */
public class Main {
	
	public static void main(String[] args){
		
		int PERIOD = 50, LR_SAMPLE = 7, BACK_SAMPLE = 7, LR_DERIVATIVES = 6, BACK_DERIVATIVES = 6;
		
		// motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);
		
		// odometry
		Odometer odo = new Odometer(leftMotor,rightMotor);
		OdometryCorrection odoCorr = new OdometryCorrection(odo);
		
		// sensors
		UltrasonicSensor leftS = new UltrasonicSensor(SensorPort.S1);
		ColorSensor rightS = new ColorSensor(SensorPort.S2);
		ColorSensor backS = new ColorSensor(SensorPort.S3);

		// light pollers
		LightPoller back = new LightPoller(backS, BACK_SAMPLE, BACK_DERIVATIVES);
		USPoller left = new USPoller(leftS, LR_SAMPLE, LR_DERIVATIVES);
		LightPoller right = new LightPoller(rightS, LR_SAMPLE, LR_DERIVATIVES);
		
		// sensor controller
		SensorController cont = new SensorController(odoCorr, back, left, right, PERIOD);
		
		// start controller
		cont.startPolling();
		
		Thread.yield();
		}

}
