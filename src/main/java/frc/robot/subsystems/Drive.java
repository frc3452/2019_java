package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZFiles;
import frc.robot.util.GZJoystick;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZPID;
import frc.robot.util.GZSRX;
import frc.robot.util.GZSRX.Breaker;
import frc.robot.util.GZSRX.Master;
import frc.robot.util.GZSRX.Side;
import frc.robot.util.GZSpeedController;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.MotorChecker.AmperageChecker.MotorTestingGroup;
import frc.robot.util.MotorChecker.PDPChannelChecker;
import frc.robot.util.Units;
import frc.robot.util.AHRS;

public class Drive extends GZSubsystem {

	// Force switch state to neutral on start up
	private DriveState mState = DriveState.OPEN_LOOP;
	private DriveState mWantedState = DriveState.NEUTRAL;
	public IO mIO = new IO();

	// PDP
	private PowerDistributionPanel pdp = GZPDP.getInstance().getPDP();

	// DRIVETRAIN
	private GZSRX L1, L2, L3, L4, R1, R2, R3, R4;

	// GYRO
	private AHRS mGyro;

	private double mModifyPercent = 1;
	private boolean mIsSlow = false;
	private double mPercentageComplete = 0;
	private double mLeft_target = 0, mRight_target = 0;

	private static Drive mInstance = null;

	public synchronized static Drive getInstance() {
		if (mInstance == null)
			mInstance = new Drive();
		return mInstance;
	}

	private Drive() {
		L1 = new GZSRX.Builder(kDrivetrain.L1, this, "L1", kPDP.DRIVE_L_1).setMaster().setSide(Side.LEFT)
				.overrideBreaker(Breaker.AMP_30).build();
		L2 = new GZSRX.Builder(kDrivetrain.L2, this, "L2", kPDP.DRIVE_L_2).setFollower().setSide(Side.LEFT)
				.overrideBreaker(Breaker.AMP_30).build();
		L3 = new GZSRX.Builder(kDrivetrain.L3, this, "L3", kPDP.DRIVE_L_3).setFollower().setSide(Side.LEFT)
				.overrideBreaker(Breaker.AMP_30).build();
		L4 = new GZSRX.Builder(kDrivetrain.L4, this, "L4", kPDP.DRIVE_L_4).setFollower().setSide(Side.LEFT)
				.overrideBreaker(Breaker.AMP_30).build();

		R1 = new GZSRX.Builder(kDrivetrain.R1, this, "R1", kPDP.DRIVE_R_1).setMaster().setSide(Side.RIGHT)
				.overrideBreaker(Breaker.AMP_30).build();
		R2 = new GZSRX.Builder(kDrivetrain.R2, this, "R2", kPDP.DRIVE_R_2).setFollower().setSide(Side.RIGHT)
				.overrideBreaker(Breaker.AMP_30).build();
		R3 = new GZSRX.Builder(kDrivetrain.R3, this, "R3", kPDP.DRIVE_R_3).setFollower().setSide(Side.RIGHT)
				.overrideBreaker(Breaker.AMP_30).build();
		R4 = new GZSRX.Builder(kDrivetrain.R4, this, "R4", kPDP.DRIVE_R_4).setFollower().setSide(Side.RIGHT)
				.overrideBreaker(Breaker.AMP_30).build();

		mGyro = new AHRS(SPI.Port.kMXP);

		brake(NeutralMode.Coast);

		talonInit();
		enableFollower();

		pdp.setSubsystem("Drive train");

		L1.setName("L1");
		L2.setName("L2");
		L3.setName("L3");
		L4.setName("L4");
		R1.setName("R1");
		R2.setName("R2");
		R3.setName("R3");
		R4.setName("R4");

		mGyro.reset();

		checkFirmware();
	}

	public void addMotorTestingGroups() {
		AmperageChecker.CheckerConfig checkerConfig = new AmperageChecker.CheckerConfig(0, 5, 1, 1, .25, true);
		AmperageChecker.getInstance()
				.addTalonGroup(new MotorTestingGroup(this, "Left", Arrays.asList(L1, L2, L3, L4), checkerConfig));
		AmperageChecker.getInstance()
				.addTalonGroup(new MotorTestingGroup(this, "Right", Arrays.asList(R1, R2, R3, R4), checkerConfig));

	}

	public boolean hasMotors() {
		return true;
	}

	public void addPDPTestingMotors() {
		PDPChannelChecker.CheckerConfig config = new PDPChannelChecker.CheckerConfig(this, .5, .125);
		ArrayList<GZSpeedController> controllers = new ArrayList<GZSpeedController>();
		for (GZSRX s : mTalons.values())
			controllers.add(s);

		PDPChannelChecker.getInstance().addGroup(new PDPChannelChecker.CheckerMotorGroup(controllers, config));
	}

	@Override
	protected synchronized void out() {
		switch (mState) {
		case MOTION_MAGIC:

			mIO.control_mode = ControlMode.MotionMagic;
			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;

			break;
		case MOTION_PROFILE:

			mIO.control_mode = ControlMode.MotionProfile;
			mIO.left_desired_output = mIO.right_desired_output = SetValueMotionProfile.Enable.value;

			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;

			break;
		case NEUTRAL:

			mIO.control_mode = ControlMode.Disabled;
			mIO.left_output = 0;
			mIO.right_output = 0;

			break;
		case OPEN_LOOP:

			mIO.control_mode = ControlMode.PercentOutput;
			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;

			break;
		case OPEN_LOOP_DRIVER:

			arcade(GZOI.driverJoy);
			mIO.control_mode = ControlMode.PercentOutput;
			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;

			break;
		case DEMO:

			alternateArcade(GZOI.driverJoy);
			mIO.control_mode = ControlMode.PercentOutput;
			mIO.left_output = mIO.left_desired_output * kDrivetrain.DEMO_DRIVE_MODIFIER;
			mIO.right_output = mIO.right_desired_output * kDrivetrain.DEMO_DRIVE_MODIFIER;

			break;

		default:
			System.out.println("WARNING: Incorrect drive state " + mState + " reached.");
			break;
		}

		L1.set(mIO.control_mode, mIO.left_output);
		R1.set(mIO.control_mode, mIO.right_output);
	}

	@Override
	public void addLoggingValues() {
		new LogItem("L-RPM") {
			@Override
			public String val() {
				return Drive.getInstance().getLeftVel().toString();
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
				return Drive.getInstance().getRightVel().toString();
			}
		};

		new LogItem("R-ENC-PRSNT") {
			@Override
			public String val() {
				return Drive.getInstance().mIO.rightEncoderValid.toString();
			}
		};

		for (GZSRX c : mTalons.values()) {
			new LogItem("DRV-" + c.getGZName() + "-AMP") {
				public String val() {
					return mIO.getMapValue(mIO.amperages, c.getID()).toString();
				}
			};

			new LogItem("DRV-" + c.getGZName() + "-AMP-AVG", true) {
				public String val() {
					return LogItem.Average_Left_Formula;
				}
			};

			// Temperature sensors
			if (c.hasTemperatureSensor()) {
				new LogItem("DRV-" + c.getGZName() + "-TEMP") {
					@Override
					public String val() {
						return String.valueOf(c.getTemperatureSensor());
					}
				};

				new LogItem("DRV-" + c.getGZName() + "-TEMP-AVG") {
					@Override
					public String val() {
						return String.valueOf(c.getTemperatureSensor());
					}
				};
			}

		}

		// VOLTAGE
		for (GZSRX c : mTalons.values()) {
			new LogItem("DRV-" + c.getGZName() + "-VOLT") {
				public String val() {
					return mIO.getMapValue(mIO.voltages, c.getID()).toString();
				}
			};
		}

	}

	public enum DriveState {
		OPEN_LOOP(false), OPEN_LOOP_DRIVER(false), DEMO(false), NEUTRAL(false), MOTION_MAGIC(true),
		MOTION_PROFILE(true);

		private final boolean usesClosedLoop;

		DriveState(final boolean s) {
			usesClosedLoop = s;
		}
	}

	@Override
	public synchronized void stop() {
		setWantedState(DriveState.NEUTRAL);
	}

	public synchronized void setWantedState(DriveState wantedState) {
		this.mWantedState = wantedState;
	}

	private synchronized void switchToState(DriveState state) {
		if (mState != state) {
			onStateExit(mState);
			mState = state;
			onStateStart(mState);
		}
	}

	private synchronized void handleStates() {
		GZOI gzOI = GZOI.getInstance();

		boolean neutral = false;
		neutral |= this.isSafetyDisabled() && !gzOI.isFMS();
		neutral |= mWantedState == DriveState.NEUTRAL;
		neutral |= ((mState.usesClosedLoop || mWantedState.usesClosedLoop) && !mIO.encodersValid);

		if (neutral) {

			switchToState(DriveState.NEUTRAL);

		} else if (Auton.getInstance().isDemo() && !gzOI.isFMS()) {

			switchToState(DriveState.DEMO);

		} else {
			switchToState(mWantedState);
		}
	}

	private void talonInit() {
		for (GZSRX s : mTalons.values()) {
			String name = s.getGZName();

			GZSRX.logError(s.configFactoryDefault(GZSRX.TIMEOUT), this, AlertLevel.ERROR,
					"Could not factory reset Talon " + name);

			s.setInverted((s.getSide() == Side.LEFT) ? kDrivetrain.L_INVERT : kDrivetrain.R_INVERT);

			// s.enableVoltageCompensation(true);

			// CURRENT LIMIT
			GZSRX.logError(s.configContinuousCurrentLimit(
					s.getBreakerSize() == Breaker.AMP_40 ? kDrivetrain.AMP_40_LIMIT : kDrivetrain.AMP_30_LIMIT,
					GZSRX.TIMEOUT), this, AlertLevel.WARNING, "Could not set current limit for Talon " + name);

			GZSRX.logError(
					s.configPeakCurrentLimit(s.getBreakerSize() == Breaker.AMP_40 ? kDrivetrain.AMP_40_TRIGGER
							: kDrivetrain.AMP_30_TRIGGER, GZSRX.TIMEOUT),
					this, AlertLevel.WARNING, "Could not set current limit trigger for Talon " + name);

			GZSRX.logError(
					s.configPeakCurrentDuration(
							s.getBreakerSize() == Breaker.AMP_40 ? kDrivetrain.AMP_40_TIME : kDrivetrain.AMP_30_TIME,
							GZSRX.TIMEOUT),
					this, AlertLevel.WARNING, "Could not set current limit time for Talon " + name);

			s.enableCurrentLimit(true);

			GZSRX.logError(s.configOpenloopRamp(kDrivetrain.OPEN_LOOP_RAMP_TIME, GZSRX.TIMEOUT), this,
					AlertLevel.WARNING, "Could not set open loop ramp time for Talon " + name);

			GZSRX.logError(s.configNeutralDeadband(0.05, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
					"Could not set Neutral Deadband for Talon " + name);

			s.setSubsystem("Drive train");

			if (s.getMaster() == Master.MASTER) {

				GZSRX.logError(
						s.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, GZSRX.TIMEOUT), this,
						AlertLevel.WARNING, "Could not setup " + s.getSide() + " encoder");

				GZSRX.logError(s.setSelectedSensorPosition(0, 0, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
						"Could not zero " + s.getSide() + " encoder");

				if (!s.isEncoderValid())
					Health.getInstance().addAlert(this, AlertLevel.ERROR, s.getSide() + " encoder not found");

				s.setSensorPhase(true);

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
		for (GZSRX s : mTalons.values())
			s.checkFirmware();
	}

	private synchronized void onStateStart(DriveState newState) {
		switch (newState) {
		case MOTION_MAGIC:
			brake(NeutralMode.Brake);
			break;
		case MOTION_PROFILE:
			brake(NeutralMode.Brake);
			break;
		case NEUTRAL:
			brake(GZOI.getInstance().wasTele() || GZOI.getInstance().wasAuto() ? NeutralMode.Brake : NeutralMode.Coast);
			break;
		case OPEN_LOOP:
			brake(NeutralMode.Brake);
			break;
		case OPEN_LOOP_DRIVER:
			brake(NeutralMode.Coast);
			break;
		case DEMO:
			brake(NeutralMode.Coast);
			break;
		default:
			break;
		}
	}

	public synchronized void onStateExit(DriveState prevState) {
		switch (prevState) {
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
		// System.out.println(getLeftRotations() + "\t" + getRightRotations());
		outputSmartDashboard();
		handleStates();
		in();
		out();
	}

	public static class IO {
		// in
		public Map<Integer, Double> amperages = new HashMap<>();
		public Map<Integer, Double> voltages = new HashMap<>();

		public IO() {
			left_encoder_total_delta_rotations = 0;
			right_encoder_total_delta_rotations = 0;
		}

		public void updateMapValue(Map<Integer, Double> map, int id, double value) {
			if (!map.containsKey(id))
				map.put(id, Double.NaN);
			map.replace(id, value);
		}

		public Double getMapValue(Map<Integer, Double> map, int id) {
			if (!map.containsKey(id))
				return Double.NaN;

			return map.get(id);
		}

		public Double left_encoder_ticks = Double.NaN, left_encoder_vel = Double.NaN;

		public double left_encoder_total_delta_rotations = 0, right_encoder_total_delta_rotations = 0;

		public Double right_encoder_ticks = Double.NaN, right_encoder_vel = Double.NaN;

		public Boolean leftEncoderValid = false;
		public Boolean rightEncoderValid = false;
		public Boolean encodersValid = false;

		// out
		private double left_output = 0;
		public double left_desired_output = 0;

		private double right_output = 0;
		public double right_desired_output = 0;
		ControlMode control_mode = ControlMode.PercentOutput;
	}

	public Double getLeftRotations() {
		return Units.ticks_to_rotations(mIO.left_encoder_ticks);
	}

	public Double getLeftVel() {
		return Units.ticks_to_rotations(mIO.left_encoder_vel);
	}

	public Double getRightRotations() {
		return -Units.ticks_to_rotations(mIO.right_encoder_ticks);
	}

	public Double getRightVel() {
		return -Units.ticks_to_rotations(mIO.right_encoder_vel);

	}

	@Override
	protected synchronized void in() {
		this.mModifyPercent = (mIsSlow ? .5 : 1);

		mIO.leftEncoderValid = L1.isEncoderValid();
		mIO.rightEncoderValid = R1.isEncoderValid();

		mIO.encodersValid = mIO.leftEncoderValid && mIO.rightEncoderValid;

		if (mIO.leftEncoderValid) {
			mIO.left_encoder_ticks = (double) L1.getSelectedSensorPosition(0);
			mIO.left_encoder_vel = (double) L1.getSelectedSensorVelocity(0);
		} else {
			mIO.left_encoder_ticks = Double.NaN;
			mIO.left_encoder_vel = Double.NaN;
		}

		if (mIO.rightEncoderValid) {
			mIO.right_encoder_ticks = (double) R1.getSelectedSensorPosition(0);
			mIO.right_encoder_vel = (double) R1.getSelectedSensorVelocity(0);
		} else {
			mIO.right_encoder_ticks = Double.NaN;
			mIO.right_encoder_vel = Double.NaN;
		}

		for (GZSRX c : mTalons.values()) {
			mIO.updateMapValue(mIO.amperages, c.getID(), c.getOutputCurrent());
			mIO.updateMapValue(mIO.voltages, c.getID(), c.getMotorOutputVoltage());
		}

		mIO.left_encoder_total_delta_rotations = L1.getTotalEncoderRotations(getLeftRotations());
		mIO.right_encoder_total_delta_rotations = R1.getTotalEncoderRotations(getRightRotations());
	}

	@Override
	public void outputSmartDashboard() {
		SmartDashboard.putNumber("NavX Angle", mGyro.getAngle());

		SmartDashboard.putNumber("L1", getLeftRotations());
		SmartDashboard.putNumber("R1", getRightRotations());

		SmartDashboard.putNumber("L1 Vel", mIO.left_encoder_vel);
		SmartDashboard.putNumber("R1 Vel", mIO.right_encoder_vel);

		SmartDashboard.putNumber("PercentageCompleted", getPercentageComplete());
	}

	// called in OPEN_LOOP_DRIVER state
	private synchronized void arcade(GZJoystick joy) {
		double turnScalar;
		if (Elevator.getInstance().isLimiting())
			turnScalar = Constants.kDrivetrain.ELEV_TURN_SCALAR;
		else
			turnScalar = 1;

		double elv = Elevator.getInstance().getPercentageModify();

		arcadeNoState(joy.getLeftAnalogY() * elv,
				elv * turnScalar * ((joy.getRightTrigger() - joy.getLeftTrigger()) * .65));
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
		setWantedState(DriveState.OPEN_LOOP);
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

	public synchronized void tank(double left, double right) {
		setWantedState(DriveState.OPEN_LOOP);
		mIO.left_desired_output = left * Elevator.getInstance().getPercentageModify() * mModifyPercent;
		mIO.right_desired_output = right * Elevator.getInstance().getPercentageModify() * mModifyPercent;
	}

	public synchronized void tank(GZJoystick joy) {
		tank(joy.getLeftAnalogY(), joy.getRightAnalogY());
	}

	private synchronized void brake(NeutralMode mode) {
		for (GZSRX c : mTalons.values())
			c.setNeutralMode(mode);
	}

	public synchronized void motionMagic(double leftRotations, double rightRotations, double leftAccel,
			double rightAccel, double leftSpeed, double rightSpeed) {

		setWantedState(DriveState.MOTION_MAGIC);

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

	public synchronized void getMotionProfileStatus(Side side, MotionProfileStatus statusToFill) {
		if (side == Side.LEFT)
			L1.getMotionProfileStatus(statusToFill);
		else if (side == Side.RIGHT)
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
	// private synchronized TrajectoryDuration GetTrajectoryDuration(int durationMs) {
	// 	TrajectoryDuration retval = TrajectoryDuration.Trajectory_Duration_0ms;
	// 	retval = retval.valueOf(durationMs);

	// 	if (retval.value != durationMs)
	// 		System.out.println("ERROR Invalid trajectory duration: " + durationMs);

	// 	return retval;
	// }

	public synchronized void enableFollower() {
		for (GZSRX c : mTalons.values()) {
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
		}
	}

	public synchronized void zeroEncoders() {
		L1.setSelectedSensorPosition(0, 0, 10);
		R1.setSelectedSensorPosition(0, 0, 10);
	}

	public synchronized void zeroSensors() {
		zeroEncoders();
		zeroGyro();
	}

	public synchronized Double getPercentageComplete() {
		return mPercentageComplete;
	}

	public synchronized void zeroGyro() {
		mGyro.reset();
	}

	public synchronized void slowSpeed(boolean isSlow) {
		mIsSlow = isSlow;
	}

	public Boolean isSlow() {
		return mIsSlow;
	}

	public synchronized Double getGyroAngle() {
		return mGyro.getAngle();
	}

	public synchronized double getGyroFusedHeading() {
		return mGyro.getFusedHeading();
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
