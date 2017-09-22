package main.sampler.subroutines;

import static allocation.algorithms.AllocationAlgorithm.ADAPTIVE_BOSTON_MECHANISM_TIE;
import static allocation.algorithms.AllocationAlgorithm.ADAPTIVE_BOSTON_MECHANISM_PATH;
import static allocation.algorithms.AllocationAlgorithm.EXTENDED_PROBABLILISTIC_SERIAL;
import static allocation.algorithms.AllocationAlgorithm.NAIVE_BOSTON_MECHANISM_TIE;
import static allocation.algorithms.AllocationAlgorithm.NAIVE_BOSTON_MECHANISM_PATH;
import static allocation.algorithms.AllocationAlgorithm.POPULAR_ASSIGNMENT;
import static allocation.algorithms.AllocationAlgorithm.RANDOM_SERIAL_DICTATORSHIP;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import allocation.Allocation;
import allocation.AllocationConverter;
import allocation.AllocationStrategy;
import allocation.algorithms.AllocationAlgorithm;
import main.sampler.ScenarioGenerator;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

/**
 * Class used to export the preference profiles for the thesis. Doesn't work properly in some cases. 
 * @author Dominik
 *
 */
@Deprecated
public class LatexExport {
	public static void latexExportSampling(String path) throws IOException {
		String out = "";
		out+= "\\section{List of all profiles modulo anonymity and neutrality}\n";

		int max = 3;
		for(int i=2;i<=max;i++) {
			Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(i,i);

			out+= "\\subsection{Profiles of size $n="+i+"$}\n";
			out+= "\\begin{calstable}\n";
			for(int j=0;j<scenarios.size();j++) {
				if(j%4 == 0) {
					out+="\\brow\n";
				}

				out+="\\cell{\n";
				out+= "\\textbf{Profile "+i+","+i+" - "+(j+1)+"}\n";
				out+= toLatex(scenarios.get(j));

				out+="}\n";

				if(j%4 == 3) {
					out+="\\erow\n";
				} 

			}

			out+= "\\end{calstable}\n";
		}


		PrintWriter writer = new PrintWriter(path+"/09_profiles.tex", "UTF-8");
		writer.println(out);
		writer.close();
	}

	private static String toLatex(Scenario scenario) {
		String out = "\\begin{align*}\n";

		for(int i=0;i<scenario.getAgentCount();i++) {
			out+= "a_"+(i+1)+": ";
			PreferenceRelation rel = scenario.getAgentRelation(i);
			for(int j=0;j<rel.getGroupcount();j++) {
				if(j!=0) {
					out+=",";
				}
				out+= "o_"+(rel.getGroup(j)[0]+1);
			}

			out+="\\\\\n";
		}

		out+="\\end{align*}";
		return out;
	}

	public static void latexExportListAll(int size, String file) throws IOException {
		String out = "";
		
		AllocationAlgorithm[] algs ={ADAPTIVE_BOSTON_MECHANISM_PATH,ADAPTIVE_BOSTON_MECHANISM_TIE,NAIVE_BOSTON_MECHANISM_PATH,NAIVE_BOSTON_MECHANISM_TIE,EXTENDED_PROBABLILISTIC_SERIAL,POPULAR_ASSIGNMENT,RANDOM_SERIAL_DICTATORSHIP};

		for(int i=2;i<=size;i++) {
			Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(i,i);
			for(Scenario s:scenarios) {
				HashMap<String, Allocation> allocs = new HashMap<>();
				
				for(AllocationAlgorithm alg : algs) {
					AllocationStrategy a = (AllocationStrategy) alg.getAllocationStrategy();
					String name = alg.toString();
					
					Allocation alloc = a.allocate(s);
					
					boolean add = true;
					
					for(String key: allocs.keySet()) {
						Allocation allocPrime = allocs.get(key);
						if(!CheckDifferences.arrayDiff(alloc.getData(), allocPrime.getData())) {
							allocs.remove(key);
							key = key+"\\& "+name;
							allocs.put(key, allocPrime);
							add = false;
							break;
						}
					}
					
					if(add) {
						allocs.put(name, alloc);
					}
				}
				
				out += s.getStoreVersion().replaceFirst(".*?\n", "").replaceFirst(".*?\n", "").replaceAll("(^|(?<=\n))", "a_i:").replaceAll("(?=[0-3])", "o_")+"\n\n";
				for(String key: allocs.keySet()) {
					Allocation alloc = allocs.get(key);
					out+=key+"\n";
					out+=AllocationConverter.toFractionFile(s, alloc).replaceAll("Agent ", "a_").replaceFirst(".*?\n", "").replaceAll("(?<=a_[1-3]),", ":")+"\n\n";
				}
				out+="====================================\n";	
			}
		}
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println(out);
		writer.close();
	}
}
