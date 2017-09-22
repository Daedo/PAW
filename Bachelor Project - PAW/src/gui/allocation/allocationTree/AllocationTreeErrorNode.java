package gui.allocation.allocationTree;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

/**
 * {@link AllocationTree} node indicating, that an error occurred, during allocation.
 * Returns a {@link ErrorPanel} with error details.
 *  
 * @author Dominik
 *
 */
public class AllocationTreeErrorNode extends AllocationTreePanelNode{
	private static final long serialVersionUID = 1L;
	private ErrorPanel panel;

	public AllocationTreeErrorNode(String name, List<String> errors) {
		super(name);
		panel = new ErrorPanel(errors);
	}

	public AllocationTreeErrorNode(String name, String error) {
		this(name, Arrays.asList(error));
	}

	@Override
	public Component getPanelComponent() {
		return panel;
	}
}
