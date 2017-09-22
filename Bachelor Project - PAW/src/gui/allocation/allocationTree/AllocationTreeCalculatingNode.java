package gui.allocation.allocationTree;

import java.awt.Component;

/**
 * Node that was used to indicate that an allocation is currently still processing.
 * @author Dominik
 *
 */
@Deprecated
public class AllocationTreeCalculatingNode extends AllocationTreePanelNode{
	private static final long serialVersionUID = 1L;
	private CalculatingPanel panel = new CalculatingPanel();
	
	public AllocationTreeCalculatingNode(String name) {
		super(name);
	}

	@Override
	public Component getPanelComponent() {
		return panel;
	}
	
	
}
