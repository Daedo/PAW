package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import allocation.algorithms.AllocationAlgorithm;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

/**
 * Basically a carbon copy of {@link AllocationDialog} but with an additional option to select an export destination and export options.
 * Called by "Assign>Export Assignments"
 * @author Dominik
 *
 */
public class AllocationExportDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JList<AllocationAlgorithm> listUsed;
	private JList<AllocationAlgorithm> listAvailable;
	private String filePath = null;
	private JLabel lblPath;
	private JComboBox<AllocationExportOption> comboBox;
	
	/**
	 * Create the panel.
	 */
	public AllocationExportDialog(Collection<AllocationAlgorithm> availableAlgorithms) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblUsedAlgorithms = new JLabel("Used Algorithms:");
		GridBagConstraints gbc_lblUsedAlgorithms = new GridBagConstraints();
		gbc_lblUsedAlgorithms.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsedAlgorithms.gridx = 0;
		gbc_lblUsedAlgorithms.gridy = 0;
		add(lblUsedAlgorithms, gbc_lblUsedAlgorithms);

		JLabel lblAvailableAlgorithms = new JLabel("Available Algorithms:");
		GridBagConstraints gbc_lblAvailableAlgorithms = new GridBagConstraints();
		gbc_lblAvailableAlgorithms.insets = new Insets(0, 0, 5, 0);
		gbc_lblAvailableAlgorithms.gridx = 2;
		gbc_lblAvailableAlgorithms.gridy = 0;
		add(lblAvailableAlgorithms, gbc_lblAvailableAlgorithms);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 6;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);

		listUsed = new JList<AllocationAlgorithm>();
		listUsed.setModel(new DefaultListModel<>());
		scrollPane.setViewportView(listUsed);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridheight = 6;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 1;
		add(scrollPane_1, gbc_scrollPane_1);

		listAvailable = new JList<AllocationAlgorithm>();
		DefaultListModel<AllocationAlgorithm> mod = new DefaultListModel<>();
		for(AllocationAlgorithm a:availableAlgorithms) {
			mod.addElement(a);
		}
		
		
		listAvailable.setModel(mod);
		scrollPane_1.setViewportView(listAvailable);

		JButton buttonAdd = new JButton("←");
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addSelected();
			}
		});
		
		JButton btnAddAll = new JButton("Add All");
		btnAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int max = listAvailable.getModel().getSize()-1;
				listAvailable.setSelectionInterval(0, max);
				addSelected();
			}
		});
		GridBagConstraints gbc_btnAddAll = new GridBagConstraints();
		gbc_btnAddAll.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddAll.gridx = 1;
		gbc_btnAddAll.gridy = 2;
		add(btnAddAll, gbc_btnAddAll);
		GridBagConstraints gbc_buttonAdd = new GridBagConstraints();
		gbc_buttonAdd.insets = new Insets(0, 0, 5, 5);
		gbc_buttonAdd.gridx = 1;
		gbc_buttonAdd.gridy = 3;
		add(buttonAdd, gbc_buttonAdd);

		JButton buttonRemove = new JButton("→");
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelected();
			}
		});
		GridBagConstraints gbc_buttonRemove = new GridBagConstraints();
		gbc_buttonRemove.insets = new Insets(0, 0, 5, 5);
		gbc_buttonRemove.gridx = 1;
		gbc_buttonRemove.gridy = 4;
		add(buttonRemove, gbc_buttonRemove);
		
		JButton btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int max = listUsed.getModel().getSize()-1;
				listUsed.setSelectionInterval(0, max);
				removeSelected();
			}
		});
		GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
		gbc_btnRemoveAll.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveAll.gridx = 1;
		gbc_btnRemoveAll.gridy = 5;
		add(btnRemoveAll, gbc_btnRemoveAll);
		
		lblPath = new JLabel("Path");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.gridwidth = 2;
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 7;
		add(lblPath, gbc_lblPath);
		
		JButton btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("Select the target directory.");
				int returnVal = chooser.showOpenDialog(AllocationExportDialog.this);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       filePath = chooser.getSelectedFile().getAbsolutePath();
			       lblPath.setText("Path: "+filePath);
			       
			    }
			}
		});
		GridBagConstraints gbc_btnSelectFolder = new GridBagConstraints();
		gbc_btnSelectFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelectFolder.gridx = 2;
		gbc_btnSelectFolder.gridy = 7;
		add(btnSelectFolder, gbc_btnSelectFolder);
		
		JLabel lblFileType = new JLabel("File Type");
		GridBagConstraints gbc_lblFileType = new GridBagConstraints();
		gbc_lblFileType.anchor = GridBagConstraints.EAST;
		gbc_lblFileType.insets = new Insets(0, 0, 0, 5);
		gbc_lblFileType.gridx = 0;
		gbc_lblFileType.gridy = 8;
		add(lblFileType, gbc_lblFileType);
		
		comboBox = new JComboBox<AllocationExportOption>(AllocationExportOption.values());
		comboBox.setSelectedIndex(0);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 8;
		add(comboBox, gbc_comboBox);
	}

	/**
	 * Adds the selected items to listUsed.
	 */
	public void addSelected() {
		List<AllocationAlgorithm> selected = listAvailable.getSelectedValuesList();

		DefaultListModel<AllocationAlgorithm> modAvailable = (DefaultListModel<AllocationAlgorithm>) listAvailable.getModel();
		DefaultListModel<AllocationAlgorithm> modUsed   = (DefaultListModel<AllocationAlgorithm>) listUsed.getModel();

		for(AllocationAlgorithm o:selected) {
			modAvailable.removeElement(o);
			modUsed.addElement(o);
		}
		
		invalidate();
		repaint();
	}

	/**
	 * removes selected items from listUsed. 
	 */
	public void removeSelected() {
		List<AllocationAlgorithm> selected = listUsed.getSelectedValuesList();

		DefaultListModel<AllocationAlgorithm> modAvailable = (DefaultListModel<AllocationAlgorithm>) listAvailable.getModel();
		DefaultListModel<AllocationAlgorithm> modUsed   = (DefaultListModel<AllocationAlgorithm>) listUsed.getModel();

		for(AllocationAlgorithm o:selected) {
			modUsed.removeElement(o);
			modAvailable.addElement(o);
		}
		invalidate();
		repaint();
	}

	/**
	 * Returns the algorithms chosen by the user.
	 * @return
	 */
	public List<AllocationAlgorithm> getAlgorithms() {
		Vector<AllocationAlgorithm> out = new Vector<>();
		ListModel<AllocationAlgorithm> mod = listUsed.getModel();
		
		for(int i=0; i< mod.getSize();i++) {
			out.addElement(mod.getElementAt(i));
		}
		
		return out;
	}
	
	/**
	 * Get save path selected by the user
	 * @return
	 */
	public String getPath() {
		return this.filePath;
	}
	
	/**
	 * Returns the selected export option.
	 * @return
	 */
	public AllocationExportOption getExportOption() {
		return (AllocationExportOption)comboBox.getSelectedItem();
	}
}
