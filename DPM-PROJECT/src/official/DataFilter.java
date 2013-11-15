package official;

import java.util.Arrays;
import java.util.List;

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
	public static int medianFilter(List<Integer> data) {

		// get first sample
		int sample = data.get(0);
		
		// initialize window and median
		int[] window = new int[data.size()];
		int median = -1;

			// copy samples contained in window
			for (int j = 0; j < data.size(); j++) {
				window[j] = data.get(j);
			}

			// calculate median of the samples in window
			median = calculateMedian(window);

			// if first sample in window > median ==> sample = median
			if (sample > median) {
				return median;
			}else{
				return sample;
			}
	}
	

	/**
	 * apply derivative filter to last two data samples of the
	 * inputed List
	 * 
	 * @param data
	 *            - integer list of data
	 * @return filtered data
	 */
	public static int derivativeFilter(List<Integer> data) {

		return data.get(data.size()-1) - data.get(data.size()-2);
		
	}

	// helper methods

	/**
	 * compute median of data sample
	 * 
	 * @param data
	 *            - data sample for which median will be calculated
	 * @return median
	 */
	public static int calculateMedian(int[] data) {

		int median = 0;

		// create temp. int[] identical to data
		int[] orderedArray = (int[]) data.clone();

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
