package official;

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * control center for light sensor and us sensors used in ObjectDetection and
 * OdometryCorrection. Manages polling and filtering of data collected from
 * these sensors.
 * 
 * @author Francois Lemay
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
	 *            - polling period (miliseconds)
	 * @param detector
	 *            - object detection class
	 */
	public SensorController(OdometryCorrection odoCorr, LightPoller[] lp,
			USPoller[] up, int period, ObjectDetection detector) {

		this.odoCorr = odoCorr;

		this.lp = lp;
		this.up = up;
		
		// fill data arrays of sensors
		for(int i=0;i<20;i++){
			collectRawData(lp,up);
		}
		applyMedianFilter(lp, up);
		applyDerivativeFilter(lp, up);

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
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--odoCorr", 0, 2);
		}

		// run ObjectDetection if the class exists
		try {
			if (!ObjectDetection.isDetecting && detector != null) {
				detector.start();
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--detector", 0, 2);
		}
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
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--lp", 0, 2);
		}
		
		// collect data from us sensors if any exist
		try {
			if (lp != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].collectRawData();
				}
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("--up", 0, 2);
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
					lp[i].updateFilteredDataArray(DataFilter.medianFilter(lp[i]
							.getRawDataArray()));
				}
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("lp mFilter", 0, 2);
		}

		// filter data from us sensors if any exist
		try {
			if (up != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].updateFilteredDataArray(DataFilter.medianFilter(up[i]
							.getRawDataArray()));
				}
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("up mFilter", 0, 2);
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
					lp[i].updateDerivativesArray(DataFilter.derivativeFilter(lp[i]
							.getfilteredDataArray()));
				}
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("lp dFilter", 0, 2);
		}

		// filter data from us sensors if any exist
		try {
			if (up != null) {
				for (int i = 0; i < up.length; i++) {
					up[i].updateDerivativesArray(DataFilter.derivativeFilter(up[i]
							.getfilteredDataArray()));
				}
			}
		} catch (NullPointerException e) {
			// display debugging message
			LCD.clear();
			LCD.drawString("Null pointer in", 0, 0);
			LCD.drawString("SensorController", 0, 1);
			LCD.drawString("up dFilter", 0, 2);
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
