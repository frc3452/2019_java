package frc.robot;

import frc.robot.util.GZPID;
import frc.robot.util.GZFiles.Folder;

/**
 * Robot subsystem constants
 *
 * @author Max
 * @since 5/4/18
 */

public class Constants {
	public static class kLoop {
		public final static double LOOP_SPEED = .02;
		public final static double ENCODER_CHECKER_SPEED = .1;
	}

	public static class kAuton {
		public final static double GYRO_TURN_SPEED = .25;

		public final static int COMMAND_ARRAY_SIZE = 41;

		public final static double CORRECTION = 0.025;

		public final static int SAFTEY_SWITCH = 96;

		public final static int AUTO_SELECTOR_1 = 2;
		public final static int AUTO_SELECTOR_2 = 3;

		public final static int AUTO_VARIANCE = 15;

		// COMP
		public final static int AUTO_1 = 2683;
		public final static int AUTO_2 = 2992;
		public final static int AUTO_3 = 3185;
		public final static int AUTO_4 = 3321;
		public final static int AUTO_5 = 3427;
		public final static int AUTO_6 = 3507;
		public final static int AUTO_7 = 3565;
		public final static int AUTO_8 = 3658;
		public final static int AUTO_9 = 3721;
		public final static int AUTO_10 = 3781;

		public final static int AUTO_1_L = AUTO_1 - AUTO_VARIANCE;
		public final static int AUTO_1_H = AUTO_1 + AUTO_VARIANCE;

		public final static int AUTO_2_L = AUTO_2 - AUTO_VARIANCE;
		public final static int AUTO_2_H = AUTO_2 + AUTO_VARIANCE;

		public final static int AUTO_3_L = AUTO_3 - AUTO_VARIANCE;
		public final static int AUTO_3_H = AUTO_3 + AUTO_VARIANCE;

		public final static int AUTO_4_L = AUTO_4 - AUTO_VARIANCE;
		public final static int AUTO_4_H = AUTO_4 + AUTO_VARIANCE;

		public final static int AUTO_5_L = AUTO_5 - AUTO_VARIANCE;
		public final static int AUTO_5_H = AUTO_5 + AUTO_VARIANCE;

		public final static int AUTO_6_L = AUTO_6 - AUTO_VARIANCE;
		public final static int AUTO_6_H = AUTO_6 + AUTO_VARIANCE;

		public final static int AUTO_7_L = AUTO_7 - AUTO_VARIANCE;
		public final static int AUTO_7_H = AUTO_7 + AUTO_VARIANCE;

		public final static int AUTO_8_L = AUTO_8 - AUTO_VARIANCE;
		public final static int AUTO_8_H = AUTO_8 + AUTO_VARIANCE;

		public final static int AUTO_9_L = AUTO_9 - AUTO_VARIANCE;
		public final static int AUTO_9_H = AUTO_9 + AUTO_VARIANCE;

		public final static int AUTO_10_L = AUTO_10 - AUTO_VARIANCE;
		public final static int AUTO_10_H = AUTO_10 + AUTO_VARIANCE;

		public final static String DEFAULT_NAME = "NO COMMAND";
	}

	public static class kCamera {
	}

	public static class kClimber {
		public final static int CLIMBER_1 = 2;
		public final static boolean CLIMBER_1_INVERT = false;
	}

	public static class kDrivetrain {

		public static class PID {

			static double p = .3; //.1 
			static double d = p * 0; //40
			public final static GZPID Left = new GZPID(0, p, 0, d, .235, 0);
			public final static GZPID Right = new GZPID(0, p, 0, d, .239, 0);

			// public final static GZPID Left = new GZPID(0, 2.7, 0, 2.7 * 25, .235, 0);
			// public final static GZPID Right = new GZPID(0, 2.7, 0, 2.7 * 25, .239, 0);
			
			public final static GZPID OldLeft = new GZPID(0, .425, 0, 4.25, 0, 0);
			public final static GZPID OldRight = new GZPID(0, .8, 0, 4.25, 0, 0);
		}

		public final static double WHEEL_DIAMATER_IN = 6;

		public final static int L1 = 1, L2 = 2, L3 = 3, L4 = 4;
		public final static int R1 = 5, R2 = 6, R3 = 7, R4 = 8;

		public final static boolean L_INVERT = false;
		public final static boolean R_INVERT = false;

		public final static double DIFFERENTIAL_DRIVE_DEADBAND = 0.045;

		public final static int AMP_40_TRIGGER = 60, AMP_40_LIMIT = 30, AMP_40_TIME = 4000;

		public final static int AMP_30_TRIGGER = 45, AMP_30_LIMIT = 25, AMP_30_TIME = 3000;

		public final static double OPEN_LOOP_RAMP_TIME = 0.125;

		public final static double DEMO_DRIVE_MODIFIER = .4;

		public final static double ELEV_TURN_SCALAR = 1.67; //1.67
	}

	public static class kPDP {
		public final static int DRIVE_L_1 = 0, DRIVE_L_2 = 1, DRIVE_L_3 = 5, DRIVE_L_4 = 4;
		public final static int DRIVE_R_1 = 15, DRIVE_R_2 = 14, DRIVE_R_3 = 11, DRIVE_R_4 = 10;

		public final static int ELEVATOR_1 = 12;
		public final static int ELEVATOR_2 = 13;

		public final static int INTAKE_L = 9;
		public final static int INTAKE_R = 8;

		public final static int CLIMBER_1 = 3;
		public final static int CLIMBER_2 = 2;
	}

	public static class kElevator {

		public class PID {
			public final static double F = 0;
			public final static double P = .185;
			public final static double I = 0.000028; //0.000028
			public final static double D = 6;
		}
		
		public class OLDPID {
			public final static double F = 0;
			public final static double P = .2;
			public final static double I = 0.000028; //0.000028
			public final static double D = 2.5;
		}
		
		//CLOSED LOOP
		public final static double CLOSED_DOWN_SPEED_LIMIT = .5;
		public final static double CLOSED_UP_SPEED_LIMIT = 1;
		public final static double CLOSED_COMPLETION = .06;
		
		public final static double OPEN_RAMP_TIME = .5;
		public final static double CLOSED_RAMP_TIME = .2;
		
		public final static int E_1 = 9;
		public final static int E_2 = 10;

		public final static boolean E_1_INVERT = false;
		public final static boolean E_2_INVERT = false;

		public final static boolean ENC_INVERT = true;
		public final static double ENC_TICKS_PER_INCH = -507.0;
		public final static double ENC_HOME_INCHES = 10.4375;
		
		//LIMITING
		public final static double BOTTOM_ROTATION = HeightsInches.Floor;
		public final static double TOP_ROTATION = 9.414;

		public final static double SPEED_LIMIT_SLOWEST_SPEED = .17;
		public final static double SPEED_LIMIT_STARTING_ROTATION = 2.08;
		
		public final static double TOP_HEIGHT_INCHES = (TOP_ROTATION * 4096) / -ENC_TICKS_PER_INCH;

		public final static double LOWER_SOFT_LIMIT_INCHES = 0;
		public final static double UPPER_SOFT_LIMIT_INCHES = TOP_HEIGHT_INCHES / 2;

		public final static boolean USE_DEMO_SOFT_LIMITS = false;


		//TELEOP MODIFIERS
		public final static double JOYSTICK_MODIFIER_UP = 1;
		public final static double JOYSTICK_MODIFIER_DOWN = .6;

		public final static double DEMO_JOYSTICK_MODIFIER_DOWN = .3;
		public final static double DEMO_JOYSTICK_MODIFIER_UP = .5;


		public final static int AMP_TRIGGER = 50;
		public final static int AMP_LIMIT = 40;
		public final static int AMP_TIME = 1000;

		public class HeightsInches {
			public final static double Floor = 0;
			public final static double Switch = 25;
			public final static double Scale_Mid = 52; //53
			public final static double Scale_High = 68.75; //68.75
		}

		public class HeightsRotations {
			public final static double Floor = 0;
			public final static double Switch = 3.3; //3.3 rotations //23.6 inches
			public final static double Scale = 8; //8 rotations //TODO TUNE
		}

	}

	public static class kIntake {
		public final static int INTAKE_L = 0;
		public final static int INTAKE_R = 1;

		public final static boolean INTAKE_L_INVERT = false;
		public final static boolean INTAKE_R_INVERT = true;

		public class Speeds {
			public final static double INTAKE = -.8;
			public final static double SHOOT = .75;
			public final static double SLOW = .3;
			public final static double PLACE = .325; //.225
			public final static double SPIN = .425; //.35
		}

	}

	public class kTempSensor {
		public final static double LOW_TEMP_C = -50;
		public final static double HIGH_TEMP_C = 100;
		public final static double LOW_VOLT = 0;
		public final static double HIGH_VOLT = 1.75;
	}


	public static class kLights {
		public final static int CANIFIER_ID = 0;

		public final static int RED = 0;
		public final static int BLUE = 120;
		public final static int PURPLE = 55;
		public final static int GREEN = 254; // ;)
		public final static int YELLOW = 330;
	}

	public static class kFiles {
		private kFiles(){}

		public final static String MP_NAME = "MP1";
		public final static Folder MP_FOLDER = new Folder("MotionProfiles"); // if on rio, folder is MotionProfiles/MP1.csv
		public final static boolean MP_USB = true;								// if on usb, folder is 3452/MotionProfiles/MP1.csv

		
		public final static Folder STATS_FILE_FOLDER = new Folder("GZStats");
		public final static String STATS_FILE_NAME = "Stats";
		public final static boolean STATS_FILE_ON_USB = false;
		public final static double DEFAULT_STATS_RECORD_TIME = .5;

		public final static int RECORDING_MOTION_PROFILE_MS = 30; // 20
		public final static double LOGGING_SPEED = .125;
		public final static String DEFAULT_LOG_VALUE = "N/A";
		
		public final static double PDPChannelCheckerWaitTime = 0;
	}

	public static class kOI {
		public class Rumble {

			public final static double INTAKE = .3;
			public final static double ELEVATOR_SPEED_OVERRIDE_DRIVE = .45;
			public final static double ELEVATOR_SPEED_OVERRIDE_OP = ELEVATOR_SPEED_OVERRIDE_DRIVE - .2;
			public final static double ELEVATOR_LIMIT_OVERRIDE = .45;
			public final static double ENDGAME = .6;

		}
	}
}
