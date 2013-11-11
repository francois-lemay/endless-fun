package official;

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
	private Timer timer;
	private final int PERIOD;

	private LightPoller[] lp = new LightPoller[3];
	private USPoller[] up = new USPoller[2];

	private OdometryCorrection odoCorr;
	private ObjectDetection detector;

	/**
	 * threshold derivative value used for gridline detection
	 */
	private final int GRIDLINE_THRESH = -100;

	// constructor
	public SensorController(OdometryCorrection odoCorr, LightPoller[] lp,
			USPoller[] up, int period, ObjectDetection detector) {

		this.odoCorr = odoCorr;

		this.lp[0] = lp[0]; // bottom
		this.lp[1] = lp[1]; // left
		this.lp[2] = lp[2]; // right

		this.up[0] = up[0]; // bottom
		this.up[1] = up[1]; // top

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

		// apply derivative filter to back sensor and us sensor only
		applyDerivativeFilter(new LightPoller[] { lp[0] }, up);

		// run OdometryCorrection if back sensor detected a line
		if (lp[0].searchForSmallerDerivative(GRIDLINE_THRESH)) {
			odoCorr.start();
		}

		// run ObjectDetection
		if (!ObjectDetection.isDetecting) {
			detector.start();
		}
	}

	/**
	 * collect raw data from every sensor
	 * 
	 * @param ls
	 *            - array of LightSensor
	 */
	private void collectRawData(LightPoller[] ls, USPoller[] us) {

		for (int i = 0; i < ls.length; i++) {
			ls[i].collectRawData();
		}

		for (int i = 0; i < us.length; i++) {
			us[i].collectRawData();
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
	private void applyMedianFilter(LightPoller[] ls, USPoller[] us) {

		for (int i = 0; i < ls.length; i++) {
			ls[i].updateFilteredDataArray(DataFilter.medianFilter(ls[i]
					.getRawDataArray()));
		}

		for (int i = 0; i < us.length; i++) {
			us[i].updateFilteredDataArray(DataFilter.medianFilter(us[i]
					.getRawDataArray()));
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
	private void applyDerivativeFilter(LightPoller[] ls, USPoller[] us) {

		for (int i = 0; i < ls.length; i++) {
			ls[i].updateDerivativesArray(DataFilter.derivativeFilter(ls[i]
					.getfilteredDataArray()));
		}

		for (int i = 0; i < us.length; i++) {
			us[i].updateDerivativesArray(DataFilter.derivativeFilter(us[i]
					.getfilteredDataArray()));
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
