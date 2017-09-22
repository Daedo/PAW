package allocation.algorithms.parametricMaximumFlow;

import java.util.HashSet;
import java.util.Vector;

import main.Main;

/**
 * Implements the regular preflow algorithm. 
 * Based on https://sites.google.com/site/indy256/algo/preflow
 * 
 * @author Dominik
 *
 */
public class PreflowAlgorithm {

	int vertexCount;
	double[][] cap;
	double[][] flow;
	double[]   excess;
	int[]	   height;


	public void init(int nodes) {
		vertexCount = nodes;
		cap = new double[nodes][nodes];
	}

	public void addEdge(int s, int t, double capacity) {
		cap[s][t] = capacity;
	}

	public MinCutMaxFlow maxFlow(int s, int t) {
		//Initialization
		height = new int[vertexCount];
		height[s] = vertexCount - 1;

		int[] maxh = new int[vertexCount];

		flow = new double[vertexCount][vertexCount];
		excess = new double[vertexCount];

		for (int i = 0; i < vertexCount; ++i) {
			flow[s][i] = cap[s][i];
			flow[i][s] = -flow[s][i];
			excess[i]  = cap[s][i];
		}

		for (int sz = 0;;) {
			if (sz == 0) {
				for (int i = 0; i < vertexCount; ++i)
					if (i != s && i != t && excess[i] > 0) {
						if (sz != 0 && height[i] > height[maxh[0]]) {
							sz = 0;
						}
						maxh[sz++] = i;
					}
			}
			if (sz == 0) {
				break;
			}
			while (sz != 0) {
				int i = maxh[sz - 1];
				boolean pushed = false;
				for (int j = 0; j < vertexCount; ++j) {
					if(Math.abs(excess[i]) < Main.F_POINT_PRECISION) {
						break;
					}

					if (height[i] == height[j] + 1 && isResidual(i, j)) {
						//Push
						double df;
						df = cap[i][j] - flow[i][j];
						df = Math.min(df, excess[i]);
						flow[i][j] += df;
						flow[j][i] -= df;
						excess[i] -= df;
						excess[j] += df;

						if (excess[i] < Main.F_POINT_PRECISION) {
							excess[i] = 0;
							--sz;
						}
						pushed = true;
					}
				}
				if (!pushed) {
					//Relabel
					height[i] = Integer.MAX_VALUE;
					for (int j = 0; j < vertexCount; ++j){
						if (height[i] > height[j] + 1 && isResidual(i, j)) {
							height[i] = height[j] + 1;
						}
					}
					if (height[i] > height[maxh[0]]) {
						sz = 0;
						break;
					}
				}
			}
		}

		//Get Flow
		double maxFlow = 0;
		for (int i = 0; i < vertexCount; i++){
			maxFlow += flow[s][i];
		}

		//Get Cut
		CutSet[] cut = getCutSet(s,t);

		return new MinCutMaxFlow(maxFlow, cut,flow);
	}

	private CutSet[] getCutSet(int s,int t) {
		CutSet[] out = new CutSet[vertexCount];

		int[] distanceToSource = getResidualDistance(s);
		int[] distanceToSink   = getResidualDistance(t);
		
		for(int i=0;i<vertexCount;i++) {
			int distToSink = distanceToSink[i];
			int distToSouce= distanceToSource[i];

			if(distToSouce != -1 ) {
				distToSouce += vertexCount;
			} else {
				//"Infinite Distance"
				distToSouce = vertexCount;
			}
			
			if(distToSink == -1) {
				//"Infinite Distance"
				distToSink = vertexCount;
			}

			int minDist = Math.min(distToSink, distToSouce);
			
			height[i] = minDist;
			if(height[i]>=vertexCount) {
				out[i] = CutSet.SOURCE_SET;
			} else {
				out[i] = CutSet.SINK_SET;
			}
		}
		return out;
	}
	
	private int[] getResidualDistance(int to) {
		int[] dist = new int[vertexCount];
		for(int i=0;i<vertexCount;i++) {
			if(i==to) {
				dist[i] = 0;
			} else {
				dist[i] = -1;
			}
		}

		//We use BFS
		Vector<Integer> worklist = new Vector<>();
		Vector<Integer> nextLevel= new Vector<>();
		HashSet<Integer> visited = new HashSet<>();

		worklist.addElement(to);
		visited.add(to);
		int level = 1;

		while(!worklist.isEmpty()) {
			while(!worklist.isEmpty()) {
				int currentNode = worklist.remove(0).intValue();
				for(int i=0;i<vertexCount;i++) {
					if(isResidual(i, currentNode)) {
						Integer from = new Integer(i);
						if(!visited.contains(from)) {
							visited.add(from);
							nextLevel.add(from);
							dist[i] = level;
						}	
					}	
				}
			}
			worklist.addAll(nextLevel);
			nextLevel.clear();
			level++;
		}

		return dist;
	}

	private boolean isResidual(int v1,int v2) {
		return Math.abs(cap[v1][v2]-flow[v1][v2])>Main.F_POINT_PRECISION;
	}
}