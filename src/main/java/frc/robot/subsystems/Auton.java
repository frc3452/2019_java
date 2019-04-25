package frc.robot.subsystems;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder;
import frc.robot.auto.commands.AutoModeBuilder.FeederStation;
import frc.robot.auto.commands.AutoModeBuilder.ScoringLocation;
import frc.robot.auto.commands.AutoModeBuilder.ScoringPosition;
import frc.robot.auto.commands.AutoModeBuilder.ScoringSide;
import frc.robot.auto.commands.AutoModeBuilder.StartingPosition;
import frc.robot.auto.commands.functions.NoCommand;
import frc.robot.auto.commands.functions.superstructure.RunAction;
import frc.robot.auto.commands.paths.Curve_test_path;
import frc.robot.auto.commands.paths.center.Center_CS_Face_Left;
import frc.robot.auto.commands.paths.left.Left_Rocket_Close_Same;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;
import frc.robot.util.GZTimer;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.DigitalSelector;
import frc.robot.util.drivers.GZJoystick.AnalogAngle;
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
	private GZCommand autonomousCommand = null;

	private int m_controllerOverrideValue = -1;
	private int p_controllerOverrideValue = m_controllerOverrideValue;

	private int m_selectorValue = 0;
	private int p_selectorValue = -1;

	public GZTimer matchTimer = new GZTimer("AutonTimer");

	private static Auton mInstance = null;

	private LatchedBoolean mLBAutoCancel = new LatchedBoolean();
	private LatchedBoolean mLBWaitOnAutoStart = new LatchedBoolean();
	private LatchedBoolean mLBAutoGamePiece = new LatchedBoolean();
	private boolean mWaitOnAutoStart = false;
	private boolean mAutoPieceIsHatch = true;

	private DigitalSelector mSelectorOnes = null, mSelectorTens = null;

	private LatchedBoolean mCustomAutoMoveStartPosLeft = new LatchedBoolean();
	private LatchedBoolean mCustomAutoMoveStartPosRight = new LatchedBoolean();
	private StartingPosition mCustomAutoStartPos = null;
	private Rotation2d mCustomAutoStartingAngle = null;

	public synchronized static Auton getInstance() {
		if (mInstance == null)
			mInstance = new Auton();
		return mInstance;
	}

	public void fillAutonArray() {
		if (commandArray != null)
			return;

		// TODO remove
		// m_controllerOverrideValue = 0;

		commandArray = new ArrayList<GZCommand>();

		commandArray.add(new GZCommand("Do nothing", () -> new GZCommandGroup() {
			{
				tele();
			}
		}));

		commandArray.add(new GZCommand("Custom auto", () -> new GZCommandGroup() {
			{
				tele();
				angle(Rotation2d.fromDegrees(180));
				tele();
				angle(Rotation2d.fromDegrees(0));
			}
		}));

		commandArray.add(new GZCommand("Zero odometry (LEFT)", () -> new GZCommandGroup() {
			{
				resetPos(new Left_Rocket_Close_Same().getLeft());
				tele();
				angle(Rotation2d.fromDegrees(180));
				tele();
			}
		}));

		commandArray.add(new GZCommand("Zero odometry (CENTER)", () -> new GZCommandGroup() {
			{
				resetPos(new Center_CS_Face_Left().getLeft());
				tele();
			}
		}));

		commandArray.add(new GZCommand("Zero odometry (RIGHT)", () -> new GZCommandGroup() {
			{
				resetPos(new Left_Rocket_Close_Same().getRight());
				tele();
				angle(Rotation2d.fromDegrees(180));
				tele();
			}
		}));

		commandArray.add(new GZCommand("Place", () -> new GZCommandGroup() {
			{
				// add(new GoToHeight(Heights.HP_2));
				add(new RunAction(Actions.SCORE_HATCH));
			}
		}));

		ArrayList<GZCommand> commandsIn = AutoModeBuilder.getAllPaths();
		for (GZCommand c : commandsIn) {
			commandArray.add(c);
		}

		// commandArray.add(AutoModeBuilder.getCommand(StartingPosition.LEFT,
		// new ScoringLocation(ScoringPosition.ROCKET_NEAR, ScoringSide.LEFT),
		// FeederStation.LEFT));

		defaultCommand = new GZCommand("DEFAULT", () -> new NoCommand());

		autonChooser();
	}

	private Auton() {
		mSelectorOnes = new DigitalSelector(kAuton.SELECTOR_ONES);
		mSelectorTens = new DigitalSelector(kAuton.SELECTOR_TENS);
		// fillAutonArray();
	}

	public void printAllCommands() {
		for (GZCommand c : commandArray)
			System.out.println(c.getName());
	}

	public void print() {
		System.out.println(getSelector());
	}

	public int getSelector() {
		// return -1;
		// return mSelectorOnes.get();
		return DigitalSelector.get(mSelectorTens, mSelectorOnes);
	}

	public void autonChooser() {
		controllerChooser();

		m_selectorValue = getSelector();

		if (m_controllerOverrideValue != -1) {
			autonomousCommand = commandArray.get(m_controllerOverrideValue);
		} else {
			// Check if auton selectors are returning what they should be
			if (m_selectorValue <= (commandArray.size() - 1) && m_selectorValue >= 0) {
				autonomousCommand = commandArray.get(m_selectorValue);
			} else {
				autonomousCommand = defaultCommand;
			}
		}

		printSelected();
	}

	public boolean isAutoControl() {
		if (autonomousCommand == null)
			return Superstructure.getInstance().fakeAutoScore();

		return !autonomousCommand.hasBeenCancelled() && (autonomousCommand.isRunning() || !autonomousCommand.hasRun())
				&& GZOI.getInstance().isAuto();
	}

	public void toggleAutoWait(boolean updateValue) {
		if (mLBWaitOnAutoStart.update(updateValue)) {
			mWaitOnAutoStart = !mWaitOnAutoStart;
			System.out.println("WARNING Auto start set to " + (mWaitOnAutoStart ? "WAIT" : "NOT WAIT")
					+ " at the start of SANDSTORM");
		}
	}

	public void toggleAutoGamePiece(boolean updateValue) {
		if (mLBAutoGamePiece.update(updateValue)) {
			mAutoPieceIsHatch = !mAutoPieceIsHatch;
			System.out.println("WARNING Auto game piece set to " + (mAutoPieceIsHatch ? "HATCH" : "CARGO"));
		}

	}

	public boolean isAutoPieceHatch() {
		return mAutoPieceIsHatch;
	}

	private void addWaitAndStart() {
		autonomousCommand.addTeleBefore();
		startAutoCommand();
	}

	private void startAutoCommand() {
		autonomousCommand.setCommand();
		AutoModeBuilder.setFeederStation(autonomousCommand.getFeederStation());
		autonomousCommand.start();
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
		customAuto();

		if (GZOI.driverJoy.getButtons(Buttons.LB, Buttons.RB)) {
			if (GZOI.driverJoy.getButtonLatched(Buttons.A)) {
				m_controllerOverrideValue++;
				sanityCheckControllerValue();
			} else if (GZOI.driverJoy.getButtonLatched(Buttons.B)) {
				m_controllerOverrideValue--;
				sanityCheckControllerValue();
			} else if (GZOI.driverJoy.getButtonLatched(Buttons.RIGHT_CLICK)) {
				m_controllerOverrideValue = -1;
				printSelectors();
				return;
			}
		}
	}

	private void updateCustomAuto() {
		if (mCustomAutoStartPos == null) {
			updateCustomAutoError("No Position");
			return;
		}

		if (mCustomAutoStartingAngle == null) {
			updateCustomAutoError("No Angle");
			return;
		}

		Translation2d position;

		switch (mCustomAutoStartPos) {
		case CENTER:
			position = new Center_CS_Face_Left().getStartPose().getTranslation();
			break;
		case LEFT:
			position = new Left_Rocket_Close_Same().getStartPose().getTranslation();
			break;
		case RIGHT:
			position = new Left_Rocket_Close_Same().getRight().getStartPose().getTranslation();
			break;
		default:
			System.out.println("UNHANDLED STARTING POSITION " + mCustomAutoStartPos + " IN updateCustomAuto()");
			return;
		}

		Drive.getInstance().zeroOdometry(new RigidTransform2d(position, mCustomAutoStartingAngle.inverse()));
	}

	private void updateCustomAutoError(String msg) {
		System.out.println("WARNING Cannot update odometry [" + msg + "]");
	}

	private void customAuto() {
		if (GZOI.driverJoy.getButton(Buttons.BACK)) {
			if (GZOI.driverJoy.getButtonLatched(Buttons.LEFT_CLICK)) {
				updateCustomAuto();
			} else if (GZOI.driverJoy.getButtonLatched(Buttons.RIGHT_CLICK)) {
				System.out.println("WARNING Custom auto deselected!");
				mCustomAutoStartPos = null;
				mCustomAutoStartingAngle = null;
			} else {
				AnalogAngle newAngle = GZOI.driverJoy.getRightAnalogAngle();
				Rotation2d mappedAngle = Rotation2d.closestCoordinatePlus(newAngle.angle);

				if (Math.abs(newAngle.magnitude) > .2) {
					if (mCustomAutoStartingAngle == null || !mCustomAutoStartingAngle.equals(mappedAngle)) {
						System.out.println("WARNING Custom auto angle set to " + mappedAngle.getNormalDegrees());
						mCustomAutoStartingAngle = mappedAngle;
					}
				}

				if (mCustomAutoMoveStartPosLeft.update(GZOI.driverJoy.getLeftAnalogX() < -.5)) {
					if (mCustomAutoStartPos == null)
						mCustomAutoStartPos = StartingPosition.CENTER;
					else if (mCustomAutoStartPos == StartingPosition.CENTER)
						mCustomAutoStartPos = StartingPosition.LEFT;
					else if (mCustomAutoStartPos == StartingPosition.RIGHT)
						mCustomAutoStartPos = StartingPosition.CENTER;

					customAutoStartPosUpdate();
				} else if (mCustomAutoMoveStartPosRight.update(GZOI.driverJoy.getLeftAnalogX() > .5)) {
					if (mCustomAutoStartPos == null)
						mCustomAutoStartPos = StartingPosition.CENTER;
					else if (mCustomAutoStartPos == StartingPosition.CENTER)
						mCustomAutoStartPos = StartingPosition.RIGHT;
					else if (mCustomAutoStartPos == StartingPosition.LEFT)
						mCustomAutoStartPos = StartingPosition.CENTER;
					customAutoStartPosUpdate();
				}
			}
		}
	}

	private void customAutoStartPosUpdate() {
		final boolean disabled = (mCustomAutoStartPos == null);
		if (disabled)
			System.out.println("WARNING Custom auto disabled");
		else
			System.out.println("WARNING Custom auto position set to " + mCustomAutoStartPos);
	}

	private void sanityCheckControllerValue() {
		if (m_controllerOverrideValue < 0)
			m_controllerOverrideValue = commandArray.size() - 1;
		if (m_controllerOverrideValue > commandArray.size() - 1)
			m_controllerOverrideValue = 0;
	}

	/**
	 * Ran once by autonomousInit
	 */
	public void startAuton() {
		if (autonomousCommand != null) {
			if (mWaitOnAutoStart) {
				System.out.println("WARNING Auto run with wait!");
				addWaitAndStart();
			} else {
				startAutoCommand();
				System.out.println("Starting auto...");
			}

		}
	}

	// Ran on teleopInit
	public void cancelAuton() {
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
			System.out.println("WARNING Cancelling auto...");
		}
	}

	private void printSelected() {

		if (m_controllerOverrideValue == -1) {
			if (m_selectorValue != p_selectorValue) {
				printSelectors();
			}
		} else {
			if (m_controllerOverrideValue != p_controllerOverrideValue)
				System.out.println("Auton Controller override: (" + m_controllerOverrideValue + ") "
						+ commandArray.get(m_controllerOverrideValue).getName());
			// System.out.println(commandArray.get(m_controllerOverrideValue).getName());
		}

		p_selectorValue = m_selectorValue;
		p_controllerOverrideValue = m_controllerOverrideValue;
	}

	private void printSelectors() {
		// if is valid
		if (m_selectorValue >= 0 && m_selectorValue <= (commandArray.size() - 1)) {
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
