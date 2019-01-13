package frc.robot.subsystems;

import frc.robot.Constants.kClimber;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.motorcontrollers.dumbcontrollers.GZSpark;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public class Climber extends GZSubsystem {

    private ClimberState mState = ClimberState.MANUAL;
    private ClimberState mWantedState = ClimberState.NEUTRAL;
    public IO mIO = new IO();

    private GZSpark mClimberFront, mClimberBack;
    private GZSolenoid mRampDrop;

    private static Climber mInstance = null;

    public static Climber getInstance() {
        if (mInstance == null)
            mInstance = new Climber();

        return mInstance;
    }

    private Climber() {
        mClimberFront = new GZSpark.Builder(kClimber.FRONT_MOTOR_ID, this, "Climber Front", kPDP.CLIMBER_FRONT).build();
        mClimberBack = new GZSpark.Builder(kClimber.BACK_MOTOR_ID, this, "Climber Back", kPDP.CLIMBER_FRONT).build();
        mRampDrop = new GZSolenoid(kClimber.SOLENOID_RAMP_DROP, this, "Ramp Drop");

        mClimberFront.setInverted(kClimber.CLIMBER_FRONT_INVERT);
        mClimberFront.setInverted(kClimber.CLIMBER_BACK_INVERT);

    }

    @Override
    public void stop() {
        setWantedState(ClimberState.NEUTRAL);
    }

    public void rampsDrop() {
        mRampDrop.set(true);
    };

    public void runClimber(double Frontspeed, double Backspeed) {
        setWantedState(ClimberState.MANUAL);
        mIO.front_desired_output = Frontspeed;
        mIO.back_desired_output = Backspeed;
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
        this.addLoggingItemsDumbControllers();
    }

    @Override
    public void loop() {
        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;

        boolean lockSolenoids = false;

        if (mWantedState == ClimberState.NEUTRAL) {
            neutral = true;
        } else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
            lockSolenoids = true;
        }

        this.lockSolenoids(lockSolenoids);

        if (neutral) {

            switchToState(ClimberState.NEUTRAL);

        } else {
            switchToState(mWantedState);
        }
    }

    private void switchToState(ClimberState s) {
        if (mState != s) {
            onStateExit(mState);
            mState = s;
            onStateStart(mState);
        }
    }

    private void onStateStart(ClimberState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }

    private void onStateExit(ClimberState s) {
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

    public class IO {
        // out
        private double front_output = 0;
        public Double front_desired_output = 0.0;
        private double back_output = 0;
        public Double back_desired_output = 0.0;
    }

    private void out() {

        if (mState != ClimberState.NEUTRAL) {
            mIO.front_output = mIO.front_desired_output;
            mIO.back_output = mIO.back_desired_output;
        } else {
            mIO.front_output = 0;
            mIO.back_output = 0;
        }

        mClimberFront.set(mIO.front_output);
        mClimberBack.set(mIO.back_output);
    }

    public synchronized void enableFollower() {
        // controller_2.follow(controller_1);
    }

    @Override
    protected void initDefaultCommand() {
    }

    public ClimberState getState() {
        return mState;
    }

    @Override
    public String getStateString() {
        return mState.toString();
    }

    public boolean setWantedState(ClimberState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    public enum ClimberState {
        NEUTRAL, MANUAL,
    }

    @Override
    public String getSmallString() {
        return "CLMBR";
    }

}