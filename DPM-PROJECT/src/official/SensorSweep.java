package official;

import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * concurrent thread used to make an ultrasonic pivot regularly on a motor.
 * this is used to widen the scope of the ultrasonic sensor's field of view
 * @author Francois
 * @deprecated
 *
 */
public class SensorSweep implements TimerListener {
	
	/**
	 * sensor Motor
	 */
	NXTRegulatedMotor sensorMotor;
	/**
	 * timer
	 */
	Timer timer;
	/**
	 * period for timer (in miliseconds)
	 */
	private final int PERIOD = 1500;
	/**
	 * dummy variable used for timedOut()
	 */
	private int foo;
	
	/**
	 *constructor
	 * @param sensorMotor - motor for top us sensor
	 */
	public SensorSweep(NXTRegulatedMotor sensorMotor){
		
		this.sensorMotor = sensorMotor;
		
		// initialize foo
		foo = 1;
		
		// set up timer
		timer = new Timer(PERIOD,this);
	}
	
	/**
	 * main thread
	 */
	public void timedOut() {

		if (!ObjectDetection.objectDetected) {

			sensorMotor.rotateTo(30 * foo, false);

			// change sign for next timedOut
			foo = -1 * foo;
		}
	}
	
	
	/**
	 * start timer
	 */
	public void start(){
		timer.start();
	}
	/**
	 * stop timer
	 */
	public void stop(){
		timer.stop();
	}
}

