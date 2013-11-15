package official;

import java.util.ArrayList;
import java.util.List;
import lejos.nxt.ColorSensor;

/**
 * Instance of a ColorSensor. Conserve both raw and filtered data retrieved from
 * the ColorSensor.
 * 
 * @author François
 * 
 */
public class LightPoller {

	// class variables

	/**
	 * color sensor
	 */
	private ColorSensor ls;

	/**
	 * determines the number of readings to be held in rawData[] at a time
	 */
	private int NUM_SAMPLES;
	/**
	 * the number of derivatives stored in 'derivatives
	 */
	private int NUM_OF_DERIVATIVES = NUM_SAMPLES-1;
	/**
	 * ArrayList of integers that will hold raw data from sensors
	 */
	private List<Integer> rawData;
	/**
	 * array of sensor readings in which the outliers have been removed
	 */
	private List<Integer> filteredData;
	/**
	 * array that will hold NUMBER_OF_DERIVATIVES consecutive values of discrete
	 * diff.
	 */
	private List<Integer> derivatives;


	// constructor
	public LightPoller(ColorSensor ls, int num_samples) {

		this.ls = ls;

		// avoid null pointer exceptions
		if (num_samples <= 0) {
			NUM_SAMPLES = 1;
		} else {
			NUM_SAMPLES = num_samples;
		}
		
		// initialize all data
		this.rawData = new ArrayList<Integer>();
		this.filteredData = new ArrayList<Integer>();
		this.derivatives = new ArrayList<Integer>();
		
		// fill rawData list
		for(int i=0;i<NUM_SAMPLES;i++){
			rawData.add(ls.getRawLightValue());
		}
		
		// fill filteredData list
		for(int i=0;i<NUM_SAMPLES;i++){
			filteredData.add(DataFilter.medianFilter(rawData));
		}
		
		// fill derivatives
		int value = 0;
		for(int i=0;i<NUM_SAMPLES-1;i++){
			value = filteredData.get(i+1) - filteredData.get(i);
			derivatives.add(value);
		}
		
		
		// turn ON floodlight
		ls.setFloodlight(true);
	}

	/**
	 * get light value from the light sensor and add to rawData
	 */
	public void collectRawData() {

		// get light value
		int data = ls.getRawLightValue();
		
		// remove tail and add sample at head
		rawData.remove(0);
		rawData.add(data);

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
			if (derivatives.get(i) <= threshold) {
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
			if (derivatives.get(i) >= threshold) {
				return true;
			}
		}
		return false;
	}

	// accessors

	/**
	 * get address of rawData
	 * 
	 * @return address
	 */
	public List<Integer> getRawData() {
		return rawData;
	}

	/**
	 * get the value of the last filtered data point to have been added to
	 * filteredData.
	 * 
	 * @return data point
	 */
	public int getLatestFilteredDataPoint() {

		return filteredData.get(filteredData.size()-1);
	}

	/**
	 * get address of 'filteredData'
	 * 
	 * @return address
	 */
	public List<Integer> getfilteredData() {
		return filteredData;
	}

	/**
	 * get the value of the last derivative obtained
	 * 
	 * @return value of derivative
	 */
	public int getLatestDerivative() {

		return derivatives.get(derivatives.size()-1);
	}

	/**
	 * get address of 'derivatives'
	 * 
	 * @return address
	 */
	public List<Integer> getDerivativeArray() {
		return derivatives;
	}

	// mutators

	/**
	 * add data point to head of filteredData. Remove data point at its tail
	 * 
	 * @param value
	 *            - data point
	 */
	public void addToFilteredData(int value) {
		filteredData.remove(0);
		filteredData.add(value);
	}

	/**
	 * add value to head of derivatives. Remove element at its tail.
	 * 
	 * @param value
	 *            - value of derivative
	 */
	public void addToDerivatives(int value) {
		derivatives.remove(0);
		derivatives.add(value);
	}
}
