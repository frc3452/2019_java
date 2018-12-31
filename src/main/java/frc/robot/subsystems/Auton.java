package frc.robot.subsystems;

import java.util.Arrays;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.commands.drive.EncoderFrom;
import frc.robot.util.GZCommand;
import frc.robot.util.GZJoystick.Buttons;
import frc.robot.util.GZTimer;
import frc.robot.commands.NoCommand;

/**
 * <h1>AutonSelector Subsystem</h1> Handles autonomous selector case statements
 * and printing.
 * 
 * @author max
 *
 */
public class Auton {

	private AnalogInput as_A;
	private AnalogInput as_B;

	private int m_prev_as1, m_prev_as2;
	private int m_asA, m_asB;

	public GZCommand commandArray[];

	private GZCommand defaultCommand = null;
	public Command autonomousCommand = null;

	private boolean controllerOverride = false;

	private int overrideValue = 1;
	private String overrideStringPrevious = "";
	private String overrideString = "", autonString = "";
	private String gameMsg = "NOT";

	public GZTimer matchTimer = new GZTimer("AutonTimer");

	private static Auton mInstance = null;

	public synchronized static Auton getInstance() {
		if (mInstance == null)
			mInstance = new Auton();
		return mInstance;
	}

	private Auton() {
		as_A = new AnalogInput(Constants.kAuton.AUTO_SELECTOR_1);
		as_B = new AnalogInput(Constants.kAuton.AUTO_SELECTOR_2);

		as_A.setSubsystem("AutonSelector");
		as_B.setSubsystem("AutonSelector");
		as_A.setName("Selector A");
		as_B.setName("Selector B");

		commandArray = null;

		// fillAutonArray();
	}

	public String getAutonString() {
		return autonString;
	}

	public void crash() {
		if (GZOI.getInstance().isDisabled()) {
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

		if (controllerOverride) {
			autonomousCommand = commandArray[overrideValue].getCommand();
		} else {
			// Check if auton selectors are returning what they should be
			if (uglyAnalog() <= kAuton.COMMAND_ARRAY_SIZE && uglyAnalog() >= 1) {
				autonomousCommand = commandArray[uglyAnalog()].getCommand();
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
		if (GZOI.driverJoy.getRawButton(Buttons.LB) && GZOI.driverJoy.getRawButton(Buttons.RB)) {

			if (GZOI.driverJoy.isAPressed()) {
				overrideValue++;
				controllerOverride = true;
			} else if (GZOI.driverJoy.isBPressed()) {
				overrideValue--;
				controllerOverride = true;
			} else if (GZOI.driverJoy.getRawButton(Buttons.RIGHT_CLICK)) {
				controllerOverride = false;
				System.out.println(autonString);
			}

			if (overrideValue < 0)
				overrideValue = commandArray.length - 1;
			if (overrideValue > commandArray.length - 1)
				overrideValue = 0;

			if (controllerOverride)
				overrideString = "Controller override:\t (" + overrideValue + ")"
						+ commandArray[overrideValue].getName();
		}
	}

	public void fillAutonArray() {
		if (commandArray == null) {
			commandArray = new GZCommand[kAuton.COMMAND_ARRAY_SIZE];
			GZCommand noCommand = new GZCommand("NO AUTO", new NoCommand());
			Arrays.fill(commandArray, noCommand);
		}

		defaultCommand = new GZCommand("DEFAULT", new NoCommand());

		autonChooser();
	}

	public void startAuton() {
		fillAutonArray();

		if (autonomousCommand != null) {
			autonomousCommand.start();
		}
	}

	public boolean isDemo() {
		return uglyAnalog() == kAuton.SAFTEY_SWITCH;
	}

	private void printSelected(){}
	private void printSelected2() {
		m_asA = as_A.getValue();
		m_asB = as_B.getValue();

		// If overriden, print overide
		if (controllerOverride && (!overrideString.equals(overrideStringPrevious)))
			System.out.println(overrideString);

		if (((m_asA + 8 < m_prev_as1 || m_prev_as1 < m_asA - 8)
				|| (m_asB + 8 < m_prev_as2 || m_prev_as2 < m_asB - 8))) {

			if ((uglyAnalog() >= 1) && (uglyAnalog() <= 10)) {
				autonString = "A / " + uglyAnalog() + ": " + commandArray[uglyAnalog()].getName();
			} else if ((uglyAnalog() >= 11) && (uglyAnalog() <= 20)) {
				autonString = "B / " + (uglyAnalog() - 10) + ": " + commandArray[uglyAnalog()].getName() + " ("
						+ uglyAnalog() + ")";
			} else if ((uglyAnalog() >= 21) && (uglyAnalog() <= 30)) {
				autonString = "C / " + (uglyAnalog() - 20) + ": " + commandArray[uglyAnalog()].getName() + " ("
						+ uglyAnalog() + ")";
			} else if ((uglyAnalog() >= 31) && (uglyAnalog() <= 40)) {
				autonString = "D / " + (uglyAnalog() - 30) + ": " + commandArray[uglyAnalog()].getName() + " ("
						+ uglyAnalog() + ")";
			} else {
				autonString = "AUTON NOT SELECTED: " + uglyAnalog();
			}
			System.out.println(autonString);
		}

		// update values for one time display
		m_prev_as1 = m_asA;
		m_prev_as2 = m_asB;

		overrideStringPrevious = overrideString;
	}

	/**
	 * @author max
	 * @return Number between 1 - 100, A1 = 1, A10 = 10, B1 = 11, B10 = 20, or 3452
	 *         as error
	 */
	public int uglyAnalog() {
		if (m_asA < Constants.kAuton.AUTO_1 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_1 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(0);

		} else if (m_asA < Constants.kAuton.AUTO_2 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_2 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(1);

		} else if (m_asA < Constants.kAuton.AUTO_3 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_3 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(2);

		} else if ((m_asA < Constants.kAuton.AUTO_4 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_4 - Constants.kAuton.AUTO_VARIANCE)) {
			return selectorB(3);

		} else if ((m_asA < Constants.kAuton.AUTO_5 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_5 - Constants.kAuton.AUTO_VARIANCE)) {
			return selectorB(4);

		} else if (m_asA < Constants.kAuton.AUTO_6 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_6 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(5);

		} else if (m_asA < Constants.kAuton.AUTO_7 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_7 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(6);

		} else if (m_asA < Constants.kAuton.AUTO_8 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_8 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(7);
		} else if (m_asA < Constants.kAuton.AUTO_9 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_9 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(8);

		} else if (m_asA < Constants.kAuton.AUTO_10 + Constants.kAuton.AUTO_VARIANCE
				&& m_asA > Constants.kAuton.AUTO_10 - Constants.kAuton.AUTO_VARIANCE) {
			return selectorB(9);

		} else {
			// ERROR
			return 3452;
		}

	}

	private int selectorB(int selectorA) {
		if (m_asB > Constants.kAuton.AUTO_1_L && m_asB < Constants.kAuton.AUTO_1_H) {
			return 1 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_2_L && m_asB < Constants.kAuton.AUTO_2_H) {
			return 2 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_3_L && m_asB < Constants.kAuton.AUTO_3_H) {
			return 3 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_4_L && m_asB < Constants.kAuton.AUTO_4_H) {
			return 4 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_5_L && m_asB < Constants.kAuton.AUTO_5_H) {
			return 5 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_6_L && m_asB < Constants.kAuton.AUTO_6_H) {
			return 6 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_7_L && m_asB < Constants.kAuton.AUTO_7_H) {
			return 7 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_8_L && m_asB < Constants.kAuton.AUTO_8_H) {
			return 8 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_9_L && m_asB < Constants.kAuton.AUTO_9_H) {
			return 9 + (selectorA * 10);
		} else if (m_asB > Constants.kAuton.AUTO_10_L && m_asB < Constants.kAuton.AUTO_10_H) {
			return 10 + (selectorA * 10);
		} else {
			// ERROR
			return 3452;
		}
	}

	public String gsm() {
		String f;
		f = DriverStation.getInstance().getGameSpecificMessage();

		if (f.length() > 0)
			gameMsg = f;
		else
			gameMsg = "NOT";

		return gameMsg;
	}

	/**
	 * Autonomous versions enum
	 * 
	 * @author max
	 *
	 */
	public enum AV {
		SEASON, FOREST_HILLS, CURRENT
	}

	/**
	 * Autonomous options enum
	 * 
	 * @author max
	 *
	 */
	public enum AO {
		SWITCH, SCALE, SWITCH_PRIORITY_NO_CROSS, SCALE_PRIORITY_NO_CROSS, SWITCH_ONLY, SCALE_ONLY, DEFAULT
	}

}
