package official;

import lejos.nxt.UltrasonicSensor;

/**
 * Instance of an UltrasonicSensor. Conserve both raw and filtered data
 * retrieved from the UltrasonicSensor
 * 
 * @author Fran�ois
 * 
 */
public class USPoller {

	// class variables
	/**
	 * physicially implemented US sensor
	 */
	public UltrasonicSensor us;

	/**
	 * lock object used for atomic access
	 */
	private Object lock = new Object();

	/**
	 * determines the number of readings to be held in rawData and filteredData
	 * at a time
	 */
	public final int SAMPLE_SIZE;
	/**
	 * the number of derivatives stored in 'derivatives
	 */
	public final int NUM_OF_DERIVATIVES;
	/**
	 * array that will hold raw data from sensors
	 */
	private int[] rawData;
	/**
	 * array of sensor readings in which the outliers have been removed
	 */
	private int[] filteredData;
	/**
	 * array that will hold NUMBER_OF_DERIVATIVES consecutive values of discrete
	 * diff.
	 */
	private int[] derivatives;

	/**
	 * index used for rawData
	 */
	private int index;

	// constructor
	public USPoller(UltrasonicSensor us, int sample_size, int num_of_derivatives) {
		this.us = us;
		SAMPLE_SIZE = sample_size;
		NUM_OF_DERIVATIVES = num_of_derivatives;

		// initialize all data arrays
		rawData = new int[SAMPLE_SIZE];
		filteredData = new int[SAMPLE_SIZE];
		derivatives = new int[NUM_OF_DERIVATIVES];

		this.us.setMode(UltrasonicSensor.MODE_PING);
	}

	/**
	 * get raw data from the UltrasonicSensor and store in rawData
	 */
	public void collectRawData() {
		// if sample full, loop to index 0
		if (index == SAMPLE_SIZE) {
			index = 0;
		}
		us.ping();
		setRawDataPoint(us.getDistance(), index);
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

		int[] temp = getDerivativeArray();

		for (int i = 0; i < NUM_OF_DERIVATIVES; i++) {
			if (temp[i] <= threshold) {
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

		int[] temp = getDerivativeArray();

		for (int i = 0; i < NUM_OF_DERIVATIVES; i++) {
			if (temp[i] >= threshold) {
				return true;
			}
		}

		return false;
	}

	// accessors
	/**
	 * atomic retrieval of raw data at specified index
	 * 
	 * @param index
	 * @return value at index
	 */
	public int getRawData(int index) {
		synchronized (lock) {
			return rawData[index];
		}
	}

	/**
	 * atomic retrieval of raw data array
	 * 
	 * @return array of raw data
	 */
	public int[] getRawDataArray() {
		synchronized (lock) {
			return rawData;
		}
	}

	/**
	 * atomic retrieval of filtered data at specified index
	 * 
	 * @param index
	 * @return value at index
	 */
	public int getfilteredDataPoint(int index) {
		synchronized (lock) {
			return filteredData[index];
		}
	}

	/**
	 * get the value of the last filtered data point to have been added to
	 * filteredData. The index of this data point corresponds approximately to the value of
	 * 'this.index'.
	 * 
	 * @return data point
	 */
	public int getLatestFilteredDataPoint() {
		synchronized (lock) {
			return filteredData[this.index];
		}
	}

	/**
	 * atomic retrieval of filtered data array
	 * 
	 * @return array of median-filtered data
	 */
	public int[] getfilteredDataArray() {
		synchronized (lock) {
			return filteredData;
		}
	}

	/**
	 * atomic retrieval of derivative at specified index
	 * 
	 * @param index
	 * @return value at index
	 */
	public int getDerivative(int index) {
		synchronized (lock) {
			return derivatives[index];
		}
	}

	/**
	 * atomic retrieval of derivative array
	 * 
	 * @return array of derivatives
	 */
	public int[] getDerivativeArray() {
		synchronized (lock) {
			return derivatives;
		}
	}

	// mutators

	/**
	 * atomic set of raw data at specified index
	 * 
	 * @param value
	 *            - data point
	 * @param index
	 *            - array index
	 */
	public void setRawDataPoint(int value, int index) {
		synchronized (lock) {
			rawData[index] = value;
		}
	}

	/**
	 * atomic set of raw data array
	 * 
	 * @param array
	 *            - new array of raw data
	 */
	public void updateRawDataArray(int[] array) {
		synchronized (lock) {
			rawData = array;
		}
	}

	/**
	 * atomic set of median-filtered data at specified index
	 * 
	 * @param value
	 *            - data point
	 * @param index
	 *            - array index
	 */
	public void setFilteredDataPoint(int value, int index) {
		synchronized (lock) {
			filteredData[index] = value;
		}
	}

	/**
	 * atomic set of median-filtered-data array
	 * 
	 * @param array
	 *            - new array of raw data
	 */
	public void updateFilteredDataArray(int[] array) {
		synchronized (lock) {
			filteredData = array;
		}
	}

	/**
	 * atomic set of derivative at specified index
	 * 
	 * @param value
	 *            - value of derivative
	 * @param index
	 *            - array index
	 */
	public void setDerivativePoint(int value, int index) {
		synchronized (lock) {
			derivatives[index] = value;
		}
	}

	/**
	 * atomic set of derivatives array
	 * 
	 * @param array
	 *            - new array of derivatives
	 */
	public void updateDerivativesArray(int[] array) {
		synchronized (lock) {
			derivatives = array;
		}
	}
}
