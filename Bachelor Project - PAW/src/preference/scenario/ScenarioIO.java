package preference.scenario;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import preference.PreferenceRelation;
import preference.PreferenceRelationCreator;

/**
 * Class to read and write {@link Scenario}s.
 * @author Dominik
 *
 */
public class ScenarioIO {
	//Regex for Arrays with optional comma separated Strings [a,b,c] or [,,c]
	private final static String ARRAY_REGEX = "\\[((.*?,)*(.*?))?\\]";
	
	/**
	 * Writes a given Scenario to a file
	 * 
	 * @param filename
	 * @param scenario
	 * @throws IOException If there are writing issues
	 */
	public static void saveScenario(String filename, Scenario scenario) throws IOException{
		String fileContent = scenario.getStoreVersion();
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
	    writer.println(fileContent);
	    writer.close();
	}
	
	/**
	 * Reads a given file and create a scenario from it
	 * @param filename
	 * @return
	 * @throws IOException if there are file problems
	 * @throws IllegalStateException if the file doesn't fit the expected pattern 
	 */
	public static Scenario loadScenario(String filename) throws IOException,ScenarioIOException {
		Path p = Paths.get(filename);
		List<String> fileContent = Files.readAllLines(p)
										.stream()
										.map(String::trim)
										.filter(s->(s!=null && !s.isEmpty()))
										.collect(Collectors.toList());
		
		
		if(fileContent.size()<2) {
			throw new ScenarioIOException("Invalid File: File only has "+fileContent.size()+" line(s).");
		}
		//Parse File
		// 1. Agents
		String agent = fileContent.get(0);
		if(!agent.matches(ARRAY_REGEX)) {
			throw new ScenarioIOException("Invalid Syntax: Agents are not in a data Array: \""+agent+"\"");
		}
		String[] agents = agent.split(",");
		int agentSize = agents.length;
		//This is a workaround since split doesn't accept empty strings at the beginning
		agents[0] = agents[0].substring(1, agents[0].length());
		agents[agentSize-1] = agents[agentSize-1].substring(0, agents[agentSize-1].length()-1);
		
		if((agentSize+2)!=fileContent.size()) {
			throw new ScenarioIOException("Invalid Syntax: Wrong number of relation definition lines: "+ fileContent.size()+" should be "+(agentSize+2));
		}
		
		// 2. Objects
		String object = fileContent.get(1);
		if(!object.matches(ARRAY_REGEX)) {
			throw new ScenarioIOException("Invalid Syntax: Objects are not in a data array: "+object);
		}

		String[] objects = object.split(",");
		int objSize = objects.length;
		objects[0] = objects[0].substring(1, objects[0].length());
		objects[objSize-1] = objects[objSize-1].substring(0, objects[objSize-1].length()-1);
		
		// 3.-X.
		PreferenceRelation[] relations = new PreferenceRelation[agentSize];
		for(int i=2;i<fileContent.size();i++) {
			String relation = fileContent.get(i);
			PreferenceRelation rel = PreferenceRelationCreator.createFromString(relation);
			relations[i-2] = rel;
		}
		//Create Scenario:
		Scenario s = new Scenario(agents,objects,relations);
		
		return s;
	}

	public static void saveScenarioSimple(String filename, Scenario scenario) throws IOException {
		String fileContent = scenario.getStoreVersion().replaceFirst(".*?\n", "").replaceFirst(".*?\n", "");
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
	    writer.println(fileContent);
	    writer.close();
	}
}
