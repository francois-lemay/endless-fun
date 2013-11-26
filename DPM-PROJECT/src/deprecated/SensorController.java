package deprecated;

import official.DataFilter;
import official.LightPoller;
import official.ObjectDetection;
import official.OdometryCorrection;
import official.USPoller;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * control center for light sensor and us sensors used in ObjectDetection and
 * OdometryCorrection. Manages polling and filtering of data collected from
 * these sensors.
 * 
 * @author Francois Lemay
 * @deprecated
 * 
 */
public class SensorController implements TimerListener {

	// class variables
	/**
	 * timer used for thread being run at set frequency
	 */
	private Timer timer;
	/**
	 * timer period
	 */
	private final int PERIOD;
	/**
	 * light sensors
	 */
	private LightPoller[] lp;
	/**
	 * ultrasonic sensors
	 */
	private USPoller[] up;
	/**
	 * robot's odometry correction class
	 */
	private OdometryCorrection odoCorr;
	/**
	 * robot's object detection class
	 */
	private ObjectDetection detector;

	/**
	 * constructor
	 * 
	 * @param odoCorr
	 *            - robot's odometry correction class
	 * @param lp
	 *            - light sensors
	 * @param up
	 *            - us sensors
	 * @param period
	 *            - polling period (milliseconds)
	 * @param detector
	 *            - object detection class
	 */
	public SensorController(OdometryCorrection odoCorr, LightPoller[] lp,
			USPoller[] up, int period, ObjectDetection detector) {

		this.odoCorr = odoCorr;

		// copy all light pollers if applicable
		try {
			if (lp != null) {
				this.lp = new LightPoller[lp.length];

				for (int i = 0; i < lp.length; i++) {
					this.lp[i] = lp[i];
				}
			} else {
				this.lp = lp;
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("constructor", 0, 2);
			LCD.drawString("--lp", 0, 3);
			LCD.drawInt(lp.length, 0, 5);
			Button.waitForAnyPress();
		}

		// copy all us pollers if applicable
		try {
			if (up != null) {
				this.up = new USPoller[up.length];

				for (int i = 0; i < up.length; i++) {
					this.up[i] = up[i];
				}
			} else {
				this.up = null;
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("constructor", 0, 2);
			LCD.drawString("--up", 0, 3);
			Button.waitForAnyPress();
		}
		
		// set polling period
		PERIOD = period;

		this.detector = detector;

		// set up timer
		timer = new Timer(PERIOD, this);
		
	}

	/**
	 * main thread
	 */
	public void timedOut() {
/*
		try{
		// collect raw data from all sensors
		collectRawData(lp, up);

		// apply median filter to all sensors
		applyMedianFilter(lp, up);

		// apply derivative filter to all sensors
		applyDerivativeFilter(lp, up);

		// run OdometryCorrection if the class exists
		try {
			if (odoCorr != null) {
				odoCorr.start();
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--odoCorr", 0, 2);
			Button.waitForAnyPress();
		}

		// run ObjectDetection if the class exists
		try {
			if (!ObjectDetection.isDetecting && detector != null) {
				detector.start();
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--detector", 0, 2);
			Button.waitForAnyPress();
		}
		}catch(Exception e){
			LCD.drawString("wtf guys", 0, 0);
		}

		// ------------------------------------------------------------//
		// RConsole.println(""+up[0].getLatestFilteredDataPoint());
		// -----------------------------------------------------------//
		// ------------------------------------------------------------//
		// RConsole.println(""+lp[0].getLatestFilteredDataPoint());
		// -----------------------------------------------------------//
		// ------------------------------------------------------------//
		// RConsole.println(""+lp[0].getLatestDerivative());
		// -----------------------------------------------------------//
*/	
}

	/**
	 * collect raw data from every sensor
	 * 
	 * @param ls
	 *            - array of LightSensor
	 */
	private void collectRawData(LightPoller[] lp, USPoller[] up) {

		// collect data from light sensors if any exist
		try {
			if (lp != null) {
				for (int i = 0; i < lp.length; i++) {
					lp[i].collectRawData();
				}
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--lp", 0, 2);
			Button.waitForAnyPress();
		}

		// collect data from us sensors if any exist
		try {
			if (up != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].collectRawData();
				}
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--up", 0, 2);
			Button.waitForAnyPress();
		}

	}

	/**
	 * pass every sensor's raw data through median filter
	 * 
	 * @param ls
	 *            - light sensors
	 * @param us
	 *            - us sensors
	 */
	private void applyMedianFilter(LightPoller[] lp, USPoller[] up) {

		// filter data from light sensors if any exist
		try {
			if (lp != null) {
				for (int i = 0; i < lp.length; i++) {

					lp[i].addToFilteredData(DataFilter.medianFilter(lp[i]
							.getRawDataList()));

				}
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("lp mFilter", 0, 2);
			Button.waitForAnyPress();
		}

		// filter data from us sensors if any exist

		try {
			if (up != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].addToFilteredData(DataFilter.medianFilter(up[i]
							.getRawDataList()));
				}
			}
		} catch (Exception e) {
			// display debugging message LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("up mFilter", 0, 2);
			Button.waitForAnyPress();

		}

	}

	/**
	 * pass every sensor's raw data through a derivative filter
	 * 
	 * @param ls
	 *            - light sensor
	 * @param us
	 *            - us sensors
	 */
	private void applyDerivativeFilter(LightPoller[] lp, USPoller[] up) {

		// filter data from light sensors if any exist
		try {
			if (lp != null) {
				for (int i = 0; i < lp.length; i++) {
					lp[i].addToDerivatives(DataFilter.derivativeFilter(lp[i]
							.getFilteredDataList()));
				}
			}
		} catch (Exception e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("lp dFilter", 0, 2);
			Button.waitForAnyPress();

		}

		// filter data from us sensors if any exist try { if (up != null) {
		try {
			if (up != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].addToDerivatives(DataFilter.derivativeFilter(up[i]
							.getFilteredDataList()));
				}
			}
		} catch (Exception e) {
			// display debugging message LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("up dFilter", 0, 2);
			Button.waitForAnyPress();

		}
	}

	// helper methods

	/**
	 * start main thread
	 */
	public void startPolling() {
		timer.start();
	}

	/**
	 * stop main thread
	 */
	public void stopPolling() {
		timer.stop();
	}
}
