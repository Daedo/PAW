package allocation.algorithms.parametricMaximumFlow;

/**
 * Solution Class for the minimal breakpoint algorithm. Contains the solving MinCutMaxFlow and the value for lambda.
 * @author Dominik
 *
 */
public class ParametricMinimalBreakpoint {
	public MinCutMaxFlow minimalBreakpoint;
	public double parameterValue;
	
	public ParametricMinimalBreakpoint(MinCutMaxFlow breakpoint, double lambda) {
		this.minimalBreakpoint = breakpoint;
		this.parameterValue = lambda;
	}
	
	@Override
	public String toString() {
		String out = "Breakpoint:\nLambda: "+parameterValue+"\n"+minimalBreakpoint;
		return out;
	}
}
