package testing;

import official.Constants;
import official.USPoller;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.UltrasonicSensor;

public class ObjectIDTesting {

	public static void main(String[] args) {

		// two front us sensors
		UltrasonicSensor bottomS = new UltrasonicSensor(
				Constants.bottomSensorPort);
		UltrasonicSensor topS = new UltrasonicSensor(Constants.topSensorPort);

		// us pollers
		USPoller bottom = new USPoller(bottomS, Constants.BOTT_SAMPLE,
				Constants.M_PERIOD);
		USPoller top = new USPoller(topS, Constants.TOP_SAMPLE,
				Constants.M_PERIOD);
		
		do{
			LCD.drawString("bottom ", 0, 0);
			LCD.clear(8, 0, 5);
			LCD.drawInt(bottom.getLatestFilteredDataPoint(), 8, 0);
			LCD.drawString("top ",0,1);
			LCD.clear(8, 1, 5);
			LCD.drawInt(top.getLatestFilteredDataPoint(),8,1);
			Button.waitForAnyEvent(100);
		}while(true);
	}

}
