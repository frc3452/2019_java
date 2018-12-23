package frc.robot.util;

import java.util.ArrayList;

import frc.robot.Constants;

public class GZLog {

	private ArrayList<LogItem> values = new ArrayList<>();

	private static GZLog mInstance = null;

	public synchronized static GZLog getInstance() {
		if (mInstance == null)
			mInstance = new GZLog();
		return mInstance;
	}

	private GZLog() {
	}

	public void add(LogItem item) {
		values.add(item);
	}

	public void update() {
		values.forEach((s) -> s.update());
	}

	public String getHeader() {
		String retval = "";
		retval += GZUtil.dateTime(false);

		for (LogItem item : values) {
			retval += ("," + item.getHeader());
		}
		return retval;
	}

	public String getFunctions() {
		this.update();

		String retval = "";
		retval += "Functions";

		int counter = 1;
		// Loop through all values
		for (LogItem item : values) {

			// If standard value, just ignore this column
			if (!item.mIsFormula) {
				retval += ",";
			} else {
				// If is formula
				String temp;

				try {
					// Replace "$L"s and "$R"s with appropriate letter
					temp = item.getValue();
					temp = temp.replace("$L", GZUtil.letters[counter]);
					temp = temp.replace("$R", GZUtil.letters[counter + 2]);

				} catch (Exception e) {

					// In case of out of bounds area
					temp = "Error with function " + item.getHeader();
					System.out.println(temp);

				}

				// Add to value
				retval += "," + temp;
			}
			// Add to counter
			counter++;
		}

		return retval;
	}

	public String getLog() {
		String retval = "";
		update();

		retval += GZUtil.dateTime(true);

		for (LogItem item : values) {
			if (!item.mIsFormula)
				retval += "," + item.getValue();
			else
				retval += ",";
		}

		return retval;
	}

	public static abstract class LogItem {
		private String mName = "";
		public String mValue = Constants.kFiles.DEFAULT_LOG_VALUE;
		public static final String Average_Left_Formula = "=AVERAGE($L:$L)";
		public static final String Average_Right_Formula = "=AVERAGE($R:$R)";

		private Boolean mIsFormula = false;

		public LogItem(String header) {
			this.mName = header;
			GZLog.getInstance().add(this);
		}

		/**
		 * Use $L to signify column to the left of value Use $R to signify column to the
		 * right of value
		 */
		public LogItem(String header, boolean isFormula) {
			this.mName = header;
			mIsFormula = isFormula;
			GZLog.getInstance().add(this);
		}

		public String getHeader() {
			return this.mName;
		}

		public String getValue() {
			return this.mValue;
		}

		public void update() {
			this.mValue = val();
		}

		public abstract String val();
	}
}
