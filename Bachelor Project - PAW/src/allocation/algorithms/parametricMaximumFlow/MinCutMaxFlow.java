package allocation.algorithms.parametricMaximumFlow;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Class containing the solution of the preflow algorithm:
 * Contains the maximum flow, the flow on every edge and the cutSet information for every node.   
 * @author Dominik
 *
 */
public class MinCutMaxFlow {
	public double maxFlow;
	public CutSet[] cutSets;
	public double[][] solutionFlow;
	
	public MinCutMaxFlow(double value, CutSet[] sets,double[][] flow) {
		maxFlow = value;
		cutSets = sets;
		solutionFlow = flow;
	}

	public HashSet<Integer> sourceSet() {
		HashSet<Integer> out = new HashSet<>();
		for(int i=0;i<cutSets.length;i++) {
			if(CutSet.SOURCE_SET.equals(cutSets[i])) {
				out.add(i);
			}
		}
		return out;
	}

	public HashSet<Integer> sinkSet() {
		HashSet<Integer> out = new HashSet<>();
		for(int i=0;i<cutSets.length;i++) {
			if(CutSet.SINK_SET.equals(cutSets[i])) {
				out.add(i);
			}
		}
		return out;
	}
	
	@Override
	public String toString() {
		String out= "Max Flow = "+maxFlow+"\n";
		
		String source = sourceSet().stream().map(Object::toString).collect(Collectors.joining(", "));
		out+= "Source Set: {"+source+"}\n";
		String sink = sinkSet().stream().map(Object::toString).collect(Collectors.joining(", "));
		out+= "Sink Set: {"+sink+"}";
				
		return out;
	}
}
