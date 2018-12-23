package frc.robot.subsystems;

import java.util.Arrays;

import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.GZSpark;
import frc.robot.util.GZSubsystem;

public class Intake extends GZSubsystem {

	private static GZSpark left_intake, right_intake;

	// Force switch state to neutral on start up
	private IntakeState mState = IntakeState.MANUAL;
	private IntakeState mWantedState = IntakeState.NEUTRAL;

	public IO mIO = new IO();

	private static Intake mInstance = null;

	public synchronized static Intake getInstance() {
		if (mInstance == null)
			mInstance = new Intake();

		return mInstance;
	}

	public boolean hasMotors() {
		return true;
	}

	public void addPDPTestingMotors() {
	}

	public void addMotorTestingGroups() {
		AmperageChecker.CheckerConfig checkerConfig = new AmperageChecker.CheckerConfig(0, .5, 5, 2, 1, true);
		AmperageChecker.getInstance().addTalonGroup(new AmperageChecker.MotorTestingGroup(this, "Intake Wheels",
				Arrays.asList(left_intake, right_intake), checkerConfig));
	}

	public void addLoggingValues() {
		new LogItem("INTAKE-L-AMP") {
			@Override
			public String val() {
				return Intake.getInstance().mIO.left_amperage.toString();
			}
		};

		new LogItem("INTAKE-L-AMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("INTAKE-R-AMP") {
			@Override
			public String val() {
				return Intake.getInstance().mIO.right_amperage.toString();
			}
		};

		new LogItem("INTAKE-R-AMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("INTAKE-L-PRCNT") {
			@Override
			public String val() {
				return Intake.getInstance().mIO.left_desired_output.toString();
			}
		};

		new LogItem("INTAKE-R-PRCNT") {
			@Override
			public String val() {
				return Intake.getInstance().mIO.right_desired_output.toString();
			}
		};
	}

	private Intake() {
		left_intake = new GZSpark.Builder(kIntake.INTAKE_L, this, kPDP.INTAKE_L, "Left").build();
		right_intake = new GZSpark.Builder(kIntake.INTAKE_R, this, kPDP.INTAKE_R, "Right").build();

		left_intake.setInverted(kIntake.INTAKE_L_INVERT);
		right_intake.setInverted(kIntake.INTAKE_R_INVERT);

		left_intake.setSubsystem(Intake.class.getName());
		right_intake.setSubsystem(Intake.class.getName());

		left_intake.setName("left_intake");
		right_intake.setName("right_intake");
	}

	private synchronized void onStateStart(IntakeState wantedState) {
		switch (wantedState) {
		case MANUAL:
			break;
		case NEUTRAL:
			break;
		default:
			break;
		}
	}

	private synchronized void onStateExit(IntakeState prevState) {
		switch (prevState) {
		case MANUAL:
			break;
		case NEUTRAL:
			break;
		default:
			break;

		}
	}

	public synchronized void loop() {
		outputSmartDashboard();
		in();
		handleStates();
		out();
	}

	private void switchToState(IntakeState s) {
		if (mState != s) {
			onStateExit(mState);
			mState = s;
			onStateStart(mState);
		}
	}

	private void handleStates() {
		boolean neutral = false;

		neutral |= this.isSafetyDisabled() && !GZOI.getInstance().isFMS();
		neutral |= mWantedState == IntakeState.NEUTRAL;

		// we dont need to worry about isDemo() here
		// Dont allow disable on the field
		if (neutral) {

			switchToState(IntakeState.NEUTRAL);

		} else {
			switchToState(mWantedState);
		}
	}

	public class IO {
		// in
		public Double left_amperage = Double.NaN, right_amperage = Double.NaN;

		// out
		private double left_output = 0;
		public Double left_desired_output = 0.0;

		private double right_output = 0;
		public Double right_desired_output = 0.0;
	}

	@Override
	public synchronized void in() {
		mIO.left_amperage = left_intake.getAmperage();
		mIO.right_amperage = right_intake.getAmperage();
	}

	public void manual(double percentage) {
		setWantedState(IntakeState.MANUAL);
		mIO.left_desired_output = mIO.right_desired_output = percentage;
	}

	private void setWantedState(IntakeState wantedState) {
		this.mWantedState = wantedState;
	}

	public void spin(boolean clockwise) {
		setWantedState(IntakeState.MANUAL);
		mIO.left_desired_output = kIntake.Speeds.SPIN * (clockwise ? -1 : 1);
		mIO.right_desired_output = kIntake.Speeds.SPIN * (clockwise ? 1 : -1);
	}

	@Override
	public synchronized void out() {
		switch (mState) {
		case MANUAL:

			mIO.left_output = mIO.left_desired_output;
			mIO.right_output = mIO.right_desired_output;

			break;

		case NEUTRAL:

			mIO.left_output = 0.0;
			mIO.right_output = 0.0;

			break;
		default:
			System.out.println("WARNING: Incorrect intake state " + mState + " reached.");
			break;
		}

		left_intake.set(mIO.left_output);
		right_intake.set(mIO.right_output);
	}

	public enum IntakeState {
		NEUTRAL, MANUAL,
	}

	@Override
	public synchronized void stop() {
		setWantedState(IntakeState.NEUTRAL);
	}

	public synchronized boolean stateNot(IntakeState state) {
		return mState != state;
	}

	public String getStateString() {
		return mState.toString();
	}

	public IntakeState getState() {
		return mState;
	}

	@Override
	protected void initDefaultCommand() {
	}

}
