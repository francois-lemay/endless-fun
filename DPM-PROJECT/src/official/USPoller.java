package official;

import lejos.nxt.UltrasonicSensor;

/**
 * Instance of an UltrasonicSensor. Conserve both raw and filtered data
 * retrieved from the UltrasonicSensor
 * 
 * @author Francois
 * 
 */
public class USPoller {

	// class variables
	public UltrasonicSensor us;
	public final int SAMPLE_SIZE; /*
								 * determines the number of readings to be held
								 * in rawData[] at a time
								 */
	public final int NUM_OF_DERIVATIVES; /*
										 * the number of derivatives stored in
										 * 'derivatives
										 */

	public int[] rawData; // array that will hold raw data from sensors
	public int[] filteredReadings; // array of sensor readings in which
									// the outliers have been removed

	public int[] derivatives; /*
							 * array that will hold NUMBER_OF_DERIVATIVES
							 * consecutive values of discrete diff.
							 */
	// index used for rawData
	private int index;

	// constructor
	public USPoller(UltrasonicSensor us, int sample_size, int num_of_derivatives) {
		this.us = us;
		SAMPLE_SIZE = sample_size;
		NUM_OF_DERIVATIVES = num_of_derivatives;
		
		this.us.setMode(UltrasonicSensor.MODE_PING);
	}

	/**
	 * get raw data from the UltrasonicSensor and
	 * store in rawData
	 */
	public void getRawData() {
		// if sample full, loop to index 0
		if (index == SAMPLE_SIZE) {
			index = 0;
		}
		rawData[index] = us.getDistance();
		index++;
	}

	// data look-up methods

	/**
	 * search for a derivative value that is smaller or equal to threshold
	 * 
	 * @param threshold
	 *            - derivative threshold
	 * @return whether such a derivative exist
	 */
	public boolean searchForSmallerDerivative(int threshold) {

		for (int i = 0; i < NUM_OF_DERIVATIVES; i++) {
			if (derivatives[i] <= threshold) {
				return true;
			}
		}

		return false;
	}

	/**
	 * search for a derivative value that is larger or equal to threshold
	 * 
	 * @param threshold
	 *            - derivative threshold
	 * @return whether such a derivative exists
	 */
	public boolean searchForLargerDerivative(int threshold) {

		for (int i = 0; i < NUM_OF_DERIVATIVES; i++) {
			if (derivatives[i] >= threshold) {
				return true;
			}
		}

		return false;
	}
}