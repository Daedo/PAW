package gui.preferenceTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * Renderer to make the {@link PreferenceTree} look nice.
 * Based on http://www.java2s.com/Code/Java/Swing-JFC/TreeCellRenderer.htm
 * @author Dominik
 *
 */
public class PreferenceTreeCellRenderer implements TreeCellRenderer {
	JLabel textLabel;
	JPanel renderer;

	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	Color backgroundSelectionColor;
	Color backgroundNonSelectionColor;

	public PreferenceTreeCellRenderer() {
		renderer = new JPanel(new GridLayout(0, 2));
		renderer.setMinimumSize(new Dimension(100, 0));
		textLabel = new JLabel(" ",SwingConstants.CENTER);
		textLabel.setForeground(Color.blue);
		renderer.add(textLabel);

		renderer.setBorder(BorderFactory.createLineBorder(Color.black,1,true));
		backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
		backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
	}

	/**
	 * Renders the cells.
	 * Includes behaivour for drag and drip.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component returnValue = null;

		//FROM http://docs.oracle.com/javase/6/docs/api/javax/swing/tree/TreeCellRenderer.html
		JTree.DropLocation dropLocation = tree.getDropLocation();

		boolean isDropLocation = false;
		if (dropLocation != null
				&& dropLocation.getChildIndex() == -1
				&& tree.getRowForPath(dropLocation.getPath()) == row) {

			// this row represents the current drop location
			// so render it specially, perhaps with a different color
			isDropLocation = true;
		}


		if ((value != null) && (value instanceof PreferenceTreeNode)) {
			returnValue = new PreferenceCell(PreferenceTree.ROW_WIDTH, PreferenceTree.ROW_HEIGHT, value.toString(),selected,isDropLocation);

		}
		else if(value instanceof FolderTreeNode) {
			returnValue = new FolderCell(selected,isDropLocation);
		}

		if (returnValue == null) {
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree,value, selected, expanded, leaf, row, hasFocus);
		}
		return returnValue;
	}

}
