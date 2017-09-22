package allocation;

import java.util.List;
import java.util.Vector;

import preference.scenario.Scenario;

/**
 * Base interface for all allocation strategies.
 * 
 * @author Dominik
 *
 */
public abstract interface AllocationStrategyBase {
	/**
	 * Returns true if the given scenario is compatible with the constraints of the allocation strategy.
	 * 
	 * @param scenario
	 * @return
	 */
	public default boolean canApply(Scenario scenario) {
		return getViolatedConstraints(scenario).isEmpty();
	}
	
	/**
	 * Returns a list of all constraints that are violated by the provided scenario. Returns an empty list if canApply is true.
	 * 
	 * @param scenario
	 * @return
	 */
	public default List<String> getViolatedConstraints(Scenario scenario) {
		return new Vector<>();
	}
}
