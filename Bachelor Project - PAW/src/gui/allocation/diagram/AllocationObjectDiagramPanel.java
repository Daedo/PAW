package gui.allocation.diagram;

import javax.swing.JPanel;

import allocation.Allocation;
import preference.scenario.Scenario;

/**
 * Displays the charts for the object overview.
 * @author Dominik
 *
 */
public class AllocationObjectDiagramPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	PieChartFactory factory;
	/**
	 * Create the panel.
	 */
	public AllocationObjectDiagramPanel(Scenario sc, Allocation allocation) {
		factory = new PieChartFactory(sc, allocation);
		add(factory.createObjectOverviewPies());
	}

}
