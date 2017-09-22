package allocation.algorithms.parametricMaximumFlow;

/**
 * Helper class, for identifying different types of nodes in the maxflow graph. 
 * @author Dominik
 *
 */
public class VertexTag {
	public enum VertexTagType {
		AGENT,OBJECT,SOURCE,SINK,NONE;
	}
	
	public VertexTagType type;
	public int id;
	
	private VertexTag(VertexTagType t,int data) {
		this.type = t;
		this.id = data;
	}
	
	public static VertexTag getAgent(int agent) {
		return new VertexTag(VertexTagType.AGENT, agent);
	}
	
	public static VertexTag getObject(int object) {
		return new VertexTag(VertexTagType.OBJECT, object);
	}
	
	public static VertexTag getSource() {
		return new VertexTag(VertexTagType.SOURCE, 0);
	}
	
	public static VertexTag getSink() {
		return new VertexTag(VertexTagType.SINK, 0);
	}
	
	public static VertexTag getDefault() {
		return new VertexTag(VertexTagType.NONE, 0);
	}
}
