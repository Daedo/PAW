package gui.allocation.allocationTree;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel returned by the {@link AllocationTreeCalculatingNode}
 * @author Dominik
 *
 */
@Deprecated
public class CalculatingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public CalculatingPanel() {
		add(new JLabel("Caluculating Allocation..."));
	}

}
