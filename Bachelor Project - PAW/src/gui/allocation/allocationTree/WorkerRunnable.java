package gui.allocation.allocationTree;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import allocation.Allocation;
import gui.allocation.AllocationLiveWindow;

/**
 * Runnable, that runs the allocations of the live window, without stopping the UI.
 * @author Dominik
 *
 */
public class WorkerRunnable implements Runnable {
	public volatile boolean run;
	public volatile boolean updateTree;
	public volatile boolean updateNames;
	public ConcurrentLinkedQueue<FutureAllocation> futures;

	private volatile boolean isInWaitState;

	private AllocationLiveWindow liveWindow;

	public WorkerRunnable(AllocationLiveWindow window) {
		this.liveWindow = window;
		this.run = true;
		this.updateTree = false;
		this.updateNames = false;
		isInWaitState = true;
		this.futures = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Update loop.
	 */
	@Override
	public void run() {
		while(run) {
			if(updateTree) {
				//Update tree
				updateTree();
				if(updateNames) {
					//Update Names
					updateNames();
					updateNames = false;
				}

			} else {
				isInWaitState = true;
			}

			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				run = false;
			}
		}
		isInWaitState = true;
	}

	public boolean isInWaitState() {
		return isInWaitState;
	}

	/**
	 * Update the nodes of the {@link AllocationTree}
	 */
	@SuppressWarnings("unchecked")
	private void updateTree() {
		if(!futures.isEmpty()) {
			FutureAllocation alloc = futures.element();	

			if(alloc.allocation.isCancelled()) {
				futures.remove();
				return;
			}

			if(!alloc.allocation.isDone()) {
				return;
			}

			Allocation allocation = null;
			List<Allocation> subAllocations = null;
			Object rawAllocation;
			try {
				rawAllocation = alloc.allocation.get();
			} catch (InterruptedException e) {
				return;
			} catch (ExecutionException e) {
				System.out.println(e.getCause());
				
				//TODO Add Error node
				futures.remove();
				return;
			}

			if (rawAllocation instanceof Allocation) {
				allocation = (Allocation) rawAllocation;
			} else {
				subAllocations = (List<Allocation>) rawAllocation;
				if(subAllocations.isEmpty()) {
					System.err.println("EMPTY!");
				}
				
				allocation = liveWindow.getAllocationRepesenter(subAllocations);
			}

			if(updateTree) {
				AllocationTreeBaseNode node = new AllocationTreeBaseNode(alloc.name, liveWindow.getScenario(), allocation);

				if(subAllocations != null) {
					Vector<AllocationTreeBaseNode> subNodes = new Vector<>();
					for(int i=0;i<subAllocations.size();i++) {
						subNodes.addElement(new AllocationTreeBaseNode(alloc.name+" "+(i+1), liveWindow.getScenario(), subAllocations.get(i)));
					}
					liveWindow.allocationTree.addNodeFamily(node, subNodes);
				} else {
					liveWindow.allocationTree.addNode(node);
				}

				futures.remove();
			}
		}
	}

	/**
	 * Update the names of the {@link AllocationTree}
	 */
	private void updateNames() {
		liveWindow.allocationTree.reloadBaseNodes();
		//System.out.println("Update Names");
	}

	/**
	 * Stop all allocations. 
	 */
	public void killFutures() {
		for(FutureAllocation fut:this.futures) {
			fut.allocation.cancel(true);
		}
		this.futures.clear();
	}

	/**
	 * Helper class containing an allocation {@link Future} and its name.
	 * @author Dominik
	 *
	 */
	public static class FutureAllocation {
		public Future<?> allocation;
		public String name;
		public FutureAllocation(Future<?> future, String name) {
			this.allocation = future;
			this.name = name;
		}
	}
}
