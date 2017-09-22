package axiom;

import java.util.Vector;

import main.Main;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

public class StochasticDominance {
	/**
	 * Determines if allocationA >=(SD)_(i) allocationB
	 * @param rel
	 * @param allocationA
	 * @param allocationB
	 * @return
	 */
	public static boolean isDominant(PreferenceRelation rel, double[] allocationA,double[] allocationB) {
		if(rel.getRelationSize()!=allocationA.length || allocationA.length!=allocationB.length) {
			throw new IllegalArgumentException("Relation and assignments must be the same size");
		}

		//AssignmentA is preferred iff
		for(int i = 0;i<rel.getRelationSize();i++) {
			//For all Objects h*
			int groupIndex = rel.getGroupindex(i);
			//Sum of all AllocationA(h) with h>=h*
			//Is bigger or equal to
			//Sum of all AllocationB(h) with h>=h*
			double sumA = 0;
			double sumB = 0;

			for(int j=0;j<=groupIndex;j++) {
				int[] objects = rel.getGroup(j);
				for(int object: objects) {
					sumA += allocationA[object];
					sumB += allocationB[object];
				}
			}
			if(sumA-sumB<-Main.LOW_F_POINT_PRECISION) {
				//We found a violation of stochastic dominance
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines if allocationA >(SD)_(i) allocationB
	 * @param rel
	 * @param allocationA
	 * @param allocationB
	 * @return
	 */
	public static boolean isStrictlyDominant(PreferenceRelation rel, double[] allocationA,double[] allocationB) {
		return isDominant(rel, allocationA, allocationB) && !isDominant(rel, allocationB, allocationA);
	}
	
	/**
	 * Returns all violations of weak envy given an allocation.
	 * 
	 * @param scenario
	 * @param allocation
	 * @return
	 */
	public static Vector<ViolationPair> getWeakEnvy(Scenario scenario, double[][] allocation) {
		Vector<ViolationPair> envy = new Vector<>();
		
		for(int i=0;i<scenario.getAgentCount();i++) {
			PreferenceRelation agentRelation = scenario.getAgentRelation(i);
			
			for(int j=0;j<scenario.getAgentCount();j++) {
				if(i==j) {
					continue;
				}
				if(isStrictlyDominant(agentRelation, allocation[j], allocation[i])) {
					//Agent i strictly prefers allocation of Agent j
					envy.add(new ViolationPair(i, j));
				}
			}
		}
		return envy;
	}

	/**
	 * Returns all violations of envy given an allocation.
	 * @param scenario
	 * @param allocation
	 * @return
	 */
	public static Vector<ViolationPair> getEnvy(Scenario scenario, double[][] allocation) {
		Vector<ViolationPair> envy = new Vector<>();
		
		for(int i=0;i<scenario.getAgentCount();i++) {
			PreferenceRelation agentRelation = scenario.getAgentRelation(i);
			
			for(int j=0;j<scenario.getAgentCount();j++) {
				if(i==j) {
					continue;
				}
				if(!isDominant(agentRelation, allocation[i], allocation[j])) {
					//Agent i does not prefer his allocation over agent j's
					envy.addElement(new ViolationPair(i, j));
				}
			}
		}
		return envy;
	}
	
	/**
	 * Helper class. Ordered pair of agents, that violate a constraint.
	 * @author Dominik
	 *
	 */
	public static class ViolationPair {
		public int agentA,agentB;
		public ViolationPair(int a, int b) {
			agentA = a;
			agentB = b;
		}
	}

}
