package allocation.algorithms.parametricMaximumFlow;

public class GraphTransformer {
	/**
	 * Transforms a given graph with a certain lambda into from a parametric preflow problem to a regular preflow problem.
	 * @param g
	 * @param lambda
	 * @return
	 */
	public static PreflowAlgorithm transformGraph(Graph g, double lambda) {
		PreflowAlgorithm alg = new PreflowAlgorithm();
		alg.init(g.getVertexCount());
		for(int i=0;i<g.getArcCount();i++) {
			Arc arc = g.getArc(i);
			int s = arc.v1;
			int t = arc.v2;
			double cap = arc.getCapacity(lambda);
			alg.addEdge(s, t, cap);
		}
		
		return alg;
	}
	
}
