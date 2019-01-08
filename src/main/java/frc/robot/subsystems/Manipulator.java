package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.GZOI;
import frc.robot.Constants.kManipulator;
import frc.robot.Constants.kPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZSRX;
import frc.robot.util.drivers.GZSRX.Breaker;

public class Manipulator extends GZSubsystem {

    private ExampleState mState = ExampleState.MANUAL;
    private ExampleState mWantedState = ExampleState.NEUTRAL;
    public IO mIO = new IO();
    private GZSRX example_motor;

    private static Manipulator mInstance = null;

    public static Manipulator getInstance() {
        if (mInstance == null)
            mInstance = new Manipulator();

        return mInstance;
    }

    private Manipulator() {
        example_motor = new GZSRX.Builder(kManipulator.EXAMPLE_MOTOR_ID, this, "Example", kPDP.EXAMPLE_MOTOR).build();
    }

    private void talonInit() {
        for (GZSRX t : mTalons.values()) {
            t.configFactoryDefault();
        }
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
        boolean neutral = false;

        if (mWantedState == ExampleState.NEUTRAL) {
            neutral = true;
        }

        else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
        }

        else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
            neutral = true;
        }

        if (neutral) {

            switchToState(ExampleState.NEUTRAL);

        } else {
            switchToState(mWantedState);
        }
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
        mIO.encoders_valid = example_motor.isEncoderValid();

        if (mIO.encoders_valid) {
            mIO.ticks_position = (double) example_motor.getSelectedSensorPosition();
            mIO.ticks_velocity = (double) example_motor.getSelectedSensorVelocity();
        }

        mIO.motor_1_amperage = example_motor.getOutputCurrent();
        mIO.motor_1_voltage = example_motor.getMotorOutputVoltage();
    }

    public class IO {
        // In
        public Double ticks_velocity = Double.NaN;
        public Double ticks_position = Double.NaN;

        public Boolean encoders_valid = false;

        public Double motor_1_amperage = Double.NaN;
        public Double motor_1_voltage = Double.NaN;

        // out
        ControlMode control_mode = ControlMode.PercentOutput;
        private double output = 0;
        public Double desired_output = 0.0;
    }

    private void out() {

        switch (mState) {
        case MANUAL:

            mIO.output = mIO.desired_output;
            mIO.control_mode = ControlMode.PercentOutput;
            break;
        case NEUTRAL:

            mIO.output = 0;
            mIO.control_mode = ControlMode.PercentOutput;
            break;

        case DEMO:
            mIO.output = mIO.desired_output;
            mIO.control_mode = ControlMode.PercentOutput;
            break;
        case MOTION_PROFILE:
            mIO.output = mIO.desired_output;
            mIO.control_mode = ControlMode.MotionProfile;
        default:
            System.out.println("WARNING: Incorrect ExampleSubsystem state " + mState + " reached.");
            break;
        }

        example_motor.set(mIO.control_mode, mIO.output);
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