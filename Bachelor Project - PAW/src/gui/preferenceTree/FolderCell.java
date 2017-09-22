package gui.preferenceTree;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Cell used by the {@link PreferenceTreeCellRenderer} to display a {@link FolderTreeNode}.
 * @author Dominik
 *
 */
public class FolderCell extends JPanel {
	private static final long serialVersionUID = 1L;

	public FolderCell(boolean selected, boolean isDropLocation) {
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		
		JLabel lblText = new JLabel("Group");
		lblText.setHorizontalAlignment(SwingConstants.LEFT);
		
		if(!selected) {
			setOpaque(false);
		} else {
			lblText.setBackground(Color.LIGHT_GRAY);
		}
		
		if(isDropLocation) {
			lblText.setForeground(new Color(255, 128, 0));
		}
		add(lblText);
	}
}
