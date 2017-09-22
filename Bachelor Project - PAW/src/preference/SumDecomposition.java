package preference;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import main.HelperFunctions;

/**
 * Helper class used by non-strict impartial culture to get fair weights for all preference relations.
 * @author Dominik
 *
 */
public class SumDecomposition {
	private static final Random random = new Random();
	
	/**
	 * Calculates the correct weights for every preference relation with a given number of objects.
	 * @param objects
	 * @return
	 */
	public static int[] getRandomWeighted(int objects) {
		Vector<int[]> decoms = getDecompositions(objects);
		int[] weights = new int[decoms.size()];
		int weightSum = 0;
		
		for(int i = 0;i<weights.length;i++) {
			weights[i] = getWeight(decoms.elementAt(i));
			weightSum+= weights[i];
		}
		
		int r = random.nextInt(weightSum);
		int tempSum = weights[0];
		int index = 0;
		while(tempSum < r) {
			tempSum+=weights[++index];
		}
		int[] out = decoms.get(index).clone();
		
		List<Integer> l = IntStream.of(out).boxed().collect(Collectors.toList());
		Collections.shuffle(l);
		return l.stream().mapToInt(Integer::intValue).toArray();
	}
	
	/**
	 * Calculates the number of partitions  [p1,...,pn] of arbitrary order.
	 * So given (4,3,1,1) we consider all possible orderings
	 * (4,3,1,1)
	 * (4,1,3,1)
	 * (4,1,1,3)
	 * (3,4,1,1)
	 * (1,4,3,1)
	 * (1,4,1,3)
	 * (3,1,4,1)
	 * (1,3,4,1)
	 * (1,1,4,3)
	 * (3,1,1,4)
	 * (1,3,1,4)
	 * (1,1,3,4)
	 * This number is equal to n!/(g1! *...*gk!)
	 * Where g1,...,gk are the number of occurrences of a numbers in p
	 * So here 4!/(1!1!2!) = 12
	 * 
	 * @param partition the array p = [p1,...,pn]
	 * @return
	 */
	private static int getWeight(int[] partition) {
		long a = HelperFunctions.factorial(partition.length);
		
		int group = partition[0];
		int count = 0;
		for(int i=0;i<partition.length;i++) {
			if(partition[i] == group) {
				count++;
			} else {
				a/=HelperFunctions.factorial(count);
				group=partition[i];
				count = 1;
			}
		}
		a/=HelperFunctions.factorial(count);
		
		return (int) a;
	}
	
	private static HashMap<Integer, Vector<int[]>> cache = new HashMap<>();
	
	//https://stackoverflow.com/questions/400794/generating-the-partitions-of-a-number
	private static Vector<int[]> getDecompositions(int n) {
		if(cache.containsKey(n)) {
			return cache.get(n);
		}
		
		Vector<int[]>result = new Vector<>();
		result.addElement(new int[]{n});
		for(int i=0;i<n;i++){
			int a = n-i;
			Vector<int[]> R = getDecompositions(i);
			for( int[] r: R) {
				if(r[0]<=a) {
					int[] rDash = new int[r.length+1];
					for(int j=0;j<r.length;j++) {
						rDash[j+1] = r[j];
					}
					rDash[0] = a;
					
					result.addElement(rDash);
				}
			}
		}
		
		cache.put(n, result);
		return result;
	}
}
