package preference;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import preference.scenario.Scenario;
import preference.scenario.ScenarioIOException;
import preference.spatial.Cluster;
import preference.spatial.DataPoint2D;
import preference.spatial.PointSample;

/**
 * Creates different {@link PreferenceRelation}s and {@link Scenario}s.
 * @author Dominik
 *
 */
public class PreferenceRelationCreator {
	/*================= CREATE =================*/

	//RANDOM
	public static Scenario createScenarioSpacialModel(int agents, int objects, double strictness) {
		Scenario out = new Scenario(agents,objects);
		//1.Sample Points
		PointSample sample = new PointSample(agents, objects);
		//2.Create Preference Relations
		for(int i=0;i<agents;i++) {
			DataPoint2D agent = sample.agentPoints[i];
			PreferenceRelation rel = createRelationFromSample(agent,sample.objectsPoints,strictness);
			out.setAgentRelation(i, rel, false);
		}

		return out;
	}

	public static PreferenceRelation createRandomRelationSpacial(int objCount, double strictness) {
		PointSample sample = new PointSample(1, objCount);
		return createRelationFromSample(sample.agentPoints[0], sample.objectsPoints, strictness);
	}

	private static PreferenceRelation createRelationFromSample(DataPoint2D agent, DataPoint2D[] objectsPoints, double strictness) {
		//We work relative to agent
		DataPoint2D.reference = agent.point;
		Vector<DataPoint2D> objects = new Vector<>();
		Collections.addAll(objects, objectsPoints);


		Vector<Cluster> outClusters = new Vector<>();
		//While there are Objects left:
		while(!objects.isEmpty()) {
			//Find Clusters
			Vector<Cluster> clusters = cluster(objects,strictness);
			//Move biggest Cluster outClusters
			Cluster maxCluster = Collections.max(clusters, (a,b) -> Integer.compare(a.points.size(), b.points.size()));
			outClusters.addElement(maxCluster);
			//Update Objectlist
			objects.removeAll(maxCluster.points);
		}

		Collections.sort(outClusters);
		//Use these clusters now for the relation
		Vector<PreferenceGroup> rOut = new Vector<>();
		for (Cluster cluster: outClusters) {
			PreferenceGroup g = new PreferenceGroup();
			for (DataPoint2D obj : cluster.points) {
				g.add(obj.data);
			}
			rOut.addElement(g);
		}
		return new PreferenceRelation(rOut);
	}

	/**
	 * Helper method for the spacial model. Used by createRelationFromSample for greedy distance clustering.
	 * @param objects
	 * @param strictness
	 * @return
	 */
	private static Vector<Cluster> cluster(Vector<DataPoint2D> objects, double strictness) {
		//Calculate Cluster radius as  (maxDistance-minDistance)
		double dMax = Collections.max(objects).distanceToReference();
		double dMin = Collections.min(objects).distanceToReference();

		final double CLUSTER_DISTANCE = (1-strictness)*(dMax-dMin);

		Vector<Cluster> out = new Vector<>();
		for(int i=0;i<objects.size();i++) {
			DataPoint2D obj1 = objects.get(i);
			Cluster cluster = new Cluster(obj1);

			for(int j=0;j<objects.size();j++) {
				DataPoint2D obj2 = objects.get(j);

				double d1 = obj1.distanceToReference();
				double d2 = obj2.distanceToReference();
				double dDif = Math.abs(d1-d2);
				if(dDif<=CLUSTER_DISTANCE) {
					cluster.add(obj2);
				}
			}
			out.add(cluster);
		}
		return out;
	}


	public static Scenario createScenarioIterativeJoining(int agents, int objects, double strictness) {
		Scenario out = new Scenario(agents, objects);
		for(int i=0;i<agents;i++) {
			out.setAgentRelation(i, createRandomRelationIterativeJoining(objects, strictness), false);
		}
		return out;
	}

	public static PreferenceRelation createRandomRelationIterativeJoining(int size, double strictness) {
		List<Integer> obj = IntStream.range(0, size).sorted().boxed().collect(Collectors.toList());
		Collections.shuffle(obj);

		Vector<PreferenceGroup> rawData = new Vector<>();
		PreferenceGroup current = new PreferenceGroup();
		current.add(obj.get(0));
		rawData.addElement(current);

		Random r = new Random();
		for(int i=1;i<size;i++) {
			if(r.nextDouble()>strictness) {
				//Join
				current.add(obj.get(i).intValue());
			} else {
				//New
				current = new PreferenceGroup();
				rawData.add(current);
				current.add(obj.get(i).intValue());
			}
		}

		return new PreferenceRelation(rawData);
	}

	public static Scenario createScenarioImpartialCulture(int agents, int objects, boolean isStrict) {
		Scenario out = new Scenario(agents, objects);
		for(int i=0;i<agents;i++) {
			out.setAgentRelation(i, createRandomRelationImpartialCulture(objects, isStrict), false);
		}
		return out;
	}

	public static PreferenceRelation createRandomRelationImpartialCulture(int objects, boolean isStrict) {
		List<Integer> obj = IntStream.range(0, objects).boxed().collect(Collectors.toList());
		Collections.shuffle(obj);
		
		if(isStrict) {
			Vector<PreferenceGroup> rel = new Vector<>();
			for(int i : obj) {
				PreferenceGroup g = new PreferenceGroup();
				g.add(i);
				rel.addElement(g);
			}
			return new PreferenceRelation(rel);
		}

		int[] decomposition = SumDecomposition.getRandomWeighted(objects);
		
		Vector<PreferenceGroup> rel = new Vector<>();
		for(int i : decomposition) {
			PreferenceGroup g = new PreferenceGroup();
			obj.stream().limit(i).forEach(g::add);
			obj = obj.stream().skip(i).collect(Collectors.toList());
			rel.addElement(g);
		}
		return new PreferenceRelation(rel);
	}

	/**
	 * Create a random dichotomous {@link PreferenceRelation} of a given size.
	 * @param size
	 * @return
	 */
	public static PreferenceRelation createDichotomousPreference(int size) {
		Vector<PreferenceGroup> rawData = new Vector<>();
		PreferenceGroup like = new PreferenceGroup();
		PreferenceGroup dislike = new PreferenceGroup();

		Random r = new Random();
		for(int i=0;i<size;i++) {
			if(r.nextBoolean()) {
				like.add(i);
			} else {
				dislike.add(i);
			}
		}

		rawData.add(like);
		rawData.add(dislike);
		return new PreferenceRelation(rawData);
	}

	//OTHER
	/**
	 * Creates the relations
	 * 1,2,..,size
	 * or size,...,2,1 depending on ascending
	 * @param size
	 * @param ascending
	 * @return
	 */
	public static PreferenceRelation createOrderedRelation(int size, boolean ascending) {
		if(ascending) {
			return new PreferenceRelation(size);
		}

		Vector<PreferenceGroup> rawData = new Vector<>();
		for(int i=0;i<size;i++) {
			rawData.add(new PreferenceGroup());
			rawData.lastElement().add(size-1-i);
		}

		return new PreferenceRelation(rawData);
	}

	/**
	 * Creates the relation {1,2,...,size}
	 * @param size
	 * @return
	 */
	public static PreferenceRelation createIndifferentRelation(int size) {
		Vector<PreferenceGroup> rawData = new Vector<>();
		rawData.add(new PreferenceGroup());
		for(int i=0;i<size;i++) {
			rawData.firstElement().add(i);
		}

		return new PreferenceRelation(rawData);
	}

	private final static String NUMBER = "(\\d+)";					//An Int Number
	private final static String GROUP  = "(\\{(\\d+)(,\\d+)*\\})";	//An Group of numbers in {}
	private final static String NUMBER_OR_GROUP = "("+NUMBER+"|"+GROUP+")";
	private final static String PREFERENCE_REGEX= NUMBER_OR_GROUP+"(,"+NUMBER_OR_GROUP+")*";

	/**
	 * Parses , and {} notation.
	 * @param relation
	 * @return
	 */
	public static PreferenceRelation createFromString(String relation) throws ScenarioIOException{
		String rel = relation.replaceAll("\\s", "");
		if(!rel.matches(PREFERENCE_REGEX)) {
			throw new ScenarioIOException("Given String "+rel+" doesn't fit relation pattern.");
		}
		//Parse
		rel = rel+","; //Makes the automatic parsing easier

		String buffer = "";
		Vector<PreferenceGroup> groups = new Vector<>();
		PreferenceGroup current = new PreferenceGroup();
		boolean isInGroup = false; //Checks if we are in a {} block

		for(int i=0;i<rel.length();i++) {
			char currentChar = rel.charAt(i);
			if(currentChar>='0' && currentChar<='9') {
				buffer+=currentChar;
			}

			int numberBuffer = -1;

			switch(currentChar) {

			case '{':
				isInGroup = true;
				break;

			case '}':
				isInGroup = false;
				break;

			case ',':
				numberBuffer = Integer.parseInt(buffer);
				buffer = "";
				current.add(numberBuffer);
				if(!isInGroup) {
					groups.addElement(current);
					current = new PreferenceGroup();
				}
				break;
			}
		}
		if(!PreferenceRelation.isValidPreferenceData(groups)) {
			throw new ScenarioIOException("Invalid Preference Relation: \""+relation+"\"");
		}
		
		PreferenceRelation rOut = new PreferenceRelation(groups);
		return rOut;
	}

}
