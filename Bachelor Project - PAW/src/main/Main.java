package main;

import java.awt.EventQueue;

import gui.MainFrame;

public class Main {
	/*
	 * Tasks:
	 * 
	 * Optional Content
	 * TODO Allow The user to drop the last element in a group (if possible)? (PreferenceTree)
	 * TODO LaTeX Export?
	 * TODO Icon
	 * TODO Optimize?
	 * 
	 * Additional Tasks
	 * Sample RSD and check for SD-Efficiency
	 */

	/**
	 * The precision of any floating point comparison.
	 */
	public static final double F_POINT_PRECISION = 1E-10;
	public static final double LOW_F_POINT_PRECISION = 1E-5;

	/**
	 * Entrypoint. Starts the main window.
	 * @param args
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
