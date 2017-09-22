package allocation.algorithms.convexLPSet;

import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

public class PreferenceInequallities {
	public SimpleMatrix AInequality;
	public SimpleMatrix bInequality;
	// A*x <= b
	
	public SimpleMatrix AEquality;
	public SimpleMatrix bEquality;
	// A*x = b
	
	//Cleared Eqalities
	public Vector<Integer> cleared;
}
