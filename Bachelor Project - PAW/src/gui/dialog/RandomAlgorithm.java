package gui.dialog;

/**
 * Enum containing the display names for all random algorithms. Used in different dialogs for selection.
 * @author Dominik
 *
 */
public enum RandomAlgorithm {
	DEFAULT("Default"), ITERATIVE_JOINING("Iterative Joining"),SPATIAL_MODEL("Spatial Model 2D"),
	STRICT_IMPARTIAL_CULTURE("Strict Impartial Culture"),IMPARTIAL_CULTURE("Impartial Culture");
	public String text;
	private RandomAlgorithm(String val) {
		this.text = val;
	}
	
	@Override
	public String toString() {
		return text;
	}
}