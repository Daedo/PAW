package allocation;

import java.util.List;

import preference.scenario.Scenario;

/**
 * Interface for all allocation strategies that can return multiple allocations.
 * 
 * @author Dominik
 *
 */
public interface AllocationStrategyFamily extends AllocationStrategyBase{
	/**
	 * Runs the algortihm and returns a list of all resulting allocations.
	 * 
	 * @param scenario
	 * @return
	 */
	public List<Allocation> getAllAllocations(Scenario scenario);
}
