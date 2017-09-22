package allocation.algorithms.convexLPSet;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;
import java.util.function.DoublePredicate;
import java.util.stream.IntStream;

import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

import main.Main;

public class MatrixHelper {	
	public static Vector<int[]> nchoosek(int[] data,int k) {
		return nchoosek(data,0,k,k);
	}
	
	private static Vector<int[]> nchoosek(int[] data,int start, int k,int kMax) {
		Vector<int[]> out = new Vector<>();
		
		if(start > (data.length-k)) {
			return out;
		}
		
		if(k==0) {
			int[] add = new int[kMax];
			out.add(add);
			return out;
		}
		
		Vector<int[]> subA = nchoosek(data, start+1, k  ,kMax);
		Vector<int[]> subB = nchoosek(data, start+1, k-1,kMax);
		for(int[] a:subB) {
			a[kMax-k] = data[start];
		}
		out.addAll(subA);
		out.addAll(subB);
		return out;
	}
	
	public static Vector<Integer> arrayDiff(int[] a, int [] b) {
		HashSet<Integer> set = new HashSet<>();
		for(int i : a) {
			set.add(i);
		}
		
		for(int i:b) {
			set.remove(i);
		}
		return new Vector<>(set);
	}
	
	public static SimpleMatrix toVector(double[] data) {
		return new SimpleMatrix(new double[][]{data}).transpose();
	}
	
	public static void setRow(SimpleMatrix mat, int index,SimpleMatrix row) {
		mat.setRow(index, 0, row.getMatrix().data);
	}
	
	public static SimpleMatrix getRow(SimpleMatrix mat,int row) {
		return mat.extractMatrix(row, row+1, 0, mat.numCols());
	}
	
	public static void setColumn(SimpleMatrix mat, int index,SimpleMatrix col) {
		mat.setColumn(index, 0, col.getMatrix().data);
	}
	
	public static SimpleMatrix getColumn(SimpleMatrix mat,int column) {
		return mat.extractMatrix(0, mat.numRows(), column, column+1);
	}

	public static SimpleMatrix getColumns(SimpleMatrix mat, int[] columns) {
		SimpleMatrix out = new SimpleMatrix(mat.numRows(), columns.length);
		for(int i=0;i<columns.length;i++) {
			setColumn(out, i, getColumn(mat, columns[i]));
		}
		return out;
	}
	
	public static SimpleMatrix getRows(SimpleMatrix mat, int[] rows) {
		SimpleMatrix out = new SimpleMatrix(rows.length,mat.numCols());
		for(int i=0;i<rows.length;i++) {
			setRow(out, i, getRow(mat, rows[i]));
		}
		return out;
	}

	public static SimpleMatrix removeColumn(SimpleMatrix mat,int column) {
		int[] columns = IntStream.range(0, mat.numCols()).filter(n->n!=column).toArray();
		return getColumns(mat, columns);
	}
	
	public static SimpleMatrix removeRow(SimpleMatrix mat,int row) {
		int[] rows = IntStream.range(0, mat.numRows()).filter(n->n!=row).toArray();
		return getRows(mat, rows);
	}
	
	public static SimpleMatrix filterRows(SimpleMatrix mat,boolean[] logical) {
		int[] rowIndex = IntStream.range(0, mat.numRows()).filter(n->logical[n]).toArray();
		return getRows(mat, rowIndex);
	}
	
	public static SimpleMatrix filterCols(SimpleMatrix mat,boolean[] logical) {
		int[] colIndex = IntStream.range(0, mat.numCols()).filter(n->logical[n]).toArray();
		return getColumns(mat, colIndex);
	}
	
	public static int rank(SimpleMatrix mat ) {
		if(mat.numCols() == 0 || mat.numRows()==0) {
			return 0;
		}
		return mat.svd(true).rank();
	}
	
	public static Vector<SimpleMatrix> nullBasis(SimpleMatrix mat) {
		Vector<SimpleMatrix> out = new Vector<>();
		
		if(mat.numRows()==0) {
			SimpleMatrix ID = SimpleMatrix.identity(mat.numCols());
			for(int i=0;i<mat.numCols();i++) {
				out.addElement(getColumn(ID, i));
			}
			
			return out; 
		}
		
		if(mat.numCols()==0) {
			return out;
		}
		
		int rank = rank(mat);
		SimpleMatrix V = mat.svd(false).getV();
		for(int i = rank;i<V.numCols();i++) {
			out.addElement(getColumn(V, i));
		}
		
		return out;
	}

	public static SimpleMatrix rref(SimpleMatrix mat, boolean keepZeroRows) {
		SimpleMatrix roundMat = matRound(mat, 6);
		SimpleMatrix rref = new SimpleMatrix(CommonOps.rref(roundMat.getMatrix(), -1, null));
		int rank = rank(mat);
		
		//Since rref dosn't fully reduce the matrix we do it by hand
		for(int i=1;i<rank;i++) {
			SimpleMatrix reducingRow = getRow(rref,i);
			int firstNonZero = i;
			for(int k=i;k<reducingRow.numCols();k++) {
				double val = reducingRow.get(k);
				if(Math.abs(val)>Main.F_POINT_PRECISION) {
					if(Math.abs(val - 1) > Main.F_POINT_PRECISION) {
						reducingRow = reducingRow.scale(1/val);
						setRow(rref, i, reducingRow);
					}
					
					firstNonZero = k;
					break;
				}
			}
			
			for(int j=0;j<rref.numRows();j++) {
				if(j==i) {
					continue;
				}
				
				double val = rref.get(j,firstNonZero);
				if(Math.abs(val)>Main.F_POINT_PRECISION) {
					SimpleMatrix testRow = getRow(rref,j);
					testRow = testRow.minus(reducingRow.scale(val));
					setRow(rref, j, testRow);
				}
			}
		}
		
		
		if(keepZeroRows) {
			//Set rows to Zero since rref doesn't eliminate them
			for(int i=rank;i<rref.numRows();i++) {
				setRow(rref, i, new SimpleMatrix(1, rref.numCols()));
			}
			return rref;
		}
		return rref.extractMatrix(0, rank, 0, rref.numCols());
	}
	
	public static boolean isPartOfInvertible(SimpleMatrix mat) {
		SimpleMatrix r = rref(mat, true);
		int[] rows = getFullfillingRows(r, d->Math.abs(d)>Main.F_POINT_PRECISION);
		
		return rows.length == r.numRows();//rank(mat)==mat.numRows();
	}
	
	public static SimpleMatrix matAbs(SimpleMatrix mat) {
		SimpleMatrix out = new SimpleMatrix(mat.numRows(), mat.numCols());
		for(int i=0;i<mat.numRows();i++) {
			for(int j=0;j<mat.numCols();j++) {
				out.set(i, j, Math.abs(mat.get(i, j)));
			}
		}
		return out;
	}
	
	public static SimpleMatrix matRound(SimpleMatrix extremalPoints, int n) {
		SimpleMatrix out = extremalPoints.copy();
		
		String d = ".";
		for(int i=0;i<n;i++) {
			d+="#";
		}
		
		DecimalFormat df = new DecimalFormat("#"+d);
		DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(sym);
		df.setRoundingMode(RoundingMode.HALF_UP);

		for(int i=0;i<out.getNumElements();i++) {
			double val = out.get(i);
			if(Math.abs(val)<Main.F_POINT_PRECISION) {
				val = 0;
			}
			
			out.set(i, Double.parseDouble(df.format(val)));
		}
		
		return out;
	}
	
	public static SimpleMatrix removeRows(SimpleMatrix mat, int[] ind) {
		Vector<Integer> rows = new Vector<>();
		for(int i=0;i<mat.numRows();i++) {
			boolean add = true;
			for(int j=0;j<ind.length;j++) {
				if(i == ind[j]) {
					add = false;
					break;
				}
			}
			if(add) {
				rows.addElement(i);
			}
		}
		
		return getRows(mat, rows.stream().mapToInt(Integer::intValue).toArray());
	}
	
	public static SimpleMatrix removeColumns(SimpleMatrix mat, int[] ind) {
		Vector<Integer> cols = new Vector<>();
		for(int i=0;i<mat.numCols();i++) {
			boolean add = true;
			for(int j=0;j<ind.length;j++) {
				if(i == ind[j]) {
					add = false;
					break;
				}
			}
			if(add) {
				cols.addElement(i);
			}
		}
		
		return getColumns(mat, cols.stream().mapToInt(Integer::intValue).toArray());
	}
	
	public static SimpleMatrix sortColumns(SimpleMatrix mat) {
		
		SimpleMatrix[] columns = new SimpleMatrix[mat.numCols()];
		for(int i=0;i<mat.numCols();i++)  {
			columns[i] = getColumn(mat, i);
		}
		
		Comparator<SimpleMatrix> matCompare = (a,b) -> {
			for(int i=0;i<a.numRows();i++) {
				int c = -Double.compare(a.get(i), b.get(i));
				if(c !=0) {
					return c;
				}
			}
			return 0;
		};
		
		
		Arrays.sort(columns, matCompare);
		
		
		SimpleMatrix out = new SimpleMatrix(mat.numRows(), mat.numCols());
		for(int i=0;i<columns.length;i++) {
			out.setColumn(i, 0, columns[i].getMatrix().data);
		}
		
		return out;
	}
	
	public static boolean matchAll(SimpleMatrix mat, DoublePredicate filter) {
		for(int i=0;i<mat.getNumElements();i++) {
			if(!filter.test(mat.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static int[] getFullfillingIndices(SimpleMatrix mat, DoublePredicate filter) {
		Vector<Integer> out = new Vector<>();
		for(int i=0;i<mat.getNumElements();i++) {
			if(filter.test(mat.get(i))) {
				out.addElement(i);
			}
		}
		
		return out.stream().mapToInt(Integer::intValue).toArray();
	}

	public static int[] getFullfillingRows(SimpleMatrix mat, DoublePredicate filter) {
		Vector<Integer> out = new Vector<>();
		for(int i=0;i<mat.numRows();i++) {
			for(int j=0;j<mat.numCols();j++) {
				if(filter.test(mat.get(i, j))) {
					out.addElement(i);
					break;
				}
			}
		}
		return out.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public static int[] getTrues(boolean[] logical) {
		Vector<Integer> out = new Vector<>();
		for(int i=0;i<logical.length;i++) {
			if(logical[i]) {
				out.addElement(i);
			}
		}
		return out.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public static int[] truthReduction(int[] data, boolean[] logical) {
		int[] trues = getTrues(logical);
		
		int[] out = new int[trues.length];
		for(int i=0;i<trues.length;i++) {
			out[i] = data[trues[i]];
		}
		return out;
	}
	
	public static SimpleMatrix subVector(SimpleMatrix mat,int[] indices) {
		SimpleMatrix out = new SimpleMatrix(indices.length,1);
		for(int i=0;i<indices.length;i++) {
			out.set(i, mat.get(indices[i]));
		}
		
		return out;
	}
	
	public static MinData matMin(SimpleMatrix mat) {
		double minElement = Double.POSITIVE_INFINITY;
		Vector<Integer> minIndices = new Vector<>();
		
		
		for(int i=0;i<mat.getNumElements();i++) {
			double elem = mat.get(i);
			
			if(Math.abs(elem-minElement)<Main.F_POINT_PRECISION) {
				minIndices.addElement(i);
			} else if(elem < minElement) {
				minIndices.clear();
				minElement = elem;
				minIndices.addElement(i);
			}
		}
		
		MinData out = new MinData(); 
		out.minElement = minElement;
		out.minIdices = minIndices.stream().mapToInt(Integer::intValue).toArray();
		return out;
	}
	
	public static class MinData {
		public double minElement;
		public int[] minIdices;
	}
}
