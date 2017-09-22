package allocation.algorithms.convexLPSet;

import java.util.ArrayList;
import java.util.HashMap;

import org.ejml.simple.SimpleMatrix;

import scpsolver.constraints.Constraint;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;
import scpsolver.problems.LinearProgram;

/**
 * Helper Class for Linear Programming
 * 
 * @author Dominik
 */
public class LPHelper {
	/**
	 * Check if the solution of a LP is feasible. Given the form (min b^T*x s.t. A*x <= c).
	 * 
	 * @param sol
	 * @param lp
	 * @return
	 */
	public static boolean isFeasibleSolution(LPSolution sol,LinearProgram lp) {
		double[] v = valVector(sol, lp.getIndexmap());
		return lp.isFeasable(v);
	}
	
	/**
	 * Check if a LP is bounded.
	 * 
	 * @param lp
	 * @return
	 */
	public static boolean isBounded(LinearProgram lp) {
		LinearProgram dual = getDual(lp);
		LinearProgramSolver solver = SolverFactory.newDefault();
		double[] solution = solver.solve(dual);
		
		return dual.isFeasable(solution);
	}
	
	/**
	 * Returns the dual of a given LP of the form min b^T*x s.t. A*x <= c.
	 * 
	 * @param lp
	 * @return
	 */
	private static LinearProgram getDual(LinearProgram lp) {
		//Primal:	min b^T*x s.t. A*x <= c
		ArrayList<Constraint> con = lp.getConstraints();
		double[]   b = lp.getC();
		double[][] A	= new double[con.size()][];
		double[]   c	= new double[con.size()];
		
		for(int i=0;i<con.size();i++) {
			LinearSmallerThanEqualsConstraint stec = (LinearSmallerThanEqualsConstraint) con.get(i);
			
			A[i] = stec.getC();
			c[i] = stec.getRHS();
		}
		
		//Dual:		max c^T*y s.t. A^T*y = b y<=0
		LinearProgram out = new LinearProgram(c);
		out.setMinProblem(false);
		
		for(int i=0;i<b.length;i++) {
			double[] at = new double[con.size()];
			for(int j=0;j<con.size();j++) {
				at[j] = A[j][i];
			}
			
			out.addConstraint(new LinearEqualsConstraint(at, b[i], "c"+i));
		}
		
		out.setUpperbound(new double[c.length]);
		return out;
	}

	/**
	 * Helper function for isFeasibleSolution. Generates an array of the solution values.
	 * @param sol
	 * @param indexMap
	 * @return
	 */
	private static double[] valVector(LPSolution sol, HashMap<String, Integer> indexMap) {
		double[] out = new double[indexMap.size()];
		for(String key : indexMap.keySet()) {
			int index = indexMap.get(key);
			out[index] = sol.getDouble(key);
		}
		
		return out;
	}

	/**
	 * Solve the LP min f^T*x s.t. A*x <= b
	 * @param f
	 * @param A
	 * @param b
	 * @return
	 * @throws LPSetException if there is no bounded and feasible solution.
	 */
	public static double solve(SimpleMatrix f, SimpleMatrix A, SimpleMatrix b)  throws LPSetException {
		LPWizard wizard = new LPWizard();
		wizard.setMinProblem(true);
		
		for(int i=0;i<f.numCols();i++) {
			wizard = wizard.plus("X"+i, f.get(i));
		}
		
		
		for(int i=0;i<A.numRows();i++) {
			LPWizardConstraint c = wizard.addConstraint("C"+i, b.get(i), ">=");
			for(int j=0;j<A.numCols();j++) {
				c.plus("X"+j, A.get(i, j));
			}
		}
		LPSolution sol = wizard.solve();
		double[] x = new double[A.numCols()];
		for(int i=0;i<A.numCols();i++) {
			x[i] = sol.getDouble("X"+i);
		}
		
		
		if(!LPHelper.isFeasibleSolution(sol, wizard.getLP())) {
			throw new LPSetException("LP can't be solved: No feasable Solution");
		}
		
		if(!LPHelper.isBounded(wizard.getLP())) {
			throw new LPSetException("LP is unbounded");
		}
		
		return sol.getObjectiveValue();
	}

	//Helper Methods. Create String arrays.
	/**
	 * Create a 1D String array.
	 * @param count
	 * @param base The prefix of every String.
	 * @return
	 */
	public static String[] getVarArray(int count,String base) {
		String[] out = new String[count];
		for(int i=0;i<out.length;i++) {
			out[i] = base+i;
		}
		return out;
	}

	/**
	 * Create a 2D String array.
	 * 
	 * @param countA
	 * @param countB
	 * @param base The prefix of every String.
	 * @return
	 */
	public static String[][] getVarMatrix(int countA,int countB,String base) {
		String[][] out = new String[countA][countB];
		for(int i=0;i<countA;i++) {
			for(int j=0;j<countB;j++) {
				out[i][j] = base+i+","+j;
			}
		}
	
		return out;
	}

	/**
	 * Create a 3D String array.
	 * 
	 * @param countA
	 * @param countB
	 * @param countC
	 * @param base The prefix of every String.
	 * @return
	 */
	public static String[][][] getVarTensor(int countA, int countB, int countC, String base) {
		String[][][] out = new String[countA][countB][countC];
		for(int i=0;i<countA;i++) {
			for(int j=0;j<countB;j++) {
				for(int k=0;k<countC;k++) {
					out[i][j][k] = base+i+","+j+","+k;
				}
			}
		}
	
		return out;
	}
}
