package allocation.algorithms;

import allocation.algorithms.boston.BostonMechanismType;
import allocation.algorithms.boston.GenericBostonMechanism;

public class PathEqualAdaptiveBostonMechanism extends GenericBostonMechanism{
	public PathEqualAdaptiveBostonMechanism() {
		super(BostonMechanismType.PATH_EQUAL,true);
	}
}
