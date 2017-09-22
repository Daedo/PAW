package allocation.algorithms;

import java.util.Arrays;
import java.util.List;

import allocation.Allocation;
import allocation.AllocationStrategy;
import preference.PreferenceRelation;
import preference.PreferenceTypeIdentifier;
import preference.scenario.Scenario;

public class RandomSerialDictatorship implements AllocationStrategy{

	private int counter;
	private int[][] tempAllocation;

	@Override
	public Allocation allocate(Scenario scenario) {
		if(!canApply(scenario)) {
			return null;
		}

		int agentCount = scenario.getAgentCount();
		int objCount   = scenario.getObjectCount();

		int[] allocationMap = new int[agentCount];
		boolean[] objectIsAllocated = new boolean[objCount];
		Arrays.setAll(allocationMap,(n)->(-1));
		counter = 0;
		tempAllocation = new int[agentCount][objCount];

		runAllocationRecursive(allocationMap,objectIsAllocated, scenario);
		double[][] alloc = new double[agentCount][objCount];
		for(int i=0;i<agentCount;i++) {
			for(int j=0;j<objCount;j++) {
				alloc[i][j] = tempAllocation[i][j]/(double)counter;
			}
		}

		return new Allocation(alloc);
	}

	private void runAllocationRecursive(int[] allocationMap,boolean[] objectIsAllocated, Scenario scenario) {
		boolean hasAssigned = false;
		for(int i=0;i<allocationMap.length;i++) {
			if(allocationMap[i] == -1) {

				PreferenceRelation rel = scenario.getAgentRelation(i);

				for(int j=0;j<rel.getGroupcount();j++) {
					int tmp = rel.getGroup(j)[0];
					if(!objectIsAllocated[tmp]) {
						//Object still free
						//Allocate
						allocationMap[i] = tmp;
						objectIsAllocated[tmp] = true;
						break;
					}
				}

				if(allocationMap[i] != -1) {
					hasAssigned = true;
					runAllocationRecursive(allocationMap,objectIsAllocated , scenario);
					objectIsAllocated[allocationMap[i]] = false;
					allocationMap[i] = -1;
				}
			}
		}
		if(!hasAssigned) {
			counter++;
			for(int i=0;i<allocationMap.length;i++) {
				if(allocationMap[i]!=-1) {
					tempAllocation[i][allocationMap[i]]++;
				}
			}
		}
	}

	@Override
	public List<String> getViolatedConstraints(Scenario scenario) {
		List<String> out = AllocationStrategy.super.getViolatedConstraints(scenario);

		for(int i=0;i<scenario.getAgentCount();i++) {
			PreferenceRelation rel = scenario.getAgentRelation(i);
			int type = PreferenceTypeIdentifier.getRelationType(rel);
			if(!PreferenceTypeIdentifier.isStrict(type)) {
				out.add("Preference relation of agent "+scenario.getAgent(i)+" has to be strict.");
			}
		}

		if(scenario.getAgentCount() > 10) {
			out.add("Problem size to big.");
		}
		return out;
	}
}
