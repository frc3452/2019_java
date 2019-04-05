package frc.robot.subsystems;

import java.text.DecimalFormat;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.kauailabs.navx.frc.AHRS;

import frc.robot.Constants.kDrivetrain;
import frc.robot.GZOI;
import frc.robot.GZOI.Level;
import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZJoystick;

public class Drive extends GZSubsystem {
	// Force switch state to neutral on start up
	private DriveState mState = DriveState.OPEN_LOOP;
	private DriveState mWantedState = DriveState.NEUTRAL;
	public IO mIO = new IO();

	// DRIVETRAIN
	// public GZSRX L1, L2, L3, L4, R1, R2, R3, R4;

	private AHRS mNavX;

	private double mModifyPercent = 1;
	private boolean mIsSlow = false;
	private double mPercentageComplete = 0;
	private double mLeft_target = 0, mRight_target = 0;

	private static Drive mInstance = null;

	private double curvatureDriveQuickStopThreshold = .2;
	private double curvatureDriveQuickStopAlpha = .1;
	private double curvatureDriveQuickStopAccumulator;

	private GZFile mPIDConfigFile = null;

	public synchronized static Drive getInstance() {
		if (mInstance == null)
			mInstance = new Drive();
		return mInstance;
	}

	private Drive() {
		// L1 = new GZSRX.Builder(kDrivetrain.L1, this, "L1",
		// kPDP.DRIVE_L_1).setMaster().setSide(Side.LEFT).build();
		// L2 = new GZSRX.Builder(kDrivetrain.L2, this, "L2",
		// kPDP.DRIVE_L_2).setFollower().setSide(Side.LEFT).build();
		// L3 = new GZSRX.Builder(kDrivetrain.L3, this, "L3",
		// kPDP.DRIVE_L_3).setFollower().setSide(Side.LEFT).build();
		// L4 = new GZSRX.Builder(kDrivetrain.L4, this, "L4",
		// kPDP.DRIVE_L_4).setFollower().setSide(Side.LEFT).build();

		// R1 = new GZSRX.Builder(kDrivetrain.R1, this, "R1",
		// kPDP.DRIVE_R_1).setMaster().setSide(Side.RIGHT).build();
		// R2 = new GZSRX.Builder(kDrivetrain.R2, this, "R2",
		// kPDP.DRIVE_R_2).setFollower().setSide(Side.RIGHT).build();
		// R3 = new GZSRX.Builder(kDrivetrain.R3, this, "R3",
		// kPDP.DRIVE_R_3).setFollower().setSide(Side.RIGHT).build();
		// R4 = new GZSRX.Builder(kDrivetrain.R4, this, "R4",
		// kPDP.DRIVE_R_4).setFollower().setSide(Side.RIGHT).build();

		// mNavX = new NavX(SPI.Port.kMXP);

		try {
			mPIDConfigFile = GZFileMaker.getFile("DrivePID", new Folder(""), FileExtensions.CSV, false, false);
		} catch (Exception e) {
		}

		mNavX.reset();
	}



	public String getSmallString() {
		return "DRV";
	}

	private synchronized void out() {
		switch (mState) {
		case OPEN_LOOP_DRIVER:
			arcade(GZOI.driverJoy);
			break;
		case DEMO:
			alternateArcade(GZOI.driverJoy);
			break;
		}

		if (mState == DriveState.NEUTRAL) {
			mIO.left_output = 0;
			mIO.right_output = 0;
		} else {
			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;
		}

		// L1.set(mIO.left_output);
		// R1.set(mIO.right_output);

	}

	@Override
	public void addLoggingValues() {
	}

	public enum DriveState {
		OPEN_LOOP(false, ControlMode.PercentOutput), OPEN_LOOP_DRIVER(false, ControlMode.PercentOutput),
		DEMO(false, ControlMode.PercentOutput), NEUTRAL(false, ControlMode.Disabled);

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
		boolean neutral = false;
		neutral |= this.isSafetyDisabled();
		neutral |= mWantedState == DriveState.NEUTRAL;

		if (neutral) {
			switchToState(DriveState.NEUTRAL);
		} else {
			switchToState(mWantedState);
		}
	}

	private synchronized void onStateStart(DriveState newState) {
		switch (newState) {
		case NEUTRAL:
			break;
		case OPEN_LOOP:
			break;
		default:
			break;
		}
	}

	public synchronized void onStateExit(DriveState prevState) {
		switch (prevState) {
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

	DecimalFormat df = new DecimalFormat("#0.00");

	@Override
	public synchronized void loop() {
		// System.out.println("right " + getRearTopLimit() + "\t" +
		// getRearBottomLimit());
		// System.out.println(df.format(getLeftVelocityInchesPerSec()) + "\t" +
		// df.format(getRightVelocityInchesPerSec()));

		handleStates();
		in();
		out();
	}

	public static class IO {

		// out
		private double left_output = 0;
		public double left_desired_output = 0;

		private double right_output = 0;
		public double right_desired_output = 0;
	}

	private synchronized void in() {
		this.mModifyPercent = (mIsSlow ? .5 : 1);
	}

	/**
	 * POSITIVE --> LIFTS SIDE
	 */
	public synchronized void runClimber(double front, double rear) {
		tankNoState(-front, -rear);
	}

	public synchronized double getLeftPercent() {
		return mIO.left_output;
	}

	public synchronized double getRightPercent() {
		return mIO.right_output;
	}

	public synchronized boolean driveOutputLessThan(double percent) {
		percent = Math.abs(percent);
		return Math.abs(getLeftPercent()) < percent && Math.abs(getRightPercent()) < percent;
	}

	public synchronized void handleDriving(GZJoystick joy) {
		setWantedState(DriveState.OPEN_LOOP_DRIVER);
	}

	private synchronized void arcade(GZJoystick joy) {
		// final double move = joy.getLeftAnalogY() * elv;
		// final double rotate = elv * turnScalar * ((joy.getRightTrigger() -
		// joy.getLeftTrigger()) * .6);
		// arcadeNoState(move, rotate, !joy.getButton(Buttons.RB));
		// arcadeNoState(move, rotate, false);

		final double rotate = (joy.getRightTrigger() - joy.getLeftTrigger());
		final double move = joy.getLeftAnalogY() * getTotalModifer();
		cheesyNoState(move, rotate * 0.55, !usingCurvature());
		// 0.6 or 0.65
	}

	public boolean usingCurvature() {
		return !driveOutputLessThan(.5);
	}

	private double getTotalModifer() {
		return mModifyPercent;
	}

	private synchronized void alternateArcade(GZJoystick joy) {
		arcadeNoState(joy.getLeftAnalogY() * getTotalModifer(), (joy.getRightAnalogX() * .85) * getTotalModifer());
	}

	private synchronized void arcadeNoState(double move, double rotate) {
		arcadeNoState(move, rotate, false);
	}

	private synchronized void arcadeNoState(double move, double rotate, boolean squaredInputs) {
		double[] temp = arcadeToLR(move, rotate, squaredInputs);

		mIO.left_desired_output = temp[0];
		mIO.right_desired_output = temp[1];
	}

	private synchronized void cheesyNoState(double move, double rotate, boolean quickTurn) {
		double[] temp = cheesyToLR(move, rotate, quickTurn);

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
		xSpeed = GZUtil.limit1to1(xSpeed);
		xSpeed = GZUtil.applyDeadband(xSpeed, kDrivetrain.DIFFERENTIAL_DRIVE_DEADBAND);

		zRotation = GZUtil.limit1to1(zRotation);
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
		retval[0] = GZUtil.limit1to1(leftMotorOutput);
		retval[1] = -GZUtil.limit1to1(rightMotorOutput);

		return retval;
	}

	public double[] cheesyToLR(double xSpeed, double zRotation, boolean isQuickTurn) {
		xSpeed = GZUtil.limit1to1(xSpeed);
		// xSpeed = applyDeadband(xSpeed, m_deadband);

		zRotation = GZUtil.limit1to1(zRotation);
		// zRotation = applyDeadband(zRotation, m_deadband);

		double angularPower;
		boolean overPower;

		if (isQuickTurn) {
			if (Math.abs(xSpeed) < curvatureDriveQuickStopThreshold) {
				curvatureDriveQuickStopAccumulator = (1 - curvatureDriveQuickStopAlpha)
						* curvatureDriveQuickStopAccumulator
						+ curvatureDriveQuickStopAlpha * GZUtil.limit1to1(zRotation) * 2;
			}
			overPower = true;
			angularPower = zRotation;
		} else {
			overPower = false;
			angularPower = Math.abs(xSpeed) * zRotation - curvatureDriveQuickStopAccumulator;

			if (curvatureDriveQuickStopAccumulator > 1) {
				curvatureDriveQuickStopAccumulator -= 1;
			} else if (curvatureDriveQuickStopAccumulator < -1) {
				curvatureDriveQuickStopAccumulator += 1;
			} else {
				curvatureDriveQuickStopAccumulator = 0.0;
			}
		}

		double leftMotorOutput = xSpeed + angularPower;
		double rightMotorOutput = xSpeed - angularPower;

		// If rotation is overpowered, reduce both outputs to within acceptable range
		if (overPower) {
			if (leftMotorOutput > 1.0) {
				rightMotorOutput -= leftMotorOutput - 1.0;
				leftMotorOutput = 1.0;
			} else if (rightMotorOutput > 1.0) {
				leftMotorOutput -= rightMotorOutput - 1.0;
				rightMotorOutput = 1.0;
			} else if (leftMotorOutput < -1.0) {
				rightMotorOutput -= leftMotorOutput + 1.0;
				leftMotorOutput = -1.0;
			} else if (rightMotorOutput < -1.0) {
				leftMotorOutput -= rightMotorOutput + 1.0;
				rightMotorOutput = -1.0;
			}
		}

		// Normalize the wheel speeds
		double maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput));
		if (maxMagnitude > 1.0) {
			leftMotorOutput /= maxMagnitude;
			rightMotorOutput /= maxMagnitude;
		}

		double temp[] = { 0, 0 };
		temp[0] = leftMotorOutput;
		temp[1] = (rightMotorOutput * -1);
		return temp;
	}

	public synchronized void tank(double left, double right) {
		if (setWantedState(DriveState.OPEN_LOOP)) {
			tank(left * mModifyPercent, right * mModifyPercent);
		}
	}

	private synchronized void tankNoState(double left, double right) {
		// System.out.println(df.format(left) + "\t" + df.format(right));
		mIO.left_desired_output = left;
		mIO.right_desired_output = right;
	}

	public synchronized void tank(GZJoystick joy) {
		tank(joy.getLeftAnalogY(), joy.getRightAnalogY());
	}

	public synchronized Double getPercentageComplete() {
		return mPercentageComplete;
	}

	public synchronized void zeroGyro() {
		mNavX.reset();
	}

	public synchronized void toggleSlowSpeed() {
		slowSpeed(!isSlow());
	}

	public synchronized void slowSpeed(boolean isSlow) {
		if (mIsSlow != isSlow) {
			mIsSlow = isSlow;
			System.out.println("Drivetrain speed: " + (isSlow() ? "slow" : "faster"));
			GZOI.getInstance().addRumble(isSlow() ? Level.LOW : Level.MEDIUM);
		}

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

	public enum ClimbingState {
		FRONT, BOTH, NONE, MOVING, REAR
	}

	protected void initDefaultCommand() {
	}
}
