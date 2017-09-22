package main.sampler;

import java.util.Iterator;
import java.util.Vector;

/**
 * Iteratively iterates over all strict profiles with given relations, whilst enforcing lexicographic ordering.
 * @author Dominik
 *
 */
public class StrictPreferenceProfileIterator implements Iterator<int[]>,Iterable<int[]>{

	private int[] state;
	private Vector<int[]> relations;
	
	public StrictPreferenceProfileIterator(Vector<int[]> rel,int agents) {
		this.relations = rel;
		this.state = new int[agents];
	}

	
	@Override
	public boolean hasNext() {
		return state[0] == 0;
	}

	@Override
	public int[] next() {
		int[] out = state.clone();
		incrementState();
		return out;
	}

	private void incrementState() {
		int max = relations.size();
		
		for(int i= state.length-1; i>=0;i--) {
			state[i]++;
			if(state[i]==max) {
				state[i] = 0;
			} else {
				break;
			}
		}
		
		for(int i=1; i<state.length;i++) {
			//The indices have to strictly increase
			state[i] = Math.max(state[i-1], state[i]);
		}
		
	}
	
	@Override
	public Iterator<int[]> iterator() {
		return this;
	}

}
