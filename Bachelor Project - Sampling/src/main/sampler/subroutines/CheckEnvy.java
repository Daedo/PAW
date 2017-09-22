package main.sampler.subroutines;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import axiom.PopularEnvyChecker;
import main.sampler.ScenarioGenerator;
import preference.scenario.Scenario;

/**
 * Tests all preference profiles up to a given size against the {@link PopularEnvyChecker} heuristic.
 * Doesn't work really well.
 * @author Dominik
 *
 */
@Deprecated
public class CheckEnvy {
	public static void envy(int size, String file) {
		try {
			CheckEnvy.checkEnvy(size, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checkEnvy(int max, String file) throws IOException {
		String logData = "";
		int progress = -1;
		boolean foundSolution = false;
	
		for(int i=2;i<=max;i++) {
			System.out.println("Size: "+i);
			Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(i, i);
			int maxProg = scenarios.size();
			for(int k=0; k<maxProg; k++) {
				if(k==52 || k==54 || k==55 || k==56) {
					continue;
				}
				
				Scenario s = scenarios.elementAt(k);
				System.out.println(k);
				
				if( PopularEnvyChecker.violatesEnvyHeuristic(s)) {
					logData += "Problem ID: "+i+","+i+" - "+(k+1)+"\n";
					logData += s+"\n\n";
					System.out.println("Problem ID: "+i+","+i+" - "+(k+1)+"\n");
					foundSolution = true;
					System.out.println(s);
					//return;
				}
	
				if(((k*100)/maxProg)!=progress) {
					progress = (k*100)/maxProg;
					System.out.println(progress+"%");
	
				}
			}
	
			if(!foundSolution) {
				logData = "No violations found at size "+i+"\n";
			}
		}
		if(!foundSolution) {
			logData = "Weak envy-free up to maximal size\n";
		}
	
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println(logData);
		writer.close();
	
	}

}
