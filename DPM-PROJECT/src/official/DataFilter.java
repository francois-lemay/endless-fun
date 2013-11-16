package official;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * modular data filter. can filter data using a moving-window median filter
 * and/or using a derivative filter
 * 
 * @author Francois
 * 
 */
public class DataFilter {

	/**
	 * filter one data point using a median filter
	 * 
	 * @param data
	 *            - list of data
	 * @return filtered data value
	 */
	public static int medianFilter(LinkedList<Integer> data) {

		// get first sample
		int sample = data.get(0);

		// calculate median of all the samples
		int median = calculateMedian(data);

		// if first sample in window > median ==> sample = median
		if (sample > median) {
			return median;
		} else {
			return sample;
		}
	}
	

	/**
	 * apply derivative filter to last two samples of the
	 * inputed List
	 * 
	 * @param data
	 *            - integer list of data
	 * @return filtered data
	 */
	public static int derivativeFilter(LinkedList<Integer> data) {
		
		int lastIndex = data.size()-1;

		return data.get(lastIndex) - data.get(lastIndex-1);
		
	}

	// helper methods

	/**
	 * compute median of data sample
	 * 
	 * @param data
	 *            - data sample for which median will be calculated
	 * @return median
	 */
	public static int calculateMedian(LinkedList<Integer> data) {
		
		// initialize window and median
		int[] orderedArray = new int[data.size()];
		int median = 0;

		// copy samples contained in data
		for (int i = 0; i < data.size(); i++) {
				orderedArray[i] = data.get(i);
		}

		// sort array in ascending order
		Arrays.sort(orderedArray);

		// find median depending on array size

		if (orderedArray.length % 2 == 0) { // if orderedArray is of EVEN size, the
			// median is average of the 2 middle
			// values
			median = (orderedArray[(orderedArray.length / 2) - 1] + orderedArray[orderedArray.length / 2]) / 2; // take
			// average
		} else {
			median = orderedArray[(orderedArray.length - 1) / 2]; // if SAMPLE_SIZE is
			// odd, median is
			// middle value
		}

		return median;
	}

}
