package gui.allocation.allocationTree;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * {@link JTree} to display allocations in a live window
 * 
 * @author Dominik
 *
 */
public class AllocationTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	
	public AllocationTree() {
		setRootVisible(false);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new AllocationTreeRenderer());
		clearTree();
	}

	public void clearTree() {
		model = new DefaultTreeModel(new DefaultMutableTreeNode());
		setModel(model);
	}
	
	/**
	 * Add a single node
	 * 
	 * @param node
	 */
	public void addNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		model.insertNodeInto(node, root, root.getChildCount());
		model.reload();
		repaint();
		
		if(getSelectionCount() == 0) {
			setSelectionPath(new TreePath(((DefaultMutableTreeNode)root.getChildAt(0)).getPath()));
		}
	}
	
	/**
	 * Adds a given node with a list of children.
	 * @param node
	 * @param subNodes
	 */
	public void addNodeFamily(DefaultMutableTreeNode node, List<? extends DefaultMutableTreeNode> subNodes) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		
		model.insertNodeInto(node, root, root.getChildCount());
		
		for(DefaultMutableTreeNode subNode:subNodes) {
			model.insertNodeInto(subNode, node, node.getChildCount());
		}
		
		model.reload();
		repaint();
		if(getSelectionCount() == 0) {
			setSelectionPath(new TreePath(((DefaultMutableTreeNode)root.getChildAt(0)).getPath()));
		}
	}
	
	/**
	 * Reload all nodes that are children of the root node.
	 */
	public void reloadBaseNodes() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		for(int i=0;i<root.getChildCount();i++) {
			if(root.getChildAt(i) instanceof AllocationTreeBaseNode) {
				AllocationTreeBaseNode child = (AllocationTreeBaseNode) root.getChildAt(i);
				child.reloadPanel();
				for(int j=0;j<child.getChildCount();j++) {
					if(child.getChildAt(j) instanceof AllocationTreeBaseNode) {
						((AllocationTreeBaseNode) child.getChildAt(j)).reloadPanel();
					}	
				}
			}
		}
		model.reload();
	}
}
