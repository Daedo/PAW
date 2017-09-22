package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import allocation.algorithms.convexLPSet.PermutationGenerator;

public class PermutationGeneratorTest {

	@Test
	public void testGetPermutations() {
		Vector<int[]> perm = PermutationGenerator.getPermutations(3);
		assertEquals(6, perm.size());
		assertTrue(allDiff(perm));
	}
	
	private boolean allDiff(Vector<int[]> perms) {
		for(int i=0;i<perms.size();i++) {
			for(int j=0;j<perms.size();j++) {
				if(i==j) {
					continue;
				}
				int[] a = perms.elementAt(i);
				int[] b = perms.elementAt(j);
				if(Arrays.equals(a, b)) {
					return false;
				}
			}
		}
		return true;
	}

}
