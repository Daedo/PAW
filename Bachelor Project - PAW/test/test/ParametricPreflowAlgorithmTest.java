package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import allocation.algorithms.parametricMaximumFlow.CutSet;
import allocation.algorithms.parametricMaximumFlow.Graph;
import allocation.algorithms.parametricMaximumFlow.MinCutMaxFlow;
import allocation.algorithms.parametricMaximumFlow.ParametricPreflowAlgorithm;
import allocation.algorithms.parametricMaximumFlow.Vertex;
import allocation.algorithms.parametricMaximumFlow.VertexTag;

public class ParametricPreflowAlgorithmTest {

	private static Graph graph;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graph = new Graph();
		int[] agents = new int[6];
		for(int i=0;i<agents.length;i++) {
			agents[i] = graph.addVertex(new Vertex(VertexTag.getAgent(i)));
			graph.addArcFromSource(agents[i], 0, 1);
		}
		
		int[] objects = new int[3];
		for(int i=0;i<objects.length;i++) {
			objects[i] = graph.addVertex(new Vertex(VertexTag.getObject(i)));
			graph.addArcToSink(objects[i], 1, 0);
		}
		
		//We use 2*|V| instead of infinity to avoid math problems
		double pMax = 2*(2+objects.length+agents.length);
		graph.addArc(agents[0], objects[0], pMax, 0);
		graph.addArc(agents[0], objects[1], pMax, 0);
		graph.addArc(agents[1], objects[1], pMax, 0);
		graph.addArc(agents[2], objects[0], pMax, 0);
		graph.addArc(agents[2], objects[1], pMax, 0);
		graph.addArc(agents[2], objects[2], pMax, 0);
		graph.addArc(agents[3], objects[2], pMax, 0);
		graph.addArc(agents[4], objects[2], pMax, 0);
		graph.addArc(agents[5], objects[2], pMax, 0);
	}

	@Test
	public void testGetMinimalBreakpoint() {
		ParametricPreflowAlgorithm alg = new ParametricPreflowAlgorithm(graph);
		MinCutMaxFlow mcmf = alg.getMinimalBreakpoint().minimalBreakpoint;
		
		CutSet s = CutSet.SOURCE_SET;
		CutSet t = CutSet.SINK_SET;
		
		CutSet[] expected = {s,t,t,t,t,s,s,s,t,t,s};
		
		assertArrayEquals(expected,mcmf.cutSets);
	}

}
