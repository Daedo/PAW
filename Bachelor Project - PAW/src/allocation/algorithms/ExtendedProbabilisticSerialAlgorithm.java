package allocation.algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import allocation.Allocation;
import allocation.algorithms.parametricMaximumFlow.Graph;
import allocation.algorithms.parametricMaximumFlow.ParametricMinimalBreakpoint;
import allocation.algorithms.parametricMaximumFlow.ParametricPreflowAlgorithm;
import allocation.algorithms.parametricMaximumFlow.Vertex;
import allocation.algorithms.parametricMaximumFlow.VertexTag;
import allocation.algorithms.parametricMaximumFlow.VertexTag.VertexTagType;
import main.Main;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

public class ExtendedProbabilisticSerialAlgorithm{

	Scenario scenario;
	private int agentCount;
	private int objCount;
	private Set<Integer> objectSet;
	private double[][] allocs;
	private double[] capacities;
	Set<Integer> agentSet;
	
	public Allocation allocate(Scenario sc, boolean firstPreferenceOnly) {
		scenario 	= sc;
		agentCount 	= scenario.getAgentCount();
		objCount 	= scenario.getObjectCount();

		objectSet = rangeSet(objCount);							// Object Set A'
		agentSet  = rangeSet(agentCount);
		
		allocs 	   = new double[agentCount][objCount];			// P = 0
		capacities = new double[agentCount];					// c(i,1) = 0 

		
		
		// While A' is not empty do steps 2-5
		while(!objectSet.isEmpty() && !(firstPreferenceOnly && agentSet.isEmpty())) {
			// 2. Graph Construction
			// Calculate H(i,A') = Most Preferred objects of i in A'
			Vector<Vector<Integer>> mostPreffered = getMostPrefferedObjects();
			//Generate the parametric Graph
			Graph graph = constructAllocationGraph(mostPreffered);
			
			// 3. Solve parametric max-flow problem
			ParametricPreflowAlgorithm alg = new ParametricPreflowAlgorithm(graph);
			ParametricMinimalBreakpoint sol = alg.getMinimalBreakpoint();
			// lambdaStarK smallest breakpoint
			// X* Source Vertex Set
			
			// B set of bottleneck objects B:= X* intersect A'
			HashSet<Integer> bottleneck = new HashSet<>();
			for(int v: sol.minimalBreakpoint.sourceSet()) {
				Vertex vert = graph.getVertex(v);
				boolean isObject = (vert.tag.type == VertexTagType.OBJECT);
				Integer objID = new Integer(vert.tag.id);
				boolean isInObjectSet = objectSet.contains(objID);
				
				if(isObject && isInObjectSet) {
					bottleneck.add(objID);
				} else {
					boolean isAgent = (vert.tag.type == VertexTagType.AGENT);
					if(isAgent && firstPreferenceOnly) {
						//Remove Agent (Only if we want to use first Preference only)
						agentSet.remove(new Integer(vert.tag.id));
					}
					
				}
			}
			// 4. Update Graph
			// For all Agents:
			for(int agent = 0;agent<agentCount;agent++) {
				Vector<Integer> prefferedByAgent = mostPreffered.get(agent);
				//update c(i,k+1) = c(i,k)+lambdaStarK
				capacities[agent]+=sol.parameterValue;

				if(bottleneck.containsAll(prefferedByAgent)) {	//If H(i,A') is subset of B 
					for(int object:prefferedByAgent) {
						//Give agent c(i,k)+lambdaStarK worth of objects in H(i,A')
						//Avoid getting Allocations with chances >1 
						if(aSum(allocs,agent) -1 < -Main.F_POINT_PRECISION) {
							allocs[agent][object] = getFairSplit(graph,sol,agent,object);//capacities[agent];	
						}
						
					}

					//update c(i,k+1) = 0
					capacities[agent] = 0;
				} 
			}
			// 5. Update Problem
			// A' = A' - B
			objectSet.removeAll(bottleneck);
		}
		// Termination
		// Return the outcome
		return new Allocation(allocs);
	}
	
	private double getFairSplit(Graph graph, ParametricMinimalBreakpoint sol, int agent, int object) {
		int ageID = 0;
		int objID = 0;
		
		for(int i=0;i<graph.getVertexCount();i++) {
			Vertex v = graph.getVertex(i);
			
			if(v.tag.type == VertexTagType.AGENT && v.tag.id == agent) {
				ageID = i;
			}
			
			if(v.tag.type == VertexTagType.OBJECT && v.tag.id == object) {
				objID = i;
			}
		}
		
		return sol.minimalBreakpoint.solutionFlow[ageID][objID];
	}

	private Graph constructAllocationGraph(Vector<Vector<Integer>> mostPreffered) {
		Graph out = new Graph();
		int[] agents = new int[agentCount];
		Arrays.fill(agents, -1);
		
		for(int i:agentSet) {
			agents[i] = out.addVertex(new Vertex(VertexTag.getAgent(i)));
			// G: 	s -> Agents			with capacity c(i,k)+lambda 
			out.addArcFromSource(agents[i], capacities[i], 1);
		}
		
		int[] objects = new int[objCount];
		for(int i=0;i<objects.length;i++) {
			objects[i] = -1;
			Integer o = new Integer(i);
			for(int j :agentSet) {
				Vector<Integer> agPref = mostPreffered.elementAt(j);

				if(agPref.contains(o)) {
					if(objects[i]==-1) {
						objects[i] = out.addVertex(new Vertex(VertexTag.getObject(i)));
						out.addArcToSink(objects[i], 1, 0);
						//Objects in A' -> t 	with capacity 1
						
					}
					// Agents -> H(i,A')
					out.addArc(agents[j], objects[i], objCount, 0);
				}
			}
		}
		return out;
	}

	private Vector<Vector<Integer>> getMostPrefferedObjects() {
		Vector<Vector<Integer>> mostPreffered = new Vector<>();

		for(int agent = 0; agent<agentCount; agent++) {
			Vector<Integer> mostPrefferedOfAgent = new Vector<>();
			mostPreffered.addElement(mostPrefferedOfAgent);
			if(!agentSet.contains(agent)) {
				continue;
			}
			
			PreferenceRelation rel = scenario.getAgentRelation(agent);

			for(int object:objectSet) {
				if(mostPrefferedOfAgent.isEmpty()) {
					mostPrefferedOfAgent.addElement(object);
				} else {
					int oldObj = mostPrefferedOfAgent.get(0);
					int compare= rel.compareObjects(object, oldObj);
					if(compare > 0) {
						mostPrefferedOfAgent.clear();
					}

					if(compare >= 0) {
						mostPrefferedOfAgent.addElement(object);
					}
				}
			}
		}
		return mostPreffered;
	}

	private static Set<Integer> rangeSet(int max) {
		return IntStream.range(0, max) 
				.boxed()
				.collect(Collectors.toSet());
	}
	
	private double aSum(double[][] matrix, int row) {
		double out = 0;
		for(int i=0;i<matrix[row].length;i++) {
			out+=matrix[row][i];
		}
		return out;
	}
}
