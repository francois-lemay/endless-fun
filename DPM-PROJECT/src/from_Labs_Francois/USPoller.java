/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 *
 * DESCRIPTION
 *
 * This is a median filter. The Ultrasonic
 * Sensor will poll continuously while the raw data is smoothed out
 * using a median filter.
 * Then, the discrete derivatives of the smooth data are 
 * computed and stored in 'derivatives' (array). This array gets updated
 * every PERIOD seconds.
 * 
 * 
 * The rate at which the chosen sensor collects data is set by
 * PERIOD.
 * 
 * Optimal values for SAMPLE_SIZE, and PERIOD
 * must be found to obtain accurate filtering.
 * 
 */

package from_Labs_Francois;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class USPoller implements TimerListener {

	// class variables
	private UltrasonicSensor usSensor;
	
	private Timer timer;
	private int PERIOD = 50; // period of light sampling (in ms)
	
	private final int SAMPLE_SIZE = 5;  /* determines the number of
										 * readings to be held in
										 * rawData[] at a time
										 */
	private final int NUMBER_OF_DERIVATIVES = 5; /* the number of derivatives
	 												* stored in 'derivatives
	 												*/
	
	private int[] rawData; // array that will hold raw data from sensors
	private int[] smoothData; //array that will hold median-smooth data
	
	private int[] derivatives; /*
								 * array that will hold NUMBER_OF_DERIVATIVES consecutive values of
								 * discrete diff.
								 */
	private int index; // index used for rawData
	private int index2; // index used for 'derivatives
	
	
	// constructor
	public USPoller(UltrasonicSensor us) {

		usSensor = us;
		
		rawData = new int[SAMPLE_SIZE];
		smoothData = new int[2];
		
		// fill raw Data 
		for(int i=0; i<SAMPLE_SIZE;i++){
			rawData[i] = getRawData();
		}

		derivatives = new int[NUMBER_OF_DERIVATIVES];
		index = 0;
		index2 = 0;

		// set up timer
		timer = new Timer(PERIOD, this);
		timer.start();
	}

	
	
	public void timedOut() {

		// re-start index at 0 when all elements of array have been filled
		if (index == SAMPLE_SIZE) {
			index = 0;
		}

		rawData[index] = getRawData();

	// ------------------------------------------------------------//
		//RConsole.println(""+rawData[index]);
	// -----------------------------------------------------------//

		// apply a median smoother to the raw data
		medianSmoother(rawData);

		// calculate discrete derivatives of filteredReadings
		// and store into 'derivatives'
		storeDerivatives(smoothData);

		// increment index
		index++;

	}
	
	public int getRawData(){
		
			// get us reading from usSensor and store in rawData array
			return usSensor.getDistance();
	}
	
	
	
	/*
	 * Applies median smoothing to the sensor's
	 * raw data and stores it in smoothData
	 */
	public void medianSmoother(int[] array){
		
		smoothData[0] = smoothData[1];  // conserve previous data point
		
		smoothData[1] = calculateMedian(array);  // store median of current sample
		
		// ------------------------------------------------------------//
				//RConsole.println(""+smoothData[1]);
		// -----------------------------------------------------------//
		
	}

	// this method finds the median of the inputed array
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
	


	// this method stores consecutive derivatives of the
	// inputed array, into 'derivatives'
	private void storeDerivatives(int[] smoothData) {

		// re-start index at 0 when all elements of array have been filled
		if (index2 == NUMBER_OF_DERIVATIVES) {
			index2 = 0;
		}

		derivatives[index2] = smoothData[1] - smoothData[0];

	// ------------------------------------------------------------//
		//RConsole.println(""+derivatives[index2]);
	// -----------------------------------------------------------//

			index2++;
	}

	// search for a derivative value that is smaller
	// or equal to 'threshold'
	public boolean searchForSmallerDerivative(int threshold) {
		
		for(int i=0; i<NUMBER_OF_DERIVATIVES; i++){
			if(derivatives[i] <= threshold){
				return true;
			}
		}
		
		return false;
	}
	
	// search for a derivative value that is larger
	// or equal to 'threshold'
	public boolean searchForLargerDerivative(int threshold) {
		
		for(int i=0; i<NUMBER_OF_DERIVATIVES; i++){
			if(derivatives[i] >= threshold){
				return true;
			}
		}
		
		return false;
	}
	
	//----accessors----//
	
	// return the most recent smooth data point
	public int getLatestMedian(){
		return smoothData[1];
	}
	
	// return the previous smooth data point
	public int getPreviousMedian(){
		return smoothData[0];
	}

	// start timer
	public void startPolling(){
		timer.start();
	}
	
	// stop timer
	public void stopPolling(){
		timer.stop();
	}

}

