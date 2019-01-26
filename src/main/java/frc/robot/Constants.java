package frc.robot;

import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZPID;
import frc.robot.util.drivers.DigitalSelector.DigitalSelectorConstants;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidConstants;

/**
 * Robot subsystem constants
 *
 * @author Max
 * @since 5/4/18
 */

public class Constants {
	public static class kLoop {
	}

	public static class kElevator {
		public static final int ELEVATOR_MOTOR_ID = 0;
		public static final boolean E_1_INVERT = false;

		public static final SolenoidConstants SLIDES = new SolenoidConstants(0,0, 1.5, 1.5);
		public static final SolenoidConstants CLAW = new SolenoidConstants(0,1, 1.5, 1.5);

		// Peak should be half continuous
		public final static int AMP_PEAK = 20, AMP_CONTINUOUS = 40, AMP_TIME = 50;

		public static final int CARGO_SENSOR_CHANNEL = 0;
		public static final double CARGO_SENSOR_LOW_VOLT = 1.2; // TODO TUNE
		public static final double CARGO_SENSOR_HIGH_VOLT = 1.4; // TODO TUNE

		public static final double OPEN_RAMP_TIME = 0;

		public static final double HOME_INCHES = 14;
		public static final int TICKS_PER_INCH = 3; // TODO TUNE ME

		public static final boolean ENC_INVERT = false;
		public static final double TARGET_TOLERANCE = 1; // TODO TUNE
		public static final int CARGO_SENSOR_LOOPS_FOR_VALID = 30; // TODO TUNE

		public static GZPID PID = new GZPID(0, 0, 0, 0, 0); // TODO TUNE
		public static GZPID PID2 = new GZPID(0, 0, 0, 0, 0);

		public static enum Heights {
			Home(HOME_INCHES), HP_Floor_Grab(17, true), HP_1(19, true), HP_2(47, true), HP_3(75, true), Cargo_Ship(46),
			Cargo_1(27.5), Cargo_2(55.5), Cargo_3(83.5), HP_Feeder_Jog(HP_1, 2, true);

			public final double inches;
			public final boolean moving_hp;

			private Heights(double inches, boolean movingHatchPanel) {
				this.inches = inches;
				this.moving_hp = movingHatchPanel;
			}

			private Heights(double inches) {
				this(inches, false);
			}

			private Heights(Heights h) {
				this(h.inches);
			}

			private Heights(Heights h, double jog) {
				this(h, jog, false);
			}

			private Heights(Heights h, double jog, boolean movingHatchPanel) {
				this(h.inches + jog, movingHatchPanel);
			}
		}
	}

	public static class kAuton {
		public final static int SAFTEY_SWITCH = 96;

		public final static DigitalSelectorConstants SELECTOR_TENS = new DigitalSelectorConstants("Tens selector", 1,2,3,4);
		public final static DigitalSelectorConstants SELECTOR_ONES = new DigitalSelectorConstants("Ones selector", 5, 6,7, 8);
	}

    public static double kPathFollowingMaxAccel = 120.0; // inches per second^2
	public static double kPathFollowingMaxVel = 120.0; // inches per second
	public static double kSegmentCompletionTolerance = 0.1; // inches
	public static double kTrackWidthInches = 26.655;
	public static double kTrackScrubFactor = 0.924;
	public static double kDriveHighGearMaxSetpoint = 17.0 * 12.0; // 17 fps


	//Path constants
	public static double kMinLookAhead = 12.0; // inches
    public static double kMinLookAheadSpeed = 9.0; // inches per second
    public static double kMaxLookAhead = 24.0; // inches
    public static double kMaxLookAheadSpeed = 120.0; // inches per second
    public static double kDeltaLookAhead = kMaxLookAhead - kMinLookAhead;
	public static double kDeltaLookAheadSpeed = kMaxLookAheadSpeed - kMinLookAheadSpeed;
	public static double kInertiaSteeringGain = 0.0; // angular velocity command is multiplied by this gain *
                                                     // our speed
													 // in inches per sec
													 
	public static double kPathFollowingProfileKp = 3 ; //2.5 //50
    public static double kPathFollowingProfileKi = 0.03; //.03 
    public static double kPathFollowingProfileKv = 0.11; //.11
    public static double kPathFollowingProfileKffv = 5; //1 //.5
    public static double kPathFollowingProfileKffa = 0.025; //.05 //.025

	public static double kPathFollowingGoalPosTolerance = 0.75; // .75
    public static double kPathFollowingGoalVelTolerance = 12.0; // 12.0
    public static double kPathStopSteeringDistance = 9.0; //9.0

	public static class kDrivetrain {

		public static final boolean TUNING = false;

		public static class PID {
			static final double p = .9; // 1.2 
			static final double d = 20; // 10
			static final double f = .78; //.23
			public final static GZPID Left = new GZPID(p, 0, d, f, 0);
			public final static GZPID Right = new GZPID(p, 0, d, f, 0);

			public final static GZPID OldLeft = new GZPID(0, .425, 0, 4.25, 0, 0);
			public final static GZPID OldRight = new GZPID(0, .8, 0, 4.25, 0, 0);
		}

		public final static double WHEEL_DIAMATER_IN = 6;

		public final static int L1 = 1, L2 = 2, L3 = 3, L4 = 4;
		public final static int R1 = 5, R2 = 6, R3 = 7, R4 = 8;

		public final static boolean L_INVERT = false;
		public final static boolean R_INVERT = true;

		public final static double DIFFERENTIAL_DRIVE_DEADBAND = 0.025;

		// Wasserman: Peak should be half of continuous with duration of 50ms
		public final static int AMP_40_PEAK = 20, AMP_40_CONTINUOUS = 40, AMP_40_TIME = 50;
		public final static int AMP_30_PEAK = 15, AMP_30_CONTINUOUS = 30, AMP_30_TIME = 50;

		public final static double OPEN_LOOP_RAMP_TIME = 0.125;

		public final static double DEMO_DRIVE_MODIFIER = .4;

		public final static double ELEV_TURN_SCALAR = 1.67; // 1.67

		public final static double NEUTRAL_DEADBAND = 0.025;

		public static final SolenoidConstants SHIFTER = new SolenoidConstants(0,3, .5, .5);
	}

	public static class kPDP {
		public final static int DRIVE_L_1 = 10, DRIVE_L_2 = 11, DRIVE_L_3 = 12, DRIVE_L_4 = 13;
		public final static int DRIVE_R_1 = 5, DRIVE_R_2 = 4, DRIVE_R_3 = 3, DRIVE_R_4 = 2;

		public static final int ELEVATOR_MOTOR = 0;

		public static final int CLIMBER_FRONT = 0;

		public static final int INTAKE_LEFT = 0;
		public static final int INTAKE_RIGHT = 0;
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

	public static class kClimber {
		public static final SolenoidConstants SOLENOID_RAMP_DROP = new SolenoidConstants(1,0, 1.5, 1.5);

		public static final boolean CLIMBER_FRONT_INVERT = false;
		public static final boolean CLIMBER_BACK_INVERT = true;

		public static final double OPEN_RAMP_TIME = 0;

		public static final int AMP_CONTINUOUS = 40;
		public static final int AMP_PEAK = 20;
		public static final int AMP_TIME = 50;
	}

	public static class kIntake {
		public static final int INTAKE_LEFT = 1;
		public static final int INTAKE_RIGHT = 2;
		public static final SolenoidConstants INTAKE_SOLENOID = new SolenoidConstants(1,1, 1.5, 1.5);
		public static final double INTAKE_SPEED = 0;
	}

	public static class kPneumatics {
		public static final int COMPRESSOR_MODULE = 0;
		public static final SolenoidConstants CRAWLER = new SolenoidConstants(1,2, 1.5, 1);
		public static final int PRESSURE_GUAGE_PORT = 1;
	}

	public static class kPoofs {
		// PROBABLY GOTTA BE BIG TUNED
		// public static final double kRobotLinearInertia = 60.0; // kg TODO tune
		// public static final double kRobotAngularInertia = 10.0; // kg m^2 TODO tune
		// public static final double kRobotAngularDrag = 12.0; // N*m / (rad/sec) TODO
		// tune
		// public static final double kDriveVIntercept = 1.055; // V
		// public static final double kDriveKv = 0.135; // V per rad/s //.135
		// public static final double kDriveKa = 0.012; // V per rad/s^2 //.012
		// ~BIG TUNE

		public static final double kRobotLinearInertia = 60.0; // kg TODO tune
		public static final double kRobotAngularInertia = 10.0; // kg m^2 TODO tune
		public static final double kRobotAngularDrag = 12.0; // N*m / (rad/sec) TODO tune
		
		static double mod = 1.0;

		public static final double kDriveVIntercept = 1.055 * mod; // V
		public static final double kDriveKv = 0.135 * mod; // V per rad/s //.135
		public static final double kDriveKa = 0.012 * mod; // V per rad/s^2 //.012

		public static final double kPathKX = 4.0; // units/s per unit of error
		public static final double kPathLookaheadTime = 0.4; // seconds to look ahead along the path for steering
		public static final double kPathMinLookaheadDistance = 24.0; // inches

		public static final double kEpsilon = 1e-12;

		public static final double kLidarXOffset = -3.3211;
		public static final double kLidarYOffset = 0;
		public static final double kLidarYawAngleDegrees = 0;

		public static final double kDriveWheelTrackWidthInches = 25.5;
		public static final double kDriveWheelDiameterInches = 6;
		public static final double kDriveWheelRadiusInches = kDriveWheelDiameterInches / 2.0;
		public static final double kTrackScrubFactor = 1.0;
	}

	public static class kFiles {
		private kFiles() {
		}

		public static final boolean FAKE_PDP = false;

		public final static String MP_NAME = "MP1";
		public final static Folder MP_FOLDER = new Folder("MotionProfiles"); // if on rio, folder is
																				// MotionProfiles/MP1.csv
		public final static boolean MP_USB = true; // if on usb, folder is 3452/MotionProfiles/MP1.csv

		public final static GZFile MOTOR_TESTING_CONFIG = GZFileMaker.getSafeFile("MotorTestingConfig", new Folder(""),
				FileExtensions.CSV, false, false);

		public final static Folder STATS_FILE_FOLDER = new Folder("GZStats");
		public final static String STATS_FILE_NAME = "Stats";
		public final static boolean STATS_FILE_ON_USB = false;
		public final static double DEFAULT_STATS_RECORD_TIME = .5;

		public final static int RECORDING_MOTION_PROFILE_MS = 30; // 20
		public final static double LOGGING_SPEED = .125;
		public final static String DEFAULT_LOG_VALUE = "N/A";

		public final static double PDPChannelCheckerWaitTime = 1;

		public final static double COPY_WAIT_TIME = 5;
	}

	public static class kOI {
		public class Rumble {

			public final static double INTAKE = .3;
			public final static double ELEVATOR_SPEED_OVERRIDE_DRIVE = .45;
			public final static double ELEVATOR_SPEED_OVERRIDE_OP = ELEVATOR_SPEED_OVERRIDE_DRIVE - .2;
			public final static double ELEVATOR_LIMIT_OVERRIDE = .45;
			public final static double ENDGAME = .6;

		}

		public static final int LOCK_OUT_KEY = 3;
		public static final double KEY_HIGH_VOLT = 0;
		public static final double KEY_LOW_VOLT = 0;
	}
}
