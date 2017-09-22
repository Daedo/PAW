package allocation;

import main.HelperFunctions;
import preference.scenario.Scenario;

/**
 * Converts an allocation into a .csv table for later storage.
 * @author Dominik
 *
 */
public class AllocationConverter {
	/**
	 * Rounds the given allocation to the 6th significant digit
	 * @param sc
	 * @param alloc
	 * @return
	 */
	public static String toCSV(Scenario sc, Allocation alloc) {
		String out = "Assignment,";
		double[][] data = alloc.getData();
		
		for(int i=0;i<sc.getObjectCount();i++) {
			if(i!=0) {
				out+=",";
			}
			out+=sc.getObject(i);
		}
		out+="\n";
		
		for(int i=0;i<sc.getAgentCount();i++) {
			out+=sc.getAgent(i);
			for(int j=0;j<sc.getObjectCount();j++) {
				out+=","+HelperFunctions.round(data[i][j],6);
			}
			out+="\n";
		}
		
		return out.trim();
	}
	
	/**
	 * Uses fractions to approximate the data
	 * 
	 * @param sc
	 * @param alloc
	 * @return
	 */
	public static String toFractionFile(Scenario sc, Allocation alloc) {
		String out = "Assignment,";
		double[][] data = alloc.getData();
		
		for(int i=0;i<sc.getObjectCount();i++) {
			if(i!=0) {
				out+=",";
			}
			out+=sc.getObject(i);
		}
		out+="\n";
		
		for(int i=0;i<sc.getAgentCount();i++) {
			out+=sc.getAgent(i);
			for(int j=0;j<sc.getObjectCount();j++) {
				int precisionFactor = 1;
				double absMin	= Math.abs(HelperFunctions.getMinBiggerZero2D(data));
				double absLog	= Math.abs(Math.log10(absMin));
				precisionFactor = (int)Math.pow(10, Math.ceil(absLog));
				
				out+=","+HelperFunctions.toFraction(data[i][j],precisionFactor).trim();
			}
			out+="\n";
		}
		
		return out.trim();
	}
}
