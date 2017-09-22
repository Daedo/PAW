package allocation.algorithms.convexLPSet;

import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRow;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.getTrues;
import static allocation.algorithms.convexLPSet.MatrixHelper.matAbs;
import static allocation.algorithms.convexLPSet.MatrixHelper.matchAll;
import static allocation.algorithms.convexLPSet.MatrixHelper.removeRow;
import static allocation.algorithms.convexLPSet.MatrixHelper.removeRows;

import java.util.Arrays;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

import main.Main;

public class EquationSearcher {
	/**
	 * Wrapper for the private searchEquations method.
	 * 
	 * @param A
	 * @param b
	 * @return
	 * @throws LPSetException
	 */
	public static EquationSearchResult searchEquations(SimpleMatrix A, SimpleMatrix b) throws LPSetException {
		return searchEquations(A, b, null);
	}
	
	/**
	 * For reference see SearchEquations.m
	 * 
	 * @param A
	 * @param b
	 * @param toCheckLog
	 * @return
	 * @throws LPSetException
	 */
	private static EquationSearchResult searchEquations(SimpleMatrix A, SimpleMatrix b, boolean[] toCheckLog) throws LPSetException {
		boolean[] equationLog = new boolean[A.numRows()];
		
		if(toCheckLog == null) {
			toCheckLog  = new boolean[A.numRows()];
			Arrays.fill(toCheckLog, true);
		}
		
		int[] toKeepInd = getFullfillingRows(matAbs(A),d->d>Main.LOW_F_POINT_PRECISION);
		
		if(matchAll(removeRows(b,toKeepInd), d-> (d > -Main.LOW_F_POINT_PRECISION))) {
			A = getRows(A, toKeepInd);
			b = getRows(b, toKeepInd);
			boolean [] newToCheckLog = new boolean[toKeepInd.length];
			for(int i=0;i<toKeepInd.length;i++) {
				newToCheckLog[i] = toCheckLog[toKeepInd[i]];
			}
			toCheckLog = newToCheckLog;
		} else {
			throw new LPSetException("No feasible point");
		}
		
		int[] toCheckInd = getTrues(toCheckLog);
		
		for(int i=0;i<toCheckInd.length;i++) {
			boolean sucess = true;
			double fMax = 0;
			
			try {
				fMax =  LPHelper.solve(getRow(A, toCheckInd[i]),removeRow(A, toCheckInd[i]), removeRow(b, toCheckInd[i]));
			} catch (LPSetException e) {
				sucess = false;
			}
			
			double bVal = b.get(toCheckInd[i])-fMax;
			
			if(sucess && bVal<Main.LOW_F_POINT_PRECISION) {
				equationLog[toKeepInd[toCheckInd[i]]] = true;
				SimpleMatrix ADash = removeRow(A, toCheckInd[i]);
				SimpleMatrix bDash = removeRow(b, toCheckInd[i]);
				SimpleMatrix ARow  = getRow(A, toCheckInd[i]);
				SimpleMatrix bRow  = getRow(b, toCheckInd[i]);
				
				PreferenceInequallities inEq = InequalityFactory.reduceInequalities(ADash, bDash, ARow, bRow);
				SimpleMatrix SubANew = new SimpleMatrix(inEq.AInequality);
				SimpleMatrix SubbNew = new SimpleMatrix(inEq.bInequality);
				//Line 46
				boolean[] tempToCheckLog = toCheckLog;
				toCheckLog = new boolean[tempToCheckLog.length-1];
				for(int j=0;j<toCheckLog.length;j++) {
					if(j<toCheckInd[i]) {
						toCheckLog[j] = tempToCheckLog[j];
					} else {
						toCheckLog[j] = tempToCheckLog[j+1];
					}
				}
				//Line 49
				EquationSearchResult subResult = searchEquations(SubANew,SubbNew,toCheckLog);
				
				int[] trues = getTrues(toCheckLog);
				for(int j=i+1;j<toCheckInd.length;j++) {
					//...yeah... Thats a thing... Apparently this "puts the results together"...
					//Don't ask me what it does. I extrapolated... 
					//I Think it appends the sub result after i to the equation log
					equationLog[toKeepInd[toCheckInd[j]]] = subResult.searchLog[trues[j-(i+1)]];
				}
				//Cleared = [TempCleared;SubCleared + (SubCleared >= TempCleared)];
				//Why?
				assert (inEq.cleared.size() == 1);
				if(!inEq.cleared.isEmpty()) {
					int tempCleared = inEq.cleared.get(0);
					for(int j=0;j<subResult.cleared.size();j++) {
						int subClearedVal = subResult.cleared.get(j).intValue();
						if(subClearedVal >=tempCleared) {
							subResult.cleared.set(j, subClearedVal+1);
						}
					}
					subResult.cleared.insertElementAt(tempCleared, 0);
				}
				
				EquationSearchResult out = new EquationSearchResult();
				out.cleared = subResult.cleared;
				out.searchLog = equationLog;
				return out;
				
			}
			toCheckLog[toCheckInd[i]] = false;
		}
		
		EquationSearchResult out = new EquationSearchResult();
		out.cleared = new Vector<>();
		out.searchLog = equationLog;
		return out;
	}

	public static class EquationSearchResult {
		public boolean[] searchLog;
		Vector<Integer> cleared;
	}
}
