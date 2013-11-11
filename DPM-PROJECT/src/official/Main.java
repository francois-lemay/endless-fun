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
		
		/**
		 * polling frequency for SensorController
		 */
		int PERIOD = 50;
		/**
		 * sample sizes
		 */
		int BOTT_SAMPLE = 7, TOP_SAMPLE = 7, LEFT_SAMPLE = 7, RIGHT_SAMPLE = 7, BACK_SAMPLE = 7;
		/**
		 * derivative sample sizes
		 */
		int  BOTT_DIFF = 6, TOP_DIFF = 6, LEFT_DIFF = 6, RIGHT_DIFF = 6, BACK_DIFF = 6;
		
		// motors
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);
		
		// odometry
		Odometer odo = new Odometer(leftMotor,rightMotor);
		OdometryCorrection odoCorr = new OdometryCorrection(odo);
		
		// light sensors
		ColorSensor leftS = new ColorSensor(SensorPort.S1);
		ColorSensor rightS = new ColorSensor(SensorPort.S2);
		
		//TODO change sensor.port
		ColorSensor backS = new ColorSensor(SensorPort.S2);
		
		// us sensors
		UltrasonicSensor bottomS = new UltrasonicSensor(SensorPort.S3);
		UltrasonicSensor topS = new UltrasonicSensor(SensorPort.S4);

		// light pollers
		LightPoller back = new LightPoller(backS, BACK_SAMPLE, BACK_DIFF);
		LightPoller left = new LightPoller(leftS, LEFT_SAMPLE, LEFT_DIFF);
		LightPoller right = new LightPoller(rightS, RIGHT_SAMPLE, RIGHT_DIFF);
		LightPoller[] lp = {back,left,right};
		
		// us pollers
		USPoller bottom = new USPoller(bottomS, BOTT_SAMPLE, BOTT_DIFF);
		USPoller top = new USPoller(topS, TOP_SAMPLE, TOP_DIFF);
		USPoller[] up = {bottom,top};

		// object detector
		ObjectDetection detector = new ObjectDetection(lp,up);
		
		// sensor controller
		SensorController cont = new SensorController(odoCorr, lp, up, PERIOD, detector);
		
		
		
		
		// start controller
		cont.startPolling();
		
		Thread.yield();
		}

}
