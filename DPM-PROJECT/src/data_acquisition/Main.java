package data_acquisition;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class Main {
	
	public static void main(String[] args){
		
		int PERIOD = 50, LR_SAMPLE = 7, BACK_SAMPLE = 7, LR_DERIVATIVES = 6, BACK_DERIVATIVES = 6;
		
		ColorSensor backS = new ColorSensor(SensorPort.S3);
		ColorSensor leftS = new ColorSensor(SensorPort.S1);
		ColorSensor rightS = new ColorSensor(SensorPort.S2);
		
		// set up light sensors
		LightSensor back = new LightSensor(backS, BACK_SAMPLE, BACK_DERIVATIVES);
		LightSensor left = new LightSensor(leftS, LR_SAMPLE, LR_DERIVATIVES);
		LightSensor right = new LightSensor(rightS, LR_SAMPLE, LR_DERIVATIVES);
		
		// set up light sensor controller
		LightSensorController cont = new LightSensorController(back, left, right, PERIOD);
		
		// start cont
		cont.startPolling();
		
		Thread.yield();
	}

}
