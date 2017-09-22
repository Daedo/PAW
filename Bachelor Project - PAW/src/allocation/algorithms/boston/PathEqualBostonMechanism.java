package allocation.algorithms.boston;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import allocation.Allocation;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

public class PathEqualBostonMechanism {
	private boolean isAdaptive;
	private int[][] accumulator;
	private Scenario sc;
	
	public Allocation allocate(Scenario scenario, boolean adaptive) {
		this.sc = scenario;
		this.isAdaptive = adaptive;
		this.accumulator = new int[sc.getAgentCount()][sc.getObjectCount()];
		Set<Integer> agents = IntStream.range(0, sc.getAgentCount()).boxed().collect(Collectors.toSet());
		Set<Integer> objects= IntStream.range(0, sc.getObjectCount()).boxed().collect(Collectors.toSet());
		int[] matching = new int[sc.getAgentCount()];
		Arrays.fill(matching, -1);
		
		nextRound(0, matching, agents, objects);
		
		double[][] allocation = new double[sc.getAgentCount()][sc.getObjectCount()];
		
		for(int i=0;i<sc.getObjectCount();i++) {
			int sum = 0;
			for(int j=0;j<sc.getAgentCount();j++) {
				sum+=accumulator[j][i];
			}
			
			for(int j=0;j<sc.getAgentCount();j++) {
				allocation[j][i] = accumulator[j][i]/(double) sum;
			}
		}
		return new Allocation(allocation);
	}

	private void nextRound(int round,int[] matching, Set<Integer> remainingAgents, Set<Integer> remainingObjects) {
		if(remainingAgents.isEmpty()) {
			for(int i=0;i<matching.length;i++) {
				accumulator[i][matching[i]]++;
			}
			return;
		}

		HashMap<Integer,Vector<Integer>> objectToAgentsMap = new HashMap<>();
		for(int agent:remainingAgents) {
			PreferenceRelation rel = sc.getAgentRelation(agent);
			int object = rel.getGroup(round)[0];

			if(isAdaptive) {
				int adder = 0;
				while(!remainingObjects.contains(object)) {
					adder++;
					object = rel.getGroup(round+adder)[0];
				}
			}

			if(remainingObjects.contains(object)) {
				if(!objectToAgentsMap.containsKey(object)) {
					objectToAgentsMap.put(object, new Vector<>());
				}
				objectToAgentsMap.get(object).addElement(agent);
			} 
		}

		Vector<int[]> matchings = new Vector<>();
		matchings.addElement(matching);

		for(int object: objectToAgentsMap.keySet()) {
			Vector<Integer> mappedAgents = objectToAgentsMap.get(object);
			if(mappedAgents.size()==1) {
				int agent = mappedAgents.get(0);
				for(int[] m:matchings) {
					m[agent] = object;	
				}
			} else {
				Vector<int[]> newMatchings = new Vector<>();

				for(int agent:mappedAgents) {
					for(int[] m:matchings) {
						int[] newM = Arrays.copyOf(m, m.length);
						newM[agent] = object;
						newMatchings.addElement(newM);
					}
				}
				matchings = newMatchings;
			}
		}

		for(int[] m:matchings) {
			//Recursion
			Set<Integer> remAgents = new HashSet<>();
			for(Integer agent:remainingAgents) {
				if(m[agent] == -1) {
					remAgents.add(agent);
				}
			}

			Set<Integer> remObjects = new HashSet<>();
			for(int object:remainingObjects) {
				boolean add = true;
				for(int i=0;i<m.length;i++) {
					if(m[i] == object) {
						add = false;
						break;
					}
				}
				if(add) {
					remObjects.add(object);
				}
			}

			nextRound(round+1, m, remAgents, remObjects);
		}
	}

}
