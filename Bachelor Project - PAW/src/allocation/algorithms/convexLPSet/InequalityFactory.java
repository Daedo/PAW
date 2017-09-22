package allocation.algorithms.convexLPSet;

import static allocation.algorithms.convexLPSet.MatrixHelper.getColumn;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingIndices;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRow;
import static allocation.algorithms.convexLPSet.MatrixHelper.removeColumns;
import static allocation.algorithms.convexLPSet.MatrixHelper.rref;
import static allocation.algorithms.convexLPSet.MatrixHelper.setRow;

import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

import main.HelperFunctions;
import main.Main;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

public class InequalityFactory {
	private static PreferenceInequallities temp;

	/**
	 * For reference see ConstructInequalities2.m
	 * @param scenario
	 * @return
	 */
	public static PreferenceInequallities constructInequalities(Scenario scenario) {
		temp = new PreferenceInequallities();

		int n = scenario.getAgentCount();
		int f = (int) HelperFunctions.factorial(n);

		// A Zeros(n! x n^2)
		SimpleMatrix A = new SimpleMatrix(f, n*n);

		//for every possible permutation c of the objects
		Vector<int[]> permutations = PermutationGenerator.getPermutations(n);
		
		for(int c = 0; c<permutations.size();c++) {
			int[] permutation = permutations.elementAt(c);

			for(int agent = 0;agent<n;agent++) {
				PreferenceRelation rel = scenario.getAgentRelation(agent);

				for(int object = 0;object<n;object++) {
					
					//if agent prefers  permutation over object
						//A(c,(agent-1)*n+permutationIndex) = -1
					//if agent prefers object over the permutation
						//A(c,(agent-1)*n+permutationIndex) = 1
					A.set(c, agent*n+object, rel.vote(permutation[agent],object));

				}
			}
		}

		//APos  = -identity(n^2)
		SimpleMatrix APos = SimpleMatrix.identity(n*n).negative();
		//A 	= A;APos
		A = A.combine(A.numRows(),0,APos);
		//b 	= zeros(n! x 1)
		//SimpleMatrix b = new SimpleMatrix(f, 1);
		//bPos 	= zeros(n^2 x 1)
		//SimpleMatrix bPos = new SimpleMatrix(n*n,1);
		//b 	= b;bPos
		SimpleMatrix b = new SimpleMatrix(f+n*n,1);

		//Aeq	= zeros(2n x n^2)
		SimpleMatrix Aeq = new SimpleMatrix(2*n,n*n);
		//for row (1 to n)
		for(int row = 0;row < n;row++) {
			//Aeq set Row = zeros(1 x (row-1)*n),ones(1 x n),zeros(1 x n^2-n*row)
			double[] r = new double[n*n];
			for(int i= row*n; i<(row*n+n);i++) {
				r[i] = 1;
			}
			Aeq.setRow(row, 0, r);
		}

		for(int agent = 0;agent<n;agent++) {
			//Aeq (n+1 to end, (agent-1)*n+(1 to n)) = identity(n);
			Aeq.insertIntoThis(n, agent*n, SimpleMatrix.identity(n));
		}

		//beq = ones(2n x 1)
		SimpleMatrix beq = new SimpleMatrix(2*n,1);
		beq.set(1);

		temp.AEquality = Aeq;
		temp.bEquality = beq;

		//IneqEq2Ineq
		PreferenceInequallities t2 = reduceInequalities(A,b,Aeq,beq);
		temp.AInequality = t2.AInequality;
		temp.bInequality = t2.bInequality;
		temp.cleared   	 = t2.cleared;

		return temp;
	}

	/**
	 * For reference see IneqEq2Ineq.m
	 * @param A
	 * @param b
	 * @param Aeq
	 * @param beq
	 * @return
	 */
	public static PreferenceInequallities reduceInequalities(SimpleMatrix A, SimpleMatrix b, SimpleMatrix Aeq, SimpleMatrix beq) {
		SimpleMatrix lgs = Aeq.combine(0, Aeq.numCols(), beq);
		
		SimpleMatrix B = rref(lgs, false);
		
		SimpleMatrix AeqNew = B.extractMatrix(0, B.numRows(), 0, B.numCols()-1);
		SimpleMatrix beqNew = getColumn(B, B.numCols()-1);

		SimpleMatrix ANew = A.copy();
		SimpleMatrix bNew = b.copy();

		Vector<Integer> cleared = new Vector<>(AeqNew.numRows());
		
		for(int row = 0;row < AeqNew.numRows();row++) {
			SimpleMatrix currentRow = getRow(AeqNew,row);
			int[] clearingArray = getFullfillingIndices(currentRow,d->Math.abs(d)>Main.F_POINT_PRECISION);
			if(clearingArray.length == 0) {
				continue;
			}
			int toClear = clearingArray[0];

			for(int j = 0; j<ANew.numRows(); j++) {
				double bVal = bNew.get(j)-ANew.get(j, toClear)/AeqNew.get(row, toClear)*beqNew.get(row);
				bNew.set(j, bVal);
				
				double scaler = ANew.get(j,toClear)/AeqNew.get(row,toClear);
				SimpleMatrix newRow = getRow(ANew,j).minus(getRow(AeqNew,row).scale(scaler));
				setRow(ANew,j,newRow);
			}
			cleared.add(toClear);
		}

		//Remove all cleared Columns 
		int[] clearedArray = cleared.stream().mapToInt(Integer::intValue).toArray();
		ANew = removeColumns(ANew, clearedArray);

		PreferenceInequallities out = new PreferenceInequallities();
		out.AInequality = ANew;
		out.bInequality = bNew;
		out.cleared   = cleared;
		return out;
	}
}
