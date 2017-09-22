package gui.dialog;

import java.util.function.BiFunction;

import allocation.Allocation;
import allocation.AllocationConverter;
import preference.scenario.Scenario;

/**
 * Options to export an allocation. 
 * Contain a display name and a method to convert the allocation to a String.
 * See {@link AllocationConverter} for more details.
 * @author Dominik
 *
 */
public enum AllocationExportOption {
	CSV_ROUND(AllocationConverter::toCSV, ".csv (round)"),
	CSV_FRACTION(AllocationConverter::toFractionFile, ".csv (fraction)");
	
	BiFunction<Scenario, Allocation, String> converter;
	private String name;
	private AllocationExportOption(BiFunction<Scenario, Allocation, String> conv, String name) {
		this.converter = conv;
		this.name = name;
	}
	
	public String convert(Scenario sc, Allocation alloc) {
		return this.converter.apply(sc, alloc);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
