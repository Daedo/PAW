package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;

/**
 * Dialog window for creating a new scenario. Called by the menu item "Profile>New Profile" 
 * @author Dominik
 *
 */
public class CreationDialog extends JPanel {
	private static final long serialVersionUID = 1L;

	//Icons inspired by the GIMP Project 
	private static final ImageIcon chain 		= new ImageIcon(Main.class.getResource("/chain.png"));//"icons/chain.png");
	private static final ImageIcon chainBroken 	= new ImageIcon(Main.class.getResource("/chain-broken.png"));//"icons/chain-broken.png");

	private SpinnerNumberModel modelAgents;
	private SpinnerNumberModel modelObjects;
	private JComboBox<RandomAlgorithm> comboBox;
	private StrictnessSlider slider;
	private JLabel lblStrictness;
	private JTextField textField;

	private JSpinner spinnerAgents;
	private JSpinner spinnerObjects;
	private SpinnerChainer chainer;
	/**
	 * Create the panel.
	 */
	public CreationDialog() {
		chainer = new SpinnerChainer();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 150, 0};
		gridBagLayout.rowHeights = new int[]{16, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblSelectTheOptions = new JLabel("Select the options for the new profile:");
		lblSelectTheOptions.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblSelectTheOptions = new GridBagConstraints();
		gbc_lblSelectTheOptions.gridwidth = 4;
		gbc_lblSelectTheOptions.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectTheOptions.anchor = GridBagConstraints.NORTH;
		gbc_lblSelectTheOptions.gridx = 0;
		gbc_lblSelectTheOptions.gridy = 0;
		add(lblSelectTheOptions, gbc_lblSelectTheOptions);

		JLabel lblAgents = new JLabel("Agents:");
		GridBagConstraints gbc_lblAgents = new GridBagConstraints();
		gbc_lblAgents.insets = new Insets(0, 0, 5, 5);
		gbc_lblAgents.gridx = 0;
		gbc_lblAgents.gridy = 1;
		add(lblAgents, gbc_lblAgents);

		spinnerAgents = new JSpinner();

		Integer value = new Integer(3);
		Integer min = new Integer(1);
		Integer max = new Integer(20);
		Integer step = new Integer(1);
		modelAgents = new SpinnerNumberModel(value, min, max, step);
		spinnerAgents.setModel(modelAgents);
		chainer.addSpinner(spinnerAgents);


		GridBagConstraints gbc_spinnerAgents = new GridBagConstraints();
		gbc_spinnerAgents.anchor = GridBagConstraints.EAST;
		gbc_spinnerAgents.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerAgents.gridx = 1;
		gbc_spinnerAgents.gridy = 1;
		add(spinnerAgents, gbc_spinnerAgents);

		JLabel label = new JLabel("┓");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 1;
		add(label, gbc_label);


		JButton btnChain = new JButton(chain);
		btnChain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chainer.toggleChain();
				if(chainer.isChained()) {
					btnChain.setIcon(chain);
				} else {
					btnChain.setIcon(chainBroken);
				}
			}
		});
		GridBagConstraints gbc_btnChain = new GridBagConstraints();
		gbc_btnChain.insets = new Insets(0, 0, 5, 5);
		gbc_btnChain.gridx = 2;
		gbc_btnChain.gridy = 2;
		add(btnChain, gbc_btnChain);

		JLabel lblObjects = new JLabel("Objects: ");
		GridBagConstraints gbc_lblObjects = new GridBagConstraints();
		gbc_lblObjects.insets = new Insets(0, 0, 5, 5);
		gbc_lblObjects.gridx = 0;
		gbc_lblObjects.gridy = 3;
		add(lblObjects, gbc_lblObjects);

		spinnerObjects = new JSpinner();
		modelObjects = new SpinnerNumberModel(value, min, max, step);
		spinnerObjects.setModel(modelObjects);
		chainer.addSpinner(spinnerObjects);

		GridBagConstraints gbc_spinnerObjects = new GridBagConstraints();
		gbc_spinnerObjects.anchor = GridBagConstraints.EAST;
		gbc_spinnerObjects.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerObjects.gridx = 1;
		gbc_spinnerObjects.gridy = 3;
		add(spinnerObjects, gbc_spinnerObjects);

		JLabel label_1 = new JLabel("┛");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 2;
		gbc_label_1.gridy = 3;
		add(label_1, gbc_label_1);

		JLabel lblAlgorithm = new JLabel("Model: ");
		GridBagConstraints gbc_lblAlgorithm = new GridBagConstraints();
		gbc_lblAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithm.gridx = 0;
		gbc_lblAlgorithm.gridy = 4;
		add(lblAlgorithm, gbc_lblAlgorithm);

		RandomAlgorithm[] algorithms = RandomAlgorithm.values();
		comboBox = new JComboBox<RandomAlgorithm>(algorithms);
		comboBox.setSelectedIndex(0);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean enableSlider = false;
				switch(comboBox.getSelectedIndex()) {
				case 0:
				case 3:
				case 4:
					enableSlider = false;
					break;
				case 1:
				case 2:
					enableSlider = true;
					break;
				}

				slider.setEnabled(enableSlider);
				lblStrictness.setEnabled(enableSlider);
				textField.setEnabled(enableSlider);
			}
		});

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 4;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 4;
		add(comboBox, gbc_comboBox);

		slider = new StrictnessSlider();
		slider.setEnabled(false);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				textField.setText(getStrictness()+"");
			}
		});

		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.gridwidth = 3;
		gbc_slider.insets = new Insets(0, 0, 0, 5);
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 5;
		add(slider, gbc_slider);

		lblStrictness = new JLabel("Strictness:");
		lblStrictness.setEnabled(false);
		GridBagConstraints gbc_lblStrictness = new GridBagConstraints();
		gbc_lblStrictness.insets = new Insets(0, 0, 0, 5);
		gbc_lblStrictness.gridx = 0;
		gbc_lblStrictness.gridy = 5;
		add(lblStrictness, gbc_lblStrictness);

		textField = new JTextField(getStrictness()+"");
		textField.setEnabled(false);
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = textField.getText();
				try {
					double value = Double.parseDouble(text);
					slider.setStrictness(value);
				} catch (NumberFormatException e1) {
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridx = 4;
		gbc_textField.gridy = 5;
		add(textField, gbc_textField);
		textField.setColumns(5);
	}

	public int getAgents() {
		Integer i = (Integer)this.modelAgents.getValue();
		return i.intValue();
	}

	public int getObjects() {
		Integer i = (Integer)this.modelObjects.getValue();
		return i.intValue();
	}

	public RandomAlgorithm getAlgorithm() {
		return (RandomAlgorithm)this.comboBox.getSelectedItem();
	}

	public double getStrictness() {
		return this.slider.getStrictness();
	}

	/**
	 * Helper Class to chain multiple spinners together. 
	 * So changes of one spinner affect the other ones, if the isChained Value is true.
	 * 
	 * @author Dominik
	 *
	 */
	private class SpinnerChainer {
		private boolean isChained;
		private Vector<JSpinner> spinners;
		private Vector<Integer> values;

		public SpinnerChainer() {
			spinners  = new Vector<>();
			values 	  = new Vector<>();
			isChained = true;
		}

		/**
		 * Adds a new spinner to the chainer
		 * @param spinner
		 */
		public void addSpinner(JSpinner spinner) {
			spinners.addElement(spinner);
			values.addElement((Integer)spinner.getValue());
			spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					update(spinners.indexOf(spinner), ((Integer)spinner.getValue()).intValue());
				}
			});

		}

		/**
		 * Toggles the isChained Value
		 */
		public void toggleChain() {
			isChained = !isChained;
		}

		public boolean isChained() {
			return isChained;
		}

		/**
		 * Sets the value of one of the spinners. If isChained is true this will affect all other spinners.
		 * @param spinnerIndex
		 * @param newValue
		 */
		public void update(int spinnerIndex, int newValue) {
			int oldValue = values.elementAt(spinnerIndex);
			if(oldValue == newValue) {
				return;
			}
			//Update self
			values.setElementAt(newValue, spinnerIndex);

			if(!isChained) {
				return;
			}
			//Update others
			int delta = newValue - oldValue;
			for(int i=0;i<spinners.size();i++) {
				if(i==spinnerIndex) {
					continue;
				}
				
				int spinDelta = delta;
				while(spinDelta!=0) {
					JSpinner current = spinners.elementAt(i);
					Integer next = (Integer) current.getNextValue();
					Integer prev = (Integer) current.getPreviousValue();

					if(delta > 0 && next!=null) {
						values.setElementAt(next, i);
						current.setValue(next);
					}

					if(delta < 0 && prev!=null) {
						values.setElementAt(prev, i);
						current.setValue(prev);
					}
					
					spinDelta += spinDelta>0 ? -1 : +1;
					
				}
			}	
		}
	}

}
