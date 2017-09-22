package allocation.algorithms;

import allocation.AllocationStrategy;
import allocation.AllocationStrategyBase;
import allocation.AllocationStrategyFamily;
import preference.scenario.Scenario;

/**
 * Enum for use in the GUI. Contains the name of each algorithm with a reference used for allocation.
 * @author Dominik
 *
 */
public enum AllocationAlgorithm {
	ADAPTIVE_BOSTON_MECHANISM_PATH("Adaptive Boston Mechanism (Path Equal)",new PathEqualAdaptiveBostonMechanism()),
	NAIVE_BOSTON_MECHANISM_PATH("Naive Boston Mechanism (Path Equal)", new PathEqualNaiveBostonMechanism()),
	ADAPTIVE_BOSTON_MECHANISM_TIE("Adaptive Boston Mechanism (Tie Equal)",new TieEqualAdaptiveBostonMechanism()),
	NAIVE_BOSTON_MECHANISM_TIE("Naive Boston Mechanism (Tie Equal)", new TieEqualNaiveBostonMechanism()),
	//DICHOTOMOUS_PROBABLILISTIC_SERIAL("Dichotomous Probabilistic Serial",new DichotomousProbabilisticSerial()),
	EXTENDED_PROBABLILISTIC_SERIAL("Extended Probabilistic Serial", new ExtendedProbabilisticSerial()),
	POPULAR_ASSIGNMENT("Popular Assignment", new PopularAssignment()),
	POPULAR_CONVEX_SET("Popular Convex Set",new PopularConvexSet()),
	RANDOM_SERIAL_DICTATORSHIP("Random Serial Dictatorship", new RandomSerialDictatorship());
	
	private String displayName;
	private AllocationStrategyBase strategy;
	
	private AllocationAlgorithm(String name, AllocationStrategy strat) {
		this.displayName = name;
		this.strategy = strat;
	}
	
	private AllocationAlgorithm(String name, AllocationStrategyFamily strat) {
		this.displayName = name;
		this.strategy = strat;
	}
	
	public boolean canApply(Scenario sc) {
		return strategy.canApply(sc);
	}
	
	@Override
	public String toString() {
		return displayName;
	}

	public AllocationStrategyBase getAllocationStrategy() {
		return this.strategy;
	}
}
