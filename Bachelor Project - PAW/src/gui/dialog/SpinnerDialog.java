package gui.dialog;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * Generic Dialog with text and a {@link JSpinner}. Called by "Preference>Transform>Rotate"
 * @author Dominik
 *
 */
public class SpinnerDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	public SpinnerNumberModel model;

	/**
	 * Create the panel.
	 */
	public SpinnerDialog(String text, int min,int max,int value) {
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblLabel = new JLabel(text);
		lblLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblLabel);
		
		model = new SpinnerNumberModel(value, min, max, 1);
		JSpinner spinner = new JSpinner(model);
		add(spinner);

	}

}
