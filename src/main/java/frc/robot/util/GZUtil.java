package frc.robot.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.Constants.kTempSensor;
import frc.robot.poofs.util.control.PathSegment;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;

public class GZUtil {

	private GZUtil() {
	}

	public static GZPID getGainsFromFile(GZFile file, int line) {
		GZPID ret;

		try {
			GZFile myFile = GZFileMaker.getFile(file, file.isOnUsb(), false);
			Scanner scnr = new Scanner(new FileReader(myFile.getFile()));
			double p, i, d, f, iZone;

			if (line > 1)
				for (int loop = 0; loop < line; loop++)
					scnr.nextLine();

			String[] arr = scnr.nextLine().split(",");
			scnr.close();

			p = Double.parseDouble(arr[0]);
			i = Double.parseDouble(arr[1]);
			d = Double.parseDouble(arr[2]);
			f = Double.parseDouble(arr[3]);
			iZone = Double.parseDouble(arr[4]);
			ret = new GZPID(p, i, d, f, (int) iZone);
		} catch (Exception e) {
			ret = new GZPID(0, 0, 0, 0, 0);
		}

		return ret;
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

	public static Double round(double value) {
		return roundTo(value, 2);
	}

	public static Double roundTo(double value, int place) {
		try {
			double ret = (double) Math.round(value * Math.pow(10, place)) / Math.pow(10, place);
			return ret;
		} catch (Exception e) {
			return Double.NaN; // just incase some dumb divided by 0 on accident kinda thing
		}
	}

	public static Integer getRandInt(int min, int max) {
		int x = min + (int) (Math.random() * ((max - min + 1)));
		return x;
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

	public static Rotation2d angleOfPathSegment(PathSegment segment) {
		return angleBetweenPoints(segment.getStart(), segment.getEnd());
	}

	public static Rotation2d angleBetweenPoints(Translation2d point1, Translation2d point2) {
		Rotation2d ret = Rotation2d.identity();

		double yDelta = point1.y() - point2.y();
		double xDelta = point1.x() - point2.x();

		if (xDelta == 0 && yDelta == 0) {
			System.out.println(
					"Translations " + point1.toString() + " and " + point2.toString() + " are in the same place!");
			return null;
		}

		if (xDelta == 0) {
			if (point2.y() > point1.y())
				ret = Rotation2d.fromDegrees(270);
			else
				ret = Rotation2d.fromDegrees(90);
		} else if (yDelta == 0) {
			if (point2.x() > point1.x())
				ret = Rotation2d.fromDegrees(0);
			else
				ret = Rotation2d.fromDegrees(180);
		} else {
			ret = Rotation2d.fromDegrees(Math.toDegrees(Math.atan(yDelta / xDelta)));
			if (point2.y() < point1.y())
				ret = ret.rotateBy(Rotation2d.fromDegrees(180));
		}

		// if x delta 0, if point2 above point1 angle is 270, point2 is below angle is
		// 90
		// if y delta 0, if point2 in front of point1 angle is 0, if point2 is behind
		// 180

		return ret;
	}

	public static double distanceBetween(Translation2d point1, Translation2d point2) {
		double a = point1.x() - point2.x();
		double b = point1.y() - point2.y();

		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean between(double value, double low, double high) {
		if (value >= low && value <= high)
			return true;

		return false;
	}

	public static boolean epsilonEquals(double value, double point, double areaAroundPoint) {
		return (value - areaAroundPoint <= point) && (value + areaAroundPoint >= point);
	}

	public static boolean allCloseTo(final ArrayList<Double> list, double point, double areaAroundPoint) {
		boolean result = true;
		for (Double value_in : list) {
			result &= epsilonEquals(value_in, point, areaAroundPoint);
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

	public static GZFile getDateFile(FileExtensions extension, Folder folder, boolean usb, boolean write)
			throws Exception {
		return GZFileMaker.getFile(dateTime(true), new Folder(folder.get(usb) + "/" + getDate()), extension, usb,
				write);
	}

	public static GZFile getSafeDateFile(FileExtensions extension, Folder folder, boolean usb, boolean write) {
		try {
			return getDateFile(extension, folder, usb, write);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDate() {
		String ret;
		ret = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
		return ret;
	}

	public static double celsiusToFahrenheit(double celsius) {
		double fahrenheit = (5.0 / 9) * celsius + 32;
		return fahrenheit;
	}

	public static double fahrenheitToCelsius(double fahrenheit) {
		double celsius = (9.0 / 5) * fahrenheit - 32;
		return celsius;
	}
}
