package testing;

import official.Constants;
import official.USPoller;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.UltrasonicSensor;

/**
 * code to test robot's ability for identifying styrofoam blocks using two ultrasonic sensors. (one at top and another at bottom).
 * @author Francois
 *
 */
public class ObjectIDTesting {

	public static void main(String[] args) {
		
		// two front us sensors
		UltrasonicSensor leftS = new UltrasonicSensor(
				Constants.leftSensorPort);
		UltrasonicSensor rightS = new UltrasonicSensor(Constants.rightSensorPort);

		// us pollers
		USPoller left = new USPoller(leftS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		USPoller right = new USPoller(rightS, Constants.US_SAMPLE,
				Constants.M_PERIOD);
		
		do{
			LCD.drawString("left ", 0, 0);
			LCD.clear(8, 0, 5);
			LCD.drawInt(left.getLatestFilteredDataPoint(), 8, 0);
			LCD.drawString("right ",0,1);
			LCD.clear(8, 1, 5);
			LCD.drawInt(right.getLatestFilteredDataPoint(),8,1);
			Button.waitForAnyEvent(100);
		}while(true);
	}

}
