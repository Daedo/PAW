package gui.allocation;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Vector;

import allocation.Allocation;
import allocation.AllocationStrategy;
import allocation.AllocationStrategyFamily;
import preference.scenario.Scenario;

/**
 * Runs different allocaiton strategies.
 * @author Dominik
 *
 */
public class Allocator {
	private HashMap<String,AllocationStrategy> strategies;
	private  HashMap<String,AllocationStrategyFamily> families;
	
	public Allocator() {
		strategies = new HashMap<>();
		families   = new HashMap<>();
	}
	
	/**
	 * Adds a strategy to the allocator
	 * @param strategy
	 * @param name
	 */
	public void addStrategy(AllocationStrategy strategy, String name) {
		if(strategy == null) {
			return;
		}
		strategies.put(name, strategy);
	}
	
	/**
	 * Adds a family to the allocator
	 * @param family
	 * @param name
	 */
	public void addFamily(AllocationStrategyFamily family, String name) {
		if(family == null) {
			return;
		}
		families.put(name, family);
	}
	
	/**
	 * Apply all added strategies and families to the scenario and open a new Allocation window.
	 * @param sc
	 */
	public void allocate(Scenario sc) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HashMap<String,Vector<Allocation>> alloc = createAllocations(sc);
					AllocationMainFrame frame = new AllocationMainFrame(sc,alloc);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Apply all added strategies and families to the scenario.
	 * @param sc
	 * @return
	 */
	public HashMap<String,Vector<Allocation>> createAllocations(Scenario sc) {
		HashMap<String,Vector<Allocation>> alloc = new HashMap<>();
		
		for(String key:strategies.keySet()) {
			Vector<Allocation> collector = alloc.getOrDefault(key, new Vector<>());
			AllocationStrategy s = strategies.get(key);
			collector.addElement(s.allocate(sc));
			alloc.put(key, collector);
		}
		
		for(String key:families.keySet()) {
			Vector<Allocation> collector = alloc.getOrDefault(key, new Vector<>());
			AllocationStrategyFamily f = families.get(key);
			collector.addAll(f.getAllAllocations(sc));
			alloc.put(key, collector);
		}
		
		return alloc;
	}
}
