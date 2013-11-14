package official;

import java.util.Arrays;


/**
 * modular data filter. can filter data using a moving-window median filter
 * and/or using a derivative filter
 * @author Francois
 * 
 */
public class DataFilter {

	/**
	 * apply moving-window median filter to a data sample
	 * @param data
	 *            - data sample
	 * @return filtered data
	 */
	public static int[] medianFilter(int[] data) {
		
		// clone data
		int[] filteredData = (int[])data.clone();

		// calculate median of the readings
		int median = calculateMedian(filteredData);
		
		// compare every reading with the median.
		// store either the reading or the median
		// into filteredData
		for(int i=0 ; i<data.length ; i++){
			
			if(data[i] > median){
				filteredData[i] = median;
			}
		}
		
		return filteredData;
	}
	
	/**
	 * apply derivative filter to data sample
	 * @param data - data sample
	 * @return filtered data
	 */
	public static int[] derivativeFilter(int[] data) {
		
		int[] derivatives = new int[data.length - 1];

		// calculate derivatives
		for (int i = 0; i < data.length - 1; i++) {
			derivatives[i] = data[i + 1] - data[i];
		}

		return derivatives;
	}
	
	
	
	// helper methods
	
	/**
	 * compute median of data sample
	 * 
	 * @param data - data sample for which median will be calculated
	 * @return median
	 */
	public static int calculateMedian(int[] data) {
		
		int median = 0;
		
		// create temp. int[] identical to data
		int[] orderedArray = (int[])data.clone();

		// sort array in ascending order
		Arrays.sort(orderedArray);

		// find median depending on array size

		if (data.length % 2 == 0) { // if orderedArray is of EVEN size, the
		// median is average of the 2 middle
		// values
			median = (orderedArray[(data.length / 2) - 1] + orderedArray[data.length / 2]) / 2; // take
			// average
		} else {
			median = orderedArray[(data.length - 1) / 2]; // if SAMPLE_SIZE is
			// odd, median is
			// middle value
		}

		return median;
	}

}
