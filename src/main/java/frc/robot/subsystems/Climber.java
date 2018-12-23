package frc.robot.subsystems;

import frc.robot.Constants.kClimber;
import frc.robot.Constants.kPDP;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.GZOI;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSpark;
import frc.robot.util.GZSubsystem;

public class Climber extends GZSubsystem {

	private GZSpark climber_1;

	private ClimberState mState = ClimberState.MANUAL;
	private ClimberState mWantedState = ClimberState.NEUTRAL;
	public IO mIO = new IO();

	private PowerDistributionPanel pdp = GZPDP.getInstance().getPDP();

	private int climbCounter = 0;

	private static Climber mInstance = null;

	public synchronized static Climber getInstance() {
		if (mInstance == null)
			mInstance = new Climber();

		return mInstance;
	}

	// Construction
	private Climber() {
		climber_1 = new GZSpark.Builder(kClimber.CLIMBER_1, this, kPDP.CLIMBER_1, "C1").build();
		climber_1.setInverted(kClimber.CLIMBER_1_INVERT);

		climber_1.setSubsystem(Climber.class.getName());
		climber_1.setName("climber_1");
	}

	public void addPDPTestingMotors(){}

	private synchronized void onStateStart(ClimberState wantedState) {
		switch (wantedState) {
		case MANUAL:
			break;
		case NEUTRAL:
			break;
		default:
			break;
		}
	}

	private synchronized void onStateExit(ClimberState prevState) {
		switch (prevState) {
		case MANUAL:
			break;
		case NEUTRAL:
			break;
		default:
			break;
		}
	}

	@Override
	public synchronized void loop() {
		handleStates();
		in();
		out();
	}

	public void addLoggingValues() {
		new LogItem("CLIMBER-1-AMP") {
			@Override
			public String val() {
				return Climber.getInstance().mIO.climber_1_amperage.toString();
			}
		};

		new LogItem("CLIMBER-1-AMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("CLIMBER-2-AMP") {
			@Override
			public String val() {
				return Climber.getInstance().mIO.climber_2_amperage.toString();
			}
		};

		new LogItem("CLIMBER-2-AMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("CLIMBER-PRCNT") {

			@Override
			public String val() {
				return Climber.getInstance().mIO.climber_desired_output.toString();
			}
		};

	}

	public class IO {
		// in
		public Double climber_1_amperage = Double.NaN;
		public Double climber_2_amperage = Double.NaN;

		// out
		private double climber_output = 0;
		public Double climber_desired_output = 0.0;
	}

	public void runClimber(double percentage) {
		runClimber(percentage, 3);
	}

	public void runClimber(double percentage, int greaterThan) {
		if (climbCounter > greaterThan)
			manual(percentage);
		else
			stop();
	}

	private void manual(double percentage) {
		setWantedState(ClimberState.MANUAL);
		mIO.climber_desired_output = percentage;
	}

	@Override
	protected synchronized void in() {
		mIO.climber_1_amperage = climber_1.getAmperage();
		mIO.climber_2_amperage = pdp.getCurrent(kPDP.CLIMBER_2);
	}

	@Override
	protected synchronized void out() {

		switch (mState) {

		case MANUAL:
			mIO.climber_output = mIO.climber_desired_output;
			break;

		case NEUTRAL:
			mIO.climber_output = 0;
			break;

		default:
			System.out.println("WARNING: Incorrect climber state " + mState + " reached.");
			break;
		}
		climber_1.set(Math.abs(mIO.climber_output));
	}

	public void addMotorTestingGroups() {
		// MotorChecker.CheckerConfig checkerConfig = new MotorChecker.CheckerConfig(0, 1, 3, 2, .5, true);
		// MotorChecker.getInstance()
		// 		.addTalonGroup(new MotorTestingGroup(this, "Left", Arrays.asList(L1, L2, L3, L4), checkerConfig));
	}

	public boolean hasMotors()
	{
		return true;
	}

	public enum ClimberState {
		NEUTRAL, MANUAL;
	}

	@Override
	public synchronized void stop() {
		setWantedState(ClimberState.NEUTRAL);
	}

	public synchronized void setWantedState(ClimberState wantedState) {
		this.mWantedState = wantedState;
	}

	private void switchToState(ClimberState s) {
		if (mState != s) {
			onStateExit(mState);
			mState = s;
			onStateStart(mState);
		}
	}

	private synchronized void handleStates() {
		boolean neutral = false;
		neutral |= (this.isSafetyDisabled() || Auton.getInstance().isDemo()) && !GZOI.getInstance().isFMS();
		neutral |= mWantedState == ClimberState.NEUTRAL;

		// if trying to disable or run demo mode while not connected to field
		if (neutral) {

			switchToState(ClimberState.NEUTRAL);

		} else {
			switchToState(mWantedState);
		}
	}

	public String getStateString() {
		return mState.toString();
	}

	public ClimberState getState() {
		return mState;
	}

	@Override
	protected void initDefaultCommand() {
	}

	public int getClimbCounter() {
		return climbCounter;
	}

	public void addClimberCounter() {
		this.climbCounter += 1;
	}

}
