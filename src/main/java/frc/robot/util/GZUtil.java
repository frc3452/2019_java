package frc.robot.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.Constants.kTempSensor;
import frc.robot.motionprofiles.Path;

public class GZUtil {

	private GZUtil() {
	}

	public static String[] letters = { "", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI",
			"AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA",
			"BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS",
			"BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK",
			"CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CW", "CX", "CY", "CZ", "DA", "DB", "DC",
			"DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", };

	public static int scaleBetween(int unscaledNum, int minAllowed, int maxAllowed, int min, int max) {
		return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
	}

	public static Double getRandDouble(double min, double max) {
		double x = min + (Math.random() * (max - min));
		return x;
	}

	public static Double round(double value)
	{
		return roundTo(value, 2);
	}

	public static Double roundTo(double value, int place) {
		try {
			double ret = (double) Math.round(value * Math.pow(10, place)) / Math.pow(10, place);
			return ret;
		} catch (Exception e) {
			return Double.NaN; //just incase some dumb divided by 0 on accident kinda thing
		}
	}

	public static Integer getRandInt(int min, int max) {
		int x = min + (int) (Math.random() * ((max - min + 1)));
		return x;
	}

	public static Double readTemperatureFromAnalogInput(AnalogInput a) {
		double retval = -3452;

		if (a != null) {
			retval = GZUtil.scaleBetween(a.getVoltage(), kTempSensor.LOW_TEMP_C, kTempSensor.HIGH_TEMP_C,
					kTempSensor.LOW_VOLT, kTempSensor.HIGH_VOLT);
			retval = GZUtil.celsiusToFahrenheit(retval);
		}

		return retval;
	}

	public static double scaleBetween(double unscaledNum, double minAllowed, double maxAllowed, double min,
			double max) {
		return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
	}

	public static StackTraceElement[] currentThread() {
		return Thread.currentThread().getStackTrace();
	}

	/**
	 * currentThread();
	 */
	public static void trace(StackTraceElement e[]) {

		String retval = "";
		try {
			for (int i = e.length - 5; i > 1; i--) {
				retval += e[i].getMethodName();

				if (i != 2)
					retval += ".";
			}
		} catch (Exception ex) {
			System.out.println(
					"Max was a dummy that tried to write something to make his life easier but he made it much much harder");
			// ex.printStackTrace();
		}

		System.out.println(retval);
	}

	public static double limit(double value) {
		if (value > 1.0) {
			return 1.0;
		}
		if (value < -1.0) {
			return -1.0;
		}
		return value;
	}

	public static boolean between(double value, double low, double high) {
		if (value >= low && value <= high)
			return true;

		return false;
	}

	public static boolean epsilonEquals(double value, double epislonPoint, double epsilon) {
		return (value - epsilon <= epislonPoint) && (value + epsilon >= epislonPoint);
	}

	public static boolean allCloseTo(final ArrayList<Double> list, double value, double epsilon) {
		boolean result = true;
		for (Double value_in : list) {
			result &= epsilonEquals(value_in, value, epsilon);
		}
		return result;
	}

	public static double applyDeadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			if (value > 0.0) {
				return (value - deadband) / (1.0 - deadband);
			} else {
				return (value + deadband) / (1.0 - deadband);
			}
		} else {
			return 0.0;
		}
	}

	/**
	 * toRound = 2.342, wanting to round to nearest .05 1/<b>20</b> is .05
	 * roundToFraction(2.342,20)
	 *
	 * @author max
	 * @param value
	 * @param denominator double
	 * @return double
	 */
	public static double roundToFraction(double value, double denominator) {
		return Math.round(value * denominator) / denominator;
	}

	/**
	 * <p>
	 * Returns current date in format
	 * </p>
	 * <p>
	 * <b>MM.dd.HH.mm.ss.SSS</b>
	 * </p>
	 * <p>
	 * or
	 * </p>
	 * <p>
	 * <b>yyyy.MM.dd.HH.mm</b>
	 * </p>
	 * 
	 * @author max
	 * @since
	 */
	public static String dateTime(boolean precision) {
		String temp;
		if (precision)
			temp = new SimpleDateFormat("MM.dd.HH.mm.ss.SSS").format(new Date());
		else
			temp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
		return temp;
	}

	public static double celsiusToFahrenheit(double celsius) {
		double fahrenheit = (5.0 / 9) * celsius + 32;
		return fahrenheit;
	}

	public static double fahrenheitToCelsius(double fahrenheit) {
		double celsius = (9.0 / 5) * fahrenheit - 32;
		return celsius;
	}

	public static class Parse implements Path {

		@Override
		public double[][] mpL() {
			double[][] mpL = { { 3452, 3452 }, { 3452, 3452 }, };
			return mpL;
		}

		@Override
		public double[][] mpR() {
			double[][] mpR = { { 3452, 3452 }, { 3452, 3452 }, };
			return mpR;
		}

		@Override
		public Integer mpDur() {
			return 3452;
		}
	}

}
