package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.preferenceTree.PreferenceTree;
import preference.PreferenceRelation;
import preference.PreferenceTypeIdentifier;
import preference.scenario.Scenario;
import preference.scenario.ScenarioListener;
import preference.scenario.ScenarioUpdateEvent;

/**
 * Panel displaying the preference relation of one agent.
 * 
 * @author Dominik
 *
 */
public class PreferencePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField txtName;
	private PreferenceTree tree;
	private JLabel lblInfo;
	private Scenario scenario;
	private ScenarioListener typeCallback;
	private int index;//The index of the agent.

	/**
	 * Create the panel.
	 */
	public PreferencePanel(Scenario sc, int agent) {
		this(sc,agent,true);
	}

	public PreferencePanel(Scenario sc, int agent, boolean makeLabel) {
		this.scenario = sc;
		this.index = agent;

		setBorder(new EmptyBorder(0, 2, 0, 2));
		GridBagLayout gridBagLayout = new GridBagLayout();

		gridBagLayout.columnWidths = new int[] {0, 200};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};

		setLayout(gridBagLayout);

		txtName = new JTextField();
		txtName.setText(scenario.getAgent(agent));
		txtName.getDocument().addDocumentListener(new DocumentListener() {
			//We are only interested in the change not how it was changed
			//http://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				String newName = txtName.getText();
				scenario.setAgent(agent, newName);
			}
		});

		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.anchor = GridBagConstraints.NORTH;
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.insets = new Insets(0, 0, 5, 0);
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 0;
		add(txtName, gbc_txtName);
		txtName.setColumns(7);

		tree = new PreferenceTree(scenario,agent);
		GridBagConstraints gbc_tree = new GridBagConstraints();
		gbc_tree.fill = GridBagConstraints.BOTH;
		gbc_tree.insets = new Insets(0, 0, 5, 0);
		gbc_tree.gridx = 0;
		gbc_tree.gridy = 1;
		gbc_tree.gridwidth =2;
		add(tree, gbc_tree);

		lblInfo = new JLabel();
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblInfo.gridx = 1;
		gbc_lblInfo.gridy = 2;
		add(lblInfo, gbc_lblInfo);

		if(makeLabel) {	
			updateLabel();
			
			this.typeCallback = new ScenarioListener() {
				@Override
				public void valueChanged(ScenarioUpdateEvent e) {
					if(e == ScenarioUpdateEvent.AGENT_SET_CHANGED || e == ScenarioUpdateEvent.PREFERENCE_UPDATE) {
						updateLabel();
					}
				}
			};
			scenario.addScenarioListener(typeCallback);
		}
	}

	private void updateLabel() {
		if(index>=scenario.getAgentCount()) {
			return;
		}
		
		PreferenceRelation rel = scenario.getAgentRelation(index);
		String desc = PreferenceTypeIdentifier.getDescription(rel)
				.stream()
				.collect(Collectors.joining(", "));
		desc = desc.equals("") ? "None":desc;

		lblInfo.setText("Identified Relation Types: "+desc);
	}
	
	public void removeTreeAndTypeCallbacks() {
		scenario.removeScenarioListener(tree.getTreeCallback());
		scenario.removeScenarioListener(typeCallback);
	}
}
