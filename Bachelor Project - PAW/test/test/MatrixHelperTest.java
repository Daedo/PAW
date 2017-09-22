package test;

import static org.junit.Assert.*;
import static allocation.algorithms.convexLPSet.MatrixHelper.*;

import java.util.Arrays;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;
import org.junit.BeforeClass;
import org.junit.Test;

public class MatrixHelperTest {

	static SimpleMatrix A;
	static boolean[] filter;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		double[][] d = {{1,2,3},{1,2,3},{1,2,3}};
		A = new SimpleMatrix(d);
		filter = new boolean[]{true,false,false};
	}

	@Test
	public void testNullBasis() {
		Vector<SimpleMatrix> n = nullBasis(A);
		assertEquals(2, n.size()); 
	}
	
	@Test
	public void testFilterRows() {
		double[] r1 = getRow(A, 0).getMatrix().data;
		double[] r2 = filterRows(A, filter).getMatrix().data;
		boolean eq = Arrays.equals(r1, r2);
		assertTrue(eq);
	}

	@Test
	public void testNChooseK() {
		int[] t1 = {1,2,3,4};
		
		
		Vector<int[]> c0 = nchoosek(t1, 0);
		assertEquals(1, c0.size());
		
		Vector<int[]> c1 = nchoosek(t1, 1);
		assertEquals(t1.length, c1.size());
		
		Vector<int[]> c2 = nchoosek(t1, 2);
		assertEquals(6, c2.size());
		
		Vector<int[]> c3 = nchoosek(t1, 3);
		assertEquals(4, c3.size());
		
		Vector<int[]> c4 = nchoosek(t1, 4);
		assertEquals(1, c4.size());
	}
}
