package gui.allocation;

import java.util.function.DoubleFunction;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import allocation.Allocation;
import main.HelperFunctions;
import preference.scenario.Scenario;

/**
 * Panel displaying the allocation as a matrix.
 * @author Dominik
 *
 */
public class AllocationTablePanel extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Change the Map used to change the display in the matrix.
	//Force the display type
	public static boolean forceDefaultMap  = false;
	public static boolean forceFractionMap = true;
	
	private int precisionFactor = 1;
	private final DoubleFunction<String> fractionMap = d->HelperFunctions.toFraction(d, precisionFactor);
	private final DoubleFunction<String> roundMap    = d->HelperFunctions.round(d, 4)+"";
	private final DoubleFunction<String> defaultMap  = d->d+"";

	private RowNumberTable outerTable;
	private JTable innerTable;

	/**
	 * Create the panel.
	 */
	public AllocationTablePanel(Scenario sc,Allocation allocation) {

		int agentCount   = sc.getAgentCount(); 
		String[] agents  = new String[agentCount];
		for(int i=0;i<agentCount;i++) {
			agents[i] = sc.getAgent(i);
		}

		int objCount	 = sc.getObjectCount();
		String[] objects = new String[objCount];
		for(int i=0;i<objCount;i++) {
			objects[i] = sc.getObject(i);
		}

		boolean useRound = false;
		double[][] data = allocation.getData();
		double absMin	= Math.abs(HelperFunctions.getMinBiggerZero2D(data));
		double absLog	= Math.abs(Math.log10(absMin));
		precisionFactor = (int)Math.pow(10, Math.ceil(absLog));
		
		//Unused: It would switch to round mode if the fraction would have a denominator >= 100
		if(absLog >= 2) {
			useRound = true;
		}

		String[][] allocationData;
		if(forceDefaultMap) {
			allocationData = Map2D(data, defaultMap);//{{"X","Y"},{"A","B"}};
		} else {
			if(forceFractionMap|| !useRound) {
				allocationData = Map2D(data, fractionMap);
			}else {	
				allocationData = Map2D(data, roundMap);	
			}
		}
		innerTable = new JTable(allocationData,objects);
		//Non Editable
		innerTable.setDefaultEditor(Object.class, null);
		
		//Center all cells
		//http://stackoverflow.com/questions/7433602/how-to-center-in-jtable-cell-a-value
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();;
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		for(int i=0;i<objCount;i++){
			innerTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
		}
		
		setViewportView(innerTable);
		
		String[] rowNames = new String[sc.getAgentCount()];
		for(int i=0;i<sc.getAgentCount();i++) {
			rowNames[i] = sc.getAgent(i);
		}
		
		outerTable = new RowNumberTable(innerTable,rowNames);
		
		
		
		setRowHeaderView(outerTable);
		setCorner(JScrollPane.UPPER_LEFT_CORNER,outerTable.getTableHeader());	
	}

	/**
	 * Runs a funciton on every entry of the 2D Matrix.
	 * @param data
	 * @param mapFunction
	 * @return
	 */
	private static String[][] Map2D(double[][] data, DoubleFunction<String> mapFunction) {
		String[][] out = new String[data.length][];

		for(int i=0;i<data.length;i++) {
			out[i] = new String[data[i].length];
			for(int j=0;j<data[i].length;j++) {
				out[i][j] = mapFunction.apply(data[i][j]);
			}
		}


		return out;
	}

}
