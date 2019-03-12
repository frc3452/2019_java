package frc.robot;

import java.util.Scanner;

import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.auto.pathadapter.fieldprofiles.HeightsContainer;
import frc.robot.poofs.util.control.Lookahead;
import frc.robot.poofs.util.control.PathFollower;
import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZPID;
import frc.robot.util.GZPID.GZPIDPair;
import frc.robot.util.drivers.DigitalSelector.DigitalSelectorConstants;
import frc.robot.util.drivers.GZAnalogInput.AnalogInputConstants;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidConstants;

/**
 * Robot subsystem constants
 *
 * @author Max
 * @since 5/4/18
 */

public class Constants {
	static final boolean mCompBot = true;

	public static class kLoop {
	}

	public static class kSolenoids {
		// CORRECT
		public static final SolenoidConstants SHIFTER_FRONT = new SolenoidConstants(1, 0, 0.3, 0.3);
		public static final SolenoidConstants SHIFTER_REAR = new SolenoidConstants(1, 5, SHIFTER_FRONT);

		public static final SolenoidConstants CRAWLER = new SolenoidConstants(1, 1, 4.0, 4.0);

		public static final SolenoidConstants SLIDES = new SolenoidConstants(0, 7, 0.5, 0.5);
		public static final SolenoidConstants CLAW = new SolenoidConstants(0, 6, 0.75, 0.75);

		// fold 3 then 4 on practice
		public static final SolenoidConstants INTAKE_FOLD = new SolenoidConstants(1, 3, 1.0, 1.0);
		public static final SolenoidConstants INTAKE_DROP = new SolenoidConstants(1, 6, 2.0, 2.0); // TODO TUNE ME
	}

	public static class kElevator {
		public static final int ELEVATOR_1_ID = 9;
		public static final int ELEVATOR_2_ID = 10;
		public static final boolean ELEVATOR_INVERT = true;

		// Peak should be half continuous
		public final static int AMP_PEAK = 20, AMP_CONTINUOUS = 40, AMP_TIME = 50;

		public static final double OPEN_LOOP_RAMP_TIME = 0.5;

		public static final boolean ENC_INVERT = false;

		public static GZPID PID = new GZPID(3.5, 0, 35, 0.2, 0);
		// public static GZPID PID2 = new GZPID(0, 0, 0, 0, 0);

		public static final double HOME_INCHES = 16.0;
		public static final double TOP_LIMIT = 85.0;

		public static final double LOWEST_WITH_SLIDES_OUT = HOME_INCHES + 2.5;
		public static final double SLIDES_TOLERANCE = 1.5;

		public static final int TICKS_PER_INCH = 353; // 352.944782;
		public static final double TARGET_TOLERANCE = 1;
		// public static final double ALLOWABLE_CLOED_LOOP_ERROR = TICKS_PER_INCH *
		// (1.0/8.0);

		public static final double INTAKE_LOW_HEIGHT = 20;
		public static final double INTAKE_HIGH_HEIGHT = 45;
		public static final double INTAKE_TOLERANCE = 2;

		public static final double HATCH_PLACING_JOG = 4;

		public static final int CARGO_SENSOR_CHANNEL = 8;
		public static final int CARGO_SENSOR_LOOPS_FOR_VALID = 10;

		private final static HeightsContainer h = PathAdapter.getHeights();

		public final static double ELEV_TURN_SCALAR = 1.0; // 1.67
		public static final double SPEED_LIMIT_SLOWEST_SPEED = 0.20;
		public static final double SPEED_LIMIT_STARTING_INCHES = 16.0;

		public static final double THROW_CARGO_DELAY = 0.05;

		public static enum Heights {

			Home(HOME_INCHES + 0.25), HP_Floor_Grab(h.hp_floor_Grab()), HP_1(h.hp1()), HP_2(h.hp2()), HP_3(h.hp3()),
			Cargo_Ship(h.cargo_ship(), true), Cargo_1(h.cargo1(), true), Cargo_2(h.cargo2(), true),
			Cargo_3(h.cargo3(), true), HP_Feeder_Jog(h.hp_feed_jog());

			public final double inches;
			public final boolean moving_hp;

			private Heights(double inches, boolean movingHP) {
				this.inches = inches;
				this.moving_hp = movingHP;
			}

			private Heights(double inches) {
				this(inches, true);
			}

			public String toString() {
				return "IN: [" + this.inches + "]" + " HP: [" + this.moving_hp + "]";
			}
		}
	}

	public static class kAuton {
		public final static int SAFTEY_SWITCH = 96;

		public final static DigitalSelectorConstants SELECTOR_TENS = new DigitalSelectorConstants("Ones selector", true,
				0, 1, 2, 3);
		public final static DigitalSelectorConstants SELECTOR_ONES = new DigitalSelectorConstants("Tens selector", true,
				4, 5, 6, 7);
	}

	public static class kPathFollowing {

		static double kPathFollowingMaxAccel = 120.0; // inches per second^2
		static double kPathFollowingMaxVel = 120.0; // inches per second
		static double kTrackWidthInches = PathAdapter.getTrackWidthInches();
		static double kTrackScrubFactor = 0.924;
		static double kDriveHighGearMaxSetpoint = 17.0 * 12.0; // 17 fps

		// Path constants
		static double kMinLookAhead = 12.0; // inches
		static double kMinLookAheadSpeed = 9.0; // inches per second
		static double kMaxLookAhead = 24.0; // inches
		static double kMaxLookAheadSpeed = 120.0; // inches per second
		static double kDeltaLookAhead = kMaxLookAhead - kMinLookAhead;
		static double kDeltaLookAheadSpeed = kMaxLookAheadSpeed - kMinLookAheadSpeed;
		static double kInertiaSteeringGain = 0.0; // angular velocity command is multiplied by this gain *
		// our speed
		// in inches per sec

		// DEFAULT
		// public static double kPathFollowingProfileKp = 5.00;
		// public static double kPathFollowingProfileKi = 0.03;
		// public static double kPathFollowingProfileKv = 0.02;
		// public static double kPathFollowingProfileKffv = 1.0;
		// public static double kPathFollowingProfileKffa = 0.05;

		static double kPathFollowingProfileKp = 3.50;
		static double kPathFollowingProfileKi = 0.06;
		static double kPathFollowingProfileKv = 0.02;
		static double kPathFollowingProfileKffv = 1.0 * 0.5;
		static double kPathFollowingProfileKffa = 0.05 * 1.0;

		// START OF WEEK OF SAINT JOE
		// static double kPathFollowingProfileKp = 1.8;
		// static double kPathFollowingProfileKi = 0.06;
		// static double kPathFollowingProfileKv = 0.02;
		// static double kPathFollowingProfileKffv = 1.0 * 0.7;
		// static double kPathFollowingProfileKffa = 0.05 * 1;

		static double kSegmentCompletionTolerance = 0.1; // inches
		// public static double kPathFollowingGoalPosTolerance = 0.75;
		static double kPathFollowingGoalPosTolerance = 0.5;
		static double kPathFollowingGoalVelTolerance = 12.0;
		public static double kPathStopSteeringDistance = 9;
		// static double kPathStopSteeringDistance = 12;

		public final static PathFollower.Parameters pathFollowingConstants = new PathFollower.Parameters(
				new Lookahead(kMinLookAhead, kMaxLookAhead, kMinLookAheadSpeed, kMaxLookAheadSpeed),
				kInertiaSteeringGain, kPathFollowingProfileKp, kPathFollowingProfileKi, kPathFollowingProfileKv,
				kPathFollowingProfileKffv, kPathFollowingProfileKffa, kPathFollowingMaxVel, kPathFollowingMaxAccel,
				kPathFollowingGoalPosTolerance, kPathFollowingGoalVelTolerance, kPathStopSteeringDistance,
				kSegmentCompletionTolerance, kPathFollowingMaxAccel, kTrackWidthInches, kTrackScrubFactor,
				kDriveHighGearMaxSetpoint);
	}

	public static class kDrivetrain {

		public static final boolean TUNING = false;

		static final double p = 0.9;
		static final double d = 30;
		static final double f = 0.3;
		public static final GZPIDPair PID = new GZPIDPair(0, p, 0, d, f, 0);

		public final static double WHEEL_DIAMATER_IN = PathAdapter.getWheelDiameterInches();

		public final static int L1 = 1, L2 = 2, L3 = 3, L4 = 4;
		public final static int R1 = 5, R2 = 6, R3 = 7, R4 = 8;

		public static final double NEUTRAL_TIME_BETWEEN_SHIFTS = 0.3;
		public static final double CLIMB_PITCH_TOLERANCE = 3;
		public static final int CRAWLER_DROP_NECCESARY_TICKS = 5;

		// 2019 Robot

		public final static boolean L_INVERT = true;
		public final static boolean R_INVERT = true;

		public final static double DIFFERENTIAL_DRIVE_DEADBAND = 0;

		// Wasserman: Peak should be half of continuous with duration of 50ms
		public final static int AMP_40_PEAK = 20, AMP_40_CONTINUOUS = 40, AMP_40_TIME = 50;
		public final static int AMP_30_PEAK = 15, AMP_30_CONTINUOUS = 30, AMP_30_TIME = 50;

		public final static double OPEN_LOOP_RAMP_TIME = 0.125; // .125

		public final static double DEMO_DRIVE_MODIFIER = 0.4;

		public final static double NEUTRAL_DEADBAND = 0.01;

		public static final double CLOSED_LOOP_JOYSTICK_DEADBAND = 0.01;
		public static final double CLOSED_LOOP_TOP_TICKS = 2250 * 1;

	}

	public static class kPDP {
		public final static int DRIVE_L_1 = 1, DRIVE_L_2 = 2, DRIVE_L_3 = 3, DRIVE_L_4 = 4;
		public final static int DRIVE_R_1 = 11, DRIVE_R_2 = 12, DRIVE_R_3 = 13, DRIVE_R_4 = 14;

		public static final int ELEVATOR_1 = 0;
		public static final int ELEVATOR_2 = 15;

		public static final int INTAKE_LEFT = 6;
		public static final int INTAKE_RIGHT = 9;
	}

	public static class kTempSensor {
		public final static AnalogInputConstants TEMPERATURE_SENSOR = new AnalogInputConstants(0, 1.75, -50, 100);
	}

	public static class kLights {
		public final static int CANIFIER_ID = 0;

		public final static int RED = 0;
		public final static int BLUE = 120;
		public final static int PURPLE = 55;
		public final static int GREEN = 254; // ;)
		public final static int YELLOW = 330;
	}

	public static class kIntake {
		public static final int INTAKE_LEFT = 11;
		public static final int INTAKE_RIGHT = 12;
		public static final boolean INTAKE_L_INVERT = true;
		public static final boolean INTAKE_R_INVERT = false;
		public static final double INTAKE_SPEED = -.6;
	}

	public static class kPneumatics {
		public static final int COMPRESSOR_MODULE = 1;
		// http://www.revrobotics.com/rev-11-1107/
		public static final int PRESSURE_GUAGE_PORT = 3;
		public static final AnalogInputConstants PRESSURE_GUAGE_INFO = new AnalogInputConstants(.5, 4.5, 0, 200);
		public static final double LOW_PRESSURE_PRINT_SETPOINT = 60;
		public static final double LOW_PRESSURE = 80;
		public static final double HIGH_PRESSURE = 100;
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
