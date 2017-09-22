package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import preference.scenario.Scenario;

/**
 * Panel displaying all preference profiles of the agents. 
 * @author Dominik
 *
 */
public class OverviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Vector<PreferencePanel> panels;
	
	/**
	 * Create the panel.
	 */
	public OverviewPanel(Scenario sc) {
		panels = new Vector<>();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[sc.getAgentCount()];
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[sc.getAgentCount()];
		gridBagLayout.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		
		for(int i=0;i<sc.getAgentCount();i++) {
			GridBagConstraints con = new GridBagConstraints();
			con.gridx = i;
			con.fill = GridBagConstraints.BOTH;
			//con.insets = new Insets(0, 0, 0, 10);
			
			JPanel container = new JPanel(new BorderLayout());
			container.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));//createEtchedBorder(EtchedBorder.RAISED));
			PreferencePanel panel = new PreferencePanel(sc, i,false);
			
			panels.addElement(panel);
			container.add(panel);
			add(container,con);
		}
	}

	public void removeScenarioCallbacks() {
		for(PreferencePanel panel: panels) {
			panel.removeTreeAndTypeCallbacks();
		}
	}
	
	

}
