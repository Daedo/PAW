package allocation;

import java.io.IOException;
import java.io.PrintWriter;

import gui.dialog.AllocationExportOption;
import preference.scenario.Scenario;

public class AllocationIO {
	/**
	 * Stores the allocation at the given path using the provided export options.
	 * 
	 * @param sc
	 * @param alloc
	 * @param option
	 * @param fullPath
	 * @throws IOException
	 */
	public static void export(Scenario sc,Allocation alloc, AllocationExportOption option, String fullPath) throws IOException {
		String fileContent = option.convert(sc,alloc);
		PrintWriter writer = new PrintWriter(fullPath, "UTF-8");
	    writer.println(fileContent);
	    writer.close();
		
	}
}