package main.sampler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import allocation.algorithms.convexLPSet.PermutationGenerator;
import preference.PreferenceGroup;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

/**
 * Generates all strict {@link Scenario}s using the algorithm described in the thesis.
 * @author Dominik
 *
 */
public class ScenarioGenerator {
	private static int[][] relationGrid;
	private static Vector<int[]> relations;
	private static Vector<PreferenceRelation> prefRelations;
	
	/**
	 * Generate all strict allocation problems with a given number of agents and objects, whilst respecting anonymity and neutrality
	 * @param agents
	 * @param objects
	 * @return
	 */
	public static Vector<Scenario> generateStrictScenarios(int agents, int objects) {
		Vector<Scenario> out = new Vector<>();
		//Generate all Object Permutations (possible relations)
		relations = PermutationGenerator.getPermutations(objects);
		prefRelations = getPreferenceRelations();
		relationGrid = generateRelationGrid();
		
		//System.out.println("Relation Count "+relations.size());
		//Create empty cleared set
		HashSet<String> cleared = new HashSet<>();
		
		//For every possible relation with increasing relation-indices all starting with index 0
		StrictPreferenceProfileIterator it = new StrictPreferenceProfileIterator(relations, agents);
		for(int[] relIndices : it) {
			String key = Arrays.toString(relIndices);
			
			//If it is in the cleared map
			if(cleared.contains(key)) {
				//Remove it from cleared map and continue
				cleared.remove(key);
				continue;
			}
			
			//Generate equivalent relations
			Vector<int[]> equivalent = generateEquivalentRelations(relIndices);
			//Add this scenario to out add all other ones to set
			for(int[] r : equivalent) {
				cleared.add(Arrays.toString(r));
			}
			out.addElement(createScenario(relIndices));
			//System.out.println(out.lastElement());
			
		}		
		return out;
	}

	private static int[][] generateRelationGrid() {
		int n = relations.size();
		int[][] grid = new int[n][n];
		
		for(int i=0;i<n;i++) {
			int[] dual = getDualOf(relations.get(i));
			for(int j=0;j<n;j++) {
				int[] relation = relations.get(j).clone();
				//Apply dual transform
				for(int k = 0;k<relation.length;k++) {
					relation[k] = dual[relation[k]];
				}
				
				grid[i][j] = findArray(relations,relation);
			}
		}
		
		return grid;
	}

	private static int[] getDualOf(int[] relation) {
		int[] dual = new int[relation.length];
		for(int i=0;i<relation.length;i++) {
			dual[relation[i]] = i;
		}
		
		return dual;
	}

	private static int findArray(Vector<int[]> vec, int[] elem) {
		for(int i=0;i<vec.size();i++) {
			if(Arrays.equals(vec.elementAt(i), elem)) {
				return i;
			}
		}
		return -1;
	}

	private static int[] applyDualTransformation(int[] relIndices, int transformation) {
		int[] out = new int[relIndices.length];
		for(int i=0;i<relIndices.length;i++) {
			out[i] = relationGrid[transformation][relIndices[i]];
		}
		Arrays.sort(out);
		return out;
	}
	
	private static Vector<int[]> generateEquivalentRelations(int[] relIndices) {
		Vector<int[]> out = new Vector<>();
		HashSet<Integer> indices = new HashSet<>();
		for(int i=1;i<relIndices.length;i++) { //[Sic!]
			if(relIndices[i]!=0) {
				indices.add(relIndices[i]);
			}
		}
		
		for(int index:indices) {
			out.addElement(applyDualTransformation(relIndices,index));
		}
		
		return out;
	}
	
	//Final Interpretations
	private static Vector<PreferenceRelation> getPreferenceRelations() {
		Vector<PreferenceRelation> outRelations = new Vector<>();
		for(int[] relation:relations) {
			Vector<PreferenceGroup> groups = new Vector<>();
			for(int obj:relation) {
				groups.addElement(new PreferenceGroup(new int[]{obj}));
			}
			outRelations.addElement(new PreferenceRelation(groups));
		}
		return outRelations;
	}

	private static Scenario createScenario(int[] relIndices) { 
		int obj = relations.get(0).length;
		int age = relIndices.length;
		Scenario out = new Scenario(age, obj);
		for(int i=0;i<age;i++) {
			out.setAgentRelation(i, prefRelations.get(relIndices[i]), false);
		}
		
		return out;
	}
}
