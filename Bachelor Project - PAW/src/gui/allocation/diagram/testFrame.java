package gui.allocation.diagram;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import allocation.Allocation;
import allocation.algorithms.ExtendedProbabilisticSerial;
import preference.PreferenceRelationCreator;
import preference.scenario.Scenario;

/**
 * A simple test window for the charts.
 * @author Dominik
 *
 */
@Deprecated
public class testFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testFrame frame = new testFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Scenario sc = PreferenceRelationCreator.createScenarioIterativeJoining(6, 6, 1);
		sc.setAgentRelation(0, PreferenceRelationCreator.createIndifferentRelation(6) , false);
		Allocation a = new ExtendedProbabilisticSerial().allocate(sc);
		System.out.println(a);
		
		PieChartFactory fac = new PieChartFactory(sc, a);
		PieChart pie =  fac.createAgentOverviewPie(3);
		contentPane.add(pie);
	}

}
