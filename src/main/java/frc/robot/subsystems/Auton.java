package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.commands.NoCommand;
import frc.robot.commands.drive.DriveAtVelocityForTime;
import frc.robot.commands.paths.DrivePathGroup;
import frc.robot.commands.paths.TestPath;
import frc.robot.util.GZCommand;
import frc.robot.util.GZTimer;
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

	public ArrayList<GZCommand> commandArray;

	private GZCommand defaultCommand = null;
	public Command autonomousCommand = null;

	private int m_controllerOverrideValue = -1;
	private int p_controllerOverrideValue = m_controllerOverrideValue;

	private int m_selectorValue = 0;
	private int p_selectorValue = -1;

	private final String gameMsg = "NOT";

	public GZTimer matchTimer = new GZTimer("AutonTimer");

	private static Auton mInstance = null;

	private DigitalSelector mSelector1, mSelector2;

	public synchronized static Auton getInstance() {
		if (mInstance == null)
			mInstance = new Auton();
		return mInstance;
	}

	private Auton() {
		// mSelector1 = new DigitalSelector("AutonSelector (Tens)", 0, 1, 2, 3);
		// mSelector2 = new DigitalSelector("AutonSelector (Ones", 4, 5, 6, 7);

		commandArray = null;

		// fillAutonArray();
	}

	public int getSelector() {
		return DigitalSelector.get(mSelector1, mSelector2);
	}

	public void crash() {
		if (GZOI.getInstance().isDisabled() && !GZOI.getInstance().isFMS()) {
			Timer f = null;
			f.start();
		}
	}

	/**
	 * Used to pre-populate values for what auto is to be selected before and after
	 * commands are loaded
	 */
	public void autonChooser() {
		if (commandArray == null)
			fillAutonArray();

		controllerChooser();

		if (m_controllerOverrideValue != -1) {
			autonomousCommand = commandArray.get(m_controllerOverrideValue).getCommand();
		} else {
			// Check if auton selectors are returning what they should be
			final int selectorValue = getSelector();
			if (selectorValue <= 99 && selectorValue >= 1) {
				autonomousCommand = commandArray.get(selectorValue).getCommand();
			} else {
				autonomousCommand = defaultCommand.getCommand();
			}
		}

		printSelected();
	}

	/**
	 * Sets the names for the override and the value of the array in which to
	 * override
	 */
	private void controllerChooser() {
		if (GZOI.driverJoy.allButtons(Buttons.LB, Buttons.RB)) {

			if (GZOI.driverJoy.isAPressed()) {
				m_controllerOverrideValue++;
			} else if (GZOI.driverJoy.isBPressed()) {
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
		if (commandArray == null) {
			GZCommand noCommand = new GZCommand("NO AUTO", new NoCommand());
			commandArray = new ArrayList<GZCommand>(Collections.nCopies(100, noCommand));
		}
		commandArray.clear();

		commandArray.add(new GZCommand("Test trajectory", new DrivePathGroup(new TestPath())));

		// commandArray.add(new GZCommand("Test trajectory",
				// new DriveTrajectoryCommand(TrajectoryGenerator.getInstance().getTestTrajectoryStraight(), true)));

		// commandArray.add(new GZCommand("Test velocity", new DriveAtVelocityForTime(1024, 1024, 6)));

		defaultCommand = new GZCommand("DEFAULT", new NoCommand());

		autonChooser();
	}

	public void startAuton() {
		fillAutonArray();

		if (autonomousCommand != null) {
			autonomousCommand.start();
		}
	}

	public void cancelAuton() {
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}
	}

	public boolean isDemo() {
		return getSelector() == kAuton.SAFTEY_SWITCH;
	}

	private void printSelected() {
		// If overriden, print overide
		m_selectorValue = getSelector();

		if (m_controllerOverrideValue != p_controllerOverrideValue)
			System.out.println("Auton Controller override: (" + m_controllerOverrideValue + ") "
					+ commandArray.get(m_controllerOverrideValue).getName());

		if (m_controllerOverrideValue != -1) {
			if (m_selectorValue != p_selectorValue) {
				printSelectors();
			}
		}

		// update values for one time display
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
	public String gameMessage() {
		String badValue = gameMsg;

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

	/**
	 * Autonomous versions enum
	 * 
	 * @author max
	 *
	 */
	public enum AV {
		// SEASON, FOREST_HILLS, CURRENT
	}

	/**
	 * Autonomous options enum
	 * 
	 * @author max
	 *
	 */
	public enum AO {
	}

}
