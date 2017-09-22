package allocation.algorithms.boston;

import java.util.List;

import allocation.Allocation;
import allocation.AllocationStrategy;
import preference.PreferenceRelation;
import preference.PreferenceTypeIdentifier;
import preference.scenario.Scenario;

public class GenericBostonMechanism implements AllocationStrategy {
	private BostonMechanismType type;
	private boolean isAdaptive;
	
	public GenericBostonMechanism(BostonMechanismType mechanismType, boolean isAdaptiveFlag) {
		if(mechanismType == null) {
			mechanismType = BostonMechanismType.TIE_EQUAL;
		}
		this.type = mechanismType;
		this.isAdaptive = isAdaptiveFlag;
	}
	
	@Override
	public Allocation allocate(Scenario scenario) {
		if(this.type == BostonMechanismType.TIE_EQUAL) {
			TieEqualBostonMechanism mechanism = new TieEqualBostonMechanism();
			return mechanism.allocate(scenario, isAdaptive);
		}
		
		PathEqualBostonMechanism mechanism = new PathEqualBostonMechanism();
		return mechanism.allocate(scenario, isAdaptive);
	}
	
	@Override
	public List<String> getViolatedConstraints(Scenario scenario) {
		List<String> out = AllocationStrategy.super.getViolatedConstraints(scenario);

		if(scenario.getAgentCount() != scenario.getObjectCount()) {
			out.add("Number of agents must be equal to the number of objects");
		}
		
		for(int i=0;i<scenario.getAgentCount();i++) {
			PreferenceRelation rel = scenario.getAgentRelation(i);
			int type = PreferenceTypeIdentifier.getRelationType(rel);
			if(!PreferenceTypeIdentifier.isStrict(type)) {
				out.add("Preference relation of agent "+scenario.getAgent(i)+" has to be strict.");
			}
		}

		return out;
	}

}
