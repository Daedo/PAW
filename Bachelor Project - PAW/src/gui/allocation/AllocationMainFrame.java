package gui.allocation;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import allocation.Allocation;
import preference.scenario.Scenario;

/**
 * Main allocation window.
 * 
 * @author Dominik
 *
 */
public class AllocationMainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private AllocationPanel[] allocationPanels;
	//TODO Add comparison when multiple are selected

	/**
	 * Create the frame.
	 */
	public AllocationMainFrame(Scenario sc, HashMap<String, Vector<Allocation>> alloc) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1024,576);
		setTitle("Allocation");


		if(alloc.size() == 1) {
			String key = alloc.keySet().iterator().next();
			Vector<Allocation> allocations = alloc.get(key);
			setTitle("Allocation "+key);

			if(allocations.size() == 1) {
				//Single element
				setSingleElement(sc, allocations.elementAt(0));

			} else {
				//Multiple Elements of the same Family -> List
				Vector<String> names = new Vector<>();
				for(int i=0;i<allocations.size();i++) {
					names.addElement(key+" - "+(i+1));
				}

				allocationPanels = new AllocationPanel[allocations.size()];
				setAllocationList(sc, allocations,names);
			}
		} else {
			Vector<Allocation> al = new Vector<>();
			Vector<String> names = new Vector<>();

			for(String key: alloc.keySet()) {
				Vector<Allocation> family = alloc.get(key);
				al.addAll(family);

				if(family.size()==1) {
					names.addElement(key);
				} else {
					for(int i=0;i<family.size();i++) {
						names.addElement(key+" - "+(i+1));	
					}
				}
			}
			allocationPanels = new AllocationPanel[al.size()];
			
			setAllocationList(sc, al, names);
		}
	}

	/**
	 * Adds a single allocation to the list.
	 * @param sc
	 * @param alloc
	 */
	private void setSingleElement(Scenario sc, Allocation alloc) {
		AllocationPanel allocationFrame = new AllocationPanel(sc, alloc);
		setContentPane(allocationFrame);
	}

	/**
	 * Adds a family of allocations to the list.
	 * @param sc
	 * @param alloc
	 */
	private void setAllocationList(Scenario sc, Vector<Allocation> allocs,Vector<String> entryNames) {
		JSplitPane splitPane = new JSplitPane();
		setContentPane(splitPane);

		JList<String> selector = new JList<>(entryNames);
		selector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selector.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = selector.getSelectedIndex();
				int div = splitPane.getDividerLocation();
				
				if(allocationPanels[index] == null) {
					allocationPanels[index] = new AllocationPanel(sc, allocs.elementAt(index));
				}
				
				int currentState = 0;
				if(splitPane.getRightComponent()!=null && splitPane.getRightComponent() instanceof JTabbedPane) {
					currentState = ((JTabbedPane)(splitPane.getRightComponent())).getSelectedIndex();
				}
				if(currentState < 0 ) {
					currentState = 0;
				}
				
				splitPane.setRightComponent(allocationPanels[index]);
				allocationPanels[index].setSelectedIndex(currentState);
				splitPane.setDividerLocation(div);
				
			}
		});
		
		JScrollPane scroll = new JScrollPane(selector);
		
		splitPane.setLeftComponent(scroll);
		splitPane.setDividerLocation(selector.getPreferredSize().width + splitPane.getInsets().left);
		selector.setSelectedIndex(0);
	}
}
