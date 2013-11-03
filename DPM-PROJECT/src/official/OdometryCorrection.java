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
	 * grid width (i.e. the number of squares along x-axis)
	 */
	int grid_width = 12;

	/**
	 * grid length (i.e. the number of squares along y-axis)
	 */
	int grid_length = 12;

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
	private final int SQUARE_LENGTH = 30;

	
	// constructor
	public OdometryCorrection(Odometer odoCorr){
		this.odo = odo;
		
		x_lines = new int[grid_width];
		y_lines = new int[grid_length];
		
		x_lines[0] = 0;
		y_lines[0] = 0;
		
		// define x-coord of all vertical grid lines
		for(int i=1; i<x_lines.length-1;i++){
			x_lines[i] = x_lines[i-1] + SQUARE_LENGTH;
		}
		
		// define y-coord of all horizontal grid lines
		for(int i=1;i<y_lines.length-1;i++){
			y_lines[i] = y_lines[i-1] + SQUARE_LENGTH;
		}
		
	}
	
	/**
	 * main thread
	 */
	public void run(){
		
		correctPosition();
	}

	/**
	 * correct robot positions through Odometer.
	 */
	public void correctPosition() {

		// get actual position and heading

		// determine which line has been crossed

		// update x or y coord. accordingly

	}
	
}
