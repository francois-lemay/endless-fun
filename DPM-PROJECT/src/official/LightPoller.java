package official;

import java.util.LinkedList;

import official.Constants.theLock;
import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Instance of a ColorSensor. Conserve both raw and filtered data retrieved from
 * the ColorSensor.
 * 
 * @author François
 * 
 */
public class LightPoller implements TimerListener {

	// class variables

	/**
	 * color sensor
	 */
	private ColorSensor ls;
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
	 * integer list of integers that will hold raw data from sensors
	 */
	private LinkedList<Integer> rawData;
	/**
	 * list of sensor readings in which the outliers have been removed
	 */
	private LinkedList<Integer> filteredData;
	/**
	 * integer list that will hold NUM_OF_DERIVATIVES consecutive values of
	 * discrete diff.
	 */
	private int derivatives;

	// constructor
	public LightPoller(ColorSensor ls, int num_samples, int period) {

		this.ls = ls;

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
				rawData.add(ls.getRawLightValue());
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
				value = filteredData1.get(i + 1) - filteredData1.get(i);
				derivatives1.add(value);
			}
		}
*/
		// turn ON floodlight
		ls.setFloodlight(true);
		
		
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
	 * get light value from the light sensor and add to rawData
	 */
	public void collectRawData() {

		// get light value
		int data = ls.getRawLightValue();

		// remove tail and add sample at head
		synchronized (lock) {
			rawData.add(data);
			rawData.remove(0);

		}
		
		//********************
		//RConsole.println(""+data);
		//*********************
	

	}

	// accessors

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
			//return derivatives1.get(derivatives1.size() - 1);
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
			
			//********************
			//RConsole.println(""+value);
			//*********************

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
			//derivatives1.add(value);
			//derivatives1.remove(0);
			derivatives = value;
			
			//********************
			//RConsole.println(""+value);
			//*********************

		}
	}
}
