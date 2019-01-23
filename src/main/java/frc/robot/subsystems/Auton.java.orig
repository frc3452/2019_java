package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.commands.NoCommand;
import frc.robot.commands.paths.DrivePathGroup;
import frc.robot.commands.paths.L_R_CRGO_SHIP_3;
import frc.robot.commands.paths.TestPath;
import frc.robot.commands.paths.TestPath2;
import frc.robot.commands.paths.TestPath4;
import frc.robot.commands.paths.TestPath5;
import frc.robot.util.GZCommand;
import frc.robot.util.GZTimer;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.DigitalSelector;
import frc.robot.util.drivers.GZJoystick.Buttons;

/**
 * <h1>AutonSelector Subsystem</h1> Handles autonomous selector case statements
 * and printing.
 * 
 * @author max
 *
 */
public class Auton {

	public enum AV {
		CURRENT,
	}

	public ArrayList<GZCommand> commandArray = null;

	private GZCommand defaultCommand = null;
	public GZCommand autonomousCommand = null;

	private int m_controllerOverrideValue = -1;
	private int p_controllerOverrideValue = m_controllerOverrideValue;

	private int m_selectorValue = 0;
	private int p_selectorValue = -1;

	public GZTimer matchTimer = new GZTimer("AutonTimer");

	private static Auton mInstance = null;

	private LatchedBoolean mLBAutoStart = new LatchedBoolean();
	private LatchedBoolean mLBAutoCancel = new LatchedBoolean();
	private LatchedBoolean mLBWaitOnAutoStart = new LatchedBoolean();
	private boolean mWaitOnAutoStart = false;

	private DigitalSelector mSelectorOnes = null, mSelectorTens = null;

	public synchronized static Auton getInstance() {
		if (mInstance == null)
			mInstance = new Auton();
		return mInstance;
	}

	private Auton() {
		mSelectorOnes = new DigitalSelector(kAuton.SELECTOR_ONES);
		mSelectorTens = new DigitalSelector(kAuton.SELECTOR_TENS);
		fillAutonArray();
	}

	public int getSelector() {
		return DigitalSelector.get(mSelectorOnes, mSelectorTens);
	}

	public void crash() {
		if (GZOI.getInstance().isDisabled() && !GZOI.getInstance().isFMS()) {
			Timer f = null;
			f.start();
		}
	}

	public void autonChooser() {
		controllerChooser();

		m_selectorValue = getSelector();

		if (m_controllerOverrideValue != -1) {
			autonomousCommand = commandArray.get(m_controllerOverrideValue);
		} else {
			// Check if auton selectors are returning what they should be
			if (m_selectorValue <= 99 && m_selectorValue >= 0) {
				autonomousCommand = commandArray.get(m_selectorValue);
			} else {
				autonomousCommand = defaultCommand;
			}
		}

		printSelected();
	}

	public boolean isAutoControl() {
		if (autonomousCommand == null)
			return false;

		return autonomousCommand.isRunning() && GZOI.getInstance().isAuto();
	}

	public void toggleAutoWait(boolean updateValue) {
		if (mLBWaitOnAutoStart.update(updateValue)) {
			mWaitOnAutoStart = !mWaitOnAutoStart;
			System.out.println("WARNING Auto start set to " + (mWaitOnAutoStart ? "WAIT" : "NOT WAIT")
					+ " at the start of SANDSTORM");
		}
	}

	/**
	 * Uses internal LatchedBoolean, starts auton with controller Ignores autonomous
	 * waiting
	 */
	public void controllerStart(boolean update) {
		if (autonomousCommand == null)
			return;

		if (mLBAutoStart.update(update)) {
			if (autonomousCommand.start())
				System.out.println("WARNING Controller starting auto!");
			else
				System.out.println("WARNING Could not start controller auto!");
		}
	}

	/**
	 * Uses internal LatchedBoolean. Cancels auton
	 */
	public void controllerCancel(boolean update) {
		if (mLBAutoCancel.update(update)) {
			cancelAuton();
		}
	}

	/**
	 * Sets the names for the override and the value of the array in which to
	 * override
	 */
	private void controllerChooser() {
		if (GZOI.driverJoy.allButtons(Buttons.LB, Buttons.RB)) {

			if (GZOI.driverJoy.getButtonLatched(Buttons.A)) {
				m_controllerOverrideValue++;
			} else if (GZOI.driverJoy.getButtonLatched(Buttons.B)) {
				m_controllerOverrideValue--;
			} else if (GZOI.driverJoy.getButton(Buttons.RIGHT_CLICK)) {
				m_controllerOverrideValue = -1;
				printSelectors();
			}

			if (m_controllerOverrideValue < 0)
				m_controllerOverrideValue = commandArray.size() - 1;
			if (m_controllerOverrideValue > commandArray.size() - 1)
				m_controllerOverrideValue = 0;
		}
	}

	public void fillAutonArray() {
		if (commandArray != null)
			return;

		GZCommand noCommand = new GZCommand("NO AUTO", new NoCommand());
		commandArray = new ArrayList<GZCommand>(Collections.nCopies(100, noCommand));

		commandArray.add(new GZCommand("Test trajectory", new DrivePathGroup(new TestPath())));
		commandArray.add(new GZCommand("Test trajectory 2", new DrivePathGroup(new TestPath2())));
		commandArray.add(new GZCommand("Yes", new DrivePathGroup(new L_R_CRGO_SHIP_3())));
		commandArray.add(new GZCommand("Test trajectory 4", new DrivePathGroup(new TestPath4())));
		commandArray.add(new GZCommand("Test trajectory 5", new DrivePathGroup(new TestPath5())));

		// commandArray.add(new GZCommand("Test trajectory",
		// new
		// DriveTrajectoryCommand(TrajectoryGenerator.getInstance().getTestTrajectoryStraight(),
		// true)));

		// commandArray.add(new GZCommand("Test velocity", new
		// DriveAtVelocityForTime(1024, 1024, 6)));

		defaultCommand = new GZCommand("DEFAULT", new NoCommand());

		autonChooser();
	}

	/**
	 * Ran once by autonomousInit
	 */
	public void startAuton() {
		if (autonomousCommand != null) {
			if (!mWaitOnAutoStart) {
				if (autonomousCommand.start())
					System.out.println("Starting auto...");
			} else {
				System.out.println("WARNING Auto not running! Wait toggled!");
			}
		}
	}

	// Ran on teleopInit
	public void cancelAuton() {
		if (autonomousCommand != null) {
			if (autonomousCommand.cancel())
				System.out.println("WARNING Cancelling auto...");
			else
				System.out.println("WARNING Command cancelled, but apparently already stopped.");
		}
	}

	public boolean isDemo() {
		return getSelector() == kAuton.SAFTEY_SWITCH;
	}

	private void printSelected() {
		if (m_controllerOverrideValue != -1) {
			if (m_selectorValue != p_selectorValue) {
				printSelectors();
			}
		} else {
			if (m_controllerOverrideValue != p_controllerOverrideValue)
				System.out.println("Auton Controller override: (" + m_controllerOverrideValue + ") "
						+ commandArray.get(m_controllerOverrideValue).getName());
		}

		p_selectorValue = m_selectorValue;
		p_controllerOverrideValue = m_controllerOverrideValue;
	}

	private void printSelectors() {
		// if is valid
		if (m_selectorValue >= 0 && m_selectorValue <= 99) {
			System.out.println(
					"Auton selected: (" + m_selectorValue + ") " + commandArray.get(m_selectorValue).getName());
		} else {
			System.out.println("WARNING Auton not selected! Selectors returning value: " + m_selectorValue);
		}
	}

	// get game message, returns "NOT" if anything incorrect
	@Deprecated
	public String gameMessage() {
		String badValue = "NOT";

		String f = DriverStation.getInstance().getGameSpecificMessage();

		// Check length
		if (f.length() == 3) {

			// Check and make sure every character is an L or an R
			for (int i = 0; i < 3; i++)
				// if character doesn't equal l or r
				if (!(f.charAt(i) == 'L' || f.charAt(i) == 'R'))
					return badValue;

			// If we get here, we're good
			return f;
		}

		// Length incorrect
		return badValue;
	}
}
