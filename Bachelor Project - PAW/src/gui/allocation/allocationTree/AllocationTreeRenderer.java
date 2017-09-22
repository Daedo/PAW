package gui.allocation.allocationTree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import main.Main;

/**
 * Displays the nodes of the {@link AllocationTree} with different Icons. 
 * @author Dominik
 *
 */
public class AllocationTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	//Inspired by:
	//http://www.softicons.com/toolbar-icons/16x16-free-application-icons-by-aha-soft/error-icon
	private static final ImageIcon errorIcon 		= new ImageIcon(Main.class.getResource("/error icon.png"));
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,boolean leaf, int row, boolean hasFocus) {
		//https://stackoverflow.com/questions/20691946/set-icon-to-each-node-in-jtree
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if(node instanceof AllocationTreeErrorNode) {
			
			setTextNonSelectionColor(Color.gray);
	        setTextSelectionColor(Color.gray);
			
		} else {
			setTextNonSelectionColor(Color.black);
	        setTextSelectionColor(Color.black);
		}
		
		super.getTreeCellRendererComponent(tree, value, selected,expanded, leaf, row, hasFocus);

		if(node instanceof AllocationTreeErrorNode) {
			setIcon(errorIcon);
		}
		
		return this;

	}

}
