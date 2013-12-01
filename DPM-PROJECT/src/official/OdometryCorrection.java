package official;

import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * odometry correction class.  Detects gridlines to correct robot's position.
 * the odometry correction algorithm used in this code assumes the light sensor
 * to be positioned at a fixed distance behind the center of the wheel base
 * 
 * @author Francois
 * 
 */
public class OdometryCorrection implements TimerListener {

	// class variables

	/**
	 * robot's odometer
	 */
	private Odometer odo;
	/**
	 * timer
	 */
	private Timer timer;
	/**
	 * polling period
	 */
	private int period;
	/**
	 * back light sensor
	 */
	private LightPoller back;
	/**
	 * grid width (i.e. the number of squares along x-axis)
	 */
	int grid_width = Constants.GRID_WIDTH;

	/**
	 * grid length (i.e. the number of squares along y-axis)
	 */
	int grid_length = Constants.GRID_LENGTH;

	/**
	 * x-position of all vertical grid lines
	 */
	private int[] x_lines;

	/**
	 * y-position of all horizontal grid lines
	 */
	private int[] y_lines;

	/**
	 * length of one square (in centimeters)
	 */
	private final int SQUARE_LENGTH = Constants.SQUARE_LENGTH;

	/**
	 * distance between back light sensor and center of wheel base (in
	 * centimeters)
	 */
	private final double LS_DIST = Constants.BACK_SENSOR_DIST;
	/**
	 * allowed bandwidth for odometry correction close to gridline intersections
	 */
	private final static double LINE_CROSS_BW = Constants.LINE_CROSS_BW;

	/**
	 * constructor
	 * 
	 * @param odo
	 *            - robot's odometer
	 * @param lp
	 *            - light sensors
	 * @param up
	 *            - us sensors
	 */
	public OdometryCorrection(Odometer odo, LightPoller lp) {
		this.odo = odo;
		this.back = lp;

		x_lines = new int[grid_width];
		y_lines = new int[grid_length];

		x_lines[0] = 0;
		y_lines[0] = 0;

		// define x-coord of all vertical grid lines
		for (int i = 1; i < x_lines.length - 1; i++) {
			x_lines[i] = x_lines[i - 1] + SQUARE_LENGTH;
		}

		// define y-coord of all horizontal grid lines
		for (int i = 1; i < y_lines.length - 1; i++) {
			y_lines[i] = y_lines[i - 1] + SQUARE_LENGTH;
		}

		// set up timer
		period = 100;
		timer = new Timer(period, this);
	}

	/**
	 * main thread
	 */
	public void timedOut() {

		if (back.getLatestDerivative() < Constants.GRIDLINE_THRES) {
			//Sound.beep();
			correctPosition();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}

	}

	/**
	 * correct robot positions through Odometer.
	 */
	public void correctPosition() {

		boolean[] update = { false, false, false };
		double[] position = new double[3];
		double xLS, yLS;
		int xMod, yMod, deltaMod;

		// get actual position and heading
		odo.getPosition(position);
		double x = position[0];
		double y = position[1];
		double heading = position[2];

		// determine which line has been crossed

		// calculate light sensor's position
		xLS = x - LS_DIST * Math.cos(heading);
		yLS = y - LS_DIST * Math.sin(heading);

		// apply modulo 30 operator
		xMod = Math.abs((int) (xLS % 30));
		yMod = Math.abs((int) (yLS % 30));

		// normalize results for comparison
		if (xMod > 15) {
			xMod = 30 - xMod;
		}
		if (yMod > 15) {
			yMod = 30 - yMod;
		}

		// compute deltaMod
		deltaMod = Math.abs(Math.abs(xMod) - Math.abs(yMod));

		if (deltaMod < LINE_CROSS_BW) {
			// correct both xLS and yLS
			xLS = (Math.ceil(xLS / 10.0)) * 10;
			yLS = (Math.ceil(yLS / 10.0)) * 10;

			// correct x and y
			x = xLS + LS_DIST * Math.cos(heading);
			y = yLS + LS_DIST * Math.sin(heading);

			// set update booleans
			update[0] = true;
			update[1] = true;

		} else if (xMod < yMod) {
			// correct xLS only
			xLS = (Math.rint(xLS / 10.0)) * 10;

			// correct x only
			x = xLS + LS_DIST * Math.cos(heading);

			// set update booleans
			update[0] = true;
		} else {
			// correct yLS only
			yLS = (Math.rint(yLS / 10.0)) * 10;

			// correct y only
			y = yLS + LS_DIST * Math.sin(heading);

			// set update booleans
			update[1] = true;
		}

		// update position array
		position[0] = x;
		position[1] = y;

		// update robot's position
		odo.setPosition(position, update);
	}

	/**
	 * start timer
	 */
	public void start() {
		this.timer.start();
	}

	/**
	 * stop timer
	 */
	public void stop() {
		this.timer.stop();
	}
}
