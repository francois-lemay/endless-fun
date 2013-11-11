package localization_testing;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import official.Navigation;
import official.Odometer;
import official.USPoller;

public class LocalizationTesting {
	
	public static void main(String[] args){
		
		NXTRegulatedMotor left = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor right = new NXTRegulatedMotor(MotorPort.B);
		
		Odometer odo = new Odometer(left,right);
		Navigation nav = new Navigation(odo);
		
		// us sensor
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		
		int SAMPLE_SIZE = 7;
		int NUM_DERIVATIVES = 5;
		
		// us poller
		USPoller up = new USPoller(us, SAMPLE_SIZE, NUM_DERIVATIVES);
		
		
	}

}
