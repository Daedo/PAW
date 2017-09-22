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

public class TieEqualBostonMechanism {
	private boolean isAdaptive;
	private Scenario sc;
	
	public Allocation allocate(Scenario scenario, boolean adaptive) {
		this.isAdaptive = adaptive;
		this.sc = scenario;
		
		Set<Integer> agents = IntStream.range(0, sc.getAgentCount()).boxed().collect(Collectors.toSet());
		Set<Integer> objects= IntStream.range(0, sc.getObjectCount()).boxed().collect(Collectors.toSet());
		int[] matching = new int[sc.getAgentCount()];
		Arrays.fill(matching, -1);
		
		return nextRound(0, matching, agents, objects);
	}
	
	public Allocation nextRound(int round,int[] matching, Set<Integer> remainingAgents, Set<Integer> remainingObjects) {
		if(remainingAgents.isEmpty()) {
			double[][] alloc = new double[sc.getAgentCount()][sc.getObjectCount()];
			for(int i=0;i<matching.length;i++) {
				Arrays.fill(alloc[i], 0);
				alloc[i][matching[i]] = 1;
			}
			
			return new Allocation(alloc);
		}
		
		
		HashMap<Integer,Vector<Integer>> objectToAgentsMap = new HashMap<>();
		
		for(int agent: remainingAgents) {
			//Calculate preferences for the current round
			PreferenceRelation pref = this.sc.getAgentRelation(agent);
			int current =  pref.getGroup(round)[0];
			Integer curInteger = new Integer(current);
			
			if(isAdaptive) {
				int i=1;
				while(!remainingObjects.contains(curInteger)) {
					current =  pref.getGroup(round+i)[0];
					curInteger = new Integer(current);
					i++;
				}
			}
			
			// Calculate the collision lists ( = sets of agents that want the same object)
			// A: {1,2}, B: {3,4,5}
			if(remainingObjects.contains(curInteger)) {
				if(!objectToAgentsMap.containsKey(curInteger)) {
					objectToAgentsMap.put(curInteger, new Vector<>());
				}
				Vector<Integer> collisionList = objectToAgentsMap.get(curInteger);
				collisionList.add(agent);
			}	
		}
		
		if(objectToAgentsMap.isEmpty()) {
			return nextRound(round + 1, matching, remainingAgents, remainingObjects);
		}
		
		int allocatedObjectCount = objectToAgentsMap.keySet().size();
		Vector<int[]> tieBreaks = new Vector<>();			//Agent "tieBreak[i]" gets Object "objectIndex[i]"
		tieBreaks.addElement(new int[allocatedObjectCount]);
		
		int[] objectIndex = new int[allocatedObjectCount];
		int objectIterationIndex = 0;
		// Calculate every possible tie break
		// A1,B3
		// A1,B4
		// A1,B5
		// A2,B3
		// A2,B4
		// A2,B5
		for(Integer object: objectToAgentsMap.keySet()) {
			objectIndex[objectIterationIndex] = object.intValue();
			Vector<Integer> tieList = objectToAgentsMap.get(object);
			
			Vector<int[]> newTieBreaks = new Vector<>();
			for(int agent:tieList) {
				for(int[] tieBreak:tieBreaks) {
					int[] tieClone = Arrays.copyOf(tieBreak, tieBreak.length); 
					tieClone[objectIterationIndex] = agent;
					newTieBreaks.add(tieClone);
				}
			}
			
			tieBreaks.clear();
			tieBreaks = newTieBreaks;
			objectIterationIndex++;
		}
		
		double[][] accumulator = new double[sc.getAgentCount()][sc.getObjectCount()];
		
		//Run the algorithm recursively for every tie break
		for(int[] tieBreak:tieBreaks) {
			//Calculate matching
			int[] newMatching = Arrays.copyOf(matching, matching.length);
			Set<Integer> newRemainingAgents  = remainingAgents.stream().collect(Collectors.toCollection(HashSet::new));
			Set<Integer> newRemainingObjects = remainingObjects.stream().collect(Collectors.toCollection(HashSet::new));
			
			for(int i=0;i<tieBreak.length;i++) {
				newMatching[tieBreak[i]] = objectIndex[i];
				newRemainingAgents.remove(new Integer(tieBreak[i]));
				newRemainingObjects.remove(new Integer(objectIndex[i]));
			}
			double[][] add = nextRound(round+1, newMatching, newRemainingAgents, newRemainingObjects).getData();
			
			// Sum the allocations together
			for(int i=0;i<sc.getAgentCount();i++) {
				for(int j=0;j<sc.getObjectCount();j++) {
					accumulator[i][j] += add[i][j];
				}
			}
			
		}
		
		 //divide by the number of tie breaks 
		int tieCount = tieBreaks.size();
		for(int i=0;i<sc.getAgentCount();i++) {
			for(int j=0;j<sc.getObjectCount();j++) {
				accumulator[i][j] /= tieCount;
			}
		}
		
		
		//Return the full allocation 
		return new Allocation(accumulator);
	}
}
