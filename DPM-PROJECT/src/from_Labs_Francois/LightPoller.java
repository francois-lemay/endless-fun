/* 
 * DESCRIPTION
 *
 * This is a median filter. THe ColorSensor will poll continuously while
 * the median filter will remove all outliers.
 * Then, the discrete derivative between every consecutive sample is 
 * computed and stored in 'derivatives' (array). This array gets updated
 * every PERIOD s.
 * 
 * The rate at which the chosen sensor collects data is set by
 * PERIOD.
 * 
 * Optimal values for SAMPLE_SIZE, and PERIOD
 * must be found to obtain accurate filtering.
 * 
 */

package from_Labs_Francois;

import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * light sensor data acquisition and filtering
 * @author Francois
 *
 */
public class LightPoller implements TimerListener {

	// class variables
	private ColorSensor lightSensor;
	
	private Timer timer;
	private int PERIOD = 50; // period of light sampling (in ms)
	
	private final int SAMPLE_SIZE = 3;  /* determines the number of
										 * readings to be held in
										 * rawData[] at a time
										 */
	private final int NUMBER_OF_DERIVATIVES = SAMPLE_SIZE - 1; /* the number of derivatives
	 														* stored in 'derivatives
	 														*/
	
	private int[] rawData; // array that will hold raw data from sensors
	private int[] filteredReadings; // array of sensor readings in which
									// the outliers have been removed
	
	private int[] derivatives; /*
								 * array that will hold NUMBER_OF_DERIVATIVES consecutive values of
								 * discrete diff.
								 */
	private int index; // index used for rawData
	
	// TODO: pass through constructor values for PERIOD and SAMPLE SIZE to make this class more modular
	
	/**
	 * constructor
	 * @param ls - color sensor
	 */
	public LightPoller(ColorSensor ls) {

		lightSensor = ls;
		
		rawData = new int[SAMPLE_SIZE];
		filteredReadings = new int[SAMPLE_SIZE];
		derivatives = new int[NUMBER_OF_DERIVATIVES];
		
		index = 0; // set index of lightReadings to 0		
		
		// set up timer
		timer = new Timer(PERIOD,this);
	}

	/**
	 * main thread
	 */
	public void timedOut() {
		
		// re-start index at 0 when all elements of array have been filled
		if (index == SAMPLE_SIZE) {
			index = 0;
		}

		rawData[index] = getRawData();
		
// ------------------------------------------------------------//
		 //RConsole.println(""+rawData[index]);
// -----------------------------------------------------------//

		// remove outliers from raw data and store in
		// filteredReadings
		removeOutliers(rawData);

		// calculate discrete derivatives of filteredReadings
		// and store into 'derivatives'
		storeDerivatives(filteredReadings);

		// increment index
		index++;

	}
	/**
	 * get raw light value from the color sensor
	 * @return light value integer
	 */
	public int getRawData() {

		// get light reading from lightSensor and store in rawData array
		return lightSensor.getRawLightValue();
	}
	
	/**
	 * this method will swap a value of the sample rawData
	 * if the element in question is larger than the
	 * median of the sample. 
	 * @param readings - data that is to be filtered
	 */
	private void removeOutliers(int[] readings){
		
		// calculate median of the readings
		int median = calculateMedian(readings);
		
		// compare every reading with the median.
		// store either the reading or the median
		// into filteredReadings
		for(int i=0 ; i<readings.length ; i++){
			if(readings[i] > median){
				filteredReadings[i] = median;
			}
			else{
				filteredReadings[i] = readings[i];
			}
//------------------------------------------------------------//			
			//RConsole.println(""+filteredReadings[i]);
//-----------------------------------------------------------//
		}	
	}

	/**
	 * find the median of the inputed array
	 * @param array
	 * @return median
	 */
	private int calculateMedian(int[] array) {

		int[] orderedArray = new int[array.length]; // create temp. int[] of
													// same size as 'array'
		int median; // this will be the return value

		// sort array in ascending order
		boolean foo = true; // set to true to enter while loop
		int temp;

		// copy all elements of 'array' into orderedArray
		for (int i = 0; i < array.length; i++) {
			orderedArray[i] = array[i];
		}

		// begin swap sort
		while (foo) {
			foo = false; // will stop iteration if array is ordered

			for (int i = 0; i < array.length - 1; i++) {
				if (orderedArray[i] > orderedArray[i + 1]) {
					temp = orderedArray[i + 1];
					orderedArray[i + 1] = orderedArray[i]; // swap items'
															// positions in
															// orderedArray
					orderedArray[i] = temp;
					foo = true; // shows that swap has occured
				}
			}
		}

		// find median depending on array size

		if (array.length % 2 == 0) { // if orderedArray is of EVEN size, the
										// median is average of the 2 middle
										// values
			median = (orderedArray[(SAMPLE_SIZE / 2) - 1] + orderedArray[SAMPLE_SIZE / 2]) / 2; // take
																								// average
		} else {
			median = orderedArray[(SAMPLE_SIZE - 1) / 2]; // if SAMPLE_SIZE is
															// odd, median is
															// middle value
		}
		
		return median;
	}
	
	/**
	 * store consecutive discrete derivatives of the
	 * inputed data into derivatives
	 * @param array - data
	 */
	private void storeDerivatives(int[] array) {

		// calculate derivative and store in array called derivatives
		for (int i = 0; i < array.length - 1; i++) {
			derivatives[i] = array[i+1] - array[i];
			
//------------------------------------------------------------//			
			//RConsole.println(""+derivatives[i]);
//-----------------------------------------------------------//
			
		}

	}

	/**
	 * search for a derivative value that is smaller
	 * or equal to threshold
	 * @param threshold - derivative threshold
	 * @return whether such a derivative exist
	 */
	public boolean searchForSmallerDerivative(int threshold) {
		
		for(int i=0; i<NUMBER_OF_DERIVATIVES; i++){
			if(derivatives[i] <= threshold){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * search for a derivative value that is larger
	 * or equal to threshold
	 * @param threshold - derivative threshold
	 * @return whether such a derivative exists
	 */
	public boolean searchForLargerDerivative(int threshold) {
		
		for(int i=0; i<NUMBER_OF_DERIVATIVES; i++){
			if(derivatives[i] >= threshold){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * start main thread
	 */
	public void startPolling(){
		lightSensor.setFloodlight(true); // set floodlight 'on' before
											// polling is started
		timer.start();
	}
	
	/**
	 * stop main thread
	 */
	public void stopPolling(){
		timer.stop();
		lightSensor.setFloodlight(false); // set floodlight 'on' before
										  // polling is started
	}

}
