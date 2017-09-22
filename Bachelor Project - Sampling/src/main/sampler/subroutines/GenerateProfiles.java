package main.sampler.subroutines;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import main.HelperFunctions;
import main.sampler.ScenarioGenerator;
import preference.scenario.Scenario;
import preference.scenario.ScenarioIO;

/**
 * Generates all profiles <= or = to a given size.
 * @author Dominik
 *
 */
public class GenerateProfiles {
	public static void gen(int agents, int objects, String dir, boolean saveSimple) {
		try {
			generateScenarios(agents, objects, dir, saveSimple);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void generateScenarios(int agents, int objects, String dir, boolean saveSimple) throws IOException {
		String dirPath = dir+"/gen";
		new File(dirPath).mkdirs();
		
		System.out.println("Generating Profiles...\n");
		Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(agents,objects);
		
		String log = "Scenarios Agents: "+agents+" Objects: "+objects+" Total: "+scenarios.size()+"\n";
		System.out.println(log);
		
		System.out.println("Writing Profiles...\n");
		for(int j=0;j<scenarios.size();j++) {
			String filePath = dirPath+"/"+agents+","+objects+" - "+(j+1)+".txt";
			if(saveSimple) {
				ScenarioIO.saveScenarioSimple(filePath, scenarios.get(j));
			} else {
				ScenarioIO.saveScenario(filePath, scenarios.get(j));
			}
			
			printProgress(j, scenarios.size());
		}
		
		PrintWriter writer = new PrintWriter(dirPath+"/log.txt", "UTF-8");
		writer.println(log);
		writer.close();
	}
	
	public static void genAll(int size, String dir, boolean simple) {
		try {
			generateAllScenarios(size, dir,simple);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void generateAllScenarios(int max, String path, boolean saveSimple) throws IOException {
		String log = "";
		
		path = path +"/genAll";
		new File(path).mkdirs();
		
		for(int i=2;i<=max;i++) {			
			String dirPath = path+"/Size "+i;
			new File(dirPath).mkdirs();
			System.out.println("Generating Profiles...\n");
			Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(i,i);

			String line = "Scenarios Agents: "+i+" Objects: "+i+" Total: "+scenarios.size()+"\n";
			System.out.println(line);
			log+=line;

			System.out.println("Writing Profiles...\n");
			for(int j=0;j<scenarios.size();j++) {
				String filePath = dirPath+"/"+i+","+i+" - "+(j+1)+".txt";
				
				if(saveSimple) {
					ScenarioIO.saveScenarioSimple(filePath, scenarios.get(j));
				} else {
					ScenarioIO.saveScenario(filePath, scenarios.get(j));
				}
				
				printProgress(j, scenarios.size());
			}
		}


		PrintWriter writer = new PrintWriter(path+"/log.txt", "UTF-8");
		writer.println(log);
		writer.close();
	}

	private static void printProgress(int prog,int max) {
		double percent = prog*100.0 / max;
		percent = HelperFunctions.round(percent, 2);
		if (max >=100 && (prog % (max/100)) == 0) {
			System.out.println("Progress "+percent+"%");
		}
		
	}

}
