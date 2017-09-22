package gui;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import preference.scenario.Scenario;

/**
 * Main container for a scenario. Contais the agent list and the currently selected preference/overview panel.
 * @author Dominik
 *
 */
public class ScenarioPanel extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PreferencePanel preferencePanel;
	private OverviewPanel   overviewPanel;
	
	private JList<String> agentList;
	private JScrollPane rightScrollPane;
	private int currentAgentSelection;
	private Scenario scenario;
	
	/**
	 * Create the panel.
	 */
	public ScenarioPanel(Scenario sc) {
		this.scenario = sc;
		JScrollPane leftScrollPane = new JScrollPane();
		setLeftComponent(leftScrollPane);
		
		agentList = new JList<String>();
		agentList.setModel(scenario);
		agentList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					//We are only interested in the final state
					return;
				}
				updatePreferenceEditor();
			}
		});
	
		agentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftScrollPane.setViewportView(agentList);
		rightScrollPane = new JScrollPane();
		setRightComponent(rightScrollPane);
		
		currentAgentSelection = -1;
		agentList.setSelectedIndex(0);
		
		setDividerLocation(agentList.getPreferredSize().width + getInsets().left+5);
	}
	
	void updatePreferenceEditor() {
		int newSelection = agentList.getSelectedIndex();
		if(newSelection == currentAgentSelection) {
			//Value didn't change
			return;
		}
		//Remove old references
		if(preferencePanel!=null) {
			preferencePanel.removeTreeAndTypeCallbacks();
		}
		
		if(overviewPanel != null) {
			overviewPanel.removeScenarioCallbacks();
		}
		
		currentAgentSelection = newSelection;
		if(currentAgentSelection == 0) {
			overviewPanel = new OverviewPanel(scenario);
			rightScrollPane.setViewportView(overviewPanel);
		} else {
			preferencePanel = new PreferencePanel(scenario,currentAgentSelection-1);
			rightScrollPane.setViewportView(preferencePanel);
		}
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		agentList.addListSelectionListener(listener);
	}
	
	public int getSelectedAgent() {
		return agentList.getSelectedIndex()-1;
	}
	
	public boolean isOverview() {
		return agentList.getSelectedIndex() == 0;
	}

	public void setSelectedAgent(int agent) {
		agentList.setSelectedIndex(agent+1);
	}
}
