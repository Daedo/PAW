package allocation.algorithms.convexLPSet;

import static allocation.algorithms.convexLPSet.MatrixHelper.*;

import java.util.Arrays;
import java.util.HashSet;

import org.ejml.simple.SimpleMatrix;

import main.Main;

public class Considerer {
	/**
	 * For reference see ConsiderEquations.m
	 * 
	 * @param x
	 * @param Aeq
	 * @param beq
	 * @param cleared
	 * @return
	 */
	public static SimpleMatrix ConsiderEquations(SimpleMatrix x, SimpleMatrix Aeq, SimpleMatrix beq, int[] cleared) {
		SimpleMatrix B = rref(Aeq.combine(0,Aeq.numCols(),beq),true);
		
		int[] nonZeroRows = getFullfillingRows(B, d-> Math.abs(d)>Main.F_POINT_PRECISION);
		B = getRows(B, nonZeroRows);
		
		Arrays.sort(cleared);
		SimpleMatrix xNew = new SimpleMatrix(x.numRows()+cleared.length, x.numCols());
		
		HashSet<Integer> set = new HashSet<>();
		for(int i=0;i<x.numRows()+B.numRows();i++) {
			set.add(i);
		}
		
		for(int i=0;i<cleared.length;i++) {
			set.remove(cleared[i]);
		}
		int[] original = set.stream().mapToInt(Integer::intValue).sorted().toArray();
		for(int i=0;i<original.length;i++) {
			int o = original[i];
			setRow(xNew, o, getRow(x,i));
		}
		
		for(int i=0;i<x.numCols();i++) {
			SimpleMatrix clearedPart = getColumns(Aeq, cleared).solve(beq.minus(getColumns(Aeq, original).mult(getColumn(x, i))));
			for(int j=0;j<cleared.length;j++) {
				xNew.set(cleared[j], i, clearedPart.get(j));
			}
		}
		return xNew;
	}
}
