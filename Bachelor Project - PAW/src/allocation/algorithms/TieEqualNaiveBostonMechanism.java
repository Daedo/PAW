package allocation.algorithms;

import allocation.algorithms.boston.BostonMechanismType;
import allocation.algorithms.boston.GenericBostonMechanism;

public class TieEqualNaiveBostonMechanism extends GenericBostonMechanism{
	public TieEqualNaiveBostonMechanism() {
		super(BostonMechanismType.TIE_EQUAL,false);
	}
}
