package allocation.algorithms.convexLPSet;

import static allocation.algorithms.convexLPSet.MatrixHelper.filterRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingIndices;
import static allocation.algorithms.convexLPSet.MatrixHelper.matAbs;
import static allocation.algorithms.convexLPSet.MatrixHelper.matMin;
import static allocation.algorithms.convexLPSet.MatrixHelper.nullBasis;
import static allocation.algorithms.convexLPSet.MatrixHelper.subVector;
import static allocation.algorithms.convexLPSet.MatrixHelper.toVector;

import java.util.Arrays;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

import allocation.algorithms.convexLPSet.MatrixHelper.MinData;
import main.Main;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class StartingCornerCalculator {

	/**
	 * For reference see CalculateStartingCorner.m
	 * 
	 * @param A
	 * @param b
	 * @return
	 * @throws LPSetException
	 */
	public static SimpleMatrix CalculateStartingCorner(SimpleMatrix A, SimpleMatrix b) throws LPSetException {
		int m = A.numRows();
		int n = A.numCols();
		
		//SimpleMatrix ones = new SimpleMatrix(m, 1);
		//ones.set(1);
		//SimpleMatrix AOpt =  A.combine(0, A.numCols(), ones.negative());
		//SimpleMatrix bOpt = new SimpleMatrix(bInequality);
		
		SimpleMatrix x = toVector(solve(A,b));
		boolean[] savedRestrictionLog = new boolean[m];
		Arrays.fill(savedRestrictionLog, false);
		
		Vector<SimpleMatrix> orthogornalSpace = nullBasis(filterRows(A, savedRestrictionLog));
		
		int i=0;
		while(!orthogornalSpace.isEmpty() && (i<n)) {
			i++;
			SimpleMatrix iterationVector = orthogornalSpace.get(0);
			SimpleMatrix stock = matAbs(b.minus(A.mult(x)));
			SimpleMatrix cost = A.mult(iterationVector);
			
			int[] restrictionInd = getFullfillingIndices(cost, d -> d>Main.F_POINT_PRECISION);
			if(restrictionInd.length==0) {
				throw new LPSetException("No well-defined problem!");
			}
			
			SimpleMatrix reduced = subVector(stock, restrictionInd).elementDiv(subVector(cost, restrictionInd));
			MinData min = matMin(reduced);
			double stepLenght = Math.max(0, min.minElement);
			
			x = x.plus(iterationVector.scale(stepLenght));
			
			for(int j : min.minIdices) {
				int index = restrictionInd[j];
				savedRestrictionLog[index] = true;
			}
			
			orthogornalSpace = nullBasis(filterRows(A, savedRestrictionLog));
		}
		
		orthogornalSpace = nullBasis(filterRows(A, savedRestrictionLog));
		if(!orthogornalSpace.isEmpty()) {
			throw new LPSetException("Could not find a corner");
		}
		
		return x;
	}

	private static double[] solve(SimpleMatrix aOpt, SimpleMatrix bOpt) throws LPSetException {
		LPWizard wizard = new LPWizard();
		wizard.setMinProblem(true);
		
		for(int i=0;i<aOpt.numRows();i++) {
			LPWizardConstraint c = wizard.addConstraint("C"+i, bOpt.get(i), ">=");
			for(int j=0;j<aOpt.numCols();j++) {
				c = c.plus("X"+j, aOpt.get(i, j));
			}
		}
		LPSolution sol = wizard.solve();
		double[] x = new double[aOpt.numCols()];
		for(int i=0;i<aOpt.numCols();i++) {
			x[i] = sol.getDouble("X"+i);
		}
		
		if(!LPHelper.isFeasibleSolution(sol, wizard.getLP())) {
			throw new LPSetException("LP can't be solved: No Infeasable Solution");
		}
		
		return x;
	}
	
}
