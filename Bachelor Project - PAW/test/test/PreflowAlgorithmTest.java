package test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import allocation.algorithms.parametricMaximumFlow.Graph;
import allocation.algorithms.parametricMaximumFlow.GraphTransformer;
import allocation.algorithms.parametricMaximumFlow.MinCutMaxFlow;
import allocation.algorithms.parametricMaximumFlow.PreflowAlgorithm;
import allocation.algorithms.parametricMaximumFlow.Vertex;
import allocation.algorithms.parametricMaximumFlow.VertexTag;

public class PreflowAlgorithmTest {

	private static Graph graph;
	private static Graph graphInfinite;
	
	private static Graph graphLambda;
	private static int vL1;
	
	private static final double DELTA = 1e-15;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graph = new Graph();
		Vertex v1 = new Vertex(VertexTag.getAgent(0));
		int i1 = graph.addVertex(v1);
		Vertex v2 = new Vertex(VertexTag.getAgent(1));
		int i2 = graph.addVertex(v2);
		Vertex v3 = new Vertex(VertexTag.getObject(0));
		int i3 = graph.addVertex(v3);
		Vertex v4 = new Vertex(VertexTag.getObject(1));
		int i4 = graph.addVertex(v4);
		
		graph.addArcFromSource(i1, 12, 0);
		graph.addArcFromSource(i2, 12, 0);
		graph.addArc(i2, i1, 1, 0);
		graph.addArc(i1, i3, 12,0);
		graph.addArc(i2, i4, 11,0);
		graph.addArc(i4, i3, 7, 0);
		graph.addArcToSink(i3, 19,0);
		graph.addArcToSink(i4, 4 ,0);
		
		graphInfinite = new Graph();
		int vI1 = graphInfinite.addVertex(new Vertex(VertexTag.getAgent(0)));
		int vI2 = graphInfinite.addVertex(new Vertex(VertexTag.getObject(0)));
		graphInfinite.addArcFromSource(vI1, 2, 0);
		graphInfinite.addArc(vI1, vI2, Double.POSITIVE_INFINITY, 0);
		graphInfinite.addArcToSink(vI2, 1, 0);
		
		graphLambda = new Graph();
		vL1 = graphLambda.addVertex(new Vertex(VertexTag.getAgent(0)));
		int vL2 = graphLambda.addVertex(new Vertex(VertexTag.getObject(0)));
		graphLambda.addArcFromSource(vL1, 2, 0);
		graphLambda.addArc(vL1, vL2, 0, 1);
		graphLambda.addArcToSink(vL2, 1, 0);
	}

	@Test(timeout = 1000)
	public void testResidualDistance() {
		PreflowAlgorithm alg = GraphTransformer.transformGraph(graph, 0);
		double flow = alg.maxFlow(graph.getSource(), graph.getSink()).maxFlow;
		assertEquals(23, flow,DELTA);
	}
	
	@Test(timeout = 1000)
	public void testResidualDistanceInfiniteCapacity() {
		PreflowAlgorithm alg = GraphTransformer.transformGraph(graphInfinite, 0);
		double flow = alg.maxFlow(graph.getSource(), graph.getSink()).maxFlow;
		assertEquals(1, flow,DELTA);
	}

	@Test(timeout = 1000)
	public void testInfiniteLambda() {
		PreflowAlgorithm alg = GraphTransformer.transformGraph(graphLambda, Double.POSITIVE_INFINITY);
		double flow = alg.maxFlow(graph.getSource(), graph.getSink()).maxFlow;
		assertEquals(1, flow,DELTA);
	}
	
	@Test(timeout = 1000)
	public void testBottleneck() {
		PreflowAlgorithm alg = GraphTransformer.transformGraph(graphLambda, 0.5);
		MinCutMaxFlow mcmf = alg.maxFlow(graph.getSource(), graph.getSink());
		double flow = mcmf.maxFlow;
		assertEquals(0.5, flow,DELTA);
		Integer[] sourceSet = {new Integer(graphLambda.getSource()),new Integer(vL1)};
		boolean matches = mcmf.sourceSet().containsAll(Arrays.asList(sourceSet));
		assertTrue(matches);
	}
}
