package gui.allocation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import allocation.Allocation;
import axiom.Efficiency;
import axiom.StochasticDominance;
import axiom.StochasticDominance.ViolationPair;
import preference.scenario.Scenario;

/**
 * Panel that displays all violations of axioms of a given allocation.
 * @author Dominik
 *
 */
public class AllocationAxiomPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param allocation 
	 * @param sc 
	 */
	public AllocationAxiomPanel(Scenario sc, Allocation allocation) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0,0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0,1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblWeakEnvy = new JLabel("Weak Envy");
		GridBagConstraints gbc_lblWeakEnvy = new GridBagConstraints();
		gbc_lblWeakEnvy.insets = new Insets(0, 0, 5, 5);
		gbc_lblWeakEnvy.gridx = 0;
		gbc_lblWeakEnvy.gridy = 0;
		add(lblWeakEnvy, gbc_lblWeakEnvy);

		JScrollPane scrollPaneWeakEnvy = new JScrollPane();
		GridBagConstraints gbc_scrollPaneWeakEnvy = new GridBagConstraints();
		gbc_scrollPaneWeakEnvy.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneWeakEnvy.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneWeakEnvy.gridx = 0;
		gbc_scrollPaneWeakEnvy.gridy = 1;
		add(scrollPaneWeakEnvy, gbc_scrollPaneWeakEnvy);

		Vector<ViolationPair> weakEnvy = StochasticDominance.getWeakEnvy(sc, allocation.getData());
		JList<String> listWeakEnvy = new JList<>(getEnvyListData(sc, weakEnvy));
		
		scrollPaneWeakEnvy.setViewportView(listWeakEnvy);

		JLabel lblEnvy_1 = new JLabel("Envy");
		GridBagConstraints gbc_lblEnvy_1 = new GridBagConstraints();
		gbc_lblEnvy_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblEnvy_1.gridx = 1;
		gbc_lblEnvy_1.gridy = 0;
		add(lblEnvy_1, gbc_lblEnvy_1);
		
		JScrollPane scrollPaneEnvy = new JScrollPane();
		GridBagConstraints gbc_scrollPaneEnvy = new GridBagConstraints();
		gbc_scrollPaneEnvy.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneEnvy.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneEnvy.gridx = 1;
		gbc_scrollPaneEnvy.gridy = 1;
		add(scrollPaneEnvy, gbc_scrollPaneEnvy);

		Vector<ViolationPair> envy = StochasticDominance.getEnvy(sc, allocation.getData());
		JList<String> listEnvy = new JList<>(getEnvyListData(sc, envy));
		scrollPaneEnvy.setViewportView(listEnvy);
		
		JLabel lblNewLabel = new JLabel("Efficiency:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		add(lblNewLabel, gbc_lblNewLabel);
		
		String efState = "Can't compute efficiency: Number of agents is not equal to the number of objects!";
		
		if(sc.getAgentCount()==sc.getObjectCount()) {
			Optional<Allocation> dom = Efficiency.getDominatingAllocation(sc, allocation);
			
			if(!dom.isPresent()) {
				efState = "Efficient";
				gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
				gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
				
			} else {
				efState = "Dominated";
				AllocationTablePanel table = new AllocationTablePanel(sc, dom.get());
				GridBagConstraints gbc_table = new GridBagConstraints();
				gbc_table.anchor = GridBagConstraints.WEST;
				gbc_table.fill = GridBagConstraints.BOTH;
				gbc_table.gridx = 0;
				gbc_table.gridwidth = GridBagConstraints.REMAINDER;
				gbc_table.gridy = 3;
				add(table, gbc_table);
			}
		}
		
		JLabel lblEfficiencyState = new JLabel(efState);
		
		
		GridBagConstraints gbc_lblEfficiencyState = new GridBagConstraints();
		gbc_lblEfficiencyState.anchor = GridBagConstraints.EAST;
		gbc_lblEfficiencyState.gridx = 1;
		gbc_lblEfficiencyState.gridy = 2;
		add(lblEfficiencyState, gbc_lblEfficiencyState);
	}
	
	/**
	 * Converts ViolationPair lists into String data for a JList.
	 * @param sc
	 * @param envyData
	 * @return
	 */
	private String[] getEnvyListData(Scenario sc, Vector<ViolationPair> envyData) {
		Vector<String> out = new Vector<>();
		for(int i=0;i<envyData.size();i++) {
			ViolationPair agentEnvy = envyData.get(i);
			out.addElement("Property violated by "+sc.getAgent(agentEnvy.agentA)+" and "+sc.getAgent(agentEnvy.agentB));
			
		}
	
		if(out.isEmpty()) {
			out.add("Envy Free");
		}
		
		String[] outArray = new String[out.size()];
		return out.toArray(outArray);
	}

}
