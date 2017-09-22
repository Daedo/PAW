package gui.allocation.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;

import allocation.Allocation;
import preference.PreferenceRelation;
import preference.scenario.Scenario;

/**
 * Creates PieCharts for different agents, given a scenario and an allocation.
 * No longer used. It was replaced by {@link PieChartFactory}
 * Based on http://www.jfree.org/jfreechart/api/javadoc/src-html/org/jfree/chart/demo/PieChartDemo1.html
 * @author Dominik
 *
 */
@Deprecated
public class AgentPieChartFactory {
	private Vector<PieDataset> dataSets;
	private Scenario scenario;
	private Allocation allocation;

	public AgentPieChartFactory(Scenario scr, Allocation alloc) {
		dataSets = new Vector<>();
		this.scenario = scr;
		this.allocation = alloc;

		for(int i=0;i<scenario.getAgentCount();i++) {
			dataSets.addElement(createDataset(i));
		}
	}

	/**
	 * Create the dataset for an agent.
	 * 
	 * @param agentIndex
	 * @return
	 */
	private PieDataset createDataset(int agentIndex) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		PreferenceRelation rel = scenario.getAgentRelation(agentIndex);
		for(int i=0;i<rel.getGroupcount();i++) {
			int[] groupObj = rel.getGroup(i);
			for(int j=0;j<groupObj.length;j++) {
				int currentObj = groupObj[j];
				double alloc = allocation.getValue(agentIndex, currentObj);

				if(alloc>0) {
					Double alValue = new Double(allocation.getValue(agentIndex, currentObj));
					PieKey key = new PieKey(i+1, currentObj, scenario.getObject(currentObj));	
					dataset.setValue(key, alValue);
				}
			}
		}
		return dataset;
	}

	/**
	 * Create the pie chart for a single agent or the overview chart.
	 * Calls createChart.
	 * 
	 * @param agentIndex
	 * @param mode
	 * @return
	 */
	public PieChart createPieChart(int agentIndex, PieCreationMode mode) {
		JFreeChart chart = createChart(agentIndex,mode);
		chart.setPadding(new RectangleInsets(4, 8, 2, 2));
		PieChart out = new PieChart(chart);

		if(mode==PieCreationMode.OVERVIEW_PIE) {
			out.setMouseWheelEnabled(false);
			Dimension pSize = out.getPreferredSize();
			pSize.height /=2;
			pSize.width  /=2;
			
			out.setPreferredSize(pSize);
		}

		return out;
	}

	/**
	 * Creates a the actual chart.
	 *
	 * @param agentIndex 
	 * @param PieCreationMode 
	 *
	 * @return A chart.
	 */
	private JFreeChart createChart(int agentIndex, PieCreationMode mode) {
		boolean isNormal = mode == PieCreationMode.NORMAL_PIE;

		PieDataset dataset = dataSets.elementAt(agentIndex);
		String agent = scenario.getAgent(agentIndex);

		JFreeChart chart = ChartFactory.createPieChart(
				"Allocation of "+agent,  		// chart title
				dataset,            			// data
				true,              				// legend
				true,               			// tooltips
				false               			// no URL generation
				);

		// customise the title position and font
		TextTitle t = chart.getTitle();
		t.setHorizontalAlignment(HorizontalAlignment.LEFT);
		//t.setPaint(new Color(240, 240, 240));
		t.setFont(new Font("Arial", Font.BOLD, 26));

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(null);
		plot.setInteriorGap(0.04);
		plot.setOutlineVisible(true);

		// Set Plot Colors
		//First Preference: Green
		Color first = Color.GREEN;
		//Last Preference: Red
		Color last = Color.RED;
		//Gradient within Groups

		PreferenceRelation rel = scenario.getAgentRelation(agentIndex);
		int relCount = rel.getGroupcount();
		for(int i=0;i<relCount;i++) {
			float mix = i/((float)relCount-1);
			Color color = blendColor(first, last, mix);
			int[] group = rel.getGroup(i);

			for(int j=0;j<group.length;j++) {
				int currentObj = group[j];
				PieKey key = new PieKey(i+1, currentObj, scenario.getObject(currentObj));
				plot.setSectionPaint(key, color);
			}
		}


		plot.setBaseSectionOutlinePaint(Color.DARK_GRAY);
		plot.setSectionOutlinesVisible(true);
		plot.setBaseSectionOutlineStroke(new BasicStroke(2.0f));

		// customise the section label appearance
		plot.setLabelFont(new Font("Courier New", Font.BOLD, 20));
		plot.setLabelLinkPaint(Color.DARK_GRAY);
		plot.setLabelLinkStroke(new BasicStroke(2.0f));
		plot.setLabelOutlineStroke(null);
		plot.setLabelPaint(Color.DARK_GRAY);
		plot.setLabelBackgroundPaint(null);

		if(!isNormal) {
			plot.setLabelGenerator(null); 
		}
		return chart;
	}

	/**
	 * Blend two colors linearly using hsb Space.
	 * @param c1
	 * @param c2
	 * @param mix
	 * @return
	 */
	private static Color blendColor(Color c1,Color c2, float mix) {
		float[] col1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		float[] col2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);

		float hue		= col1[0]*(1-mix) + col2[0]*mix;
		float saturation= col1[1];
		float brightness= col1[2];

		return Color.getHSBColor(hue, saturation, brightness);
	}

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

	public enum PieCreationMode {
		NORMAL_PIE,OVERVIEW_PIE;
	}
}
