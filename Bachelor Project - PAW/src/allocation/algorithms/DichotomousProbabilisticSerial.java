package allocation.algorithms;

import java.util.List;

import allocation.Allocation;
import allocation.AllocationStrategy;
import preference.PreferenceRelation;
import preference.PreferenceTypeIdentifier;
import preference.scenario.Scenario;

@Deprecated //Use EPS instead
public class DichotomousProbabilisticSerial implements AllocationStrategy {

	@Override
	public Allocation allocate(Scenario sc) {
		if(!canApply(sc)) {
			return null;
		}

		ExtendedProbabilisticSerialAlgorithm alg = new ExtendedProbabilisticSerialAlgorithm();
		return alg.allocate(sc, true);
	}


	
	@Override
	public List<String> getViolatedConstraints(Scenario scenario) {	
		List<String> out = AllocationStrategy.super.getViolatedConstraints(scenario);

		for(int i=0;i<scenario.getAgentCount();i++) {
			PreferenceRelation rel = scenario.getAgentRelation(i);
			int type = PreferenceTypeIdentifier.getRelationType(rel);
			if(!PreferenceTypeIdentifier.isDichotomous(type)) {
				out.add("Preference relation of agent "+scenario.getAgent(i)+" has to be dichotomous.");
			}
		}
		return out; 
	}
	

}
