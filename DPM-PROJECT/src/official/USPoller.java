package official;

import java.util.Arrays;

import lejos.nxt.UltrasonicSensor;

/**
 * instance of an UltrasonicSensor. Conserve both raw and filtered data
 * retrieved from the UltrasonicSensor
 * 
 * @author François
 * 
 */
public class USPoller {

	// class variables
	/**
	 * us sensor
	 */
	private UltrasonicSensor us;

	/**
	 * lock object used for atomic access
	 */
	private Object lock = Constants.theLock;

	/**
	 * determines the number of readings to be held in rawData and filteredData
	 * at a time
	 */
	private int SAMPLE_SIZE;
	/**
	 * the number of derivatives stored in 'derivatives
	 */
	private int NUM_OF_DERIVATIVES;
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
	private int indexR;
	/**
	 * index used for derivatives
	 */
	private int indexD;

	/**
	 * constructor
	 * 
	 * @param us
	 * @param sample_size
	 * @param num_of_derivatives
	 */
	public USPoller(UltrasonicSensor us, int sample_size, int num_of_derivatives) {

		this.us = us;

		// avoid null pointer exceptions
		if (sample_size <= 0) {
			SAMPLE_SIZE = 1;
		} else {
			SAMPLE_SIZE = sample_size;
		}
		if (num_of_derivatives <= 0) {
			NUM_OF_DERIVATIVES = 1;
		} else {
			NUM_OF_DERIVATIVES = num_of_derivatives;
		}

		// initialize all data
		this.rawData = new int[SAMPLE_SIZE];
		this.filteredData = new int[SAMPLE_SIZE];
		this.derivatives = new int[NUM_OF_DERIVATIVES];

		// initialize indices
		indexR = 0;
		indexD = 0;

		// set us sensor in single ping mode
		this.us.setMode(UltrasonicSensor.MODE_PING);
	}

	/**
	 * get raw data from the UltrasonicSensor and store in rawData
	 */
	public void collectRawData() {

		us.ping();
		setRawDataPoint(us.getDistance(), indexR);

		// set value of index
		synchronized (lock) {
			// if sample full, loop to index 0
			if ((indexR + 1) == SAMPLE_SIZE) {
				indexR = 0;
			} else {
				// increment index
				indexR++;
			}
		}
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

		int[] temp = (int[]) getDerivativeArray().clone();

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

		int[] temp = (int[]) getDerivativeArray().clone();

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
			return (int[]) rawData.clone();
		}
	}

	/**
	 * atomic retrieval of filtered data at specified index
	 * 
	 * @param index
	 * @return value at index
	 */
	public int getFilteredDataPoint(int index) {
		synchronized (lock) {
			return filteredData[index];
		}
	}

	/**
	 * get the value of the last filtered data point to have been added to
	 * filteredData. The index of this data point corresponds approximately to
	 * the value of 'this.index'.
	 * 
	 * @return data point
	 */
	public int getLatestFilteredDataPoint() {
		int index1 = -1;
		synchronized (lock) {
			index1 = this.indexR;
		}
		return filteredData[index1];
	}

	/**
	 * atomic retrieval of filtered data array
	 * 
	 * @return array of median-filtered data
	 */
	public int[] getfilteredDataArray() {
		synchronized (lock) {
			return (int[]) filteredData.clone();
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
	 * get the value of the last derivative obtained
	 * 
	 * @return value of derivative
	 */
	public int getLatestDerivative() {
		int data = derivatives[indexD];

		// set value of indexD
		if ((indexD + 1) == NUM_OF_DERIVATIVES) {
			indexD = 0;
		} else {
			indexD++;
		}
		return data;
	}

	/**
	 * atomic retrieval of derivative array
	 * 
	 * @return array of derivatives
	 */
	public int[] getDerivativeArray() {
		synchronized (lock) {
			return (int[]) derivatives.clone();
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
			rawData = Arrays.copyOf(array, SAMPLE_SIZE);
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
			filteredData = Arrays.copyOf(array, SAMPLE_SIZE);
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
			derivatives = Arrays.copyOf(array, NUM_OF_DERIVATIVES);
		}
	}
}
