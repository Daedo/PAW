package allocation.algorithms;

import java.util.HashSet;
import java.util.List;
import allocation.Allocation;
import allocation.AllocationStrategy;
import allocation.algorithms.convexLPSet.LPHelper;
import preference.PreferenceRelation;
import preference.scenario.Scenario;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class PopularAssignment implements AllocationStrategy{
	private String[] alpha;
	private String[] beta;
	private String[][] x;
	private int agentCount;
	private int objectCount;

	@Override
	public List<String> getViolatedConstraints(Scenario scenario) {
		List<String> out = AllocationStrategy.super.getViolatedConstraints(scenario);

		if(scenario.getAgentCount()>scenario.getObjectCount()) {
			out.add("Number of agents must be less than or equal to the number of objects");
		}
		return out;
	}

	@Override
	public Allocation allocate(Scenario scenario) {
		if(!canApply(scenario)) {
			return null;
		}

		agentCount = scenario.getAgentCount();
		objectCount= scenario.getObjectCount();
		alpha = LPHelper.getVarArray(agentCount, "A");
		beta  = LPHelper.getVarArray(objectCount,"O");
		x     = LPHelper.getVarMatrix(agentCount, objectCount, "X");

		LPWizard lpw = getPopularWizard(scenario, true);
		LPSolution solution = lpw.solve();

		return createAllocationFromSolution(solution);
	}

	private LPWizard getPopularWizard(Scenario scenario, boolean enforceEqualTreatment) {		
		int constCount = 0;

		LPWizard lpw = new LPWizard();
		lpw.setMinProblem(true);
		for(int i=0;i<agentCount;i++) {
			PreferenceRelation pr = scenario.getAgentRelation(i);
			lpw.plus(alpha[i],"Alpha");

			//All Agents must be fully allocated 
			LPWizardConstraint c = lpw.addConstraint("C"+constCount++, 1, "=");
			for(int j=0;j<objectCount;j++) {
				c.plus(x[i][j]);

				//All allocations must be non negative
				lpw.addConstraint("C"+constCount++, 0, "<=").plus(x[i][j]);

				//Arc Constraint
				LPWizardConstraint arc = lpw.addConstraint("C"+constCount++, 0, ">=");
				arc.plus(alpha[i], -1);
				arc.plus(beta[j],  -1);
				for(int k=0;k<objectCount;k++) {
					arc.plus(x[i][k],pr.vote(j,k));
				}

			}	
		}

		for(int i=0;i<objectCount;i++) {
			lpw.plus(beta[i],"Beta");
			lpw.addConstraint("C"+constCount++, 0, "<=").plus(beta[i]);

			//All Objects must be at most fully allocated 
			LPWizardConstraint c = lpw.addConstraint("C"+constCount++, 1, ">=");
			for(int j=0;j<agentCount;j++) {
				c.plus(x[j][i]);
			}
		}

		if(!enforceEqualTreatment) {
			return lpw;
		}


		//Equal treatment of equals
		HashSet<Integer> visited  = new HashSet<>();
		for(int i=0;i<agentCount;i++) {
			if(visited.contains(i)) {
				continue;
			}

			PreferenceRelation relA = scenario.getAgentRelation(i);

			for(int j=i+1;j<agentCount;j++) {
				if(visited.contains(j)) {
					continue;
				}
				PreferenceRelation relB = scenario.getAgentRelation(j);

				if(relA.equals(relB)) {
					//Add E.t.o.e. Constraints
					for(int k=0;k<objectCount;k++) {
						lpw.addConstraint("C"+constCount++, 0, "=").plus(x[i][k],1).plus(x[j][k], -1);
					}
					visited.add(j);
				}
			}
			visited.add(i);
		}
		return lpw;
	}

	private Allocation createAllocationFromSolution(LPSolution solution) {		
		double[][] alloc = new double[agentCount][objectCount];
		for(int i=0;i<agentCount;i++) {
			for(int j=0;j<objectCount;j++) {
				alloc[i][j] = solution.getDouble(x[i][j]);
			}
		}

		return new Allocation(alloc);
	}
}

