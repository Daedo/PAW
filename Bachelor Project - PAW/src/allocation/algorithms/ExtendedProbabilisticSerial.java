package allocation.algorithms;

import allocation.Allocation;
import allocation.AllocationStrategy;
import preference.scenario.Scenario;

public class ExtendedProbabilisticSerial implements AllocationStrategy {
	
	@Override
	public Allocation allocate(Scenario sc) {
		if(!canApply(sc)) {
			return null;
		}
		//Defer call to the ExtendedProbabilisticSerialAlgorithm class
		ExtendedProbabilisticSerialAlgorithm alg = new ExtendedProbabilisticSerialAlgorithm();
		return alg.allocate(sc, false);
	}
}
