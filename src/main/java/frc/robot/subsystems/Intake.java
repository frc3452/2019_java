package frc.robot.subsystems;

import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;


public class Intake extends GZSubsystem{

    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState= IntakeState.NEUTRAL;
    public IO mIO = new IO();

    private static Intake mInstance = null;

    public static Intake getInstance() {
        if (mInstance == null)
            mInstance = new Intake();

        return mInstance;
    }

    public enum IntakeState {
		NEUTRAL(false), MANUAL(false);

		public final boolean usesClosedLoop;

		private IntakeState(boolean closed) {
			this.usesClosedLoop = closed;
		}
    }

    public boolean setWantedState(IntakeState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }
    
    @Override
    public void stop() {
        setWantedState(IntakeState.NEUTRAL);
    }

    @Override
    public String getSmallString() {
        return "INTK";
    }

    @Override
    public boolean hasMotors() {
        return true;
    }

    @Override
    public boolean hasAir() {
        return true;
    }

    @Override
    public void addLoggingValues() {
    }

    @Override
    public void loop() {
        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;
        if (mWantedState == IntakeState.NEUTRAL) {
            neutral = true;
        }

        else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
        }

        else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
            neutral = true;
        }

        if (neutral) {

            switchToState(IntakeState.NEUTRAL);

        } else {
            switchToState(mWantedState);
        }
    }

    public class IO {
        // In
        public Double ticks_velocity = Double.NaN;
        public Double ticks_position = Double.NaN;

        public Boolean encoders_valid = false;

        // out
        private double output = 0;
        public Double desired_output = 0.0;
    }

    private void switchToState(IntakeState s) {
        if (mState != s) {
            onStateExit(mState);
            mState = s;
            onStateStart(mState);
        }
    }

    private void onStateStart(IntakeState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }    

    private void onStateExit(IntakeState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }

    private void in() {

    }

    private void out() {
        if (mState != IntakeState.NEUTRAL) {
            mIO.output = mIO.desired_output;
        } else {
            mIO.output = 0;
        }
    }

    public IntakeState getState() {
        return mState;
    }

    @Override
    public String getStateString() {
        return mState.toString();
    }

    @Override
    protected void initDefaultCommand() {
    }
}