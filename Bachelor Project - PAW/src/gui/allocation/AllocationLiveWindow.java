package gui.allocation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import allocation.Allocation;
import allocation.AllocationStrategy;
import allocation.AllocationStrategyBase;
import allocation.AllocationStrategyFamily;
import allocation.algorithms.AllocationAlgorithm;
import axiom.StochasticDominance;
import gui.allocation.allocationTree.AllocationTree;
import gui.allocation.allocationTree.AllocationTreeErrorNode;
import gui.allocation.allocationTree.AllocationTreePanelNode;
import gui.allocation.allocationTree.WorkerRunnable;
import gui.allocation.allocationTree.WorkerRunnable.FutureAllocation;
import preference.scenario.Scenario;

/**
 * Live updating allocation window.
 * @author Dominik
 *
 */
public final class AllocationLiveWindow extends JFrame implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private JSplitPane contentPane;
	private Scenario scenario;
	public AllocationTree allocationTree;

	private Thread updateThread;
	private WorkerRunnable updateRunnable;
	private ExecutorService threadPool;

	private int currentTab = 0;

	/**
	 * Create the frame.
	 */
	public AllocationLiveWindow(Scenario sc) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1024,576);
		setTitle("Allocation");

		contentPane = new JSplitPane();
		setContentPane(contentPane);

		this.threadPool = Executors.newSingleThreadExecutor();
		this.updateRunnable = new WorkerRunnable(this);
		this.updateThread = new Thread(updateRunnable);
		this.updateThread.start();

		//Init JTree with all possible Algorithms
		this.allocationTree = new AllocationTree();
		allocationTree.addTreeSelectionListener(this);
		contentPane.setLeftComponent(allocationTree);

		updateScenario(sc);
	}

	/**
	 * Callback Method that updates the live window if the Scenario has changed.
	 * @param sc
	 */
	public void updateScenario(Scenario sc) {
		if(sc==null) {
			return;
		}

		this.updateRunnable.updateTree = false;
		this.scenario = sc;
		while(!this.updateRunnable.isInWaitState()) {}

		this.allocationTree.clearTree();
		updateRunnable.killFutures();

		for(AllocationAlgorithm a:AllocationAlgorithm.values()) {
			AllocationStrategyBase base = a.getAllocationStrategy();
			if(!base.canApply(sc)) {
				addErrorNode(base,a.toString());
			} else if(a == AllocationAlgorithm.POPULAR_CONVEX_SET){
				//Running the convex set calculations live, can cause uncontrolled crashes.
				//But that sounds very negative.
				addErrorNode(base,a.toString(),"Info: Convex Set calculations can not run live.");
			} else {
				addValueNode(base,a.toString());
			}
		};

		this.updateRunnable.updateTree = true;
	}

	/**
	 * Adds a node representing a successful allocation.
	 * @param base
	 * @param name
	 */
	private void addValueNode(AllocationStrategyBase base,String name) {
		if (base instanceof AllocationStrategy) {
			AllocationStrategy strategy = (AllocationStrategy) base;

			Future<Allocation> alloc = threadPool.submit(()->strategy.allocate(scenario));
			this.updateRunnable.futures.offer(new FutureAllocation(alloc, name));
		}

		if (base instanceof AllocationStrategyFamily) {
			AllocationStrategyFamily family = (AllocationStrategyFamily) base;
			Future<List<Allocation>> alloc = threadPool.submit(()->family.getAllAllocations(scenario));
			this.updateRunnable.futures.offer(new FutureAllocation(alloc, name));
		}
	}

	/**
	 * Adds a node representing a failed allocation.
	 * @param base
	 * @param name
	 */
	private void addErrorNode(AllocationStrategyBase base, String name) {
		addErrorNode(base, name, null);
	}

	/**
	 * Adds a node representing a failed allocation.
	 * @param base
	 * @param name
	 * @param error
	 */
	private void addErrorNode(AllocationStrategyBase base, String name,String error) {
		List<String> errors = Collections.emptyList();

		if (base instanceof AllocationStrategy) {
			errors = ((AllocationStrategy) base).getViolatedConstraints(this.scenario);
		}

		if (base instanceof AllocationStrategyFamily) {
			errors = ((AllocationStrategyFamily) base).getViolatedConstraints(this.scenario);
		}

		if(error!=null && !error.trim().isEmpty()) {
			errors.add(error);
		}
		//Add Grey Node, that displays a list of violated Constraints if selected
		AllocationTreeErrorNode errorNode = new AllocationTreeErrorNode(name, errors);
		this.allocationTree.addNode(errorNode);
	}

	/**
	 * Gets called on changes to the allocation tree.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		//Nothing is selected -> Select first child
		if(this.allocationTree.getSelectionCount()==0) {
			this.contentPane.setRightComponent(null);
			
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.allocationTree.getModel().getRoot();
			if(!root.isLeaf()) {
				 this.allocationTree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)root.getFirstChild()).getPath()));
			}
			return;
		} 
		//Something is selected
		if(e.getOldLeadSelectionPath() != null) {
			AllocationTreePanelNode oldPanelNode = (AllocationTreePanelNode)e.getOldLeadSelectionPath().getLastPathComponent();
			if (oldPanelNode.getPanelComponent() instanceof AllocationPanel) {
				currentTab =  ((AllocationPanel) (oldPanelNode.getPanelComponent())).getSelectedIndex();
			}
		}

		AllocationTreePanelNode panelNode = (AllocationTreePanelNode)this.allocationTree.getSelectionPath().getLastPathComponent();
		
		
		if (panelNode.getPanelComponent() instanceof AllocationPanel) {
			AllocationPanel allocPanel = (AllocationPanel) panelNode.getPanelComponent();
			allocPanel.setSelectedIndex(currentTab);
		}


		//Display Content
		this.contentPane.setRightComponent(panelNode.getPanelComponent());
		this.contentPane.setDividerLocation((int)this.allocationTree.getPreferredSize().getWidth());

	}

	/**
	 * Chooses a relation to represent a family of allocations.
	 * @param allocs
	 * @return
	 */
	public Allocation getAllocationRepesenter(List<Allocation> allocs) {
		for(Allocation al:allocs) {
			if(StochasticDominance.getWeakEnvy(scenario, al.getData()).isEmpty()) {
				return al;
			}
		}

		for(Allocation al:allocs) {
			if(StochasticDominance.getEnvy(scenario, al.getData()).isEmpty()) {
				return al;
			}
		}
		return allocs.get(0);
	}

	/**
	 * Updates the names in the live window.
	 * @param newScenario
	 */
	public void updateNames(Scenario newScenario) {
		if(newScenario == null) {
			return;
		}

		this.updateRunnable.updateTree = false;
		this.scenario = newScenario;
		while(!this.updateRunnable.isInWaitState()) {}
		this.updateRunnable.updateNames = true;
		this.updateRunnable.updateTree = true;
	}

	/**
	 * Called on window closure.
	 */
	public void killWindow() {
		updateRunnable.run = false;
		updateRunnable.updateTree = false;
		while(!updateRunnable.isInWaitState()) {}
		updateRunnable.killFutures();
	}

	public Scenario getScenario() {
		return this.scenario;
	}
}
