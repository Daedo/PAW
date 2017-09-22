package axiom;

import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

import allocation.Allocation;
import allocation.algorithms.PopularAssignment;
import allocation.algorithms.PopularConvexSet;
import allocation.algorithms.convexLPSet.LPHelper;
import allocation.algorithms.convexLPSet.MatrixHelper;
import main.HelperFunctions;
import preference.PreferenceRelation;
import preference.scenario.Scenario;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class PopularEnvyChecker {
	public static boolean violatesEnvyHeuristic(Scenario scenario) {
		Allocation pEtoE = new PopularAssignment().allocate(scenario);
		if(StochasticDominance.getWeakEnvy(scenario, pEtoE.getData()).isEmpty()) {
			return false;
		}
		
		List<Allocation> convexSet = new PopularConvexSet().getAllAllocations(scenario);
		if(convexSet.isEmpty()) {
			return true;
		}
		
		for(int i=1;i<=convexSet.size();i++) {
			Vector<int[]> combos = MatrixHelper.nchoosek(IntStream.range(0, convexSet.size()).toArray(), i);
			for(int[] combo:combos) {
				double[][] allocSum = new double[scenario.getAgentCount()][scenario.getObjectCount()];

				for(int allocId:combo) {
					double[][] add = convexSet.get(allocId).getData();
					for(int j=0;j<scenario.getAgentCount();j++) {
						for(int k=0;k<scenario.getObjectCount();k++) {
							allocSum[j][k] += add[j][k]; 
						}
					}
				}
				
				for(int j=0;j<scenario.getAgentCount();j++) {
					for(int k=0;k<scenario.getObjectCount();k++) {
						allocSum[j][k] /= i; 
					}
				}
				
				if(StochasticDominance.getWeakEnvy(scenario, allocSum).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean violatesEnvyHeuristic(Scenario scenario, List<Allocation> allocs ) {
		Allocation pEtoE = new PopularAssignment().allocate(scenario);
		if(StochasticDominance.getWeakEnvy(scenario, pEtoE.getData()).isEmpty()) {
			return false;
		}
		
		List<Allocation> convexSet = allocs;
		if(convexSet.isEmpty()) {
			return true;
		}
		
		for(int i=1;i<=convexSet.size();i++) {
			Vector<int[]> combos = MatrixHelper.nchoosek(IntStream.range(0, convexSet.size()).toArray(), i);
			for(int[] combo:combos) {
				double[][] allocSum = new double[scenario.getAgentCount()][scenario.getObjectCount()];

				for(int allocId:combo) {
					double[][] add = convexSet.get(allocId).getData();
					for(int j=0;j<scenario.getAgentCount();j++) {
						for(int k=0;k<scenario.getObjectCount();k++) {
							allocSum[j][k] += add[j][k]; 
						}
					}
				}
				
				for(int j=0;j<scenario.getAgentCount();j++) {
					for(int k=0;k<scenario.getObjectCount();k++) {
						allocSum[j][k] /= i; 
					}
				}
				
				if(StochasticDominance.getWeakEnvy(scenario, allocSum).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean oldViolatesEnvy(Scenario scenario) {
		int agentCount = scenario.getAgentCount();
		int objectCount = scenario.getObjectCount();

		String[] alpha = LPHelper.getVarArray(agentCount, "A");
		String[] beta  = LPHelper.getVarArray(objectCount,"O");
		String[][] x   = LPHelper.getVarMatrix(agentCount, objectCount, "X");
		//String[][][] gamma = LPHelper.getVarTensor(agentCount,agentCount,objectCount, "G");
		//String gammaSum = "GSum";


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

		//Envy Freeness
		for(int i=0;i<agentCount;i++) {
			PreferenceRelation pref = scenario.getAgentRelation(i);
			for(int j=0;j<agentCount;j++) {
				if(i==j) {
					continue;
				}
				//Agent i preferes Alloc i over Alloc j
				for(int hStar=0;hStar<objectCount;hStar++) {
					LPWizardConstraint c = lpw.addConstraint("E"+constCount++, 0, "<=");

					int g = pref.getGroupindex(hStar);
					for(int hGroup = 0; hGroup<=g; hGroup++) {
						int[] group = pref.getGroup(hGroup);
						for(int h :group){
							c = c.plus(x[i][h]).plus(x[j][h],-1);
						}
					}
				}
			}
		}

		LPSolution sol = lpw.solve();

		double[][] alloc = new double[agentCount][objectCount];


		for(int i=0;i<agentCount;i++) {
			for(int j=0;j<objectCount;j++) {
				alloc[i][j] = HelperFunctions.round(sol.getDouble(x[i][j]),4);
			}
		}
		Allocation a =  new Allocation(alloc);


		boolean detectEnvy = !LPHelper.isFeasibleSolution(sol, lpw.getLP()); 
		boolean hasEnvy    = !StochasticDominance.getEnvy(scenario, a.getData()).isEmpty();
		if(detectEnvy) {
			hasEnvy = !StochasticDominance.getEnvy(scenario,new PopularAssignment().allocate(scenario).getData()).isEmpty();
			System.out.println("INFEASIBLE");
		}


		if(hasEnvy!=detectEnvy) {
			System.out.println("Difference!");
			System.out.println(scenario);
			System.out.println(a);
		}


		return !LPHelper.isFeasibleSolution(sol, lpw.getLP()); //&& sol.getDouble(gammaSum)>Main.LOW_F_POINT_PRECISION;
	}
}
