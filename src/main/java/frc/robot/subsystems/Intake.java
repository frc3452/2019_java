package frc.robot.subsystems;

import frc.robot.GZOI;
import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.drivers.GZSolenoid;
import frc.robot.util.drivers.GZSpark;

public class Intake extends GZSubsystem {

    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState = IntakeState.NEUTRAL;

    private GZSpark mIntakeLeft, mIntakeRight;
    private GZSolenoid mIntakeSol;

    private GZTimer mRaisedTimer = new GZTimer();
    private GZTimer mLoweredTimer = new GZTimer();

    public IO mIO = new IO();

    private static Intake mInstance = null;

    private Intake() {
        mIntakeLeft = new GZSpark.Builder(kIntake.INTAKE_LEFT_PORT, this, "Left", kPDP.INTAKE_LEFT).build();
        mIntakeRight = new GZSpark.Builder(kIntake.INTAKE_RIGHT_PORT, this, "Right", kPDP.INTAKE_RIGHT).build();
        mIntakeSol = new GZSolenoid(kIntake.INTAKE_SOLENOID_PORT, this, "Intake Solenoid");
    }

    public static Intake getInstance() {
        if (mInstance == null)
            mInstance = new Intake();

        return mInstance;
    }

    public void grabCargo() {
        raise(false);
        if (isLowered()) {
            runIntake(-.25, -.25);
        }
    }

    public void stow()
    {
        runIntake(0, 0);
        raise(true);
    }

    public void raise(boolean raise) {
        if (raise == !mIntakeSol.get())
            return;

        mIntakeSol.set(raise);

        if (raise) {
            mRaisedTimer.startTimer();
        } else {
            mLoweredTimer.startTimer();
        }
    }

    public boolean isRaised() {
        return !mIntakeSol.get() && mRaisedTimer.get() > kIntake.RAISE_TIME;
    }

    public boolean isLowered() {
        return mIntakeSol.get() && mLoweredTimer.get() > kIntake.LOWERED_TIME;
    }

    public enum IntakeState {
        NEUTRAL, MANUAL
    }

    public boolean setWantedState(IntakeState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    @Override
    public void stop() {
        setWantedState(IntakeState.NEUTRAL);
    }

    public void runIntake(double left, double right) {
        if (setWantedState(IntakeState.MANUAL)) {
            mIO.left_desired_output = left;
            mIO.right_desired_output = right;
        }
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
        // raise(mIO.isRaised);

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

        if (neutral) {

            switchToState(IntakeState.NEUTRAL);

        } else {
            switchToState(mWantedState);
        }
    }

    public class IO {
        // out
        private double left_output = 0;
        private double right_output = 0;
        public Double left_desired_output = 0.0;
        public Double right_desired_output = 0.0;

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
            mIO.left_output = mIO.left_desired_output;
            mIO.right_output = mIO.right_desired_output;
        } else {
            mIO.left_output = 0;
            mIO.right_output = 0;
        }
        mIntakeLeft.set(mIO.left_output);
        mIntakeRight.set(mIO.right_output);

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