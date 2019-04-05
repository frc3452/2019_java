package frc.robot;

import java.util.Scanner;

import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.drivers.GZAnalogInput.AnalogInputConstants;

/**
 * Robot subsystem constants
 *
 * @author Max
 * @since 5/4/18
 */

public class Constants {
	public static final boolean COMP_BOT = true;


	public static class kDrivetrain {

		public final static int L1 = 1, L2 = 2, L3 = 3, L4 = 4;
		public final static int R1 = 5, R2 = 6, R3 = 7, R4 = 8;

		// 2019 Robot

		public final static boolean L_INVERT = true;
		public final static boolean R_INVERT = true;

		public final static double DIFFERENTIAL_DRIVE_DEADBAND = 0;

		public final static double OPEN_LOOP_RAMP_TIME = 0.125; // .125

		public final static double DEMO_DRIVE_MODIFIER = 0.4;

		public final static double NEUTRAL_DEADBAND = 0.01;
	}

	public static class kPDP {
		public final static int DRIVE_L_1 = 1, DRIVE_L_2 = 2, DRIVE_L_3 = 3, DRIVE_L_4 = 4;
		public final static int DRIVE_R_1 = 11, DRIVE_R_2 = 12, DRIVE_R_3 = 13, DRIVE_R_4 = 14;
	}

	public static class kTempSensor {
		public final static AnalogInputConstants TEMPERATURE_SENSOR = new AnalogInputConstants(0, 1.75, -50, 100);
	}

	public static class kFiles {
		public static final boolean FAKE_PDP = false;
		public final static GZFile MOTOR_TESTING_CONFIG = GZFileMaker.getSafeFile("MotorTestingConfig", new Folder(""),
				FileExtensions.CSV, false, false);

		public final static Folder STATS_FILE_FOLDER = new Folder("GZStats");
		public final static String STATS_FILE_NAME = "Stats";
		public final static boolean STATS_FILE_ON_USB = false;
		public final static double DEFAULT_STATS_RECORD_TIME = 1;

		public final static int RECORDING_MOTION_PROFILE_MS = 30; // 20
		public final static double LOGGING_SPEED = 0.125;
		public final static String DEFAULT_LOG_VALUE = "N/A";

		public final static String ROBOT_NAME;
		static {
			String badString = "Unknown_Robot";
			String tempString = badString;
			try {
				Scanner n = new Scanner(
						GZFileMaker.getFile("RobotName", new Folder(), FileExtensions.TXT, false, false).getFile());
				tempString = n.nextLine();
				n.close();
			} catch (Exception e) {
			} finally {
				ROBOT_NAME = tempString;
			}
			if (ROBOT_NAME == badString) {
				System.out.println(
						"ERROR Robot name not found! Upload [RobotName.txt] to ROBORIO home directory to set name!");
			} else {
				System.out.println("Robot identified: " + ROBOT_NAME);
			}

		}
	}

	public static class kOI {
		public class Rumble {
			public final static double ENDGAME = 0.6;
		}

		public static final int LOCK_OUT_KEY = 3;
		public static final AnalogInputConstants LOCK_OUT_KEY_VOLT = new AnalogInputConstants();
	}
}
