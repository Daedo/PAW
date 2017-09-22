package gui.allocation.diagram;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import allocation.Allocation;
import preference.scenario.Scenario;

/**
 * Displays the current pie chart for given agent or the agent overview.
 * @author Dominik
 *
 */
public class AllocationAgentDiagramPanel extends JPanel {

	/**
	 * Based on the Pie Chart Example "Class PieChartDemo1.java" of JFreeChart.
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollDiagramPane;
	private PieChartFactory pieFactory; //Hmm... Pie
	private int agentCount;
	

	public AllocationAgentDiagramPanel(Scenario sc, Allocation alloc) {
		pieFactory = new PieChartFactory(sc, alloc);
		
		setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		add(splitPane);

		JScrollPane scrollListPane = new JScrollPane();
		splitPane.setLeftComponent(scrollListPane);

		agentCount = sc.getAgentCount();
		String chartNames[] = new String[sc.getAgentCount()+1];
		chartNames[0] = "Overview";
		for(int i=0;i<agentCount;i++) {
			chartNames[i+1] = sc.getAgent(i);
		}

		JList<String> list = new JList<String>(chartNames);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateIndex(list.getSelectedIndex());
			}
		});
		scrollListPane.setViewportView(list);

		scrollDiagramPane = new JScrollPane();
		splitPane.setRightComponent(scrollDiagramPane);

		splitPane.setDividerLocation(list.getPreferredSize().width + splitPane.getInsets().left+5);
		list.setSelectedIndex(0);
	}

	void updateIndex(int newIndex) {
		if(newIndex == 0) {
			//Overview
			OverviewDiagramPanel overview = new OverviewDiagramPanel(pieFactory, agentCount);
			scrollDiagramPane.setViewportView(overview);
		} else {
			//Agent
			PieChart pie = pieFactory.createAgentPie(newIndex-1);
			scrollDiagramPane.setViewportView(pie);
		}
	}


}
