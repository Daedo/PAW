package allocation.algorithms.parametricMaximumFlow;

/**
 * Models a vertex for a parametric maxflow graph.
 * @author Dominik
 *
 */
public class Vertex {
	public VertexTag tag;
	public Vertex(VertexTag t) {
		this.tag = t;
	}
	
	public static Vertex createSource() {
		return new Vertex(VertexTag.getSource());
	}
	
	public static Vertex createSink() {
		return new Vertex(VertexTag.getSink());
	}	
}
