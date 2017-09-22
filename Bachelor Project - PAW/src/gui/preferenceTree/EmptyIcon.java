package gui.preferenceTree;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Based on http://www.java2s.com/Tutorial/Java/0240__Swing/Anemptyiconwitharbitrarywidthandheight.htm
 * Used to remove certain icons
 * @author Dominik
 */
public final class EmptyIcon implements Icon {
	private int width;
	private int height;

	public EmptyIcon() {
		this(0, 0);
	}

	public EmptyIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	}

}