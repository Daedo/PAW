package allocation.algorithms.parametricMaximumFlow;

import main.Main;

/**
 * Weighted arc for parametric preflow problems.
 * 
 * @author Dominik
 *
 */
public class Arc {
	public int v1,v2;
	public double capA,capB;
	
	/**
	 * Creates an arc with capacity a+lambda*b
	 * @param from
	 * @param to
	 * @param a 
	 * @param b
	 */
	public Arc(int from, int to, double a, double b) {
		v1 = from;
		v2 = to;
		capA = a;
		capB = b;
	}
	
	/**
	 * Calculates the capacity for a given lambda.
	 * @param lambda
	 * @return
	 */
	public double getCapacity(double lambda) {
		if(capA == Double.POSITIVE_INFINITY) {
			return Double.POSITIVE_INFINITY;
		}
		
		if(Math.abs(capB)<=Main.F_POINT_PRECISION) {
			//To avoid 0*Infinity
			return capA;
		}
		
		//Capacity(Lambda) = a+lambda*b
		return capA+lambda*capB;
	}
	
	/**
	 * Given one node of the arc, return the other one.
	 * @param vertex
	 * @return
	 */
	public int getOtherEnd(int vertex) {
		return v1==vertex?v2:v1;
	}
	
	/**
	 * Check if a given vertex is the start of the arc.
	 * @param vertex
	 * @return
	 */
	public boolean isTheStartOfTheArc(int vertex) {
		return v1==vertex;
	}

	/**
	 * Check if a node is incident to the arc.
	 * @param vertex
	 * @return
	 */
	public boolean isIncident(int vertex) {
		return v1==vertex || v2==vertex;
	}
}
