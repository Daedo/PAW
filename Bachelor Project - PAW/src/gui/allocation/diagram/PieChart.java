package gui.allocation.diagram;

import java.awt.Dimension;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * Simple Chart Panel, with added options.
 * @author Dominik
 *
 */
public class PieChart extends ChartPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the panel.
	 */
	public PieChart(JFreeChart chart) {
		super(chart);
		setMouseWheelEnabled(true);
		setPreferredSize(new Dimension(600, 300));
	}
	
}
