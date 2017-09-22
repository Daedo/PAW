package gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog window to choose a {@link RandomAlgorithm} and a strictness value it it is applicable.
 * @author Dominik
 *
 */
public class RandomAlgorithmDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private StrictnessSlider slider;
	private JComboBox<RandomAlgorithm> comboBox;
	private JTextField textField;
	private JLabel lblStrictness;

	/**
	 * Create the panel.
	 */
	public RandomAlgorithmDialog() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0,1.0,1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblAlgorithm = new JLabel("Model: ");
		GridBagConstraints gbc_lblAlgorithm = new GridBagConstraints();
		gbc_lblAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithm.gridx = 0;
		gbc_lblAlgorithm.gridy = 0;
		add(lblAlgorithm, gbc_lblAlgorithm);
		
		RandomAlgorithm[] algorithms = {RandomAlgorithm.ITERATIVE_JOINING,RandomAlgorithm.SPATIAL_MODEL,RandomAlgorithm.STRICT_IMPARTIAL_CULTURE,RandomAlgorithm.IMPARTIAL_CULTURE};
		comboBox = new JComboBox<RandomAlgorithm>(algorithms);
		comboBox.setSelectedItem(0);
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean enableSlider = false;
				switch(comboBox.getSelectedIndex()) {
				case 0:
				case 1:
					enableSlider = true;
					break;
				case 2:
				case 3:
					enableSlider = false;
					break;
				}
				
				slider.setEnabled(enableSlider);
				lblStrictness.setEnabled(enableSlider);
				textField.setEnabled(enableSlider);
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 5, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(comboBox, gbc);
		
		lblStrictness = new JLabel("Strictness: ");
		GridBagConstraints gbc_lblStrictness = new GridBagConstraints();
		gbc_lblStrictness.insets = new Insets(0, 0, 0, 5);
		gbc_lblStrictness.gridx = 0;
		gbc_lblStrictness.gridy = 1;
		add(lblStrictness, gbc_lblStrictness);
		
		slider = new StrictnessSlider();
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				textField.setText(getStrictness()+"");
			}
		});
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_1.gridx = 1;
		gbc_1.gridy = 1;
		add(slider, gbc_1);
		
		textField = new JTextField(getStrictness()+"");
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
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		add(textField, gbc_textField);
		textField.setColumns(5);
		
		
		setPreferredSize(new Dimension(400,80));
	}
	
	public RandomAlgorithm getAlgorithm() {
		return (RandomAlgorithm)comboBox.getSelectedItem();
	}
	
	public double getStrictness() {
		return slider.getStrictness();
	}

}
