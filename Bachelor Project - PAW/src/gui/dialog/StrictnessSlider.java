package gui.dialog;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Slider between 0 and MAX_STRICTNESS. Used to pick a strictness for different {@link RandomAlgorithm}s.
 * @author Dominik
 *
 */
public class StrictnessSlider extends JSlider {
	private static final long serialVersionUID = 1L;
	private static final int MAX_STRICTNESS = 100;
	
	
	public StrictnessSlider() {
		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		labels.put(new Integer(0),   new JLabel("Indifference"));
		labels.put(new Integer(MAX_STRICTNESS), new JLabel("Strict Preferences"));
		setLabelTable(labels);
		setPaintLabels(true);
		setMaximum(MAX_STRICTNESS);
		setMinimum(0);
		setValue(MAX_STRICTNESS);
	}
	
	public double getStrictness() {
		return ((double)getValue())/MAX_STRICTNESS;
	}

	public void setStrictness(double value) {
		if(value>=0 && value<=1) {
			int pos = (int) Math.rint(value*MAX_STRICTNESS);
			setValue(pos);
		}
	}
}
