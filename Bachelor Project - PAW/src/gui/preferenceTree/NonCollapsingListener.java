package gui.preferenceTree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

/**
 * Listener for the {@link PreferenceTree}. Prevents nodes from being collapsed.
 * @author Dominik
 *
 */
public class NonCollapsingListener implements TreeWillExpandListener {
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		return;
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		throw new ExpandVetoException(event, "Illegal Action");

	}

}
