package official;

import lejos.nxt.ColorSensor;


/**
 * Instance of a ColorSensor. Conserve both raw and filtered data retrieved from the ColorSensor.
 * @author François
 *
 */
public class LightPoller {

	// class variables
	
	/**
	 * physical implementation of the color sensor
	 */
	public ColorSensor ls;
	
	/**
	 * lock object used for atomic access
	 */
	private Object lock = new Object();
	
	/**
	 * determines the number of readings to be held in rawData[] at a time
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
	 * array that will hold NUMBER_OF_DERIVATIVES consecutive values of discrete diff.
	 */
	private int[] derivatives;
	
	/**
	 * index used for rawData
	 */
	private int index;

	
	// constructor
	public LightPoller(ColorSensor ls, int sample_size, int num_of_derivatives) {
		
		this.ls = ls;
		
		// avoid null pointer exceptions
		if (sample_size == 0) {
			SAMPLE_SIZE = 1;
		} else {
			SAMPLE_SIZE = sample_size;
		}
		if (num_of_derivatives == 0) {
			NUM_OF_DERIVATIVES = 1;
		} else {
			NUM_OF_DERIVATIVES = num_of_derivatives;
		}
		
		// turn ON floodlight
		ls.setFloodlight(true);
	}
	
	/**
	 * read light value from the light sensor
	 * and store into rawData
	 */
	public void collectRawData(){
		// if sample full, loop to index 0
		if(index == SAMPLE_SIZE){
			index = 0;
		}
		setRawDataPoint(ls.getNormalizedLightValue(),index);
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
	 * @param index
	 * @return value at index
	 */
	public int getRawData(int index){
		synchronized (lock) {
			return rawData[index];
		}
	}
	/**
	 * atomic retrieval of raw data array
	 * @return array of raw data
	 */
	public int[] getRawDataArray(){
		synchronized (lock) {
			return rawData;
		}
	}
	
	/**
	 * atomic retrieval of filtered data at specified index
	 * @param index
	 * @return value at index
	 */
	public int getfilteredDataPoint(int index){
		synchronized (lock) {
			return filteredData[index];
		}
	}
	/**
	 * atomic retrieval of filtered data array
	 * @return array of median-filtered data
	 */
	public int[] getfilteredDataArray(){
		synchronized (lock) {
			return filteredData;
		}
	}
	
	/**
	 * atomic retrieval of derivative at specified index
	 * @param index
	 * @return value at index
	 */
	public int getDerivative(int index){
		synchronized (lock) {
			return derivatives[index];
		}
	}
	/**
	 * atomic retrieval of derivative array
	 * @return array of derivatives
	 */
	public int[] getDerivativeArray(){
		synchronized (lock) {
			return derivatives;
		}
	}
	
	// mutators
	
	/**
	 * atomic set of raw data at specified index
	 * @param value - data point
	 * @param index - array index
	 */
	public void setRawDataPoint(int value, int index){
		synchronized (lock) {
			rawData[index] = value;
		}
	}
	/**
	 * atomic set of raw data array
	 * @param array - new array of raw data
	 */
	public void updateRawDataArray(int[] array){
		synchronized (lock) {
			rawData = array;
		}
	}
	
	/**
	 * atomic set of median-filtered data at specified index
	 * @param value - data point
	 * @param index - array index
	 */
	public void setFilteredDataPoint(int value, int index){
		synchronized (lock) {
			filteredData[index] = value;
		}
	}
	
	/**
	 * atomic set of median-filtered-data array
	 * @param array - new array of raw data
	 */
	public void updateFilteredDataArray(int[] array){
		synchronized (lock) {
			filteredData = array;
		}
	}
	
	/**
	 * atomic set of derivative at specified index
	 * @param value - value of derivative
	 * @param index - array index
	 */
	public void setDerivativePoint(int value, int index){
		synchronized (lock) {
			derivatives[index] = value;
		}
	}
	/**
	 * atomic set of derivatives array
	 * @param array - new array of derivatives
	 */
	public void updateDerivativesArray(int[] array){
		synchronized (lock) {
			derivatives = array;
		}
	}
	
}
