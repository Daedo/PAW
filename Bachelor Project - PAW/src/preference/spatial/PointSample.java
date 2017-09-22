package preference.spatial;

/**
 * Class used by the spatial model.
 * Represents the abstract 2D preference space.
 * @author Dominik
 *
 */
public class PointSample {
	public DataPoint2D[] agentPoints;
	public DataPoint2D[] objectsPoints;
	
	public PointSample(int agents, int objects) {
		agentPoints = new DataPoint2D[agents];
		objectsPoints = new DataPoint2D[objects];
		
		for(int i=0;i<agentPoints.length;i++) {
			//Agents don't need data
			agentPoints[i] = new DataPoint2D(-1);
		}
		
		for(int i=0;i<objectsPoints.length;i++) {
			objectsPoints[i] = new DataPoint2D(i);
		}
	}
}
