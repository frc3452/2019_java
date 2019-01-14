package frc.robot.subsystems;

import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.SuperstructureComponent;
import frc.robot.util.drivers.motorcontrollers.dumbcontrollers.GZSpark;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Intake extends GZSubsystem {

    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState = IntakeState.NEUTRAL;

    private GZSpark mIntakeLeft, mIntakeRight;
    private GZSolenoid mIntakeSol;

    public IO mIO = new IO();

    private static Intake mInstance = null;

    private Intake() {
        mIntakeLeft = new GZSpark.Builder(kIntake.INTAKE_LEFT, this, "Left", kPDP.INTAKE_LEFT).build();
        mIntakeRight = new GZSpark.Builder(kIntake.INTAKE_RIGHT, this, "Right", kPDP.INTAKE_RIGHT).build();
        mIntakeSol = new GZSolenoid(kIntake.INTAKE_SOLENOID, this, "Intake Solenoid");
    }

    public static Intake getInstance() {
        if (mInstance == null)
            mInstance = new Intake();

        return mInstance;
    }

    protected void stow() {
        stow(false);
    }

    protected void stow(boolean manual) {
        stop();
        raise();
    }

    protected void lower() {
        lower(false);
    }

    protected void lower(boolean manual) {
        mIntakeSol.set(true);
    }

    protected void raise() {
        raise(false);
    }

    protected void raise(boolean manual) {
        mIntakeSol.set(false);
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

    public boolean isRaised() {
        return mIntakeSol.getSolenoidState() == SolenoidState.EXTENDED;
    }

    public boolean isLowered() {
        return mIntakeSol.getSolenoidState() == SolenoidState.RETRACTED;
    }

    public SolenoidState getSolenoidState() {
        return mIntakeSol.getSolenoidState();
    }

    protected void runIntake(double left, double right) {
        setWantedState(IntakeState.MANUAL);
        mIO.left_desired_output = left;
        mIO.right_desired_output = right;
    }

    protected void runIntake(double speed) {
        runIntake(speed, speed);
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

        boolean lockSolenoids = false;
        if (mWantedState == IntakeState.NEUTRAL) {
            neutral = true;
        } else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
            lockSolenoids = true;
        }

        this.lockSolenoids(lockSolenoids);

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