package gui.allocation.allocationTree;

import java.awt.Component;

import allocation.Allocation;
import gui.allocation.AllocationPanel;
import preference.scenario.Scenario;

/**
 * {@link AllocationTree} node representing a simple allocation.
 * Returns a {@link AllocationPanel}, when asked politely.
 * 
 * @author Dominik
 *
 */
public class AllocationTreeBaseNode extends AllocationTreePanelNode{

	private static final long serialVersionUID = 1L;
	private AllocationPanel panel;
	private Scenario scenario;
	private Allocation allocation;
	
	public AllocationTreeBaseNode(String name,Scenario sc, Allocation allocation) {
		super(name);
		this.panel = null;
		this.allocation = allocation;
		this.scenario = sc;
	}

	@Override
	public Component getPanelComponent() {
		if(panel==null) {
			panel = new AllocationPanel(scenario, allocation);
		}
		return panel;
	}

	public void reloadPanel() {
		panel = new AllocationPanel(scenario, allocation);
	}
	
}
