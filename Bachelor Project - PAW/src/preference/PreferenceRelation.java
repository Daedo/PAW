package preference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

/**
 * A class simulating a total and transitive preference relation. Composed of {@link PreferenceGroup}s.
 * @author Dominik
 *
 */
public class PreferenceRelation {
	private Vector<PreferenceGroup> relation;
	private int relationSize;

	/**
	 * Creates a new preference relation with a given number of elements 
	 * @param size the number of elements. Has to be bigger or equal to 0
	 */
	public PreferenceRelation(int size) {
		if(size<0) {
			throw new IllegalStateException("Size can't be negative: "+size);
		}

		relation = new Vector<>(size);

		for(int i=0;i<size;i++) {
			PreferenceGroup group = new PreferenceGroup();
			group.add(i);
			relation.add(group);
		}

		this.relationSize = size;
	}

	/**
	 * Creates a new relation if given a valid list of {@link PreferenceGroup}s.
	 * @param data
	 */
	public PreferenceRelation(Collection<PreferenceGroup> data) {
		if(!isValidPreferenceData(data)) {
			throw new IllegalAccessError("Invalid Prefernece Data");
		}

		//Determine Size
		this.relationSize = 0;
		//Find min & max
		int min = -1;
		for(PreferenceGroup group:data) {
			this.relationSize+=group.size();
			int[] gData = group.getData();
			for(int i=0;i<gData.length;i++) {
				if(min==-1 || gData[i] < min) {
					min = gData[i];
				}
			}
		}

		//Adjust Objects
		for(PreferenceGroup g:data) {
			g.decrement(min);
		}

		this.relation = new Vector<>(data);
		for(int i=0;i<this.relation.size();i++) {
			if(relation.get(i).size()==0) {
				relation.remove(i);
				i--;
			}
		}
	}

	/**
	 * @return number of {@link PreferenceGroup}s.
	 */
	public int getGroupcount() {
		return relation.size();
	}

	/**
	 * Converts the ith {@link PreferenceGroup} into an integer array.
	 * @param i
	 * @return the array.
	 */
	public int[] getGroup(int i) {
		return relation.get(i).getData();
	}

	@Override
	public String toString() {
		String out = "";

		for(int i=0;i<relation.size();i++) {
			if(i!=0) {
				out+=",";
			}
			out+=relation.get(i).toString();		
		}

		return out;
	}

	/**
	 * @return Pretty print verision. (, and {} notation)
	 */
	public String getStoreVersion() {
		String out = "";

		for(int i=0;i<relation.size();i++) {
			if(i!=0) {
				out+=",";
			}
			out+=relation.get(i).getStoreVersion();		
		}

		return out;
	}

	/**
	 * Returns the index of a given element 0 <= n < groupsize
	 * Returns -1 in all other cases
	 * @param n
	 * @return
	 */
	public int getGroupindex(int n) {
		for(int i=0;i<getGroupcount();i++) {
			if(relation.get(i).contains(n)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return Number of objects in this relation.
	 */
	public int getRelationSize() {
		return relationSize;
	}

	/**
	 * Removes the given Number from its group and then moves it into the group with
	 * the given index. If createNewGroup is true. It creates a new group at the given
	 * index. If the move leaves a group empty this group will then be removed.
	 * @param n
	 * @param newIndex
	 * @param createNewGroup
	 */
	public void move(int n, int newIndex,boolean createNewGroup) {
		if(newIndex>getGroupcount() || (!createNewGroup && newIndex==getGroupcount())) {
			throw new ArrayIndexOutOfBoundsException(newIndex);
		}

		int oldIndex = getGroupindex(n);
		if(oldIndex==-1) {
			throw new IndexOutOfBoundsException("Preference relation doesn't contain an element "+n);
		}
		PreferenceGroup oldGroup = relation.get(oldIndex);
		oldGroup.remove(n);

		if(createNewGroup) {
			PreferenceGroup newGroup = new PreferenceGroup();
			newGroup.add(n);
			relation.insertElementAt(newGroup, newIndex);
		} else {
			relation.get(newIndex).add(n);
		}

		if(oldGroup.isEmpty()) {
			relation.remove(oldGroup);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Vector<PreferenceGroup> rawData = new Vector<>();
		for(PreferenceGroup g:this.relation) {
			rawData.addElement(new PreferenceGroup());
			int[] gData = g.getData();
			for(int i: gData) {
				rawData.lastElement().add(i);
			}
		}

		return new PreferenceRelation(rawData);
	}

	/**
	 * Removes a given object form the relation and ensures that the order is kept without holes.
	 * 1,2,3,4 -(Remove 3)-> 1,2,4 -(Renaming)-> 1,2,3 
	 * All done in one step.
	 * 
	 * @param object
	 */
	public void removeObject(int object) {
		for(int i=0;i<this.relation.size();i++) {
			PreferenceGroup g = this.relation.get(i);
			g.removingDecrement(object);
			if(g.isEmpty()) {
				this.relation.remove(i);
				i--;
			}
		}

		relationSize--;
	}

	public void addElement() {
		PreferenceGroup g = new PreferenceGroup();
		g.add(relationSize);
		this.relation.addElement(g);
		this.relationSize++;
	}

	/**
	 * Compares the two objects
	 * @param objA
	 * @param objB
	 * @return is >0 if objA> objB, =0 if objA=objB and <0 if objA<objB
	 */
	public int compareObjects(int objA,int objB) {
		int indexA = getGroupindex(objA);
		int indexB = getGroupindex(objB);
		return -Integer.compare(indexA, indexB);
	}

	/**
	 * Returns whether the agent prefers objectA or objectB
	 * 
	 * @param objA
	 * @param objB
	 * @return -1 if the prefers A, 1 if he prefers B, 0 if the is indifferent
	 */
	public int vote(int objA,int objB) {
		int out = compareObjects(objA, objB);
		if(out==0) {
			return out;
		}

		return out/Math.abs(out);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		result = prime * result + relationSize;
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
		PreferenceRelation other = (PreferenceRelation) obj;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (relationSize != other.relationSize)
			return false;
		return true;
	}

	public static boolean isValidPreferenceData(Collection<PreferenceGroup> data) {
		//Determine Size
		int relationSize = 0;
		//Find min & max
		int min = -1;
		int max = -1;
		//Make Sure there are no dublicates
		HashSet<Integer> temp = new HashSet<>();

		for(PreferenceGroup group:data) {
			relationSize+=group.size();
			int[] gData = group.getData();
			for(int i=0;i<gData.length;i++) {
				if(gData[i]<0) {
					System.out.println("Preference relation element can't be negative"+gData[i]);
					return false;
				}

				if(min==-1 || gData[i] < min) {
					min = gData[i];
				}
				if(max==-1 || gData[i] > max) {
					max = gData[i];
				}
				Integer boxedData = new Integer(gData[i]);
				if(temp.contains(boxedData)) {
					System.out.println("Preference relation element can't contain dublicates");
					return false;
				}
				temp.add(boxedData);
			}
		}

		/*if(min!=0) {
					throw new IllegalStateException("Wrong lower bound: "+min);
				}

				if((max+1)!=this.relationSize) {
					System.out.println(this.relationSize);
					throw new IllegalStateException("Wrong upper bound: "+max);
				}*/

		if((max+1-min)!=relationSize) {
			System.out.println(relationSize);
			System.out.println("Wrong relation size: "+(max+1-min));
			return false;
		}

		return true;
	}
}
