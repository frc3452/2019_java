package frc.robot.util;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Spark;
import frc.robot.GZOI;
import frc.robot.subsystems.Auton;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.GZSRX;

public class ExampleGZSubsystem extends GZSubsystem {
	/**
	 * The synchronized keyword means that if two different threads try to execute a
	 * method at the same time, it makes them wait to do it one at a time. Not doing
	 * this can make for some really fun troubleshooting :)
	 */

	/** Example motor */
	private GZSRX example_motor;

	/** Current state of subsystem and wanted state of subsystem */
	private ExampleState mState = ExampleState.NEUTRAL;
	private ExampleState mWantedState = ExampleState.MANUAL;

	// Input & output object.
	public IO mIO = new IO();

	/**
	 * This way of creating a subsystem is known as a 'singleton' By making the
	 * constructor private and having the only line to construct it in the
	 * getInstance() method, we only allow one 'instance' to ever exist. Every time
	 * we call getInstance(), it returns a reference to the same object.
	 */
	private static ExampleGZSubsystem mInstance = null;

	public static ExampleGZSubsystem getInstance() {
		if (mInstance == null)
			mInstance = new ExampleGZSubsystem();

		return mInstance;
	}

	/**
	 * Constructor for subsystem
	 */
	private ExampleGZSubsystem() {

		int pdpChannel = 4;
		example_motor = new GZSRX.Builder(0, this, "ExampleMotor", pdpChannel).build();

		example_motor.configFactoryDefault();
		example_motor.checkFirmware();
	}

	@Override
	public void addLoggingValues() {

		// Creating this object will add it to a list of other logging values
		new LogItem("EXMPL-AMP") {
			public String val() {
				return mIO.motor_1_amperage.toString();
			}
		};

		// This will put a formula for Google Sheets or Excel that will average the
		// column to the left
		new LogItem("AVG-AMP") {
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("EMXPL-VLT") {
			public String val() {
				return mIO.motor_1_voltage.toString();
			}
		};
	}

	/**
	 * this is the central loop of the subsystem. in this loop, we interface our
	 * desired_output (what we want the motor to do), with our output (what the
	 * motor is allowed to do). In manual, we allow the motor to run at what we
	 * want. But, in neutral we dont allow the motor to run at what we want, we
	 * force it to stop moving. read {@link} handleStates() in(), and out() to learn
	 * about those functions.
	 */
	@Override
	public void loop() {
		handleStates();
		in();
		out();
	}

	/**
	 * This is the method we use to control the state of the subsystem. We do this
	 * instead of just changing the mState variable because this is what keeps the
	 * robot locked in a disabled state if we call .disable(true) on the subsystem.
	 * This also calls switchToState.
	 * 
	 * First, we check if it's disabled and not connected to the field, or wanting
	 * to be stopped, thats our first priority. If the wanted or current state uses
	 * encoders and the motor controller If it is, we put the system in neutral. If
	 * it isn't, we check if its in demo. If so, set the current state to demo.
	 * Then, if we aren't in demo or disabled, which means were safe to switch to
	 * any state, we do that.
	 *
	 * <b> Keep in mind, among the if statements (on lines with 'AAAA'), only one of
	 * these can be true at any given time. By writing it this way, it lets us have
	 * a hierarchy of what we need the subsystem to be controlled by. (Saftey first)
	 */
	private synchronized void handleStates() {

		boolean neutral = false;
		neutral |= this.isSafetyDisabled() && !GZOI.getInstance().isFMS();
		neutral |= mWantedState == ExampleState.NEUTRAL;
		neutral |= (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop));

		if (neutral) { /* AAAA **/

			switchToState(ExampleState.NEUTRAL);

		} else if (Auton.getInstance().isDemo()) { /* AAAA **/

			switchToState(ExampleState.DEMO);

		} else if (mWantedState != mState) { /* AAAA **/

			switchToState(mWantedState);
		}
	}

	/**
	 * This allows us to continually call this method and only call the onStateExit
	 * and onStateStart methods once
	 */
	private void switchToState(ExampleState s) {
		if (mState != s) {
			onStateExit(mState);
			mState = s;
			onStateStart(mState);
		}
	}

	/**
	 * onStateStart is what we call when we are first entering a state. If we
	 * entering a state where we use an encoder, we may want to enable certain
	 * functions when we first enter that state.
	 */
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

	/**
	 * onStateExit is what we call when we are exiting a state. To follow from the
	 * previous example, if we are leaving a state where we are using an encoder, we
	 * may want to disable certain functions after we are done using that state
	 */
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

	/**
	 * here, we update the IO class information with input from our sensors. read
	 * into the IO class as to why we do this.
	 */
	private void in() {
		mIO.encoders_valid = example_motor.isEncoderValid();

		if (mIO.encoders_valid) {
			mIO.ticks_position = (double) example_motor.getSelectedSensorPosition();
			mIO.ticks_velocity = (double) example_motor.getSelectedSensorVelocity();
		}

		mIO.motor_1_amperage = example_motor.getOutputCurrent();
		mIO.motor_1_voltage = example_motor.getMotorOutputVoltage();
	}

	/** Set our motor values to what they should be */
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

	/**
	 * The purpose of having this class is because over CAN bus, (a type of
	 * connection), every time we want information, it has to travel through every
	 * single motor controller connected to get us that information. If we need to
	 * read a sensor in three different places, we will call that 'getSensor()'
	 * three times, and require it to send us the same number multiple times, which
	 * slows down the system. Using in(), we fill the values we need every loop (but
	 * only once) so that we can access the stored values without abusing the CAN
	 * bus.
	 * 
	 * as you can see, 'output' is private while 'desired_output' is not (which
	 * means it is public). We make 'output' private because we want that to only be
	 * changed by loop() so that you cannot bypass the disable() call that each
	 * subsystem has (see GZSubsystem) that is implemented for safety. We want
	 * anything to be able to change what the subsystem is trying to do, but nothing
	 * but the subsystem itself to change what it is actually doing.
	 */
	static class IO {
		// In

		// heres a decent amount of advantages to using a Double versus a double,
		// like being able to use .toString(), .isInfinite(), isNaN() (not a number)
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

	/**
	 * For example, in this method we will set the state to manual and the desired
	 * output to whatever the joystick left analog up and down axis that is given to
	 * this method is.
	 */
	public void exampleMethodRunMotorWithJoystick(GZJoystick joy) {
		if (setWantedState(ExampleState.MANUAL))
			mIO.desired_output = joy.getLeftAnalogY();
	}

	/**
	 * States for the subsystem. All will have a NEUTRAL, but other states will
	 * vary. We also store a variable with each state telling whether the state
	 * requires closed loop control, (uses sensors)
	 */
	public enum ExampleState {
		NEUTRAL(false), MANUAL(false), DEMO(false), MOTION_PROFILE(true);

		public final boolean usesClosedLoop;

		private ExampleState(boolean closed) {
			this.usesClosedLoop = closed;
		}
	}

	/** Set the desired state of the subsystem to NEUTRAL */
	@Override
	public void stop() {
		setWantedState(ExampleState.NEUTRAL);
	}

	/**
	 * This call returns the state that the subsystem is in as a String, which means
	 * we can use this to monitor the subsystem in the console, smartdashboard, a
	 * log, etc.
	 */
	@Override
	public String getStateString() {
		return mState.toString();
	}

	/**
	 * This method returns the current state of the subsystem. If we want to check
	 * if 'ExampleSubsytem' is in neutral or manual, we can use this call and then
	 * use an if statement to determine what the subsystem is currently doing.
	 */
	public ExampleState getState() {
		return mState;
	}

	/**
	 * This is the central control for each subsystem. This sets the wanted state to
	 * whatever we please
	 */
	public boolean setWantedState(ExampleState wantedState) {
		this.mWantedState = wantedState;
		return this.mWantedState == mState;
	}

	/**
	 * This is used with subsystems that use CAN bus and following. When entering
	 * Test mode, the robot will take every motor out of follower mode; this method
	 * puts them all back in
	 */
	public synchronized void enableFollower() {
		// controller_2.follow(controller_1);
	}

	/**
	 * This class is type 'GZSubsystem', which extends the WPI 'Subsystem', meaning
	 * it is a Subsystem with extra info with it. initDefaultCommand() is a call for
	 * 'Subsystem' which lets you set a command to run when no other command is
	 * running.
	 */
	@Override
	protected void initDefaultCommand() {
	}

	/**
	 * Some subsystems don't have motors, like an LED subsystem
	 */
	@Override
	public boolean hasMotors() {
		return true;
	}
}
