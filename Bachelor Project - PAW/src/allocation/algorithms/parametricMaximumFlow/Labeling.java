package allocation.algorithms.parametricMaximumFlow;

/**
 * Helper Class for the push-relabel algorithm
 * @author Dominik
 *
 */
public class Labeling {
	private Graph graph;
	private int[] labels;
	
	public Labeling(Graph g) {
		this.graph = g;
		labels = new int[g.getVertexCount()];
	}
	
	public void setLabel(int vertexIndex, int label) {
		this.labels[vertexIndex] = label;
	}
	
	public int getLabel(int vertexIndex) {
		return this.labels[vertexIndex];
	}

	public void prepareLabeling() {
		for(int i=0;i<labels.length;i++) {
			if(graph.isSource(i)) {
				labels[i] = graph.getVertexCount();
			} else {
				labels[i] = 0;
			}
		}
	}
}
