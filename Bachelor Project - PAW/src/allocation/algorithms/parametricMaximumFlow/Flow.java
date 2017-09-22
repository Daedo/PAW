package allocation.algorithms.parametricMaximumFlow;

import main.Main;

/**
 * Class containing the solution to a parametric preflow problem.
 * @author Dominik
 *
 */
public class Flow {
	private Graph graph;
	private double[] flow;
	private double[] excess;

	public Flow(Graph g) {
		graph = g;
		flow = new double[graph.getArcCount()];
		excess = new double[graph.getVertexCount()];
	}

	public void prepareFlow(double lambda) {
		excess[graph.getSource()] = Double.POSITIVE_INFINITY;
		
		for(int i=0;i<graph.getArcCount();i++) {
			Arc arc = graph.getArc(i);
			if(graph.isSource(arc.v1)) {
				flow[i] = arc.getCapacity(lambda);
				excess[arc.v2] = arc.getCapacity(lambda);
				
			} else {
				flow[i] = 0;
			}
		}
	}
	
	public double getFlow(int arcIndex) {
		return flow[arcIndex];
	}

	public double getResidualCapacity(int arcIndex, double lambda) {
		double capacity = graph.getArc(arcIndex).getCapacity(lambda);
		if(capacity == Double.POSITIVE_INFINITY) {
			return Double.POSITIVE_INFINITY;
		}
		
		return capacity - flow[arcIndex];
	}

	public boolean isSaturated(int arcIndex, double lambda) {
		return getResidualCapacity(arcIndex, lambda)<=Main.F_POINT_PRECISION;
	}
	
	public double getValue() {
		return getExcess(graph.getSink());
	}

	public double getExcess(int vertexIndex) {
		return excess[vertexIndex];
	}

	public boolean isActive(int vertexIndex) {
		return getExcess(vertexIndex)>0;
	}

	public void addFlow(int arcIndex, double delta) {
		flow[arcIndex]+=delta;
		Arc arc = graph.getArc(arcIndex);
		excess[arc.v1] -= delta;
		if(excess[arc.v1]<=Main.F_POINT_PRECISION) {
			excess[arc.v1] = 0;
		}
		
		excess[arc.v2] += delta;
	}
}
