package gui.allocation.diagram;

import java.awt.Color;
import java.util.HashMap;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import allocation.Allocation;
import main.HelperFunctions;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

/**
 * Implements a lot of helper methods for the PieChartFactory class.
 * 
 * @author Dominik
 *
 */
public class PieChartFactoryWorker {
	private Scenario scenario;
	private Allocation allocation;

	public PieChartFactoryWorker(Scenario scr, Allocation alloc) {
		this.scenario = scr;
		this.allocation = alloc;
	}

	///////Single Agent
	public HashMap<Object, Color> createAgentColorMap(int agent) {
		HashMap<Object, Color> out = new HashMap<>();
		// Set Plot Colors
		//First Preference: Green
		Color first = Color.GREEN;
		//Last Preference: Red
		Color last = Color.RED;
		//Gradient within Groups

		PreferenceRelation rel = scenario.getAgentRelation(agent);
		int relCount = rel.getGroupcount();
		for(int i=0;i<relCount;i++) {
			float mix = i/((float)relCount-1);
			Color color = HelperFunctions.blendColor(first, last, mix);
			int[] group = rel.getGroup(i);

			if(relCount == 1) {
				color = first;
			}

			for(int j=0;j<group.length;j++) {
				int currentObj = group[j];
				PieKey key = new PieKey(i+1, currentObj, scenario.getObject(currentObj));
				out.put(key, color);
			}
		}

		return out;
	}

	public PieDataset createAgentDataset(int agent) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		PreferenceRelation rel = scenario.getAgentRelation(agent);
		for(int i=0;i<rel.getGroupcount();i++) {
			int[] groupObj = rel.getGroup(i);
			for(int j=0;j<groupObj.length;j++) {
				int currentObj = groupObj[j];
				double alloc = allocation.getValue(agent, currentObj);

				if(alloc>0) {
					Double alValue = new Double(allocation.getValue(agent, currentObj));
					PieKey key = new PieKey(i+1, currentObj, scenario.getObject(currentObj));	
					dataset.setValue(key, alValue);
				}
			}
		}
		return dataset;
	}
	///////Multi Agent
	public CategoryDataset createAgentOverviewDataset() {
		final double[][] data = allocation.getData();
        
		Comparable<?>[] agentKeys = new String[scenario.getAgentCount()];
		for(int i=0;i<scenario.getAgentCount();i++) {
			agentKeys[i] = scenario.getAgent(i);
		}
		
		Comparable<?>[] objectKeys= new String[scenario.getObjectCount()];
		for(int i=0;i<scenario.getObjectCount();i++) {
			objectKeys[i] = scenario.getObject(i);
		}
		
        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(agentKeys, objectKeys, data);
        
        for(int i=0;i<dataset.getColumnCount();i++) {
        	System.out.println(dataset.getColumnKey(i));
        }
        
        for(int i=0;i<dataset.getRowCount();i++) {
        	System.out.println(dataset.getRowKey(i));
        	
        }
        
        return dataset;
	}
	
	///////Multi Object
	public CategoryDataset createObjectOverviewDataset() {
		final double[][] data = HelperFunctions.transpose(allocation.getData());
        
		Comparable<?>[] agentKeys = new String[scenario.getAgentCount()];
		for(int i=0;i<scenario.getAgentCount();i++) {
			agentKeys[i] = "A"+(i+1)+": \""+scenario.getAgent(i)+"\"";
		}
		
		Comparable<?>[] objectKeys= new String[scenario.getObjectCount()];
		for(int i=0;i<scenario.getObjectCount();i++) {
			objectKeys[i] = "O"+(i+1)+": \""+scenario.getObject(i)+"\"";
		}
		
        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(objectKeys,agentKeys, data); 
        return dataset;
	}
	
	/**
	 * Helper Class for datasets
	 * @author Dominik
	 *
	 */
	private static class PieKey implements Comparable<PieKey> {
		int groupNumber;
		int objectNumber;
		String objectName;

		public PieKey(int group,int obj,String name) {
			groupNumber = group;
			objectNumber = obj;
			objectName = name;
		}

		@Override
		public int compareTo(PieKey o) {
			int gComp = Integer.compare(groupNumber, o.groupNumber);
			if(gComp==0) {
				return Integer.compare(objectNumber, o.objectNumber);
			}
			return gComp;
		}

		@Override
		public String toString() {
			return "Preference "+groupNumber+"\n("+objectName+")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + groupNumber;
			result = prime * result + objectNumber;
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
			PieKey other = (PieKey) obj;
			if (groupNumber != other.groupNumber)
				return false;
			if (objectNumber != other.objectNumber)
				return false;
			return true;
		}
	}

}
