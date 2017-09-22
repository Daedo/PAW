package allocation.algorithms.parametricMaximumFlow;

import java.util.Vector;

/**
 * Class modeling a graph for a parametric preflow problem.
 * 
 * @author Dominik
 *
 */
public class Graph {
	private Vertex source;
	private Vertex sink;
	private Vector<Vertex> vertecies;
	private Vector<Arc> arcs;
	
	public Graph() {
		source = Vertex.createSource();
		sink = Vertex.createSink();
		
		vertecies = new Vector<>();
		vertecies.addElement(source);
		vertecies.addElement(sink);
		
		arcs = new Vector<>();
	}
	
	public int addVertex(Vertex v) {
		vertecies.addElement(v);
		return vertecies.size()-1;
	}
	
	public int addArc(int from,int to, double a, double b) {
		Arc arc = new Arc(from, to, a, b);
		arcs.addElement(arc);
		return arcs.size()-1;
	}
	
	public int addArcFromSource(int to, double a,double b) {
		int from = vertecies.indexOf(source);
		return addArc(from, to, a, b);
	}
	
	public int addArcToSink(int from,double a,double b) {
		int to = vertecies.indexOf(sink);
		return addArc(from, to, a, b);
	}
	
	/**
	 * Gets all arcs incident to a given node. 
	 * @param vertex
	 * @return
	 */
	public Vector<Integer> getIncidenceList(int vertex) {
		Vector<Integer> out = new Vector<>();
		for(int i=0;i<arcs.size();i++) {
			Arc arc = arcs.elementAt(i);
			
			int from = arc.v1;
			int to = arc.v2;
			if(from == vertex || to == vertex) {
				out.addElement(new Integer(i));
			}
					
		}
		return out;
	}

	public int getArcCount() {
		return arcs.size();
	}
	
	public Arc getArc(int i) {
		return arcs.elementAt(i);
	}

	public boolean isSink(int vertex) {
		return sink.equals(vertecies.elementAt(vertex));
	}
	
	public boolean isSource(int vertex) {
		return source.equals(vertecies.elementAt(vertex));
	}
	
	public int getSink() {
		return vertecies.indexOf(sink);
	}
	
	public int getSource() {
		return vertecies.indexOf(source);
	}

	public int getVertexCount() {
		return vertecies.size();
	}

	public Vertex getVertex(int v) {
		return vertecies.elementAt(v);
	}
}
