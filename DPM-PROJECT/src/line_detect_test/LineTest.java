package line_detect_test;

import lejos.nxt.ColorSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import official.Constants;
import official.LightPoller;
import official.Navigation;
import official.Odometer;
import official.SensorController;
import official.USPoller;

public class LineTest {

	public static void main(String[] args) {
		//RConsole.openUSB(0);

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

		// us poller
		USPoller bottom = new USPoller(bottomS, Constants.BOTT_SAMPLE,
				Constants.BOTT_DIFF);
		USPoller[] up = { bottom };

		// back light sensor
		ColorSensor backS = new ColorSensor(Constants.backSensorPort);

		// light poller
		LightPoller back = new LightPoller(backS, Constants.BACK_SAMPLE);
		LightPoller[] lp = { back };

		// sensor controller
		// no need for OdometryCorrection and ObjectDetection
		SensorController cont = new SensorController(null, lp, up,
			Constants.M_PERIOD, null);

		// start controller
		cont.startPolling();

		// move forward
		nav.setSpeeds(Navigation.FAST, Navigation.FAST);
		
		while(true){
			if(back.getLatestDerivative()<=Constants.GRIDLINE_THRES){
				Sound.twoBeeps();
			}
		}
	}
}
