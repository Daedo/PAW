package gui.allocation.diagram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JPanel;

public class OverviewDiagramPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public OverviewDiagramPanel(PieChartFactory pieFactory, int numberOfAgents) {
		// 1 and 2 -> 1
		// 3 and 4 -> 2 etc.
		int columns = numberOfAgents / 2 + numberOfAgents % 2; 
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[columns];
		gridBagLayout.rowHeights = new int[]{0,0};
		
		gridBagLayout.columnWeights = new double[columns];
		Arrays.fill(gridBagLayout.columnWeights, 1.0);
		gridBagLayout.rowWeights = new double[]{1.0,1.0};
		setLayout(gridBagLayout);
		
		for(int i=0;i<numberOfAgents;i++) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = i / 2;
			gbc.gridy = i%2;
			gbc.fill  = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets = new Insets(0, 0, 5, 0);
			add(pieFactory.createAgentOverviewPie(i), gbc);
		}
	}

}
