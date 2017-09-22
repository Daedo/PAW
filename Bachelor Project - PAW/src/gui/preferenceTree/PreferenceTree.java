package gui.preferenceTree;

import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import preference.PreferenceGroup;
import preference.PreferenceRelation;
import preference.scenario.Scenario;
import preference.scenario.ScenarioListener;
import preference.scenario.ScenarioUpdateEvent;

/**
 * JTree that represents a certain {@link PreferenceRelation} and allows modifying via drag and drop.
 * @author Dominik
 *
 */
public class PreferenceTree extends JTree {

	//We don't want those collapse/expand arrows so we remove them in the UIManager
	static{
		IconUIResource emptyIcon = new IconUIResource(new EmptyIcon());
		UIManager.put("Tree.collapsedIcon", emptyIcon);
		UIManager.put("Tree.expandedIcon", emptyIcon);
	}

	private static final long serialVersionUID = 1L;
	public static final int ROW_HEIGHT = 25;
	public static final int ROW_WIDTH  = 90;
	private ScenarioListener treeCallback;
	
	private PreferenceTree() {
		super();
		//View Options
		setShowsRootHandles(false);
		setRootVisible(false);
		setCellRenderer(new PreferenceTreeCellRenderer());
		setRowHeight(ROW_HEIGHT);

		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		addTreeSelectionListener(new FullSelectionListener());


		//Prepare Drag & Drop
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		setTransferHandler(new PreferenceTreeTransferHandler(this));

		//We don't allow collapsing of the tree structure
		addTreeWillExpandListener(new NonCollapsingListener());
	}

	/**
	 * Constructs a preference tree with a given number of preference elements
	 * 
	 * @param treeSize Number of Elements (>=0)
	 */
	public PreferenceTree(int treeSize) {
		this();
		if(treeSize<0) {
			throw new IllegalStateException("Treesize can't be negative");
		}

		//Add Preference Objects
		DefaultTreeModel model = (DefaultTreeModel)this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		for(PreferenceTreeNode node:PreferenceTreeNode.generateRange(treeSize)) {
			root.add(node);
		}
		model.reload();

		expandAll();
	}

	/**
	 * Constructs a preference tree from a {@link PreferenceRelation}
	 * @param rel the relation
	 */
	public PreferenceTree(PreferenceRelation rel) {
		this();

		//Add Preference Objects
		DefaultTreeModel model = (DefaultTreeModel)this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();

		for(int i=0;i<rel.getGroupcount();i++) {
			int[] group = rel.getGroup(i);
			if(group.length==1) {
				root.add(new PreferenceTreeNode(group[0]));
			} else {
				FolderTreeNode folder = new FolderTreeNode();
				for(int j=0;j<group.length;j++) {
					folder.add(new PreferenceTreeNode(group[j]));
				}
				root.add(folder);
			}
		}
		model.reload();
		expandAll();
	}

	/**
	 * Constructs tree from the {@link PreferenceRelation} that is at a given position of a {@link Scenario}.
	 * Connects the tree changes to the {@link Scenario}.
	 * @param scr
	 * @param index
	 */
	public PreferenceTree(Scenario scr, int index) {
		this();
		updateTree(scr, index);

		PreferenceTreeTransferHandler handler = (PreferenceTreeTransferHandler) getTransferHandler();
		handler.setEndOfTransferCallback(()->updateScenarioRelation(scr,index));
	}

	/**
	 * Expands the entire Tree
	 */
	public void expandAll() {
		for(int i=0;i<getRowCount();i++) {
			expandRow(i);
		}
	}

	/**
	 * Returns the {@link PreferenceRelation} displayed by this tree. 
	 * @return
	 */
	public PreferenceRelation getRelation() {
		Vector<PreferenceGroup> outBase = new Vector<>();

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		for(int i=0;i<root.getChildCount();i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);

			if (child instanceof FolderTreeNode) {
				FolderTreeNode folder = (FolderTreeNode) child;
				PreferenceGroup group = new PreferenceGroup();

				for(int j=0;j<folder.getChildCount();j++) {
					PreferenceTreeNode folderChild = (PreferenceTreeNode) folder.getChildAt(j);
					group.add(folderChild.getObjectNumber());
				}
				outBase.add(group);

			} else {
				int val = ((PreferenceTreeNode)child).getObjectNumber();
				PreferenceGroup group = new PreferenceGroup();
				group.add(val);
				outBase.add(group);
			}

		}
		PreferenceRelation out = new PreferenceRelation(outBase);
		return out;
	}

	/**
	 * Copys the {@link PreferenceRelation} displayed by this tree to an agent of the the {@link Scenario}.
	 * @param scr
	 * @param index
	 */
	private void updateScenarioRelation(Scenario scr, int index) {
		scr.setAgentRelation(index, getRelation(),false);
	}

	/**
	 * Sets the {@link PreferenceRelation} displayed by this tree to a relation of an agent of the given {@link Scenario}.
	 * Connects the tree to the {@link Scenario} so that they can change each other.
	 * @param scr
	 * @param index
	 */
	private void updateTree(Scenario scr,int index) {
		if(index>=scr.getAgentCount()) {
			return;
		}
		
		PreferenceRelation rel = scr.getAgentRelation(index);

		//Add Preference Objects
		DefaultTreeModel model = (DefaultTreeModel)this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();

		for(int i=0;i<rel.getGroupcount();i++) {
			int[] group = rel.getGroup(i);
			if(group.length==1) {
				String name = scr.getObject(group[0]);
				root.add(new PreferenceTreeNode(group[0],name));
			} else {
				FolderTreeNode folder = new FolderTreeNode();
				for(int j=0;j<group.length;j++) {
					String name = scr.getObject(group[j]);
					folder.add(new PreferenceTreeNode(group[j],name));
				}
				root.add(folder);
			}
		}
		model.reload();
		expandAll();
		//Add Callback to update the tree if the objects have changed
		
		if(this.treeCallback!=null) {
			return;
		}
		this.treeCallback = new ScenarioListener() {
			@Override
			public void valueChanged(ScenarioUpdateEvent e) {
				if(e == ScenarioUpdateEvent.OBJECT_NAME_UPDATE || e == ScenarioUpdateEvent.AGENT_SET_CHANGED || e == ScenarioUpdateEvent.PREFERENCE_UPDATE) {
					updateTree(scr, index);
				}
				
			}
		};
		
		scr.addScenarioListener(treeCallback);
	}
 
	/**
	 * Helper Class.
	 * {@link TreeSelectionListener} that forces the following behaivour:
	 * - If a folder is selected, select all of its children
	 * - If a child is deselected, deselect its parent
	 * @author Dominik
	 *
	 */
	private class FullSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			PreferenceTree tree = (PreferenceTree) e.getSource();
			TreePath[] paths = e.getPaths();

			for(TreePath p:paths) {
				MutableTreeNode lastNode = (MutableTreeNode) p.getLastPathComponent();
				//Select Children
				if(!lastNode.isLeaf()) {
					boolean folderIsSelected = tree.isPathSelected(p);
					if(folderIsSelected) {
						for(int i=0;i<lastNode.getChildCount();i++) {
							TreeNode child = lastNode.getChildAt(i);
							TreePath childPath = p.pathByAddingChild(child);
							tree.addSelectionPath(childPath);
						}	
					}
				} else {
					//Deselect Parent (Note this only works if there are no folders in folders)
					if(!tree.isPathSelected(p)) {
						tree.removeSelectionPath(p.getParentPath());
					}
				}
			}
		}
	}

	/**
	 * Gets the callback, that modifies this tree when something in the {@link Scenario} is changed.
	 * @return
	 */
	public ScenarioListener getTreeCallback() {
		return this.treeCallback;
	}
}
