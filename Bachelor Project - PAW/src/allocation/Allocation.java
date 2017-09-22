package allocation;

import main.Main;

/**
 * Class representing a allocation.
 * In essence a wrapper for a 2D double array with a better
 * toString method
 * 
 * @author Dominik
 *
 */
public class Allocation {
	private double[][] allocation;
	
	public Allocation(int agents, int objects) {
		allocation = new double[agents][objects];
	}
	
	public Allocation(double[][] alloc) {
		allocation = alloc;
	}
	
	public double[][] getData() {
		//XXX You might want to use a defensive copy here.
		return allocation;
	}
	
	public double getValue(int agent,int object) {
		return allocation[agent][object];
	}
	
	public void setValue(int agent,int object, double value) {
		if(value>-Main.F_POINT_PRECISION && value <= 1+Main.LOW_F_POINT_PRECISION) {
			allocation[agent][object] = value;	
		}
	}
	
	@Override
	public String toString() {
		String out = "";
		for(int i=0;i<allocation.length;i++) {
			out+="Agent "+i+": ";
			for(int j=0;j<allocation[i].length;j++) {
				if(j!=0) {
					out+=", ";
				}
				out+=allocation[i][j];
			}
			out+="\n";
		}
		
		return out.trim();
	}
}
