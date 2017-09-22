package allocation.algorithms.convexLPSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.IntStream;

public class PermutationGenerator {
	
	public static Vector<int[]> getPermutations(int n) {
		int[] obj = IntStream.range(0, n).toArray();
		Vector<int[]> out =  getPermutations(obj);
		
		Comparator<int[]> comp = (a,b) -> {
			for(int i=0;i<a.length;i++) {
				int c = Integer.compare(a[i], b[i]);
				if(c!=0) {
					return c;
				}
			}
			return 0;
		};
		
		Collections.sort(out, comp);
		return out;
	}
	
	private static Vector<int[]> getPermutations(int[] obj) {
		Vector<int[]> out = new Vector<>();
		int n = obj.length;

		if(n<=1) {
			out.addElement(obj);
			return out;
		}
		
		Vector<int[]> rec = getPermutations(Arrays.copyOf(obj, n-1));
	
		for(int i=0;i<n;i++) {
			for(int[] perm:rec) {
				int[] add = new int[n];
				for(int j =0;j<n;j++) {
					if(i>j) {
						add[j] = perm[j];
					} else if(i==j) {
						add[j] = obj[n-1];
					} else {
						add[j] = perm[j-1];
					}
				}
				out.add(add);
			}
		}
		
		return out;
	}
}
