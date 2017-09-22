package allocation.algorithms;

import allocation.algorithms.boston.BostonMechanismType;
import allocation.algorithms.boston.GenericBostonMechanism;

public class PathEqualNaiveBostonMechanism extends GenericBostonMechanism {
	public PathEqualNaiveBostonMechanism() {
		super(BostonMechanismType.PATH_EQUAL, false);
	}
}
