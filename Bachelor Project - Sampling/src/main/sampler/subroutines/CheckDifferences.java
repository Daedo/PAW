package main.sampler.subroutines;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import allocation.Allocation;
import allocation.AllocationConverter;
import allocation.AllocationStrategy;
import allocation.algorithms.ExtendedProbabilisticSerial;
import allocation.algorithms.PathEqualAdaptiveBostonMechanism;
import allocation.algorithms.PathEqualNaiveBostonMechanism;
import allocation.algorithms.PopularAssignment;
import allocation.algorithms.RandomSerialDictatorship;
import main.sampler.DiffStructure;
import main.sampler.ScenarioGenerator;
import preference.scenario.Scenario;

/**
 * Find the first profiles <= a given size, where given algorithms differ.
 * @author Dominik
 *
 */
public class CheckDifferences {

	public static void diff(int size, String file) {
		try {
			checkDifferences(size, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checkDifferences(int max, String file) throws IOException {

		String logData = "";

		AllocationStrategy[] algs ={new PathEqualNaiveBostonMechanism(), new PathEqualAdaptiveBostonMechanism(), new RandomSerialDictatorship(), new PopularAssignment(), new ExtendedProbabilisticSerial()};
		DiffStructure diff = new DiffStructure(algs.length);

		int size = 2;
		while(!diff.isEmpty() || size == max) {
			Vector<Scenario> scenarios = ScenarioGenerator.generateStrictScenarios(size, size);

			for(int k=0;k<scenarios.size();k++) {
				Scenario s = scenarios.elementAt(k);

				Allocation[] allocs = new Allocation[algs.length];
				for(int i=0;i<algs.length;i++) {
					if(diff.hasPairs(i)) {
						allocs[i] = algs[i].allocate(s);
					}
				}

				for(int i=0;i<allocs.length;i++) {

					if(!diff.hasPairs(i)) {
						continue;
					}

					for(int j=i+1;j<allocs.length;j++) {
						if(!diff.hasPairs(j)) {
							continue;
						}

						if(!diff.hasPair(i,j)) {
							continue;
						}

						double[][] dataA = allocs[i].getData();
						double[][] dataB = allocs[j].getData();

						if(arrayDiff(dataA,dataB)) {

							logData += "Problem ID: "+size+","+size+" - "+(k+1)+"\n";
							logData += s+"\n\n";
							logData +=algs[i].toString()+":\n";
							logData += AllocationConverter.toFractionFile(s, allocs[i])+"\n\n";
							logData +=algs[j].toString()+":\n";
							logData += AllocationConverter.toFractionFile(s, allocs[j])+"\n\n";

							diff.removePair(i, j);
						}
					}
				}
			}

			size++;
		}

		if(!diff.isEmpty()) {
			System.out.println("Persistent overlaps up to limit");
		} else {
			System.out.println("All differences found");
		}

		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println(logData);
		writer.close();
	}

	/**
	 * Checks if the added difference between two 2D Arrays is below 1e-6.
	 * @param matA
	 * @param matB
	 * @return
	 */
	public static boolean arrayDiff(double[][] matA, double[][] matB) {
		double diff = 0.0;
		for(int i=0;i<matA.length;i++) {
			for(int j=0;j<matA[i].length;j++) {
				diff+= Math.abs(matA[i][j]-matB[i][j]);
			}
		}

		return diff>1e-6;
	}
}
