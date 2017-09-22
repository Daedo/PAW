package preference;

import java.util.Vector;

/**
 * Determines certain attributes of a given {@link PreferenceRelation}.
 * @author Dominik
 *
 */
public class PreferenceTypeIdentifier {
	public static final int STRICT_PREFERENCE_RELATION = 1;
	public static final int DICHOTOMOUS_PREFERENCE_RELATION = 2;
	public static final int INDIFFERENT_PREFERENCE_RELATION = 4;
	
	/**
	 * Gets the type integer of a given {@link PreferenceRelation}.
	 * That is a number consisting of all flags |-ed together.
	 * 
	 * @param rel
	 * @return
	 */
	public static int getRelationType(PreferenceRelation rel) {
		if(rel == null) {
			return 0;
		}
		
		int out = 0;
		if(rel.getGroupcount() == rel.getRelationSize()) {
			out |= STRICT_PREFERENCE_RELATION;
		}
		
		if(rel.getGroupcount() <= 2) {
			out |= DICHOTOMOUS_PREFERENCE_RELATION;
		}
		
		if(rel.getGroupcount() == 1) {
			out |= INDIFFERENT_PREFERENCE_RELATION;
		}
		return out;
	}

	/**
	 * Checks if a certain flag is matched by a type integer.
	 * @param x
	 * @param mask
	 * @return
	 */
	private static boolean matches(int x,int mask) {
		return ((x & mask) == mask);
	}
	
	/**
	 * @param type
	 * @return true iff the type integer contains the STRICT_PREFERENCE_RELATION flag.
	 */
	public static boolean isStrict(int type) {
		return  matches(type,STRICT_PREFERENCE_RELATION);
	}
	
	/**
	 * @param type
	 * @return true iff the type integer contains the DICHOTOMOUS_PREFERENCE_RELATION flag.
	 */
	public static boolean isDichotomous(int type) {
		return  matches(type,DICHOTOMOUS_PREFERENCE_RELATION);
	}
	
	/**
	 * @param type
	 * @return true iff the type integer contains the INDIFFERENT_PREFERENCE_RELATION flag.
	 */
	public static boolean isIndifferent(int type) {
		return  matches(type,INDIFFERENT_PREFERENCE_RELATION);
	}

	/**
	 * Gets all flags and returns a list of strings describing the types of a given {@link PreferenceRelation}.
	 * @param rel
	 * @return
	 */
	public static Vector<String> getDescription(PreferenceRelation rel) {
		Vector<String> out = new Vector<>();
		int type = getRelationType(rel);
		if(isStrict(type)) {
			out.add("Strict");
		} else {
			out.add("Weak");
		}
		
		if(isDichotomous(type)) {
			out.add("Dichotomous");
		}
		
		if(isIndifferent(type)) {
			out.add("Indifferent");
		}
		return out;
	}
}
