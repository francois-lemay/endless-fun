package official;

/**
 * modular data filter. can filter data using a moving-window median filter
 * and/or using a derivative filter
 * @author Francois
 * 
 */
public class DataFilter {

	// constructor
	public DataFilter() {
		// nothing to be done
	}

	/**
	 * apply moving-window median filter to a data sample
	 * @param data
	 *            - data sample
	 * @return filtered data
	 */
	public int[] medianFilter(int[] data) {
		int[] filteredData = new int[data.length];

		// calculate median of the readings
		int median = calculateMedian(data);
		
		// compare every reading with the median.
		// store either the reading or the median
		// into filteredData
		for(int i=0 ; i<data.length ; i++){
			if(data[i] > median){
				filteredData[i] = median;
			}
			else{
				filteredData[i] = data[i];
			}
		}

		return filteredData;
	}
	
	/**
	 * apply derivative filter to data sample
	 * @param data - data sample
	 * @return filtered data
	 */
	public int[] derivativeFilter(int[] data) {
		
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
	private int calculateMedian(int[] data) {
		
		int median = 0;

		// create temp. int[] of same size as 'array'
		int[] orderedArray = new int[data.length]; 

		// sort array in ascending order
		boolean foo = true; // set to true to enter while loop
		int temp;

		// copy all elements of 'array' into orderedArray
		for (int i = 0; i < data.length; i++) {
			orderedArray[i] = data[i];
		}

		// begin swap sort
		while (foo) {
			foo = false; // will stop iteration if array is ordered

			for (int i = 0; i < data.length - 1; i++) {
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
