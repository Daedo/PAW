package allocation.algorithms;

import allocation.algorithms.boston.BostonMechanismType;
import allocation.algorithms.boston.GenericBostonMechanism;

public class TieEqualAdaptiveBostonMechanism extends GenericBostonMechanism{
	public TieEqualAdaptiveBostonMechanism() {
		super(BostonMechanismType.TIE_EQUAL,true);
	}
}
