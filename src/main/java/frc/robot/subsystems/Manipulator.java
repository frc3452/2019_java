package frc.robot.subsystems;

import frc.robot.util.GZSubsystem;

public class Manipulator extends GZSubsystem {

    private ExampleState mState = ExampleState.MANUAL;
	private ExampleState mWantedState = ExampleState.NEUTRAL;

    private static Manipulator mInstance = null;

    public static Manipulator getInstance() {
        if (mInstance == null)
            mInstance = new Manipulator();

        return mInstance;
    }

    private Manipulator() {

    }

    @Override
	public void stop() {
		setWantedState(ExampleState.NEUTRAL);
	}

    @Override
    public boolean hasMotors() {
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

    }

    private void switchToState(ExampleState s) {
		if (mState != s) {
			onStateExit(mState);
			mState = s;
			onStateStart(mState);
		}
    }
    
    private void onStateStart(ExampleState s) {
		switch (s) {
		case MANUAL:
			break;
		case NEUTRAL:
			break;
		default:
			break;
		}
	}

	private void onStateExit(ExampleState s) {
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
    }

    public synchronized void enableFollower() {
		// controller_2.follow(controller_1);
	}

    @Override
    protected void initDefaultCommand() {

    }

    public ExampleState getState() {
        return mState;
    }

    @Override
	public String getStateString() {
		return mState.toString();
	}

    public boolean setWantedState(ExampleState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    public enum ExampleState {
        NEUTRAL(false), MANUAL(false), DEMO(false), MOTION_PROFILE(true);

        public final boolean usesClosedLoop;

        private ExampleState(boolean closed) {
            this.usesClosedLoop = closed;
        }
    }

}