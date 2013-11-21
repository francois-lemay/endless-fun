package testing;

import java.util.Arrays;
import java.util.LinkedList;
import official.DataFilter;

/**
 * virtual testing of median-filtering found in DataFilter
 * @author Francois
 *
 */
public class VirtualFilterTest {

	public static void main(String[] args) {

		LinkedList<Integer> a = new LinkedList<Integer>();
		LinkedList<Integer> b = new LinkedList<Integer>();
		
		int size = 9;

		// fill raw list
		for (int i = 0; i < size; i++) {
			a.add((int)(100*Math.random()));
		}

		System.out.println("a = "+ a);
				
		int median = DataFilter.calculateMedian(a);
		System.out.println("median = "+median+ "\n");
		
		for (int i = 0; i < size; i++) {
			a.add((int)(100*Math.random()));
			a.remove(0);
			System.out.println("a = "+ a);
			median = DataFilter.calculateMedian(a);
			System.out.println("median = "+median+ "\n");
		}		
		
/*		
		
		// fill filtered list
		// fill raw list
		for (int i = 0; i < 10; i++) {
			b.add(DataFilter.medianFilter(a));
			a.add(10+i);
			a.remove();
			System.out.println("b = "+ b + "\n");

		}

*/		

/*		for (int i = 0; i < 10; i++) {
			b.add(DataFilter.medianFilter(a));
			b.remove(0);
			System.out.println(b + "\n");

		}
*/
	}
}
