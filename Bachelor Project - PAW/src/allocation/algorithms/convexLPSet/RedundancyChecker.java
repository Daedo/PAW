package allocation.algorithms.convexLPSet;

import static allocation.algorithms.convexLPSet.MatrixHelper.getColumn;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingIndices;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRow;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRows;

import org.ejml.simple.SimpleMatrix;

import main.Main;

public class RedundancyChecker {
	/**
	 * For reference see CheckRedundantConstraints.m
	 *  
	 * @param A
	 * @param b
	 * @return
	 */
	public static int[] checkRedundantConstraints(SimpleMatrix A, SimpleMatrix b) {
		
		int n = A.numRows();
		SimpleMatrix nonRedundant = new SimpleMatrix(n,1);
		nonRedundant.set(1);
		
		SimpleMatrix eye = SimpleMatrix.identity(n);
		for(int i=0;i<n;i++) {
			SimpleMatrix unitVector = getColumn(eye, i);
			int[] toKeep = getFullfillingRows(nonRedundant.minus(unitVector),d->d>Main.F_POINT_PRECISION);
			
			try {
				double fVal = LPHelper.solve(getRow(A,i).scale(-1),getRows(A,toKeep),getRows(b,toKeep));
				
				if(b.get(i)+fVal>=-Main.F_POINT_PRECISION) {//(-fVal <= b.get(i))) {
					nonRedundant.set(i,0);
				} 
			} catch (LPSetException e) {
				nonRedundant.set(i,1);
			}
		}
		
		
		return getFullfillingIndices(nonRedundant, d->Math.abs(d)>Main.F_POINT_PRECISION);
	}
}
