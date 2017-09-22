package gui.preferenceTree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import gui.preferenceTree.PreferenceTreeNode.PreferenceNodeObject;

/**
 * Implements the drag and drop behaviour of the {@link PreferenceTree}.
 * Based on http://stackoverflow.com/questions/4588109/drag-and-drop-nodes-in-jtree
 * See the link for documentation.
 * @author Dominik
 *
 */
public class PreferenceTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
	DefaultMutableTreeNode[] nodesToRemove;
	private Runnable endOfTransferCallback;
	
	public void setEndOfTransferCallback(Runnable endOfTransferCallback) {
		this.endOfTransferCallback = endOfTransferCallback;
	}

	public PreferenceTreeTransferHandler(PreferenceTree parent, Runnable callback) {
		this(parent);
		this.endOfTransferCallback = callback;
	}

	public PreferenceTreeTransferHandler(PreferenceTree parent) {
		String className = MarkedNodes.class.getName();
		String type = DataFlavor.javaJVMLocalObjectMimeType+"; class=\""+className+"\"";
		try {
			nodesFlavor = new DataFlavor(type);
		} catch (ClassNotFoundException e) {
			System.err.println("NodeFlavor couldn't be created");
			e.printStackTrace();
		}

		flavors[0] = nodesFlavor;
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		if(!support.isDrop()) {
			return false;
		}

		if(!support.isDataFlavorSupported(nodesFlavor)) {
			return false;
		}
		// Do not allow a drop on the drag source selections.
		if((!(support.getDropLocation() instanceof JTree.DropLocation))||
				!(support.getComponent() instanceof JTree)){
			return false;
		}
		JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
		JTree tree = (JTree)support.getComponent();

		//We only allow movement within a tree
		try {
			Transferable t = support.getTransferable();
			MarkedNodes mNodes = (MarkedNodes) t.getTransferData(nodesFlavor);
			if(mNodes.parent!=tree) {
				return false;
			}

		} catch(Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return false;
		} 


		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for(int i = 0; i < selRows.length; i++) {
			if(selRows[i] == dropRow) {
				return false;
			}
		}

		int action = support.getDropAction();
		if(action != MOVE) {
			return false;
		}

		//We can insert anywhere and drop on the first layer 
		//Drop "Insert" (Root = 0, Base Layer = 1, Second Layer = 2) 
		//Drop "On" is Insert Layer+1
		int pathLayer = dl.getPath().getPathCount();
		boolean isBelowMaxLevel = pathLayer<=2;
		if(!isBelowMaxLevel) {
			return false;
		}

		support.setShowDropLocation(true);
		return haveCompleteNode(tree);
	}

	// Do not allow MOVE-action drops if a non-leaf node is
	// selected unless all of its children are also selected.
	// No longer necessary since we select all leaf nodes automatically
	@Deprecated
	private boolean haveCompleteNode(JTree tree) {
		int[] selRows = tree.getSelectionRows();
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode first = (DefaultMutableTreeNode)path.getLastPathComponent();
		int childCount = first.getChildCount();
		// first has children and no children are selected.
		if(childCount > 0 && selRows.length == 1) {
			return false;
		}
		// first may have children.
		for(int i = 1; i < selRows.length; i++) {
			path = tree.getPathForRow(selRows[i]);
			DefaultMutableTreeNode next = (DefaultMutableTreeNode)path.getLastPathComponent();
			if(first.isNodeChild(next)) {
				// Found a child of first.
				if(childCount > selRows.length-1) {
					// Not all children of first are selected.
					return false;
				}
			}
		}
		return true;
	}

	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath[] paths = tree.getSelectionPaths();
		if(paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			copies.add(copy);
			toRemove.add(node);
			for(int i = 1; i < paths.length; i++) {
				DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
				// Do not allow higher level nodes to be added to list.
				if(next.getLevel() < node.getLevel()) {
					break;
				} else if(next.getLevel() > node.getLevel()) {  // child node
					copy.add(copy(next));
					// node already contains child
				} else {                                        // sibling
					copies.add(copy(next));
					toRemove.add(next);
				}
			}
			DefaultMutableTreeNode[] nodes =copies.toArray(new DefaultMutableTreeNode[copies.size()]);
			nodesToRemove =toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			return new InternalNodesTransferable(nodes, tree);
		}
		return null;
	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(TreeNode node) {
		if(node instanceof FolderTreeNode) {
			return new FolderTreeNode(node);
		}
		if (node instanceof PreferenceTreeNode) {
			PreferenceTreeNode prefNode = (PreferenceTreeNode) node;
			return new PreferenceTreeNode((PreferenceNodeObject) prefNode.getUserObject());
		}

		return new DefaultMutableTreeNode(node);
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		if((action & MOVE) == MOVE) {
			PreferenceTree tree = (PreferenceTree)source;
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			// Remove nodes saved in nodesToRemove in createTransferable.
			for(int i = 0; i < nodesToRemove.length; i++) {
				model.removeNodeFromParent(nodesToRemove[i]);
			}
			
			model.reload();
			tree.expandAll();
		}
		
		if(endOfTransferCallback!=null) {
			endOfTransferCallback.run();
		}
	}

	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	public boolean importData(TransferHandler.TransferSupport support) {
		if(!canImport(support)) {
			return false;
		}
		// Extract transfer data.
		DefaultMutableTreeNode[] nodes = {};
		try {
			Transferable t = support.getTransferable();
			MarkedNodes mNodes = (MarkedNodes) t.getTransferData(nodesFlavor);
			nodes = mNodes.nodes;
		} catch(UnsupportedFlavorException ufe) {
			System.err.println("UnsupportedFlavor: " + ufe.getMessage());
		} catch(java.io.IOException ioe) {
			System.err.println("I/O error: " + ioe.getMessage());
		}
		// Get drop location info.
		JTree.DropLocation dl =(JTree.DropLocation)support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();
		DefaultMutableTreeNode parent =(DefaultMutableTreeNode)dest.getLastPathComponent();
		PreferenceTree tree = (PreferenceTree)support.getComponent();
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

		//Check if we can keep the folder structure or just move the nodes
		boolean keepFolderNodes = true;
		int pathLayer = dl.getPath().getPathCount();
		boolean isOnMaxLevel = pathLayer==2;
		if(isOnMaxLevel) {
			keepFolderNodes = false;
		}

		// Configure for drop mode.
		int index = childIndex;    // DropMode.INSERT
		if(childIndex == -1) {     // DropMode.ON
			index = parent.getChildCount();
			keepFolderNodes = false;
			if(!(parent instanceof FolderTreeNode)){
				//If the nodes are moved on a PreferenceTreeNode, create a folder, and insert the node
				DefaultMutableTreeNode grandparent = (DefaultMutableTreeNode)parent.getParent();
				int parentIndex = grandparent.getIndex(parent);
				DefaultMutableTreeNode folder = new FolderTreeNode();
				model.insertNodeInto(folder, grandparent, parentIndex);
				model.insertNodeInto(parent, folder, 0);
				parent = folder;
				index = 1;
			}
		}
		// Add data to model.
		for(int i = 0; i < nodes.length; i++) {
			//check if we have to remove the folder nodes, remove them if not
			//and fix the tree structure
			boolean isFolder = nodes[i] instanceof FolderTreeNode;
			if(isFolder && !keepFolderNodes) {
				//Move all children
				TreeNode folder = nodes[i];
				for(int j=0;j<folder.getChildCount();j++) {
					MutableTreeNode folderChild = (MutableTreeNode) folder.getChildAt(j);
					model.insertNodeInto(folderChild, parent, index++);
					j--;
				}
			} else {
				model.insertNodeInto(nodes[i], parent, index++);
			}

		}

		//Remove empty Groups and remove groups with only one element
		TreeNode root = (TreeNode) model.getRoot();
		for(int i=0;i<root.getChildCount();i++) {
			MutableTreeNode rootChild = (MutableTreeNode) root.getChildAt(i);
			boolean isFolder = (rootChild instanceof FolderTreeNode);
			//Check if the folder will be empty or has only one child
			if(isFolder) {
				int fSize = 0;
				//Stores the remaining child in case we only have one
				MutableTreeNode remainingChild = null;
				for(int j=0;j<rootChild.getChildCount();j++) {
					//Folder contains nodes, check if they will be removed
					TreeNode folderChild = rootChild.getChildAt(j);
					boolean nodeWillBeRemoved = false;
					for(TreeNode node:nodesToRemove) {
						if(node.equals(folderChild)) {
							nodeWillBeRemoved = true;
							break;
						}
					}

					if(!nodeWillBeRemoved) {
						fSize++;
						remainingChild = (MutableTreeNode)folderChild;
					}
				}

				if(fSize == 0) {
					//Remove the empty folder
					model.removeNodeFromParent(rootChild);
				} else if(fSize==1) {
					//Move remaining Child outside
					model.insertNodeInto(remainingChild,(MutableTreeNode) root, i);
					//Remove the now empty folder
					model.removeNodeFromParent(rootChild);
				}
			}


		}
		//Update and expand all Tree Nodes
		model.reload();
		tree.expandAll();
		return true;
	}

	public String toString() {
		return getClass().getName();
	}

	public class InternalNodesTransferable implements Transferable {
		MarkedNodes data;

		public InternalNodesTransferable(DefaultMutableTreeNode[] nodes, JTree parentTree) {
			data = new MarkedNodes(nodes, parentTree);
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if(!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return data;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}

	/**
	 * Really annoying workaround to make sure, the start and end Tree are the same
	 */
	public class MarkedNodes {
		public DefaultMutableTreeNode[] nodes;
		public JTree parent;

		public MarkedNodes(DefaultMutableTreeNode[] data, JTree tree) {
			this.nodes = data;
			this.parent = tree;
		}
	}
}
