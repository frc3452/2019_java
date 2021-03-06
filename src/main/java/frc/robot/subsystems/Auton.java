package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder;
import frc.robot.auto.commands.AutoModeBuilder.ZeroPositions;
import frc.robot.auto.commands.functions.NoCommand;
import frc.robot.auto.commands.paths.center.Center_CS_Face_Left;
import frc.robot.auto.commands.paths.left.Left_Rocket_Close_Same;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;
import frc.robot.util.GZTimer;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.DigitalSelector;

import java.util.ArrayList;

/**
 * <h1>AutonSelector Subsystem</h1> Handles autonomous selector case statements
 * and printing.
 *
 * @author max
 */
public class Auton {

    private static Auton mInstance = null;
    public ArrayList<GZCommand> commandArray = null;
    public GZTimer matchTimer = new GZTimer("AutonTimer");
    private GZCommand defaultCommand = null;
    private GZCommand autonomousCommand = null;
    private int m_controllerOverrideValue = -1;
    private int p_controllerOverrideValue = m_controllerOverrideValue;
    private int m_selectorValue = 0;
    private int p_selectorValue = -1;
    private LatchedBoolean mLBAutoCancel = new LatchedBoolean();
    private LatchedBoolean mLBWaitOnAutoStart = new LatchedBoolean();
    private LatchedBoolean mLBAutoGamePiece = new LatchedBoolean();
    private boolean mWaitOnAutoStart = false;
    private boolean mAutoPieceIsHatch = true;
    private DigitalSelector mSelectorOnes = null, mSelectorTens = null;
    private LatchedBoolean mCustomAutoMoveStartPosLeft = new LatchedBoolean();
    private LatchedBoolean mCustomAutoMoveStartPosRight = new LatchedBoolean();
    private LatchedBoolean mCustomAutoMoveStartPosUp = new LatchedBoolean();
    private LatchedBoolean mCustomAutoMoveStartPosDown = new LatchedBoolean();
    private ZeroPositions mCustomAutoStartPos = null;
    private Rotation2d mCustomAutoStartingAngle = null;

    private Auton() {
        mSelectorOnes = new DigitalSelector(kAuton.SELECTOR_ONES);
        mSelectorTens = new DigitalSelector(kAuton.SELECTOR_TENS);
        // fillAutonArray();
    }

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

        commandArray.add(new GZCommand("Custom auto (Drive --> 180 --> 0", () -> new GZCommandGroup() {
            {
                tele();
                angle(Rotation2d.fromDegrees(180));
                tele();
                angle(Rotation2d.fromDegrees(0));
            }
        }));

        commandArray.add(new GZCommand("Zero odometry (Left Drive --> 180)", () -> new GZCommandGroup() {
            {
                resetPos(new Left_Rocket_Close_Same().getLeft());
                tele();
                angle(Rotation2d.fromDegrees(180));
                tele();
            }
        }));

        commandArray.add(new GZCommand("Zero odometry (Center)", () -> new GZCommandGroup() {
            {
                resetPos(new Center_CS_Face_Left().getLeft());
                tele();
            }
        }));

        commandArray.add(new GZCommand("Zero odometry (Right Drive--> 180)", () -> new GZCommandGroup() {
            {
                resetPos(new Left_Rocket_Close_Same().getRight());
                tele();
                angle(Rotation2d.fromDegrees(180));
                tele();
            }
        }));

        commandArray.add(new GZCommand("Place (ScoreHatch)", () -> new GZCommandGroup() {
            {
                // add(new GoToHeight(Heights.HP_2));
                tele();
                // add(new RunAction(Actions.SCORE_HATCH));
            }
        }));

        commandArray.add(new GZCommand("Place High (Level 2)", () -> new GZCommandGroup() {
            {
                tele();
                // add(new GoToHeight(Heights.HP_2));
                tele();
                // add(new RunAction(Actions.SCORE_HATCH));
                tele();
                // angleR(Rotation2d.fromDegrees(180));
                tele();
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
        // if (autonomousCommand == null)
        // return Superstructure.getInstance().fakeAutoScore();

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

    private void controllerChooser() {
        customAuto();

        if (GZOI.driverJoy.leftBumper.isBeingPressed() && GZOI.driverJoy.rightBumper.isBeingPressed()) {
            if (GZOI.driverJoy.aButton.wasActivated()) {
                m_controllerOverrideValue++;
                sanityCheckControllerValue();
            } else if (GZOI.driverJoy.bButton.wasActivated()) {
                m_controllerOverrideValue--;
                sanityCheckControllerValue();
            } else if (GZOI.driverJoy.rightCenterClick.wasActivated()) {
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
        position = mCustomAutoStartPos.position;

        System.out.println("Zeroing odometry to " + mCustomAutoStartPos);
        Drive.getInstance().zeroOdometry(new RigidTransform2d(position, mCustomAutoStartingAngle.inverse()));
    }

    private void updateCustomAutoError(String msg) {
        System.out.println("WARNING Cannot update odometry [" + msg + "]");
    }

    private void customAuto() {
        if (GZOI.driverJoy.backButton.isBeingPressed()) {
//			System.out.println("Pressed!!");
            if (GZOI.driverJoy.leftCenterClick.longPressed()) {
                updateCustomAuto();
            } else if (GZOI.driverJoy.rightCenterClick.wasActivated()) {
                System.out.println("WARNING Custom auto deselected!");
                mCustomAutoStartPos = null;
                mCustomAutoStartingAngle = null;
            } else {
                Translation2d newAngle = GZOI.driverJoy.getRightAnalogAngle();
                Rotation2d mappedAngle = newAngle.direction().nearestCardinalPlus();

                // System.out.println(mappedAngle + "\t" + newAngle + "\t" + newAngle.norm());

                if (Math.abs(newAngle.norm()) > .2) {
                    if (mCustomAutoStartingAngle == null || !mCustomAutoStartingAngle.equals(mappedAngle)) {
                        System.out.println("WARNING Custom auto angle set to " + mappedAngle.getNormalDegrees());
                        mCustomAutoStartingAngle = mappedAngle;
                    }
                }

                if (mCustomAutoMoveStartPosLeft.update(GZOI.driverJoy.getLeftAnalogX() < -.5)) {
                    if (mCustomAutoStartPos == null)
                        mCustomAutoStartPos = ZeroPositions.CENTER;
                    else if (mCustomAutoStartPos == ZeroPositions.CENTER)
                        mCustomAutoStartPos = ZeroPositions.LEFT;
                    else if (mCustomAutoStartPos == ZeroPositions.RIGHT)
                        mCustomAutoStartPos = ZeroPositions.CENTER;

                    customAutoStartPosUpdate();
                } else if (mCustomAutoMoveStartPosRight.update(GZOI.driverJoy.getLeftAnalogX() > .5)) {
                    if (mCustomAutoStartPos == null)
                        mCustomAutoStartPos = ZeroPositions.CENTER;
                    else if (mCustomAutoStartPos == ZeroPositions.CENTER)
                        mCustomAutoStartPos = ZeroPositions.RIGHT;
                    else if (mCustomAutoStartPos == ZeroPositions.LEFT)
                        mCustomAutoStartPos = ZeroPositions.CENTER;
                    customAutoStartPosUpdate();
                } else if (mCustomAutoMoveStartPosDown.update(GZOI.driverJoy.getLeftAnalogY() < -.5)) {
                    if (mCustomAutoStartPos == null) {
                    } else if (mCustomAutoStartPos == ZeroPositions.LEFT_2) {
                        mCustomAutoStartPos = ZeroPositions.LEFT;
                    } else if (mCustomAutoStartPos == ZeroPositions.RIGHT_2) {
                        mCustomAutoStartPos = ZeroPositions.RIGHT;
                    }
                    customAutoStartPosUpdate();
                } else if (mCustomAutoMoveStartPosUp.update(GZOI.driverJoy.getLeftAnalogY() > .5)) {
                    if (mCustomAutoStartPos == null) {
                    } else if (mCustomAutoStartPos == ZeroPositions.LEFT) {
                        mCustomAutoStartPos = ZeroPositions.LEFT_2;
                    } else if (mCustomAutoStartPos == ZeroPositions.RIGHT) {
                        mCustomAutoStartPos = ZeroPositions.RIGHT_2;
                    }
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

    public enum AV {
        CURRENT,
    }
}
