package preference.spatial;

import java.util.HashSet;

/**
 * Helper class for the spatial model. Represents a clustering of points.
 * @author Dominik
 *
 */
public class Cluster implements Comparable<Cluster> {
	public HashSet<DataPoint2D>  points;
	public DataPoint2D  mainPoint;
	
	/**
	 * Creates a new cluster without any points (= objects) but with a reference point (= the agent) to get the distances.
	 * @param main
	 */
	public Cluster(DataPoint2D main) {
		mainPoint = main;
		points = new HashSet<>();
	}
	
	public int size() {
		return points.size();
	}

	public void add(DataPoint2D p) {
		points.add(p);
	}
	
	public double getAverageDistance() {
		double dist = 0;
		
		for(DataPoint2D point:points) {
			dist+=point.distanceToReference();
			
		}
		
		return dist/points.size();
	}

	@Override
	public int compareTo(Cluster o) {
		double d1 = getAverageDistance();
		double d2 = o.getAverageDistance();
		return Double.compare(d1, d2);
	}
}