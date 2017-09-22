package gui.allocation.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.TableOrder;

import allocation.Allocation;
import preference.scenario.Scenario;

/**
 * Creates PieCharts for different agents and objects, given a scenario and an allocation.
 * Based on http://www.jfree.org/jfreechart/api/javadoc/src-html/org/jfree/chart/demo/PieChartDemo1.html
 * and on http://www.java2s.com/Code/Java/Chart/JFreeChartMultiplePieChartDemo1.htm
 * @author Dominik
 *
 */
public class PieChartFactory {
	private Scenario scenario;
	private PieChartFactoryWorker worker;

	public PieChartFactory(Scenario scr, Allocation alloc) {
		this.scenario = scr;
		worker = new PieChartFactoryWorker(scr, alloc);
	}

	/**
	 * Create the diagram for a single agent.
	 * @param agent
	 * @return
	 */
	public PieChart createAgentPie(int agent) {
		PieDataset data 			= worker.createAgentDataset(agent);
		HashMap<Object,Color> color = worker.createAgentColorMap(agent);

		JFreeChart chart = createChart(data,color,"Allocation of Agent \""+scenario.getAgent(agent)+"\"",true);
		chart.setPadding(new RectangleInsets(4, 8, 2, 2));
		return new PieChart(chart);
	}

	/**
	 * Create the diagram for an agent as part of the overview panel.
	 * This can't be done like the object overview panel, because the colors are different for every agent.
	 * @param agent
	 * @return
	 */
	public PieChart createAgentOverviewPie(int agent) {
		PieDataset data 			= worker.createAgentDataset(agent);
		HashMap<Object,Color> color = worker.createAgentColorMap(agent);

		JFreeChart chart = createChart(data, color, "Allocation of Agent \""+scenario.getAgent(agent)+"\"",false);
		chart.setPadding(new RectangleInsets(4, 8, 2, 2));
		PieChart c = new PieChart(chart); 
		c.setMouseWheelEnabled(false);
		c.setPreferredSize(new Dimension(300, 150));
		return c;
		
	}

	/**
	 * Create the Object Overview diagram.
	 * @return
	 */
	public PieChart createObjectOverviewPies() {
		CategoryDataset data 		= worker.createObjectOverviewDataset();
		
		JFreeChart chart = createMultiChart(data,new HashMap<>(),"Object Allocation Overview");
		chart.setPadding(new RectangleInsets(4, 8, 2, 2));
		return new PieChart(chart);
	}

	/**
	 * Creates a multichart, that contains multiple piecharts in one diagram.
	 * @param dataset
	 * @param colorMap
	 * @param title
	 * @return
	 */
	private JFreeChart createMultiChart(final CategoryDataset dataset, HashMap<Object,Color> colorMap, String title) {
		final JFreeChart chart = ChartFactory.createMultiplePieChart(
				title,  				// chart title
				dataset,                // dataset
				TableOrder.BY_ROW,
				true,                  // include legend
				true,
				false
				);
		final MultiplePiePlot plot = (MultiplePiePlot) chart.getPlot();
		final JFreeChart subchart = plot.getPieChart();
		final PiePlot p = (PiePlot) subchart.getPlot();

		p.setInteriorGap(0.30);

		setPiePlotOptions(p, false);
		return chart;
	}

	/**
	 * Creates a single chart.
	 *
	 * @param agentIndex 
	 * @param PieCreationMode 
	 *
	 * @return A chart.
	 */
	private JFreeChart createChart(PieDataset dataset, HashMap<Object,Color> colorMap, String title,boolean labels) {
		JFreeChart chart = ChartFactory.createPieChart(
				title,  						// chart title
				dataset,            			// data
				true,              				// legend
				true,               			// tooltips
				false               			// no URL generation
				);

		// customise the title position and font
		TextTitle t = chart.getTitle();
		t.setHorizontalAlignment(HorizontalAlignment.LEFT);
		t.setFont(new Font("Arial", Font.BOLD, 26));
		setPiePlotOptions((PiePlot)chart.getPlot(), labels);
		setPiePlotColors((PiePlot)chart.getPlot(), dataset, colorMap);
		return chart;
	}

	/**
	 * Customizes each pie chart.
	 * @param plot
	 * @param allowLabels
	 */
	private void setPiePlotOptions(PiePlot plot, boolean allowLabels) {
		plot.setBackgroundPaint(null);
		plot.setInteriorGap(0.04);
		plot.setOutlineVisible(true);

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

		if(!allowLabels) {
			plot.setLabelGenerator(null); 
		}
	}

	/**
	 * Sets the colors to each pie chart.
	 * @param plot
	 * @param dataset
	 * @param colorMap
	 */
	private void setPiePlotColors(PiePlot plot, PieDataset dataset, HashMap<Object,Color> colorMap) {
		// Set Plot Colors
		for(int i=0;i<dataset.getItemCount();i++) {
			Comparable<?> key = dataset.getKey(i);
			plot.setSectionPaint(key, colorMap.get(key));
		}
	}

	
}
