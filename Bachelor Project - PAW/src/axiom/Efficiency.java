package axiom;

import java.util.Optional;

import allocation.Allocation;
import main.Main;
import preference.PreferenceRelation;
import preference.scenario.Scenario;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class Efficiency {
	/**
	 * Tests if a given allocation is SD-Efficient using the LP from "Proving the Incompatibility of Efficiency and Strategyproofness via SMT Solving." 
	 * @param sc
	 * @param alloc
	 * @return
	 */
	public static Optional<Allocation> getDominatingAllocation(Scenario sc, Allocation alloc) {
		LPWizard wiz = new LPWizard();
		
		for(int i=0;i<sc.getAgentCount();i++) {
			for(int j=0;j<sc.getObjectCount();j++) {
				wiz = wiz.plus("r"+i+","+j);
			}
		}
		
		for(int i=0;i<sc.getAgentCount();i++) {
			PreferenceRelation rel = sc.getAgentRelation(i);
			
			for(int j=0;j<sc.getObjectCount();j++) {
				int groupIndex = rel.getGroupindex(j);
				
				double tSum = 0;
				for(int group=0;group<=groupIndex;group++) {
					int[] objects = rel.getGroup(group);
					for(int object: objects) {
						tSum += alloc.getValue(i, object);
					}
				}
				
				LPWizardConstraint c = wiz.addConstraint("C1 "+i+","+j+":", tSum, "=");
				
				for(int group=0;group<=groupIndex;group++) {
					int[] objects = rel.getGroup(group);
					for(int object: objects) {
						c = c.plus("q"+i+","+object);//.plus("r"+i+","+object,-1);
					}
				}
				c = c.plus("r"+i+","+j, -1);
			}
		}
		
		for(int i=0;i<sc.getAgentCount();i++) {
			LPWizardConstraint c = wiz.addConstraint("C2 "+i+":", 1, "=");
			for(int j=0;j<sc.getObjectCount();j++) {
				c = c.plus("q"+i+","+j);
			}
		}
		
		for(int i=0;i<sc.getObjectCount();i++) {
			LPWizardConstraint c = wiz.addConstraint("C3 "+i+":", 1, "=");
			for(int j=0;j<sc.getAgentCount();j++) {
				c = c.plus("q"+j+","+i);
			}
		}
		
		for(int i=0;i<sc.getAgentCount();i++) {
			for(int j=0;j<sc.getObjectCount();j++) {
				wiz.addConstraint("C4,"+i+","+j, 0, "<=").plus("r"+i+","+j);
				wiz.addConstraint("C5"+i+","+j, 0, "<=").plus("q"+i+","+j);
			}
		}
		
		wiz.setMinProblem(false);
		LPSolution sol = wiz.solve(SolverFactory.newDefault());
		if(Math.abs(sol.getObjectiveValue())<Main.LOW_F_POINT_PRECISION) {
			return Optional.empty();
		}
		
		double[][] dom = new double[sc.getAgentCount()][sc.getObjectCount()];
		for(int i=0;i<sc.getAgentCount();i++) {
			for(int j=0;j<sc.getObjectCount();j++) {
				dom[i][j] = sol.getDouble("q"+i+","+j);
			}
		}
		
		return Optional.of(new Allocation(dom));	
	}

	public static boolean isEfficient(Scenario sc,Allocation alloc) {
		return !getDominatingAllocation(sc, alloc).isPresent();
	}
}
