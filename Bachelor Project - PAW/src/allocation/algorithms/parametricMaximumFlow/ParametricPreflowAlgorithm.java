package allocation.algorithms.parametricMaximumFlow;

import java.util.HashMap;
import java.util.HashSet;

import main.Main;

/**
 * Solver for parametric maxFlow problems.
 *  
 * @author Dominik
 *
 */
public class ParametricPreflowAlgorithm {
	private Graph graph;
	private HashMap<Integer, VertexCoefficients> coefficients;

	public ParametricPreflowAlgorithm(Graph g) {
		graph = g;
	}

	/**
	 * Returns the minimal breakpoint for the graph associated with this class.
	 * @return
	 */
	public ParametricMinimalBreakpoint getMinimalBreakpoint() {
		ParametricMinimalBreakpoint out = null;
		coefficients = getVertexCoefficients();
		double lambda1 = Math.max(0,getLambda1());
		double lambda2 = getLambda2();
		
		PreflowAlgorithm alg = GraphTransformer.transformGraph(graph, lambda1);
		MinCutMaxFlow mcmf1 = alg.maxFlow(graph.getSource(), graph.getSink());
		double alpha1		= getAlpha(mcmf1,lambda1);
		double beta1 		= getBeta(mcmf1);
		
		while(true){ 
			alg = GraphTransformer.transformGraph(graph, lambda2);
			MinCutMaxFlow mcmf2 = alg.maxFlow(graph.getSource(), graph.getSink());

			double alpha2 		= getAlpha(mcmf2,lambda2);
			double beta2 		= getBeta(mcmf2);
			
			double lineValue1 	= alpha1 + lambda2*beta1; //[SIC]
			double lineValue2 	= alpha2 + lambda2*beta2;
			
			if(Math.abs(lineValue1-lineValue2)<Main.F_POINT_PRECISION) {
				out = new ParametricMinimalBreakpoint(mcmf2, lambda2);
				break;
			} else {
				lambda2 = (alpha2-alpha1) / (beta1-beta2);
			}
		}
		
		return out;
	}

	/**
	 * Helper calculations. See the original paper "A Fast Parametric Maximum Flow Algorithm and Applications" for details.
	 * @param mcmf
	 * @param lambda
	 * @return
	 */
	private double getAlpha(MinCutMaxFlow mcmf,double lambda) {
		HashSet<Integer> X = mcmf.sourceSet();
		HashSet<Integer> XDash = mcmf.sinkSet();
		double cutCapacity = 0;
		
		for(int i=0;i<graph.getArcCount();i++) {
			Arc arc = graph.getArc(i);
			if(X.contains(arc.v1) && XDash.contains(arc.v2)) {
				cutCapacity+=arc.getCapacity(lambda);
			}
		}
		
		return cutCapacity - lambda*getBeta(mcmf);
	}
	
	/**
	 * Helper calculations. See the original paper "A Fast Parametric Maximum Flow Algorithm and Applications" for details.
	 * 
	 * @param mcmf
	 * @return
	 */
	private double getBeta(MinCutMaxFlow mcmf) {
		double out = 0;
		
		HashSet<Integer> X = mcmf.sourceSet();
		HashSet<Integer> XDash = mcmf.sinkSet();
		
		for(int i=0;i<graph.getArcCount();i++) {
			Arc arc = graph.getArc(i);
			if(graph.isSource(arc.v1) && XDash.contains(arc.v2)) {
				out+= Math.abs(arc.capB);
			}
			
			if(graph.isSink(arc.v2) && X.contains(arc.v1)) {
				out -= Math.abs(arc.capB); 
			}
			
		}
		
		return out;
	}

	/**
	 * Calculates the VertexCoefficients for each vertex.
	 * @return
	 */
	private HashMap<Integer,VertexCoefficients> getVertexCoefficients() {
		HashMap<Integer,VertexCoefficients> out = new HashMap<>();

		for(int i=0;i<graph.getArcCount();i++) {
			Arc arc = graph.getArc(i);
			boolean fromSource = graph.isSource(arc.v1);
			boolean toSink = graph.isSink(arc.v2);

			if(fromSource) {
				VertexCoefficients coef = out.get(arc.v2);
				if(coef == null) {
					coef = new VertexCoefficients(arc.v2);
				}	
				coef.a0 = arc.capA;
				coef.a1 = arc.capB;
				out.put(arc.v2, coef);
			}

			if(toSink) {
				VertexCoefficients coef = out.get(arc.v1);
				if(coef == null) {
					coef = new VertexCoefficients(arc.v1);
				}	
				coef.b0 = arc.capA;
				coef.b1 = -arc.capB;
				out.put(arc.v1, coef);
			}
		}
		
		return out;
	}

	/**
	 * Helper calculations. See the original paper "A Fast Parametric Maximum Flow Algorithm and Applications" for details.
	 * @return
	 */
	private double getLambda1() {
		double l1 = Double.POSITIVE_INFINITY;
		for(VertexCoefficients c : coefficients.values()) {
			if(c.isRelevant()) {
				l1 = Math.min(l1, c.calculateLambda1Contribution());
			}
		}
		
		if(l1 < Double.POSITIVE_INFINITY) {
			return l1-1;
		} else {
			return l1;
		}
		
	}

	/**
	 * Helper calculations. See the original paper "A Fast Parametric Maximum Flow Algorithm and Applications" for details.
	 * @return
	 */
	private double getLambda2() {
		double l2 = Double.NEGATIVE_INFINITY;
		for(VertexCoefficients c:coefficients.values()) {
			if(c.isRelevant()) {
				l2 = Math.max(l2, c.calculateLambda2Contribution());
			}
		}
		
		if(l2> Double.NEGATIVE_INFINITY) {
			return l2+1;
		}
		return l2;
	}

	/**
	 * Helper class. Contains coefficients for every node. Used for the Labda1 and Lambda2 Calculations.
	 * 
	 * @author Dominik
	 *
	 */
	private class VertexCoefficients {
		public double a0,a1,b0,b1;
		public int vertexIndex;
		public VertexCoefficients(int index) {
			this.vertexIndex = index;
			this.a0 = 0;
			this.a1 = 0;
			this.b0 = 0;
			this.b1 = 0;
		}

		public boolean isRelevant() {
			return (a1+b1)>0;
		}

		public double calculateLambda1Contribution() {
			double out = 0;
			out = b0-a0;

			for(int i=0;i<graph.getArcCount();i++) {
				Arc arc = graph.getArc(i);
				if(graph.isSink(arc.v1) || graph.isSink(arc.v2) || graph.isSource(arc.v1) || graph.isSource(arc.v2)) {
					continue;
				}

				if(arc.v2 == vertexIndex) {
					out -= arc.getCapacity(0);
				}
			}
			out = out/(a1+b1);
			return out;
		}

		public double calculateLambda2Contribution() {
			double out = 0;

			out = b0-a0;

			for(int i=0;i<graph.getArcCount();i++) {
				Arc arc = graph.getArc(i);
				if(graph.isSink(arc.v1) || graph.isSink(arc.v2) || graph.isSource(arc.v1) || graph.isSource(arc.v2)) {
					continue;
				}

				if(arc.v1 == vertexIndex) {
					out += arc.getCapacity(0);
				} 
			}
			
			out = out/(a1+b1);
			return out;
		}
	
		@Override
		public String toString() {
			String out = "Vertex "+this.vertexIndex+"\n";
			out+= "("+a0+", "+a1+") ("+b0+", "+b1+")";
			return out;
		}
	}
}
