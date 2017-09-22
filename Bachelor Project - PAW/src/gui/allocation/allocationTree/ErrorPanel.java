package gui.allocation.allocationTree;

import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * Panel returned by {@link AllocationTreeErrorNode}
 * @author Dominik
 *
 */
public class ErrorPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public ErrorPanel(List<String> errors) {
		String[] data = (String[]) errors.toArray(new String[errors.size()]);
		JList<String> list = new JList<String>(data);
		setViewportView(list);
	}

}
