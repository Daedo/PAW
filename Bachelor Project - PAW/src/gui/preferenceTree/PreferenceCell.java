package gui.preferenceTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Cell for the {@link PreferenceTreeCellRenderer}. Represents a {@link PreferenceTreeNode}.
 * @author Dominik
 *
 */
public class PreferenceCell extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public PreferenceCell(int width, int height, String text,boolean isSelected,boolean isDropLocation) {
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		Dimension size = new Dimension(width, height);
		
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLayout(new BorderLayout(5, 5));
		setOpaque(false);
		
		JPanel innerPanel = new JPanel();
		
		if(!isSelected) {
			innerPanel.setOpaque(false);
		} else {
			innerPanel.setBackground(Color.LIGHT_GRAY);
		}
		
		if(!isDropLocation) {
			//Make Border Grey
			innerPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		} else {
			//Make Border Orange
			innerPanel.setBorder(new LineBorder(new Color(255, 128, 0), 1, true));
		}
		
		add(innerPanel, BorderLayout.CENTER);
		innerPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblText = new JLabel(text);
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		innerPanel.add(lblText, BorderLayout.CENTER);
		
		
		
		
	}
}
