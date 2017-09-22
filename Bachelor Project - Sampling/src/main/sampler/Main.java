package main.sampler;

import main.sampler.subroutines.CheckDifferences;
import main.sampler.subroutines.GenerateProfiles;

public class Main {
	/**
	 * Entry point to the software.
	 * Runs different subroutines depending on the arguements given.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<args.length;i++) {
			args[i] = args[i].toLowerCase().trim();
		}
		
		boolean simple = false;

		if(isGen(args)) {
			simple = args.length == 5;
			int agents = Integer.parseInt(args[1]);
			int objects = Integer.parseInt(args[2]);
			GenerateProfiles.gen(agents,objects,args[3],simple);
		} else if(isGenAll(args)) {
			simple = args.length == 4;
			int size = Integer.parseInt(args[1]);
			GenerateProfiles.genAll(size,args[2],simple);
		} else if(args.length == 3 && args[0].equals("-diff") && args[1].matches("[0-9]*")) {
			int size = Integer.parseInt(args[1]);
			CheckDifferences.diff(size,args[2]);
		} /*else if(args.length == 3 && args[0].equals("-envy") && args[1].matches("[0-9]*")) {
			int size = Integer.parseInt(args[1]);
			CheckEnvy.envy(size,args[2]);
		}*/ else {
			displayHelp();
		}
		System.out.println("Exited");
	}
	
	private static boolean isGen(String[] args) {
		boolean out = (args.length == 4 || args.length == 5) && args[0].equals("-gen") && args[1].matches("[0-9]*") && args[2].matches("[0-9]*");
		if(args.length == 5) {
			return args[4].toLowerCase().trim().equals("-sim");
		}
		
		return out;
	}
	
	private static boolean isGenAll(String[] args) {
		boolean out = (args.length == 3 || args.length == 4) && args[0].equals("-genall") && args[1].matches("[0-9]*");
		if(args.length == 4) {
			return args[3].toLowerCase().trim().equals("-sim");
		}
		
		return out;
	}

	private static void displayHelp() {
		System.out.println("-gen Agents Objects Directory [-sim]  -  Generates all strict profiles and saves them to the directory.");
		System.out.println("The Parameter -sim removes the first to lines of each file just storing the profile. Files are incompatible with PAW.\n\n");
		
		System.out.println("-genAll Size Directory [-sim]  -  Generates all strict profiles with 2 <= #Agents = #Objects <= Size and saves them to the directory.");
		System.out.println("The Parameter -sim removes the first to lines of each file just storing the profile. Files are incompatible with PAW.\n\n");
		
		System.out.println("-diff Size Path   -  Checks the available algorithms for their first difference (2 <=#Agents = #Objects <= Size) and saves the result.");
		//System.out.println("-envy Size Path   -  Checks PAR for weak envy (2 <=#Agents = #Objects <= Size) and saves the result.");
	}
}
