package frc.robot.subsystems;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Scanner;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.poofs.Kinematics;
import frc.robot.poofs.RobotState;
import frc.robot.poofs.util.control.Lookahead;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.control.PathFollower;
import frc.robot.poofs.util.drivers.NavX;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Twist2d;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPID;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.Units;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController.Breaker;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSmartSpeedController;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSmartSpeedController.Master;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSmartSpeedController.Side;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Drive extends GZSubsystem {
	// private GZSolenoid mShifter;

	// Force switch state to neutral on start up
	private DriveState mState = DriveState.OPEN_LOOP;
	private DriveState mWantedState = DriveState.NEUTRAL;
	public IO mIO = new IO();

	// DRIVETRAIN
	private GZSRX L1, L2, L3, L4, R1, R2, R3, R4;

	// GYRO
	private NavX mNavX;

	private double mModifyPercent = 1;
	private boolean mIsSlow = false;
	private double mPercentageComplete = 0;
	private double mLeft_target = 0, mRight_target = 0;

	private static Drive mInstance = null;

	// ~POOFS~//
	private PathFollower mPathFollower;
	private Path mCurrentPath = null;
	private RobotState mRobotState = RobotState.getInstance();

	public synchronized void setGyroAngle(Rotation2d angle) {
		mNavX.reset();
		mNavX.setAngleAdjustment(angle);
	}

	private synchronized void updateVelocitySetpoint(double left_inches_per_sec, double right_inches_per_sec) {
		final double max_desired = Math.max(Math.abs(left_inches_per_sec), Math.abs(right_inches_per_sec));
		final double scale = max_desired > Constants.kDriveHighGearMaxSetpoint
				? Constants.kDriveHighGearMaxSetpoint / max_desired
				: 1.0;
		mIO.left_desired_output = (inchesPerSecondToRpm(left_inches_per_sec * scale));
		mIO.right_desired_output = -(inchesPerSecondToRpm(right_inches_per_sec * scale));
	}

	public synchronized boolean hasPassedMarker(String marker) {
		if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
			return mPathFollower.hasPassedMarker(marker);
		} else {
			System.out.println("Robot is not in path following mode");
			return false;
		}
	}

	private void updatePathFollower() {
		RigidTransform2d robot_pose = mRobotState.getLatestFieldToVehicle().getValue();
		// System.out.println(robot_pose.getTranslation().toString());
		Twist2d command = mPathFollower.update(Timer.getFPGATimestamp(), robot_pose,
				RobotState.getInstance().getDistanceDriven(), RobotState.getInstance().getPredictedVelocity().dx);
		if (!mPathFollower.isFinished()) {
			Kinematics.DriveVelocity setpoint = Kinematics.inverseKinematics(command);
			updateVelocitySetpoint(setpoint.left, setpoint.right);
		} else {
			updateVelocitySetpoint(0, 0);
		}
	}

	public synchronized void setWantDrivePath(Path path, boolean reversed) {
		if (mCurrentPath != path || mState != DriveState.PATH_FOLLOWING) {
			RobotState.getInstance().resetDistanceDriven();
			mPathFollower = new PathFollower(path, reversed, new PathFollower.Parameters(
					new Lookahead(Constants.kMinLookAhead, Constants.kMaxLookAhead, Constants.kMinLookAheadSpeed,
							Constants.kMaxLookAheadSpeed),
					Constants.kInertiaSteeringGain, Constants.kPathFollowingProfileKp,
					Constants.kPathFollowingProfileKi, Constants.kPathFollowingProfileKv,
					Constants.kPathFollowingProfileKffv, Constants.kPathFollowingProfileKffa,
					Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel,
					Constants.kPathFollowingGoalPosTolerance, Constants.kPathFollowingGoalVelTolerance,
					Constants.kPathStopSteeringDistance));
			setWantedState(DriveState.PATH_FOLLOWING);
			mCurrentPath = path;
		} else {
			// stop();
			// setVelocitySetpoint(0, 0);
		}
	}

	public synchronized boolean isDoneWithPath() {
		if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
			return mPathFollower.isFinished();
		} else {
			System.out.println("Robot is not in path following mode");
			return true;
		}
	}

	public synchronized void forceDoneWithPath() {
		if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
			mPathFollower.forceFinish();
		} else {
			System.out.println("Robot is not in path following mode");
		}
	}

	private static Double rotationsToInches(double rotations) {
		return rotations * (kDrivetrain.WHEEL_DIAMATER_IN * Math.PI);
	}

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

	public double getLeftVelocityInchesPerSec() {
        return rpmToInchesPerSecond(Units.ticks_to_rotations(mIO.left_encoder_vel));
    }

    public double getRightVelocityInchesPerSec() {
        return rpmToInchesPerSecond(Units.ticks_to_rotations(-mIO.right_encoder_vel));
    }

	public double getLeftDistanceInches() {
		Double ret = rotationsToInches(getLeftRotations());
		if (ret.isNaN())
			return 0.0;

		return ret;
	}

	public double getRightDistanceInches() {
		Double ret = rotationsToInches(getRightRotations());
		if (ret.isNaN())
			return 0.0;

		return ret;
	}

	public synchronized Rotation2d getGyroAngle() {
		return mNavX.getYaw();
	}

	private static double inchesPerSecondToRpm(double inches_per_second) {
		return inchesToRotations(inches_per_second) * 60;
	}

	private static double inchesToRotations(double inches) {
		return inches / (kDrivetrain.WHEEL_DIAMATER_IN * Math.PI);
	}

	// ~POOFS~//

	public synchronized static Drive getInstance() {
		if (mInstance == null)
			mInstance = new Drive();
		return mInstance;
	}

	public void printNavX() {
		System.out.println(this.mNavX.toString());

	}

	private Drive() {
		L1 = new GZSRX.Builder(kDrivetrain.L1, this, "L1", kPDP.DRIVE_L_1).setMaster().setSide(Side.LEFT).build();
		L2 = new GZSRX.Builder(kDrivetrain.L2, this, "L2", kPDP.DRIVE_L_2).setFollower().setSide(Side.LEFT).build();
		L3 = new GZSRX.Builder(kDrivetrain.L3, this, "L3", kPDP.DRIVE_L_3).setFollower().setSide(Side.LEFT).build();
		L4 = new GZSRX.Builder(kDrivetrain.L4, this, "L4", kPDP.DRIVE_L_4).setFollower().setSide(Side.LEFT).build();

		R1 = new GZSRX.Builder(kDrivetrain.R1, this, "R1", kPDP.DRIVE_R_1).setMaster().setSide(Side.RIGHT).build();
		R2 = new GZSRX.Builder(kDrivetrain.R2, this, "R2", kPDP.DRIVE_R_2).setFollower().setSide(Side.RIGHT).build();
		R3 = new GZSRX.Builder(kDrivetrain.R3, this, "R3", kPDP.DRIVE_R_3).setFollower().setSide(Side.RIGHT).build();
		R4 = new GZSRX.Builder(kDrivetrain.R4, this, "R4", kPDP.DRIVE_R_4).setFollower().setSide(Side.RIGHT).build();

		mNavX = new NavX(SPI.Port.kMXP);

		// mShifter = new GZSolenoid(kDrivetrain.SHIFTER, this, "PTO Shifter");
		// mShifter.set(false);

		brake(false);

		talonInit();

		// im gonna kill the electrical team if they ever make
		// me do remote sensors again
		L1.setUsingRemoteSensorOnTalon(this, R3);
		zeroEncoders();

		enableFollower();

		in();
		if (getFrontBottomLimit() && getFrontTopLimit())
			Health.getInstance().addAlert(this, AlertLevel.ERROR, "Both PTO front limit switches tripped!");
		if (getRearBottomLimit() && getRearTopLimit())
			Health.getInstance().addAlert(this, AlertLevel.ERROR, "Both PTO rear limit switches tripped!");

		mNavX.reset();

		checkFirmware();
	}

	// POOFS
	public synchronized void setVelocity(double left, double right) {
		setWantedState(DriveState.VELOCITY);
		mIO.left_desired_output = left;
		mIO.right_desired_output = right;
	}

	public String getSmallString() {
		return "DRV";
	}

	private synchronized void out() {
		switch (mState) {
		case PATH_FOLLOWING:
			if (mPathFollower != null) {
				updatePathFollower();
			}
			break;
		case OPEN_LOOP_DRIVER:
			arcade(GZOI.driverJoy);
			break;
		case DEMO:
			alternateArcade(GZOI.driverJoy);
			break;
		}

		if (mState != DriveState.NEUTRAL) {
			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;
		} else {
			mIO.left_output = 0;
			mIO.right_output = 0;
		}

		if (mState == DriveState.PATH_FOLLOWING) {
			L1.set(ControlMode.Velocity, mIO.left_output, DemandType.ArbitraryFeedForward,
					mIO.left_feedforward + kDrivetrain.PID.Left.D * mIO.left_accel / 1023.0);
			R1.set(ControlMode.Velocity, mIO.right_output, DemandType.ArbitraryFeedForward,
					mIO.right_feedforward + kDrivetrain.PID.Left.D * mIO.right_accel / 1023.0);
		} else {
			L1.set(mState.controlMode, mIO.left_output);
			R1.set(mState.controlMode, mIO.right_output);
		}

		if (kDrivetrain.TUNING) {
			setPID(L1, getGainsFromFile(true));
			setPID(R1, getGainsFromFile(true)); // both top line
		}
	}

	private final DecimalFormat df = new DecimalFormat("#0.000");

	public void printVelocity(double tar, double tar2) {
		System.out.println(df.format(tar) + "\t" + df.format(tar2));
	}

	public GZPID getGainsFromFile(boolean left) {
		GZPID ret;

		try {
			GZFile file = GZFileMaker.getFile("DrivePID", new Folder(""), FileExtensions.CSV, false, false);
			Scanner scnr = new Scanner(new FileReader(file.getFile()));
			double p, i, d, f, iZone;

			if (!left)
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

	@Override
	public void addLoggingValues() {
		new LogItem("L-RPM") {
			@Override
			public String val() {
				return Drive.getInstance().getLeftVelNonNativeUnis().toString();
			}
		};

		new LogItem("L-ENC-PRSNT") {
			@Override
			public String val() {
				return Drive.getInstance().mIO.leftEncoderValid.toString();
			}
		};

		new LogItem("R-RPM") {
			@Override
			public String val() {
				return Drive.getInstance().getRightVelNonNativeUnits().toString();
			}
		};

		new LogItem("R-ENC-PRSNT") {
			@Override
			public String val() {
				return Drive.getInstance().mIO.rightEncoderValid.toString();
			}
		};

		this.addLoggingValuesTalons();
	}

	public enum DriveState {
		OPEN_LOOP(false, ControlMode.PercentOutput), OPEN_LOOP_DRIVER(false, ControlMode.PercentOutput),
		DEMO(false, ControlMode.PercentOutput), NEUTRAL(false, ControlMode.Disabled),
		MOTION_MAGIC(true, ControlMode.MotionMagic), MOTION_PROFILE(true, ControlMode.MotionProfile),
		PATH_FOLLOWING(true, ControlMode.Velocity), VELOCITY(true, ControlMode.Velocity),
		CLIMB(false, ControlMode.PercentOutput);

		private final boolean usesClosedLoop;
		private final ControlMode controlMode;

		DriveState(final boolean s, ControlMode c) {
			this.usesClosedLoop = s;
			this.controlMode = c;
		}
	}

	@Override
	public synchronized void stop() {
		setWantedState(DriveState.NEUTRAL);
	}

	public synchronized boolean setWantedState(DriveState wantedState) {
		this.mWantedState = wantedState;

		return this.mWantedState == this.mState;
	}

	private synchronized void switchToState(DriveState state) {
		if (mState != state) {
			onStateExit(mState);
			mState = state;
			onStateStart(mState);
		}
	}

	public synchronized void handleStates() {
		GZOI gzOI = GZOI.getInstance();

		boolean neutral = false;
		neutral |= this.isSafetyDisabled() && !gzOI.isFMS();
		neutral |= mWantedState == DriveState.NEUTRAL;
		neutral |= ((mState.usesClosedLoop || mWantedState.usesClosedLoop) && !mIO.encodersValid);
		// neutral |= getShifterState() == SolenoidState.TRANSITION;

		if (neutral) {

			switchToState(DriveState.NEUTRAL);

		} else if (getShifterState() != SolenoidState.RETRACTED) {
			switchToState(DriveState.CLIMB);
		} else if (Auton.getInstance().isDemo()) {
			if (!gzOI.isFMS()) {
				switchToState(DriveState.DEMO);
			} else {
				switchToState(DriveState.OPEN_LOOP_DRIVER);
			}
		} else {
			switchToState(mWantedState);
		}
	}

	private void talonInit() {
		for (GZSRX s : mTalons) {
			String name = s.getGZName();

			// s.setSafetyEnabled(true);
			// s.setExpiration(999);

			new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not factory reset Talon " + name) {
				@Override
				public ErrorCode error() {
					return s.configFactoryDefault(GZSRX.LONG_TIMEOUT);
				}
			};
			s.setInverted((s.getSide() == Side.LEFT) ? kDrivetrain.L_INVERT : kDrivetrain.R_INVERT);

			s.enableVoltageCompensation(true);

			// CURRENT LIMIT
			GZSRX.logError(
					s.configContinuousCurrentLimit(s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_CONTINUOUS
							: kDrivetrain.AMP_30_CONTINUOUS, GZSRX.TIMEOUT),
					this, AlertLevel.WARNING, "Could not set current-limit continuous for Talon " + name);

			GZSRX.logError(
					s.configPeakCurrentLimit(
							s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_PEAK : kDrivetrain.AMP_30_PEAK,
							GZSRX.TIMEOUT),
					this, AlertLevel.WARNING, "Could not set current-limit peak for Talon " + name);

			GZSRX.logError(
					s.configPeakCurrentDuration(
							s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_TIME : kDrivetrain.AMP_30_TIME,
							GZSRX.TIMEOUT),
					this, AlertLevel.WARNING, "Could not set current limit time for Talon " + name);

			s.enableCurrentLimit(true);

			GZSRX.logError(s.configOpenloopRamp(kDrivetrain.OPEN_LOOP_RAMP_TIME, GZSRX.TIMEOUT), this,
					AlertLevel.WARNING, "Could not set open loop ramp time for Talon " + name);

			GZSRX.logError(s.configNeutralDeadband(kDrivetrain.NEUTRAL_DEADBAND, GZSRX.TIMEOUT), this,
					AlertLevel.WARNING, "Could not set Neutral Deadband for Talon " + name);

			s.setSubsystem("Drive train");

			if (s.getMaster() == Master.MASTER) {

				new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not setup " + s.getSide() + " encoder") {

					@Override
					public ErrorCode error() {
						return s.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
								GZSRX.TIMEOUT);
					}
				};

				new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not zero " + s.getSide() + " encoder") {

					@Override
					public ErrorCode error() {
						return s.setSelectedSensorPosition(0, 0, GZSRX.TIMEOUT);
					}
				};

				new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not set up " + s.getSide() + " limit switch") {

					@Override
					public ErrorCode error() {
						return s.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
								LimitSwitchNormal.NormallyClosed);
					}
				};

				if (!s.isEncoderValid())
					Health.getInstance().addAlert(this, AlertLevel.ERROR, s.getSide() + " encoder not found");

				s.setSensorPhase(false);

				if (s.getSide() == Side.LEFT) {
					setPID(s, kDrivetrain.PID.Left);
				} else {
					setPID(s, kDrivetrain.PID.Right);
				}
			}
		}
	}

	public void setPID(GZSRX talon, GZPID pid) {
		GZSRX.logError(talon.config_kF(pid.parameterSlot, pid.F, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
				"Could not set " + talon.getSide() + " 'F' gain");
		GZSRX.logError(talon.config_kP(pid.parameterSlot, pid.P, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
				"Could not set " + talon.getSide() + " 'P' gain");
		GZSRX.logError(talon.config_kI(pid.parameterSlot, pid.I, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
				"Could not set " + talon.getSide() + " 'I' gain");
		GZSRX.logError(talon.config_kD(pid.parameterSlot, pid.D, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
				"Could not set " + talon.getSide() + " 'D' gain");
		GZSRX.logError(talon.config_IntegralZone(pid.parameterSlot, pid.iZone, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
				"Could not set " + talon.getSide() + " 'iZone' gain");
	}

	private synchronized void checkFirmware() {
		for (GZSRX s : mTalons)
			s.checkFirmware();
	}

	private void enableDriveLimits() {
		if (mState == DriveState.CLIMB) {
			L1.overrideLimitSwitchesEnable(true); // TODO TUNE
			R1.overrideLimitSwitchesEnable(true);
		}
	}

	private void disableDriveLimits() {
		if (mState != DriveState.CLIMB) {
			L1.overrideLimitSwitchesEnable(false);
			R1.overrideLimitSwitchesEnable(false);
		}
	}

	private synchronized void onStateStart(DriveState newState) {
		switch (newState) {
		case CLIMB:
			enableDriveLimits();
			break;
		case PATH_FOLLOWING:
			brake(true);
			break;
		case VELOCITY:
			brake(true);
			break;
		case MOTION_MAGIC:
			brake(true);
			break;
		case MOTION_PROFILE:
			brake(true);
			break;
		case NEUTRAL:
			// brake(false);
			disableDriveLimits();
			brake(GZOI.getInstance().wasTele() || GZOI.getInstance().wasAuto());
			break;
		case OPEN_LOOP:
			brake(true);
			break;
		case OPEN_LOOP_DRIVER:
			brake(false);
			break;
		case DEMO:
			brake(false);
			break;
		default:
			break;
		}
	}

	public synchronized void onStateExit(DriveState prevState) {
		switch (prevState) {
		case CLIMB:
			disableDriveLimits();
			break;
		case PATH_FOLLOWING:
			mIO.left_feedforward = 0;
			mIO.right_feedforward = 0;
			break;
		case MOTION_MAGIC:
			encoderDone();
			break;
		case MOTION_PROFILE:
			encoderDone();
			break;
		case NEUTRAL:
			break;
		case OPEN_LOOP:
			break;
		case OPEN_LOOP_DRIVER:
			break;
		case DEMO:
			break;
		default:
			break;
		}
	}

	@Override
	public synchronized void loop() {
		// final Translation2d trans =
		// RobotState.getInstance().getFieldToVehicle(Timer.getFPGATimestamp()).getTranslation();
		// System.out.println(df.format(trans.x()) + "\t" + df.format(trans.y()));
		outputSmartDashboard();
		handleStates();
		in();
		out();
	}

	public static class IO {

		private double left_feedforward = 0, right_feedforward = 0;
		private double left_accel = 0, right_accel = 0;
		// ~POOFS

		public IO() {
			left_encoder_total_delta_rotations = 0;
			right_encoder_total_delta_rotations = 0;
		}

		public Double left_encoder_ticks = Double.NaN, left_encoder_vel = Double.NaN;

		public double left_encoder_total_delta_rotations = 0, right_encoder_total_delta_rotations = 0;

		public Double right_encoder_ticks = Double.NaN, right_encoder_vel = Double.NaN;

		public boolean ls_left_rev = false, ls_left_fwd = false;
		public boolean ls_right_rev = false, ls_right_fwd = false;

		public Boolean leftEncoderValid = false;
		public Boolean rightEncoderValid = false;
		public Boolean encodersValid = false;

		// out
		private double left_output = 0;
		public double left_desired_output = 0;

		private double right_output = 0;
		public double right_desired_output = 0;
	}

	public Double getLeftRotations() {
		return Units.ticks_to_rotations(mIO.left_encoder_ticks);
	}

	public Double getLeftVelNonNativeUnis() {
		return Units.ticks_to_rotations(mIO.left_encoder_vel);
	}

	public Double getRightRotations() {
		return -Units.ticks_to_rotations(mIO.right_encoder_ticks);
	}

	public Double getRightVelNonNativeUnits() {
		return -Units.ticks_to_rotations(mIO.right_encoder_vel);

	}

	// TODO TUNE
	private boolean getFrontTopLimit() {
		// return mIO.ls_left_fwd;
		return false;
	}

	private boolean getFrontBottomLimit() {
		return false;
	}

	private boolean getRearTopLimit() {
		return false;
	}

	private boolean getRearBottomLimit() {
		return false;
	}

	private synchronized void in() {
		this.mModifyPercent = (mIsSlow ? .5 : 1); // .5

		mIO.leftEncoderValid = L1.isEncoderValid();
		mIO.rightEncoderValid = R1.isEncoderValid();

		mIO.encodersValid = mIO.leftEncoderValid && mIO.rightEncoderValid;

		mIO.ls_left_fwd = L1.getSensorCollection().isFwdLimitSwitchClosed();
		mIO.ls_left_rev = L1.getSensorCollection().isRevLimitSwitchClosed();

		mIO.ls_right_fwd = R1.getSensorCollection().isFwdLimitSwitchClosed();
		mIO.ls_right_rev = R1.getSensorCollection().isRevLimitSwitchClosed();

		if (mIO.leftEncoderValid) {
			mIO.left_encoder_ticks = (double) L1.getSelectedSensorPosition(0);
			mIO.left_encoder_vel = (double) L1.getSelectedSensorVelocity(0);
		} else {
			mIO.left_encoder_ticks = 0.0;
			mIO.left_encoder_vel = 0.0;
		}

		if (mIO.rightEncoderValid) {
			mIO.right_encoder_ticks = (double) R1.getSelectedSensorPosition(0);
			mIO.right_encoder_vel = (double) R1.getSelectedSensorVelocity(0);
		} else {
			mIO.right_encoder_ticks = Double.NaN;
			mIO.right_encoder_vel = Double.NaN;
		}

		mIO.left_encoder_total_delta_rotations = L1.getTotalEncoderRotations(getLeftRotations());
		mIO.right_encoder_total_delta_rotations = R1.getTotalEncoderRotations(getRightRotations());
	}

	public void shift() {
		// mShifter.set(!mShifter.get());
	}

	public synchronized void runClimber(double front, double back) {
		// TODO TUNE
		tank(front, back);
	}

	private SolenoidState getShifterState() {
		return SolenoidState.RETRACTED;
		// return mShifter.getSolenoidState();
	}

	// called in OPEN_LOOP_DRIVER state
	private synchronized void arcade(GZJoystick joy) {
		double turnScalar;
		// if (Elevator.getInstance().isLimiting())
		// turnScalar = Constants.kDrivetrain.ELEV_TURN_SCALAR;
		// else
		turnScalar = 1;

		double elv = getModifier();

		final double rotate = elv * turnScalar * ((joy.getRightTrigger() - joy.getLeftTrigger()) * .65);
		final double move = joy.getLeftAnalogY() * elv;

		arcadeNoState(move, rotate);
	}

	// called in DEMO state
	private synchronized void alternateArcade(GZJoystick joy) {
		arcadeNoState(joy.getLeftAnalogY(), (joy.getRightAnalogX() * .85));
	}

	private synchronized void arcadeNoState(double move, double rotate) {
		double[] temp = arcadeToLR(move * mModifyPercent, rotate * mModifyPercent);

		mIO.left_desired_output = temp[0];
		mIO.right_desired_output = temp[1];
	}

	public synchronized void arcade(double move, double rotate) {
		if (setWantedState(DriveState.OPEN_LOOP))
			arcadeNoState(move, rotate);
	}

	public synchronized double[] arcadeToLR(double xSpeed, double zRotation) {
		return arcadeToLR(xSpeed, zRotation, false);
	}

	// Modified from DifferentialDrive.java to produce double array, [0] being left
	// motor value, [1] being right motor value
	public synchronized double[] arcadeToLR(double xSpeed, double zRotation, boolean squaredInputs) {
		xSpeed = GZUtil.limit(xSpeed);
		xSpeed = GZUtil.applyDeadband(xSpeed, kDrivetrain.DIFFERENTIAL_DRIVE_DEADBAND);

		zRotation = GZUtil.limit(zRotation);
		zRotation = GZUtil.applyDeadband(zRotation, kDrivetrain.DIFFERENTIAL_DRIVE_DEADBAND);

		double leftMotorOutput;
		double rightMotorOutput;

		// Square the inputs (while preserving the sign) to increase fine control
		// while permitting full power.
		if (squaredInputs) {
			xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
			zRotation = Math.copySign(zRotation * zRotation, zRotation);
		}

		double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

		if (xSpeed >= 0.0) {
			// First quadrant, else second quadrant
			if (zRotation >= 0.0) {
				leftMotorOutput = maxInput;
				rightMotorOutput = xSpeed - zRotation;
			} else {
				leftMotorOutput = xSpeed + zRotation;
				rightMotorOutput = maxInput;
			}
		} else {
			// Third quadrant, else fourth quadrant
			if (zRotation >= 0.0) {
				leftMotorOutput = xSpeed + zRotation;
				rightMotorOutput = maxInput;
			} else {
				leftMotorOutput = maxInput;
				rightMotorOutput = xSpeed - zRotation;
			}
		}

		double retval[] = { 0, 0 };
		retval[0] = GZUtil.limit(leftMotorOutput);
		retval[1] = -GZUtil.limit(rightMotorOutput);

		return retval;
	}

	public double getModifier() {
		return 1;
	}

	public synchronized void tank(double left, double right) {
		if (setWantedState(DriveState.OPEN_LOOP)) {
			mIO.left_desired_output = left * getModifier() * mModifyPercent;
			mIO.right_desired_output = right * getModifier() * mModifyPercent;
		}
	}

	public synchronized void tank(GZJoystick joy) {
		tank(joy.getLeftAnalogY(), joy.getRightAnalogY());
	}

	private synchronized void brake(boolean brake) {
		brake(brake ? NeutralMode.Brake : NeutralMode.Coast);
	}

	private synchronized void brake(NeutralMode mode) {
		for (GZSRX c : mTalons)
			c.setNeutralMode(mode);
	}

	public synchronized void motionMagic(double leftRotations, double rightRotations, double leftAccel,
			double rightAccel, double leftSpeed, double rightSpeed) {

		if (setWantedState(DriveState.MOTION_MAGIC)) {

			double topspeed = 4350;

			mLeft_target = Units.rotations_to_ticks(leftRotations);
			mRight_target = -Units.rotations_to_ticks(rightRotations);

			mPercentageComplete = Math
					.abs(((mIO.left_encoder_ticks / mLeft_target) + (mIO.right_encoder_ticks / mRight_target)) / 2);

			L1.configMotionAcceleration((int) (topspeed * leftAccel), 10);
			R1.configMotionAcceleration((int) (topspeed * rightAccel), 10);

			L1.configMotionCruiseVelocity((int) (topspeed * leftSpeed), 10);
			R1.configMotionCruiseVelocity((int) (topspeed * rightSpeed), 10);

			mIO.left_desired_output = mLeft_target;
			mIO.right_desired_output = mRight_target;
		}
	}

	public synchronized boolean encoderSpeedIsUnder(double ticksPer100Ms) {
		double l = Math.abs(mIO.left_encoder_vel);
		double r = Math.abs(mIO.right_encoder_vel);

		return l < ticksPer100Ms || r < ticksPer100Ms;
	}

	public synchronized void encoderDone() {
		stop();

		processMotionProfileBuffer(3452);

		L1.clearMotionProfileTrajectories();
		R1.clearMotionProfileTrajectories();

		R1.configPeakOutputForward(1, 0);
		R1.configPeakOutputReverse(-1, 0);
		L1.configPeakOutputForward(1, 0);
		L1.configPeakOutputReverse(-1, 0);

		mLeft_target = 0;
		mRight_target = 0;

		mPercentageComplete = -3452;
	}

	public synchronized boolean encoderIsDone(double multiplier) {
		return (mIO.left_encoder_ticks < mLeft_target + 102 * multiplier)
				&& (mIO.left_encoder_ticks > mLeft_target - 102 * multiplier)
				&& (mIO.right_encoder_ticks < mRight_target + 102 * multiplier)
				&& (mIO.right_encoder_ticks > mRight_target - 102 * multiplier);
	}

	public synchronized boolean encoderIsDoneEither(double multiplier) {
		return (mIO.left_encoder_ticks < mLeft_target + 102 * multiplier
				&& mIO.left_encoder_ticks > mLeft_target - 102 * multiplier)
				|| (mIO.right_encoder_ticks < mRight_target + 102 * multiplier
						&& mIO.right_encoder_ticks > mRight_target - 102 * multiplier);
	}

	/**
	 * notifier object for running MotionProfileBuffer
	 */
	private Notifier processMotionProfile = new Notifier(new Runnable() {
		@Override
		public void run() {
			{
				L1.processMotionProfileBuffer();
				R1.processMotionProfileBuffer();
			}
		}
	});

	/**
	 * Used to turn on/off runnable for motion profiling
	 * 
	 * @param time double - if 3452, stops notifier
	 */
	public synchronized void processMotionProfileBuffer(double time) {
		if (time == 3452)
			processMotionProfile.stop();
		else
			processMotionProfile.startPeriodic(time);
	}

	public synchronized void getMotionProfileStatus(boolean left, MotionProfileStatus statusToFill) {
		if (left)
			L1.getMotionProfileStatus(statusToFill);
		else
			R1.getMotionProfileStatus(statusToFill);
	}

	/**
	 * push motion profiles to drive train talons
	 * 
	 * @since 4-22-2018
	 */
	public synchronized void motionProfileToTalons(double[][] mpL, double[][] mpR, Integer mpDur) {

		if (mpL.length != mpR.length)
			System.out.println("ERROR MOTION-PROFILE-SIZING ISSUE:\t\t" + mpL.length + "\t\t" + mpR.length);

		processMotionProfileBuffer((double) mpDur / (1000 * 2));

		TrajectoryPoint rightPoint = new TrajectoryPoint();
		TrajectoryPoint leftPoint = new TrajectoryPoint();

		// set talon srx
		L1.configMotionProfileTrajectoryPeriod(mpDur, 10);
		R1.configMotionProfileTrajectoryPeriod(mpDur, 10);
		L1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, mpDur, 10);
		R1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, mpDur, 10);

		L1.clearMotionProfileTrajectories();
		R1.clearMotionProfileTrajectories();

		// generate and push each mp point
		if (mpL.length == mpR.length) {
			for (int i = 0; i < mpL.length; i++) {

				leftPoint.position = mpL[i][0] * 4096;
				leftPoint.velocity = mpL[i][1] * 4096;

				rightPoint.position = mpR[i][0] * -4096;
				rightPoint.velocity = mpR[i][1] * -4096;

				leftPoint.timeDur = GZFiles.getInstance().mpDur;
				rightPoint.timeDur = GZFiles.getInstance().mpDur;

				leftPoint.headingDeg = 0;
				rightPoint.headingDeg = 0;

				leftPoint.profileSlotSelect0 = 0;
				rightPoint.profileSlotSelect0 = 0;

				leftPoint.profileSlotSelect1 = 0;
				rightPoint.profileSlotSelect1 = 0;

				leftPoint.zeroPos = false;
				rightPoint.zeroPos = false;

				if (i == 0) {
					leftPoint.zeroPos = true;
					rightPoint.zeroPos = true;
				}

				leftPoint.isLastPoint = false;
				rightPoint.isLastPoint = false;

				if ((i + 1) == mpL.length) {
					leftPoint.isLastPoint = true;
					rightPoint.isLastPoint = true;
				}

				L1.pushMotionProfileTrajectory(leftPoint);
				R1.pushMotionProfileTrajectory(rightPoint);
			}
			System.out.println("Motion profile pushed to Talons");
		} else {
			System.out.println("Motion profile lists not same size!!!");
		}
	}

	/**
	 * Used to process and push <b>parsed</b> motion profiles to drivetrain talons
	 * 
	 * @author max
	 * @since 4-22-2018
	 */
	public synchronized void motionProfileToTalons() {
		if (GZFiles.getInstance().mpL.size() != GZFiles.getInstance().mpR.size())
			System.out.println("ERROR MOTION-PROFILE-SIZING ISSUE:\t\t" + GZFiles.getInstance().mpL.size() + "\t\t"
					+ GZFiles.getInstance().mpR.size());

		processMotionProfileBuffer((double) GZFiles.getInstance().mpDur / (1000 * 2));

		TrajectoryPoint rightPoint = new TrajectoryPoint();
		TrajectoryPoint leftPoint = new TrajectoryPoint();

		// set talon srx
		L1.configMotionProfileTrajectoryPeriod(GZFiles.getInstance().mpDur, 10);
		R1.configMotionProfileTrajectoryPeriod(GZFiles.getInstance().mpDur, 10);
		L1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, GZFiles.getInstance().mpDur, 10);
		R1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, GZFiles.getInstance().mpDur, 10);

		L1.clearMotionProfileTrajectories();
		R1.clearMotionProfileTrajectories();

		// generate and push each mp point
		if (GZFiles.getInstance().mpL.size() != GZFiles.getInstance().mpR.size()) {
			System.out.println("Motion profile lists not same size!!!");
		} else {
			for (int i = 0; i < GZFiles.getInstance().mpL.size(); i++) {

				leftPoint.position = GZFiles.getInstance().mpL.get(i).get(0) * 4096;
				leftPoint.velocity = GZFiles.getInstance().mpL.get(i).get(1) * 4096;

				rightPoint.position = GZFiles.getInstance().mpR.get(i).get(0) * -4096;
				rightPoint.velocity = GZFiles.getInstance().mpR.get(i).get(1) * -4096;

				leftPoint.timeDur = GZFiles.getInstance().mpDur;
				rightPoint.timeDur = GZFiles.getInstance().mpDur;

				// leftPoint.timeDur = GetTrajectoryDuration(GZFiles.getInstance().mpDur);
				// rightPoint.timeDur = GetTrajectoryDuration(GZFiles.getInstance().mpDur);

				leftPoint.headingDeg = 0;
				rightPoint.headingDeg = 0;

				leftPoint.profileSlotSelect0 = 0;
				rightPoint.profileSlotSelect0 = 0;

				leftPoint.profileSlotSelect1 = 0;
				rightPoint.profileSlotSelect1 = 0;

				leftPoint.zeroPos = false;
				rightPoint.zeroPos = false;

				if (i == 0) {
					leftPoint.zeroPos = true;
					rightPoint.zeroPos = true;
				}

				leftPoint.isLastPoint = false;
				rightPoint.isLastPoint = false;

				if ((i + 1) == GZFiles.getInstance().mpL.size()) {
					leftPoint.isLastPoint = true;
					rightPoint.isLastPoint = true;
				}

				L1.pushMotionProfileTrajectory(leftPoint);
				R1.pushMotionProfileTrajectory(rightPoint);
			}
			System.out.println("Motion profile pushed to Talons");
		}

	}

	// @SuppressWarnings("static-access")
	// private synchronized TrajectoryDuration GetTrajectoryDuration(int durationMs)
	// {
	// TrajectoryDuration retval = TrajectoryDuration.Trajectory_Duration_0ms;
	// retval = retval.valueOf(durationMs);

	// if (retval.value != durationMs)
	// System.out.println("ERROR Invalid trajectory duration: " + durationMs);

	// return retval;
	// }

	public synchronized void enableFollower() {
		for (GZSmartSpeedController c : mSmartControllers) {
			if (c.getMaster() != Master.MASTER) {
				switch (c.getSide()) {
				case LEFT:
					c.follow(L1);
					break;
				case RIGHT:
					c.follow(R1);
					break;
				case NO_INFO:
					System.out.println("ERROR Drive talon " + c.getGZName() + " could not enter follower mode!");
				}
				// Timer.delay(.125); //beta
			}
		}
	}

	public synchronized void zeroEncoders() {
		L1.zero();
		R1.zero();
	}

	public synchronized void zeroSensors() {
		zeroEncoders();
		zeroGyro();
	}

	public synchronized Double getPercentageComplete() {
		return mPercentageComplete;
	}

	public synchronized void zeroGyro() {
		mNavX.reset();
	}

	public int getTotalShiftCounts() {
		return 0;
		// return mShifter.getChangeCounts();
	}

	public synchronized void toggleSlowSpeed() {
		slowSpeed(!isSlow());
		System.out.println("Drivetrain speed: " + (isSlow() ? "slow" : "faster"));
	}

	public synchronized void slowSpeed(boolean isSlow) {
		mIsSlow = isSlow;
	}

	public Boolean isSlow() {
		return mIsSlow;
	}

	public String getStateString() {
		return mState.toString();
	}

	public DriveState getState() {
		return mState;
	}

	protected void initDefaultCommand() {
	}
}
