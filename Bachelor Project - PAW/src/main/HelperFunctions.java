package main;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Miscellaneous functions for math and other stuff.
 * 
 * @author Dominik
 *
 */
public class HelperFunctions {

	/**
	 * Calculates n!
	 * 
	 * @param n
	 * @return
	 */
	public static long factorial(int n) {
		long out = 1;
		for(int i=2;i<=n;i++) {
			out*=i;
		}
		return out;
	}

	/**
	 * Approximates a double as a decimal fraction
	 * http://stackoverflow.com/questions/5968636/converting-a-float-into-a-string-fraction-representation
	 * @param d			Number
	 * @param factor	Precision
	 * @return
	 */
	public static String toFraction(double d, int factor) {
		if(Math.abs(d-1)<=Main.F_POINT_PRECISION) {
			return "1";
		}
		
		
		StringBuilder sb = new StringBuilder();
		if (d < 0) {
			sb.append('-');
			d = -d;
		}
		long l = (long) d;
		if (l != 0) sb.append(l);
		d -= l;
		double error = Math.abs(d);
		int bestDenominator = 1;
		for(int i=2;i<=factor;i++) {
			double error2 = Math.abs(d - (double) Math.round(d * i) / i);
			if (error2 < error) {
				error = error2;
				bestDenominator = i;
			}
		}
		if (bestDenominator > 1)
			sb.append(' ').append(Math.round(d * bestDenominator)).append('/') .append(bestDenominator);
		String out = sb.toString();
		if(out.isEmpty()) {
			out = "0";
		}
		//One Test
		String[] oSplit = out.replaceAll("-", "").split("/");
		if(oSplit.length==2 && oSplit[0].equals(oSplit[1])) {
			return "1";
		}
		
		return out;
	}

	/**
	 * Round to the nth decimal.
	 * 
	 * @param val
	 * @param precision
	 * @return
	 */
	public static double round(double val, int precision) {
		if(Math.abs(val)<Main.F_POINT_PRECISION) {
			return 0;
		}
		
		String d = ".";
		for(int i=0;i<precision;i++) {
			d+="#";
		}
		
		DecimalFormat df = new DecimalFormat("#"+d);
		DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(sym);
		df.setRoundingMode(RoundingMode.HALF_UP);
		
		return Double.parseDouble(df.format(val));
		
	}
	
	/**
	 * Gets the smallest entry that is bigger than 0.
	 * @param data
	 * @return The value or -1 if there is no such entry.
	 */
	public static double getMinBiggerZero2D(double[][] data) {
		double out = -1;
		for(int i=0;i<data.length;i++) {
			for(int j=0;j<data[i].length;j++) {
				double current = data[i][j];
				if(current>0) {
					if(data[i][j] < out || out <= 0) {
						out = data[i][j];
					}
				}
			}
		}
		return out;
	}

	/**
	 * Transposes a 2D Array.
	 * @param data
	 * @return
	 */
	public static double[][] transpose(double[][] data) {
		int h = data.length;
		int w = data[0].length;
		double[][] out = new double[w][h];
		for(int i=0;i<h;i++) {
			for(int j=0;j<w;j++) {
				out[j][i] = data[i][j];
			}
		}
		return out;
	}

	/**
	* Blend two colors linearly using hsb Space.
	* @param c1
	* @param c2
	* @param mix
	* @return
	*/
	public static Color blendColor(Color c1,Color c2, float mix) {
		float[] col1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		float[] col2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
	
		float hue		= col1[0]*(1-mix) + col2[0]*mix;
		float saturation= col1[1];
		float brightness= col1[2];
	
		return Color.getHSBColor(hue, saturation, brightness);
	}

}
