package preference;

import java.util.Vector;

import preference.scenario.Scenario;

/**
 * Class modeling an indifference group in a profile/ {@link Scenario}.
 * @author Dominik
 *
 */
public class PreferenceGroup {
	private Vector<Integer> group;

	public PreferenceGroup() {
		group = new Vector<>();
	}

	public PreferenceGroup(int[] values) {
		this();
		for(int v:values) {
			if(v>=0) {
				add(v);
			}
		}
	}

	public void add(int number) {
		if(!contains(number)) {
			group.add(new Integer(number));
		}
	}

	/**
	 * Remove the given object.
	 * @param number
	 * @return
	 */
	public boolean remove(int number) {
		return group.remove(new Integer(number));
	}

	/**
	 * This method removes the object n and decreases all numbers bigger than n by 1.
	 * It is used when removing an object from a relation
	 */
	public void removingDecrement(int n) {
		remove(n);
		for(int i=0;i<group.size();i++) {
			int val = group.get(i).intValue();
			if(val>n) {
				group.set(i, new Integer(val-1));
			}
		}
	}

	/**
	 * Decrements all objects by the given number.
	 * @param n
	 */
	void decrement(int n) {
		for(int i=0;i<group.size();i++) {
			int val = group.get(i).intValue();
			group.set(i, new Integer(val-n));
		}
	}

	/**
	 * Copies the values to an integer array.
	 * @return the array
	 */
	public int[] getData() {
		int[] out = new int[group.size()];
		for(int i=0;i<out.length;i++) {
			out[i] = group.get(i).intValue();
		}
		return out;
	}

	/**
	 * @param n
	 * @return true iff the group contains n.
	 */
	public boolean contains(int n) {
		for(Integer i:group) {
			if(i.intValue()==n) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the number of elements in this group.
	 */
	public int size() {
		return group.size();
	}

	/**
	 * @return true iff size()==0
	 */
	public boolean isEmpty() {
		return group.isEmpty();
	}

	@Override
	public String toString() {
		if(group.size()==1) {
			return ""+(group.get(0).intValue()+1);
		}
		String out = "{";
		for(int i=0;i<group.size();i++) {
			if(i!=0) {
				out+=",";
			}
			out+= (group.get(i).intValue()+1);
		}
		out+="}";
		return out;
	}

	/**
	 * @return Pretty print version.
	 */
	public String getStoreVersion() {
		if(group.size()==1) {
			return ""+group.get(0).intValue();
		}
		String out = "{";
		for(int i=0;i<group.size();i++) {
			if(i!=0) {
				out+=",";
			}
			out+= group.get(i).intValue();
		}
		out+="}";
		return out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		PreferenceGroup other = (PreferenceGroup) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}
}