package gui.allocation;

import javax.swing.JTabbedPane;

import allocation.Allocation;
import gui.allocation.diagram.AllocationAgentDiagramPanel;
import gui.allocation.diagram.PieChartFactory;
import preference.scenario.Scenario;

/**
 * Main Panel for the allocation window. Allows user to select the current tab.
 * @author Dominik
 *
 */
public class AllocationPanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AllocationTablePanel tablePane;
	private AllocationAgentDiagramPanel agentDiagramPane;
	private AllocationAxiomPanel axiomPane;

	/**
	 * Create the frame.
	 */
	public AllocationPanel(Scenario sc, Allocation allocation) {

		tablePane = new AllocationTablePanel(sc, allocation);
		add("Allocation", tablePane);
		
		agentDiagramPane = new AllocationAgentDiagramPanel(sc, allocation);
		add("Agents",agentDiagramPane);
		
		PieChartFactory fac = new PieChartFactory(sc, allocation);
		add("Objects",fac.createObjectOverviewPies());
		
		axiomPane = new AllocationAxiomPanel(sc,allocation);
		add("Axiomatic Properties",axiomPane);
	}

	
}
