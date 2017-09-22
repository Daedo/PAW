package preference.spatial;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Class used by the spatial model.
 * Models an object or agent in the abstract 2D preference space.
 * @author Dominik
 *
 */
public class DataPoint2D implements Comparable<DataPoint2D> {
	
	@Deprecated //We used this for absolute distance clustering
	public static final double CLUSTER_DISTANCE_MAX = Math.sqrt(2);
	
	
	public static Point2D.Double reference = new Point2D.Double(); // Reference Point (=Agent) Set during the execution of the algorithm.
	public Point2D.Double point;
	public int data; //Object ID

	public DataPoint2D() {
		this(0);
	}

	public DataPoint2D(int d) {
		data = d;
		point = getRandomPoint();
	}

	public DataPoint2D(Point2D.Double p,int d) {
		point = p;
		data = d;
	}
	
	private static Point2D.Double getRandomPoint() {
		Random r = new Random();
		double d1 = r.nextDouble();
		double d2 = r.nextDouble();
		return new Point2D.Double(d1, d2);
	}
	
	public double distance(DataPoint2D p2) {
		return point.distance(p2.point);
	}
	
	public double distanceToReference() {
		return reference.distance(point);
	}
	
	@Override
	public int compareTo(DataPoint2D o) {
		double d1 = this.distanceToReference();
		double d2 = o.distanceToReference();
		return Double.compare(d1, d2);
	}

	@Override
	public String toString() {
		return "["+point.getX()+"|"+point.getY()+"] : "+data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataPoint2D other = (DataPoint2D) obj;
		if (data != other.data)
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}
}