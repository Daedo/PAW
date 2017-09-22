package gui.allocation.allocationTree;

import java.awt.Component;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A named {@link AllocationTree} node, that returns a Component to display, when the node is selected.
 * @author Dominik
 *
 */
public abstract class AllocationTreePanelNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private String name;
	public AllocationTreePanelNode(String name) {
		this.name = name;
	}
	
	/**
	 * Returns a component, to display when the node is selected.
	 * @return
	 */
	public abstract Component getPanelComponent();
	
	@Override
	public String toString() {
		return name;
	}
}
