package main.sampler;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Data Structure that contains all unordered pairs of the first n numbers.
 * Pairs can be removed and it can be checked if a certain number has a certain partner,
 * or what partners a certain number is paired with.
 * 
 * @author Dominik
 *
 */
public class DiffStructure {
	private int[] pairCount;
	private Vector<Pair> pairs;
	
	public DiffStructure(int size) {
		pairCount = new int[size];
		Arrays.fill(pairCount, size-1);
		pairs = new Vector<>();
		for(int i=0;i<size;i++) {
			for(int j=i+1;j<size;j++) {
				pairs.addElement(new Pair(i, j));
			}
		}
		
	}

	public boolean isEmpty() {
		return pairs.isEmpty();
	}
	
	public boolean hasPairs(int x) {
		return pairCount[x]>0;
	}
	
	public boolean hasPair(int x, int y) {
		return pairs.contains(new Pair(x,y));
	}
	
	public List<Pair> getPairs(int x) {
		return pairs.stream().filter(p -> (p.x == x || p.y == x)).collect(Collectors.toList());
	}
	
	public void removePair(int x,int y) {
		Pair p = new Pair(x, y);
		if(pairs.remove(p)) {
			pairCount[x]--;
			pairCount[y]--;
		}
	}
	
	/**
	 * Helper class represents an unordered pair.
	 * @author Dominik
	 *
	 */
	public static class Pair {
		int x,y;
		public Pair(int x,int y) {
			this.x = Math.min(x, y);
			this.y = Math.max(x, y);
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		
	}
}
