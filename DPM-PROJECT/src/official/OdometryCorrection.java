package official;

/**
 * odometry correction. Assumes the minimum x and y positions to be x = y = -30.
 * The first gridlines will therefore intersect at (0,0)
 * 
 * @author Francois
 * 
 */
public class OdometryCorrection extends Thread {

	// class variables

	/**
	 * robot's odometer
	 */
	private Odometer odo;

	/**
	 * light poller
	 */
	private LightPoller lp;

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
	 * threshold derivative value used for gridline detection
	 */
	public final int GRIDLINE_THRESH = Constants.GRIDLINE_THRES;

	/**
	 * distance between back light sensor and center of wheel base (in
	 * centimeters)
	 */
	private final double DIST = Constants.BACK_SENSOR_DIST;

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

		this.lp = lp;

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

	}

	/**
	 * main thread
	 */
	public void run() {

		// correct odometry if line gridline has been crossed
		if (isLineDetected()) {
			correctPosition();
		}
	}

	/**
	 * correct robot positions through Odometer.
	 */
	public void correctPosition() {

		boolean updateX = false;
		double[] position = new double[3];

		// get actual position and heading
		odo.getPosition(position);
		double x = position[0];
		double y = position[1];
		double heading = position[2];

		// TODO
		// determine which line has been crossed

		
		// update x or y coord. accordingly

		odo.setPosition(position, new boolean[] { updateX, !updateX, false });

	}

	/**
	 * determine if a grid line has been crossed
	 * 
	 * @return - true or false
	 */
	public boolean isLineDetected() {
		if (lp.searchForSmallerDerivative(GRIDLINE_THRESH)) {
			return true;
		}
		return false;
	}

}
