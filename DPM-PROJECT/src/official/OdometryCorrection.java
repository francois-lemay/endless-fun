package official;

/**
 * odometry correction. Assumes the minimum x and y positions to be x = y = -30.
 * The first gridlines will therefore intersect at (0,0).
 * 
 * the odometry correction algorithm used in this code assumes the light sensor to be positioned
 * at a fixed distance behind the center of the wheel base
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

		correctPosition();
	}

	/**
	 * correct robot positions through Odometer.
	 */
	public void correctPosition() {

		boolean[] update = {false,false,false};
		double[] position = new double[3];
		double xLS,yLS;
		int xMod,yMod, deltaMod;

		// get actual position and heading
		odo.getPosition(position);
		double x = position[0];
		double y = position[1];
		double heading = position[2];

		// Alex's algorithm:
		
		// determine which line has been crossed
		
		// calculate light sensor's position
		xLS = x-LS_DIST*Math.cos(heading);
		yLS = y-LS_DIST*Math.sin(heading);
		
		// apply modulo 30 operator
		xMod = (int) (xLS % 30);
		yMod = (int) (yLS % 30);
		
		// normalize results
		// values may be normalized to -ve numbers in order to be used as flags for later
		if(xMod > 15){
			xMod = xMod - 30;
		}
		if(yMod > 15){
			yMod = yMod - 30;
		}
		
		// compute deltaMod
		deltaMod = Math.abs(Math.abs(xMod)-Math.abs(yMod));
		
		// if robot is very close to intersection of gridlines
		if(deltaMod < LINE_CROSS_BW){
			x = x-xMod;
			y = y-yMod;
			update[0] = true;
			update[1] = true;
		}
		else if(Math.abs(xMod)<Math.abs(yMod)){
			x = x - xMod;
			update[0] = true;
		}
		else{
			y = y - yMod;
			update[1] = true;
		}
		
		// update x or y coord. accordingly
		odo.setPosition(position, update);

	}
}
