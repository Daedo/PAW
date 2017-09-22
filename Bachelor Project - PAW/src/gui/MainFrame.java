package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import allocation.Allocation;
import allocation.AllocationIO;
import allocation.AllocationStrategy;
import allocation.AllocationStrategyBase;
import allocation.AllocationStrategyFamily;
import allocation.algorithms.AllocationAlgorithm;
import gui.allocation.AllocationLiveWindow;
import gui.allocation.Allocator;
import gui.dialog.AllocationDialog;
import gui.dialog.AllocationExportDialog;
import gui.dialog.AllocationExportOption;
import gui.dialog.CreationDialog;
import gui.dialog.ObjectRenameDialog;
import gui.dialog.RandomAlgorithm;
import gui.dialog.RandomAlgorithmDialog;
import gui.dialog.SpinnerDialog;
import preference.PreferenceRelation;
import preference.PreferenceRelationCreator;
import preference.PreferenceRelationTransformer;
import preference.scenario.Scenario;
import preference.scenario.ScenarioIO;
import preference.scenario.ScenarioIOException;
import preference.scenario.ScenarioListener;
import preference.scenario.ScenarioUpdateEvent;

/**
 * Main Window. Used for saving/loading/editing scenarios.
 * @author Dominik
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private ScenarioPanel contentPane;
	private Scenario scenario;

	private JMenuBar menuBar;
	private JMenu mnPreference;
	private JMenu mnAssign;

	private JMenuItem mntmRenameObjects;
	private JMenuItem mntmDefaultAgentNames;
	private JMenuItem mntmDefaultObjectNames;
	private JMenuItem mntmAlphabeticalObjectNames;
	private Vector<JMenuItem> preferenceRelationMenuItems;

	private JMenu mnAdd;

	private JMenu mnRemove;

	private AllocationLiveWindow liveWindow;
	private ScenarioListener liveWindowListener;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		scenario = null;
		preferenceRelationMenuItems = new Vector<>();

		setTitle("PAW: Preference & Allocation Wizard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 1024,576);
		makeMenu();

	}

	/**
	 * Create the menu bar.
	 */
	private void makeMenu() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuProfile();
		menuAssign();
		menuPreference();
		menuHelp();

		disablePreferenceRelationMenuItems();
	}

	/**
	 * Create the "Profile" menu option.
	 */
	private void menuProfile() {
		JMenu mnFile = new JMenu("Profile");
		menuBar.add(mnFile);

		JMenuItem mntmNewScenario = new JMenuItem("New Profile");
		mntmNewScenario.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CreationDialog creationDialog = new CreationDialog();
				int option = JOptionPane.showOptionDialog(null, creationDialog, "Create a new profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if(option == JOptionPane.OK_OPTION) {
					int agents = creationDialog.getAgents();
					int objects = creationDialog.getObjects();

					RandomAlgorithm algorithm = creationDialog.getAlgorithm();
					double strictness = creationDialog.getStrictness();

					Scenario newScenario = null;

					switch (algorithm) {

					case SPATIAL_MODEL:
						newScenario = PreferenceRelationCreator.createScenarioSpacialModel(agents,objects,strictness);
						break;

					case ITERATIVE_JOINING:
						newScenario = PreferenceRelationCreator.createScenarioIterativeJoining(agents,objects,strictness);
						break;
					case STRICT_IMPARTIAL_CULTURE:
						newScenario = PreferenceRelationCreator.createScenarioImpartialCulture(agents,objects,true);
						break;

					case IMPARTIAL_CULTURE:
						newScenario = PreferenceRelationCreator.createScenarioImpartialCulture(agents,objects,false);
						break;

					default:
						newScenario = new Scenario(agents, objects);
						break;
					}

					updateScenario(newScenario);
				}
			}
		});
		mnFile.add(mntmNewScenario);

		JMenuItem mntmOpenScenario = new JMenuItem("Open Profile");
		mntmOpenScenario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Ask if the user wants to save the current scenario
				String loadPath = getLoadPath();
				if(loadPath==null) {
					return;
				}

				
				try {
					Scenario loadedScenario = ScenarioIO.loadScenario(loadPath);
					updateScenario(loadedScenario);
				} catch (IOException | ScenarioIOException e1) {
					showError(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		mnFile.add(mntmOpenScenario);

		JMenuItem mntmSaveScenario = new JMenuItem("Save Profile");
		mntmSaveScenario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String savePath = getSavePath();
				if(savePath==null) {
					return;
				}
				try {
					ScenarioIO.saveScenario(savePath, scenario);
				} catch (IOException e1) {
					showError(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		mnFile.add(mntmSaveScenario);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		mntmRenameObjects = new JMenuItem("Rename Objects");
		mntmRenameObjects.setEnabled(false);
		mntmRenameObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] objectNames = new String[scenario.getObjectCount()];
				for(int i=0;i<scenario.getObjectCount();i++) {
					objectNames[i] = scenario.getObject(i);
				}
				ObjectRenameDialog dialog = new ObjectRenameDialog(objectNames);
				int option = JOptionPane.showOptionDialog(null, dialog, "Rename the objects", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if(option == JOptionPane.OK_OPTION) {
					objectNames = dialog.getObjectNames();
					for(int i=0;i<objectNames.length;i++) {
						scenario.setObject(i, objectNames[i]);
					}
					updateScenario(scenario);
				}
			}
		});
		mnFile.add(mntmRenameObjects);

		mntmAlphabeticalObjectNames = new JMenuItem("Alphabetical Object Names");
		mntmAlphabeticalObjectNames.setEnabled(false);
		mntmAlphabeticalObjectNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<scenario.getObjectCount();i++) {
					scenario.setObject(i, ((char)('a'+i))+"");
				}
				updateScenario(scenario);
			}
		});
		mnFile.add(mntmAlphabeticalObjectNames);

		mntmDefaultObjectNames = new JMenuItem("Default Object Names");
		mntmDefaultObjectNames.setEnabled(false);
		mntmDefaultObjectNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenario.resetObjectNames();
				updateScenario(scenario);
			}
		});
		mnFile.add(mntmDefaultObjectNames);

		mntmDefaultAgentNames = new JMenuItem("Default Agent Names");
		mntmDefaultAgentNames.setEnabled(false);
		mntmDefaultAgentNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenario.resetAgentNames();
				updateScenario(scenario);
			}
		});
		mnFile.add(mntmDefaultAgentNames);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);



		mnAdd = new JMenu("Add");
		mnFile.add(mnAdd);
		mnAdd.setEnabled(false);

		JMenuItem mntmAddAgent = new JMenuItem("Add Agent");
		mntmAddAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int lastAgent = scenario.getAgentCount();
				scenario.addAgent();
				contentPane.setSelectedAgent(lastAgent);
			}
		});
		mnAdd.add(mntmAddAgent);

		JMenuItem mntmAddObject = new JMenuItem("Add Object");
		mntmAddObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenario.addObject();
			}
		});
		mnAdd.add(mntmAddObject);

		JMenuItem mntmAddBoth = new JMenuItem("Add Both");
		mntmAddBoth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int lastAgent = scenario.getAgentCount();
				scenario.add();
				contentPane.setSelectedAgent(lastAgent);
			}
		});
		mnAdd.add(mntmAddBoth);

		mnRemove = new JMenu("Remove");
		mnFile.add(mnRemove);
		mnRemove.setEnabled(false);

		JMenuItem mntmRemoveAgent = new JMenuItem("Remove Agent");
		mntmRemoveAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int agentCount = scenario.getAgentCount();
				if(agentCount==1) {
					//TODO Deactivate Selection instead of the message
					showError("All profiles must contain at least one agent");
					return;
				}

				String[] agents = new String[agentCount];
				for(int i=0;i<agentCount;i++) {
					agents[i] = scenario.getAgent(i);
				}

				String agent = (String) JOptionPane.showInputDialog(null, "Select the agent","Remove Agent", JOptionPane.OK_CANCEL_OPTION, null,agents,agents[0]);
				if(agent==null) {
					return;
				}
				for(int i=0;i<agentCount;i++) {
					if(agent.equals(agents[i])) {
						scenario.removeAgent(i);
						contentPane.setSelectedAgent(0);
						return;
					}
				}

			}
		});
		mnRemove.add(mntmRemoveAgent);

		JMenuItem mntmRemoveObject = new JMenuItem("Remove Object");
		mntmRemoveObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int objCount = scenario.getObjectCount();
				if(objCount==1) {
					//TODO Deactivate Selection instead of the message
					showError("All relations must contain at least one object");
					return;
				}

				String[] objects = new String[objCount];
				for(int i=0;i<objCount;i++) {
					objects[i] = scenario.getObject(i);
				}

				String agent = (String) JOptionPane.showInputDialog(null, "Select the object","Remove Object", JOptionPane.OK_CANCEL_OPTION, null,objects,objects[0]);
				if(agent==null) {
					return;
				}
				for(int i=0;i<objCount;i++) {
					if(agent.equals(objects[i])) {
						scenario.removeObject(i);
						return;
					}
				}

			}
		});
		mnRemove.add(mntmRemoveObject);
	}

	/**
	 * Create the "Assign" menu option.
	 */
	private void menuAssign() {
		mnAssign = new JMenu("Assign");
		mnAssign.setEnabled(false);
		menuBar.add(mnAssign);

		JMenuItem mntmRandomSerialDictatorship = new JMenuItem("Random Serial Dictatorship");
		mntmRandomSerialDictatorship.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.RANDOM_SERIAL_DICTATORSHIP);
			}
		});
		mnAssign.add(mntmRandomSerialDictatorship);

		JMenuItem mntmProbabilisticSerial = new JMenuItem("Extended Probabilistic Serial");
		mntmProbabilisticSerial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.EXTENDED_PROBABLILISTIC_SERIAL);
			}
		});

		JSeparator separator_3 = new JSeparator();
		mnAssign.add(separator_3);
		mnAssign.add(mntmProbabilisticSerial);

		/*JMenuItem mntmDichotomousProbabilisticSerial = new JMenuItem("Dichotomous Probabilistic Serial");
		mntmDichotomousProbabilisticSerial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.DICHOTOMOUS_PROBABLILISTIC_SERIAL);
			}
		});
		mnAssign.add(mntmDichotomousProbabilisticSerial);*/

		JSeparator separator_2 = new JSeparator();
		mnAssign.add(separator_2);
		
		JMenuItem mntmNaiveBostonMechanism = new JMenuItem("Naive Boston Mechanism (Path Equal)");
		mntmNaiveBostonMechanism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.NAIVE_BOSTON_MECHANISM_PATH);
			}
		});
		mnAssign.add(mntmNaiveBostonMechanism);


		JMenuItem mntmNaiveBostonMechanismTie = new JMenuItem("Naive Boston Mechanism (Tie Equal)");
		mntmNaiveBostonMechanismTie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.NAIVE_BOSTON_MECHANISM_TIE);
			}
		});
		mnAssign.add(mntmNaiveBostonMechanismTie);

		JMenuItem mntmAdaptiveBostonMechanism = new JMenuItem("Adaptive Boston Mechanism (Path Equal)");
		mntmAdaptiveBostonMechanism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.ADAPTIVE_BOSTON_MECHANISM_PATH);
			}
		});
		mnAssign.add(mntmAdaptiveBostonMechanism);
		
		JMenuItem mntmAdaptiveBostonMechanismTie = new JMenuItem("Adaptive Boston Mechanism (Tie Equal)");
		mntmAdaptiveBostonMechanismTie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.ADAPTIVE_BOSTON_MECHANISM_TIE);
			}
		});
		mnAssign.add(mntmAdaptiveBostonMechanismTie);
		
		JSeparator separator_1 = new JSeparator();
		mnAssign.add(separator_1);

		JMenuItem mntmPopularAssignment = new JMenuItem("Popular Assignment");
		mntmPopularAssignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.POPULAR_ASSIGNMENT);
			}
		});
		mnAssign.add(mntmPopularAssignment);

		//Add back after first Production
		JMenuItem mntmPopularConvexSet = new JMenuItem("Popular Convex Set");
		mntmPopularConvexSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allocate(scenario, AllocationAlgorithm.POPULAR_CONVEX_SET); 
			}
		});
		mnAssign.add(mntmPopularConvexSet);

		JSeparator separator_4 = new JSeparator();
		mnAssign.add(separator_4);

		JMenuItem mntmMultipleAssignments = new JMenuItem("Multiple Assignments");
		mntmMultipleAssignments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Get List of Possible Algorithms
				Vector<AllocationAlgorithm> possibleAlgs = new Vector<>();
				for(AllocationAlgorithm a:AllocationAlgorithm.values()) {
					if(a.canApply(scenario)) {
						possibleAlgs.addElement(a);
					}
				}

				AllocationDialog dialog = new AllocationDialog(possibleAlgs);
				int option = JOptionPane.showOptionDialog(null, dialog, "Select the algorithms", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if(option == JOptionPane.OK_OPTION) {
					Vector<AllocationAlgorithm> algs = new Vector<>(dialog.getAlgorithms());
					allocateAll(scenario, algs);
				}
			}
		});
		mnAssign.add(mntmMultipleAssignments);

		JMenuItem mntmOpenLiveWindow = new JMenuItem("Open Live Window");
		mntmOpenLiveWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(liveWindow==null) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								liveWindow = new AllocationLiveWindow(scenario);
								liveWindow.addWindowListener(new WindowListener() {

									@Override
									public void windowOpened(WindowEvent e) {}
									
									@Override
									public void windowIconified(WindowEvent e) {}
									
									@Override
									public void windowDeiconified(WindowEvent e) {}
									
									@Override
									public void windowDeactivated(WindowEvent e) {}
									
									@Override
									public void windowClosing(WindowEvent e) {}
									
									@Override
									public void windowClosed(WindowEvent e) {
										liveWindow = null;
										if(liveWindowListener!=null) {
											scenario.removeScenarioListener(liveWindowListener);
											liveWindowListener = null;
										}
									}
									
									@Override
									public void windowActivated(WindowEvent e) {}
								});
								liveWindow.setVisible(true);
								
								liveWindowListener = new ScenarioListener() {
									@Override
									public void valueChanged(ScenarioUpdateEvent e) {
										if(liveWindow == null) {
											return;
										}
										
										if(e==ScenarioUpdateEvent.PREFERENCE_UPDATE || e == ScenarioUpdateEvent.AGENT_SET_CHANGED) {
											liveWindow.updateScenario(scenario);
										} else {
											liveWindow.updateNames(scenario);
										}
									}
								};
								scenario.addScenarioListener(liveWindowListener);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		mnAssign.add(mntmOpenLiveWindow);

		JSeparator separator = new JSeparator();
		mnAssign.add(separator);

		JMenuItem mntmExportAssignments = new JMenuItem("Export Assignments");
		mntmExportAssignments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Possible Algorithms
				Vector<AllocationAlgorithm> possibleAlgs = new Vector<>();
				for(AllocationAlgorithm a:AllocationAlgorithm.values()) {
					if(a.canApply(scenario)) {
						possibleAlgs.addElement(a);
					}
				}

				AllocationExportDialog dialog = new AllocationExportDialog(possibleAlgs);
				int option = JOptionPane.showOptionDialog(null, dialog, "Select the algorithms", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if(option == JOptionPane.OK_OPTION) {
					Vector<AllocationAlgorithm> algs = new Vector<>(dialog.getAlgorithms());
					String path = dialog.getPath();
					AllocationExportOption exportOption = dialog.getExportOption();
					exportAllocation(scenario, algs, exportOption, path);
				}	
			}
		});
		mnAssign.add(mntmExportAssignments);

	}

	/**
	 * Create the "Preference" menu option.
	 */
	private void menuPreference() {
		mnPreference = new JMenu("Preference");
		menuBar.add(mnPreference);
		mnPreference.setEnabled(false);

		JMenu mnSetPreferences = new JMenu("Set Preferences to");
		mnPreference.add(mnSetPreferences);
		preferenceRelationMenuItems.addElement(mnSetPreferences);

		JMenuItem mntmOrdered = new JMenuItem("Ordered");
		mntmOrdered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] choices = {"Ascending", "Descending"};
				String order = (String) JOptionPane.showInputDialog(null, "Select the preference order","Preference Order", JOptionPane.OK_CANCEL_OPTION, null,choices,choices[0]);
				if(order==null) {
					return;
				}
				boolean isAscending = order.equals(choices[0]);

				int objCount = scenario.getObjectCount();
				PreferenceRelation rel = PreferenceRelationCreator.createOrderedRelation(objCount, isAscending);
				setCurrentPreference(rel);
			}
		});
		mnSetPreferences.add(mntmOrdered);
		preferenceRelationMenuItems.addElement(mntmOrdered);

		JMenuItem mntmIndifferent = new JMenuItem("Indifferent");
		mntmIndifferent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentSelection = contentPane.getSelectedAgent();
				int objCount = scenario.getObjectCount();
				PreferenceRelation rel = PreferenceRelationCreator.createIndifferentRelation(objCount);
				scenario.setAgentRelation(currentSelection, rel, true);
				setCurrentPreference(rel);
			}
		});
		mnSetPreferences.add(mntmIndifferent);
		preferenceRelationMenuItems.addElement(mntmIndifferent);

		JMenuItem mntmRandom = new JMenuItem("Random");
		mntmRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int objCount = scenario.getObjectCount();

				RandomAlgorithmDialog dialog = new RandomAlgorithmDialog();
				int option = JOptionPane.showOptionDialog(null, dialog, "Select the model", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if(option == JOptionPane.OK_OPTION) {
					RandomAlgorithm alg = dialog.getAlgorithm();

					double strictness = dialog.getStrictness();
					PreferenceRelation rel = null;

					switch(alg) {
					case ITERATIVE_JOINING:
						rel = PreferenceRelationCreator.createRandomRelationIterativeJoining(objCount,strictness);
						break;
					case SPATIAL_MODEL:
						rel = PreferenceRelationCreator.createRandomRelationSpacial(objCount, strictness);
						break;
					case STRICT_IMPARTIAL_CULTURE:
						rel = PreferenceRelationCreator.createRandomRelationImpartialCulture(objCount, true);
						break;
					default:
						rel = PreferenceRelationCreator.createRandomRelationImpartialCulture(objCount, false);
					}
					setCurrentPreference(rel);
				}
			}
		});
		mnSetPreferences.add(mntmRandom);
		preferenceRelationMenuItems.addElement(mntmRandom);

		JMenuItem mntmInput = new JMenuItem("Input");
		mntmInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int objCount = scenario.getObjectCount();
				String input = (String)JOptionPane.showInputDialog(null, "Set preference relation to (size = "+objCount+")", "Preferences from input", JOptionPane.PLAIN_MESSAGE);
				if(input==null || input.isEmpty()) {
					return;
				}

				PreferenceRelation rel;
				try {
					rel = PreferenceRelationCreator.createFromString(input);
				} catch (Exception ex) {
					showError(ex.getMessage());
					ex.printStackTrace();
					return;
				}

				if(rel.getRelationSize()==objCount) {
					setCurrentPreference(rel);
				} else {
					String message = "The relation must have size "+objCount+" but has size "+rel.getRelationSize()+",";
					showError(message);
				}

			}
		});
		mnSetPreferences.add(mntmInput);
		preferenceRelationMenuItems.addElement(mntmInput);

		JMenuItem mntmImitateAgent = new JMenuItem("Imitate Agent");
		mntmImitateAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int agentCount = scenario.getAgentCount();
				int currentAgent = contentPane.getSelectedAgent();
				String[] choices = new String[agentCount-1];
				for(int i=0;i<choices.length;i++) {
					int agentIndex = i>=currentAgent ? i+1 : i;
					String agentName = scenario.getAgent(agentIndex);
					choices[i] = agentName;
				}
				String agent = (String) JOptionPane.showInputDialog(null, "Select the preference order","Preference Order", JOptionPane.OK_CANCEL_OPTION, null,choices,choices[0]);
				if(agent==null) {
					return;
				}
				for(int i=0;i<choices.length;i++) {
					if(agent.equals(choices[i])) {
						int agentIndex = i>=currentAgent ? i+1 : i;
						PreferenceRelation rel = scenario.getAgentRelation(agentIndex);
						rel = PreferenceRelationTransformer.cloneRelation(rel);
						setCurrentPreference(rel);
						break;
					}
				}

			}
		});
		mnSetPreferences.add(mntmImitateAgent);
		preferenceRelationMenuItems.addElement(mntmImitateAgent);

		JMenu mnTransform = new JMenu("Transform");
		mnPreference.add(mnTransform);
		preferenceRelationMenuItems.addElement(mnTransform);

		JMenuItem mntmRotate = new JMenuItem("Rotate");
		mntmRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferenceRelation rel = getCurrentPreference();
				int max = rel.getGroupcount()-1;
				int min = -max;

				SpinnerDialog spinner = new SpinnerDialog("Rotate the preference (units downward)", min, max,0);
				int option = JOptionPane.showOptionDialog(null, spinner, "Rotate", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if(option == JOptionPane.OK_OPTION) {
					int spin = spinner.model.getNumber().intValue();

					rel = PreferenceRelationTransformer.createRotatedRelation(rel, spin);
					setCurrentPreference(rel);
				}
			}
		});
		mnTransform.add(mntmRotate);
		preferenceRelationMenuItems.addElement(mntmRotate);

		JMenuItem mntmMakeStict = new JMenuItem("Make Stict");
		mntmMakeStict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferenceRelation rel = getCurrentPreference();
				rel = PreferenceRelationTransformer.createStrictPreference(rel);
				setCurrentPreference(rel);
			}
		});
		mnTransform.add(mntmMakeStict);
		preferenceRelationMenuItems.addElement(mntmMakeStict);
	}
	
	/**
	 * Create the "Help" menu option.
	 */
	private void menuHelp() {
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessage("Preference Allocation Wizard (PAW)\n\n"
						+ "Implementation and design: Dominik Spies\n"
						+ "Advisor: M. Sc. Johannes Hofbauer\n"
						+ "Supervisor: Prof. Dr. Felix Brandt");
			}
		});
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmHelp = new JMenuItem("Quick Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessage("How to use:\n\n"
						+ "1. Use drag and drop or direct input (e.g. 1,3,2,...)\n"
						+ "to create your prefered preference profile.\n\n"
						+ "2. Select the desired allocation methods or run the live window.\n"
						+ "Changes to the profile are directly reflected in the live window.\n\n"
						+ "For more detailed information refere to the documentation or\n"
						+ "the thesis \"A Tool for Random Assignments\" by Dominik Spies.");
			}
		});
		mnHelp.add(mntmHelp);
	}
	

	/**
	 * Ask user to select a path for loading.
	 * @return
	 */
	private String getLoadPath() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to open");

		//FIXME The current dir is not used
		String currentPath = getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
		fileChooser.setCurrentDirectory(new File(currentPath));


		int userSelection = fileChooser.showOpenDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			return fileToSave.getAbsolutePath();
		}
		return null;
	}

	/**
	 * Ask user to select a path for saving.
	 * @return
	 */
	private String getSavePath() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a location to save");

		//FIXME The current dir is not used
		String currentPath = getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
		fileChooser.setCurrentDirectory(new File(currentPath));

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			return fileToSave.getAbsolutePath();
		}
		return null;
	}

	/**
	 * Displays an error message in a dialog box.
	 * @param message
	 */
	private void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays an info message in a dialog box.
	 * @param message
	 */
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Set the preference relation of the selected agent.
	 * @param rel
	 */
	private void setCurrentPreference(PreferenceRelation rel) {
		if(rel==null) {
			return;
		}

		int currentSelection = contentPane.getSelectedAgent();
		scenario.setAgentRelation(currentSelection, rel, true);
	}

	/**
	 * Get the preference relation of the selected agent.
	 * @return
	 */
	private PreferenceRelation getCurrentPreference() {
		int currentSelection = contentPane.getSelectedAgent();
		return scenario.getAgentRelation(currentSelection);
	}

	/**
	 * Triggers callbacks and enables options on a change of the scenario.
	 * @param sc
	 */
	void updateScenario(Scenario sc) {
		scenario = sc;
		contentPane = new ScenarioPanel(scenario);
		setContentPane(contentPane);

		mnPreference.setEnabled(true);
		mnAssign.setEnabled(true);
		mntmRenameObjects.setEnabled(true);
		mntmDefaultObjectNames.setEnabled(true);
		mntmDefaultAgentNames.setEnabled(true);
		mntmAlphabeticalObjectNames.setEnabled(true);
		mnAdd.setEnabled(true);
		mnRemove.setEnabled(true);

		contentPane.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					//We are only interested in the final state
					return;
				}
				if(contentPane.isOverview()) {
					disablePreferenceRelationMenuItems();
				} else {
					enablePreferenceRelationMenuItems();
				}
			}
		});

		revalidate();
		repaint();
	}

	/**
	 * Run a allocation.
	 * @param sc
	 * @param algorithm
	 */
	private void allocate(Scenario sc,AllocationAlgorithm algorithm) {
		String name = algorithm.toString();
		AllocationStrategyBase str = algorithm.getAllocationStrategy();

		if(str.canApply(sc)) {
			Allocator allocator = new Allocator();
			if(str instanceof AllocationStrategyFamily) {
				allocator.addFamily((AllocationStrategyFamily) str,name);
			} else {
				allocator.addStrategy((AllocationStrategy) str,name);	
			}

			allocator.allocate(sc);
		} else {
			String message = str.getViolatedConstraints(sc).stream()
					.map(s->s+"\n")
					.reduce("", String::concat)
					.trim();
			showError(message);
		}
	}

	/**
	 * Run multiple allocations.
	 * @param sc
	 * @param strategies
	 */
	private void allocateAll(Scenario sc, Vector<AllocationAlgorithm> strategies) {
		Allocator allocator = new Allocator();

		Vector<String> errors = new Vector<>();

		for(AllocationAlgorithm s:strategies) {
			if(s.canApply(sc)) {
				errors.addAll(s.getAllocationStrategy().getViolatedConstraints(sc));
			}

			AllocationStrategyBase base = s.getAllocationStrategy();
			if(base instanceof AllocationStrategyFamily) {
				allocator.addFamily((AllocationStrategyFamily) s.getAllocationStrategy(),s.toString());
			} else {
				allocator.addStrategy((AllocationStrategy)s.getAllocationStrategy(),s.toString());
			}
		}

		if(errors.isEmpty()) {
			allocator.allocate(sc);	
		} else {
			String message = errors.stream()
					.map(s->s+"\n")
					.reduce("", String::concat)
					.trim();
			showError(message);
		}
	}

	/**
	 * Export all given allocations to a file.
	 * @param sc
	 * @param strategies
	 * @param option
	 * @param exportPath
	 */
	private void exportAllocation(Scenario sc, Vector<AllocationAlgorithm> strategies, AllocationExportOption option, String exportPath) {
		if(exportPath == null) {
			return;
		}
		
		Allocator allocator = new Allocator();
		for(AllocationAlgorithm s:strategies) {
			AllocationStrategyBase base = s.getAllocationStrategy();
			if(base instanceof AllocationStrategyFamily) {
				allocator.addFamily((AllocationStrategyFamily) s.getAllocationStrategy(),s.toString());
			} else {
				allocator.addStrategy((AllocationStrategy)s.getAllocationStrategy(),s.toString());
			}
		}
		
		HashMap<String, Vector<Allocation>> allocations = allocator.createAllocations(scenario);
		for(String key:allocations.keySet()) {
			Vector<Allocation> allocList = allocations.get(key);
			for(int i=0;i<allocList.size();i++) {
				String fullPath = exportPath +"/"+ key;
				
				if(allocList.size() != 1 ) {
					fullPath+= "- "+(i+1);
				} 
				fullPath+= ".csv";
				
				
				try {
				AllocationIO.export(scenario,allocList.elementAt(i),option,fullPath);
				} catch(IOException e) {
					showError(e.getMessage());
				}
			}
		}
	}
	
	private void enablePreferenceRelationMenuItems() {
		for(JMenuItem item:this.preferenceRelationMenuItems) {
			item.setEnabled(true);
		}
	}

	private void disablePreferenceRelationMenuItems() {
		for(JMenuItem item:this.preferenceRelationMenuItems) {
			item.setEnabled(false);
		}
	}

}
