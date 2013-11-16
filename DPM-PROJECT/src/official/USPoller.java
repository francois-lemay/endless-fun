package official;

import java.util.LinkedList;
import official.Constants.theLock;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * instance of an UltrasonicSensor. Conserve both raw and filtered data
 * retrieved from the UltrasonicSensor
 * 
 * @author François
 * 
 */
public class USPoller implements TimerListener {

	// class variables

	/**
	 * us sensor
	 */
	private UltrasonicSensor us;
	/**
	 * lock object
	 */
	private theLock lock = Constants.lockObject;
	/**
	 * timer
	 */
	private Timer timer;
	/**
	 * determines the number of readings to be held in rawData at a time
	 */
	private int NUM_SAMPLES;
	/**
	 * the number of derivatives stored in 'derivatives
	 */
//	private int NUM_OF_DERIVATIVES = NUM_SAMPLES - 1;
	/**
	 * ArrayList of integers that will hold raw data from sensors
	 */
	private LinkedList<Integer> rawData;
	/**
	 * integer list of sensor readings in which the outliers have been removed
	 */
	private LinkedList<Integer> filteredData;
	/**
	 * integer list that will hold NUM_OF_DERIVATIVES consecutive values of
	 * discrete diff.
	 */
	private int derivatives;

	// constructor
	public USPoller(UltrasonicSensor us, int num_samples, int period) {

		this.us = us;

		// avoid null pointer exceptions
		if (num_samples <= 0) {
			NUM_SAMPLES = 1;
		} else {
			NUM_SAMPLES = num_samples;
		}

		// initialize all data
		this.rawData = new LinkedList<Integer>();
		this.filteredData = new LinkedList<Integer>();
		//this.derivatives = new ArrayList<Integer>();

		// fill rawData list
		for (int i = 0; i < NUM_SAMPLES; i++) {
			synchronized (lock) {
				us.ping();
				rawData.add(us.getDistance());
			}
		}

		// fill filteredData list
		for (int i = 0; i < NUM_SAMPLES; i++) {
			synchronized (lock) {
				filteredData.add(DataFilter.medianFilter(rawData));
			}
		}

		// fill derivatives
		synchronized (lock) {
			int lastIndex = filteredData.size()-1;
			derivatives = filteredData.get(lastIndex) - filteredData.get(lastIndex-1);
		}
/*		int value = 0;
		for (int i = 0; i < NUM_OF_DERIVATIVES; i++) {
			synchronized (lock) {
				value = filteredData.get(i + 1) - filteredData.get(i);
				derivatives.add(value);
			}

		}
*/
		// set us sensor in ping mode
		us.setMode(UltrasonicSensor.MODE_PING);
		
		// set up timer
		timer = new Timer(period,this);
		
		// start timer
		timer.start();
	}
	
	/**
	 * main thread
	 */
	public void timedOut(){
		
		// stop timer to ensure following code gets completed
		timer.stop();
		
		// collect raw data
		collectRawData();
		
		// apply median filter
		addToFilteredData(DataFilter.medianFilter(
				getRawDataList()));
		
		// apply derivative filter
		addToDerivatives(DataFilter.derivativeFilter(getFilteredDataList()));
		
		// re-start timer
		timer.start();
		
	}
	

	/**
	 * get distance from the us sensor and add to rawData
	 */
	public void collectRawData() {

		// get distance
		us.ping();
		int data = us.getDistance();

		// remove tail and add sample at head
		synchronized (lock) {
			rawData.add(data);
			rawData.remove(0);

		}

	}

	// accessors
	
	/**
	 * get the value of the last raw data point to have been added to
	 * rawData.
	 * 
	 * @return data point
	 */
	public int getLatestRawDataPoint() {
		synchronized (lock) {
			int lastIndex = rawData.size()-1;
			return rawData.get(lastIndex);
		}
	}

	/**
	 * get address of rawData
	 * 
	 * @return address
	 */
	public LinkedList<Integer> getRawDataList() {
		synchronized (lock) {
			return rawData;
		}
	}

	/**
	 * get the value of the last filtered data point to have been added to
	 * filteredData.
	 * 
	 * @return data point
	 */
	public int getLatestFilteredDataPoint() {
		synchronized (lock) {
			int lastIndex = filteredData.size()-1;
			return filteredData.get(lastIndex);
		}
	}

	/**
	 * get address of 'filteredData'
	 * 
	 * @return address
	 */
	public LinkedList<Integer> getFilteredDataList() {
		synchronized (lock) {
			return filteredData;
		}
	}

	/**
	 * get the value of the last derivative obtained
	 * 
	 * @return value of derivative
	 */
	public int getLatestDerivative() {
		synchronized (lock) {
			//return derivatives.get(derivatives.size() - 1);
			return derivatives;
		}
	}

	// mutators

	/**
	 * add data point to head of filteredData. Remove data point at its tail
	 * 
	 * @param value
	 *            - data point
	 */
	public void addToFilteredData(int value) {
		synchronized (lock) {
			filteredData.add(value);
			filteredData.remove(0);

		}

	}

	/**
	 * add value to head of derivatives. Remove element at its tail.
	 * 
	 * @param value
	 *            - value of derivative
	 */
	public void addToDerivatives(int value) {
		synchronized (lock) {
			//derivatives.add(value);
			//derivatives.remove(0);
			derivatives = value;

		}

	}
}
