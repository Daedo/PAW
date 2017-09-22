package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Dialog window to change the names of all objects. Called by the menu item "Profile>Rename Objects"
 * @author Dominik
 *
 */
public class ObjectRenameDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public ObjectRenameDialog(String[] objectNames) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{225, 0};
		gridBagLayout.rowHeights = new int[]{12, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		String[] names = {"Objects"};
		String[][] data = new String[objectNames.length][1];
		for(int i=0;i<objectNames.length;i++) {
			data[i][0] = objectNames[i];
		}
		
		table = new JTable(data,names);
		scrollPane.setViewportView(table);
	}

	/**
	 * @return The array of new object names
	 */
	public String[] getObjectNames() {
		String[] out = new String[table.getRowCount()];
		for(int i=0;i<table.getRowCount();i++) {
			out[i] = table.getModel().getValueAt(i, 0).toString();
		}
		return out;
	}
}
