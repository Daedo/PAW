package allocation;

import preference.scenario.Scenario;

/**
 * Interface for all allocation strategies that return only one possible allocation.
 * @author Dominik
 *
 */
public interface AllocationStrategy extends AllocationStrategyBase{
	/**
	 * Runs the allocation algorithm on the given scenario.
	 * 
	 * @param scenario
	 * @return
	 */
	public Allocation allocate(Scenario scenario);
}
