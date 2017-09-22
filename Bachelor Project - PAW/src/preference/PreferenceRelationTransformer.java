package preference;

import java.util.Vector;

/**
 * Class containing different methods for changing existing {@link PreferenceRelation}s.
 * @author Dominik
 *
 */
public class PreferenceRelationTransformer {

	/*================= TRANSFORM =================*/
	/**
	 * Returns the given relation, but rotates the groups around
	 * @param rel
	 * @param number if the number is bigger than 0 groups are rotated downwards else they are rotated upwards
	 * @return
	 */
	public static PreferenceRelation createRotatedRelation(PreferenceRelation rel, int number) {
		int count = number % rel.getGroupcount();
	
		//Copy the relation
		Vector<PreferenceGroup> rOut = new Vector<>();
		for(int i=0;i<rel.getGroupcount();i++) {
			rOut.add(new PreferenceGroup(rel.getGroup(i)));
		}
	
		while(count!=0) {
			if(count<0) {
				//Rotate up
				PreferenceGroup tmp = rOut.remove(0);
				rOut.add(tmp);
				count++;
			}
	
			if(count>0) {
				PreferenceGroup tmp = rOut.remove(rOut.size()-1);
				rOut.insertElementAt(tmp,0);
				//Rotate down 
				count--;
			}
		}
	
		return new PreferenceRelation(rOut);
	}

	public static PreferenceRelation cloneRelation(PreferenceRelation rel) {
		return createRotatedRelation(rel, 0); 
	}

	/**
	 * Creates a strict version of a given {@link PreferenceRelation}:
	 * 1,{2,3},4 -> 1,2,3,4
	 * 1,{3,2},4 -> 1,3,2,4
	 * 
	 * @param rel
	 * @return
	 */
	public static PreferenceRelation createStrictPreference(PreferenceRelation rel) {
		Vector<PreferenceGroup> rOut = new Vector<>();
	
		for(int i=0;i<rel.getGroupcount();i++) {
			int[] group = rel.getGroup(i);
			for(int j=0;j<group.length;j++) {
				PreferenceGroup g = new PreferenceGroup();
				g.add(group[j]);
				rOut.add(g);
			}
		}
	
		return new PreferenceRelation(rOut);
	}

}
