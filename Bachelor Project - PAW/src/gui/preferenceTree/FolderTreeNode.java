package gui.preferenceTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import preference.PreferenceGroup;

/**
 * A node for the {@link PreferenceTree}, it can have children and represents {@link PreferenceGroup}s.
 */
public class FolderTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Group";
	}
	
	public FolderTreeNode() {
		super();
	}

	public FolderTreeNode(TreeNode node) {
		super(node);
	}
}
