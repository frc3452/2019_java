package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator.QueueHeights;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Drive.ClimbingState;
import frc.robot.util.*;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.drivers.GZJoystick;

import static frc.robot.Constants.kDrivetrain.NO_SHIFTER;

public class GZOI extends GZSubsystem {
    public static GZJoystick driverJoy = new GZJoystick(0, .09);
    public static GZJoystick op = new GZJoystick(1);

    // private GZSolenoid mLeds;
    private static GZOI mInstance = null;

    // private GZAnalogInput mKey = new GZAnalogInput(this, "Lockout key",
    // kOI.LOCK_OUT_KEY, kOI.LOCK_OUT_KEY_VOLT);
    private UsbCamera mCamera;
    private LatchedBoolean mUserButton = new LatchedBoolean();
    private boolean mWasTele = false, mWasAuto = false, mWasTest = false;
    private int mDisabledPrintOutLoops = 0;
    private boolean mSafetyDisable = false;
    private Drive drive = Drive.getInstance();

    // private Auton auton = Auton.getInstance();
    private Superstructure supe = Superstructure.getInstance();
    private boolean mShouldUseConfigurableDrive = false;

    private boolean hasOperatorEverInteracted = false;
//    private boolean mComplexOperatorControlsEnabled = true;

    private GZOI() {
        mCamera = CameraServer.getInstance().startAutomaticCapture(0);

        cameraSettings();

        driverJoy.setLongPressDuration(0.20);

        driverJoy.startButton.setLongPressDuration(0.5);
    }

    public static GZOI getInstance() {
        if (mInstance == null)
            mInstance = new GZOI();

        return mInstance;
    }

    public boolean shouldUseConfigurableDrive() {
        return mShouldUseConfigurableDrive;
    }

    public void resetButtons() {
        driverJoy.resetButtons();
        op.resetButtons();
    }

    private void cameraSettings() {
        mCamera.setResolution(160, 120);
        mCamera.setBrightness(30);
    }

    @Override
    public void loop() {
        driverJoy.update();
        op.update();

        // FLAGS
        if (isTele())
            mWasTele = true;
        else if (isAuto())
            mWasAuto = true;
        else if (isTest())
            mWasTest = true;

        // SAFTEY DISABLED
        // boolean safteyDisable = false;
        // if (isFMS())
        // safteyDisable = false;
        // else if (getSafteyKey())
        // safteyDisable = true;
        // else if (mUserButton.update(RobotController.getUserButton()))
        // safteyDisable = !mSafetyDisable;

        // if (mSafetyDisable != safteyDisable) {
        // mSafetyDisable = safteyDisable;
        // Robot.allSubsystems.disable(mSafetyDisable);
        // System.out.println("WARNING All subsystems " + (mSafetyDisable ? "disabled" :
        // "enabled") + "!");
        // }

        // if (mSafetyDisable) {
        // if (++mDisabledPrintOutLoops > 300) {
        // System.err.println("ERROR All subsystems disabled, check Saftey Key or toggle
        // UserButton");
        // mDisabledPrintOutLoops = 0;
        // }
        // }

        if (mUserButton.update(RobotController.getUserButton())) {
            mShouldUseConfigurableDrive = !mShouldUseConfigurableDrive;
            System.out.println("[ConfigurableDrive] " + (mShouldUseConfigurableDrive ? "enabled" : "disabled"));
        }

//        if (mUserButton.update(RobotController.getUserButton())) {
//            mComplexOperatorControlsEnabled = !mComplexOperatorControlsEnabled;
//            System.out.println("Operator controls set to [" + (mComplexOperatorControlsEnabled ? "COMPLEX" : "SIMPLE") + "]");
//        }

        // Disabled
        if (isDisabled()) {
            disabled();
        } else if (Auton.getInstance().isAutoControl()) { // running auto command
            Auton.getInstance()
                    .controllerCancel(driverJoy.aButton.isBeingPressed() && driverJoy.xButton.isBeingPressed());
        } else if (isAuto() || isTele()) { // not running auto command and in sandstorm or tele
            handleControls(isAuto());
        }
    }

    public void handleControls(boolean sandstorm) {
        final boolean climbing = Drive.getInstance()
                .isClimbing();
        handleRumble();
        handleSuperStructureControl(climbing);
        handleDriverController();
        if (!shouldUseConfigurableDrive()) {
            handleDriverSupe(sandstorm, climbing);
        }
    }

    public boolean hasOperatorEverInteracted() {
        return hasOperatorEverInteracted;
    }

    private void handleSuperStructureControl(boolean climbing) {
//        if (mComplexOperatorControlsEnabled) {
//        } else {
//            handleSuperStructureControlBasic();
//        }
    }

    private void handleSuperStructureControlBasic() {
        boolean nothingHasHappened;
            nothingHasHappened = true;
        

        if (!nothingHasHappened) {
            hasOperatorEverInteracted = true;
        }
    }

    private void handleRumble() {
        double driverRumble = 0;
        double opRumble = 0;

        
        if (!drive.isSlow()) {
            driverRumble = Math.max(0.1, driverRumble);
        }
        if (GZUtil.between(getMatchTime(), 29.1, 30)) {
            driverRumble = Math.max(.45, driverRumble);
            opRumble = Math.max(.45, opRumble);
        }

        if (!driverJoy.isRumbling()) {
            driverJoy.setRumble(driverRumble);
        }

        if (!op.isRumbling()) {
            op.setRumble(opRumble);
        }
    }

    private void disabled() {
        Auton.getInstance().autonChooser();

        Auton.getInstance().toggleAutoWait(driverJoy.aButton.isBeingPressed() && driverJoy.yButton.isBeingPressed());
        Auton.getInstance()
                .toggleAutoGamePiece(driverJoy.aButton.isBeingPressed() && driverJoy.xButton.isBeingPressed());


        // handleRumble();
    }

    public void stopRumble() {
        rumble(0.0);
    }

    private void rumble(double rumble) {
        driverJoy.setRumble(rumble);
        op.setRumble(rumble);
    }


    // Driver variables
    private void handleDriverController() {
        if (driverJoy.leftBumper.isBeingPressed()) {
            if (!NO_SHIFTER) {
                if (driverJoy.aButton.isBeingPressed())
                    drive.wantShift(ClimbingState.NONE);
                else if (driverJoy.bButton.isBeingPressed())
                    drive.wantShift(ClimbingState.FRONT);
                else if (driverJoy.xButton.isBeingPressed())
                    drive.wantShift(ClimbingState.BOTH);
                else if (driverJoy.yButton.isBeingPressed())
                    drive.wantShift(ClimbingState.REAR);
            }

        } else {
            if (driverJoy.aButton.wasActivated() && !driverJoy.xButton.isBeingPressed()) {
                drive.toggleSlowSpeed();
            }
        }



        drive.handleDriving(driverJoy);
    }

    private void handleDriverSupe(boolean sandstorm, boolean climbing) {

                }
                

    public String getSmallString() {
        // no motors, so not really used but
        return "GZOI";
    }

    public void addLoggingValues() {
        new LogItem("BATTERY-VOLTAGE") {
            @Override
            public String val() {
                return String.valueOf(RobotController.getBatteryVoltage());
            }
        };

        new LogItem("BROWNED-OUT") {
            @Override
            public String val() {
                return String.valueOf(RobotController.isBrownedOut());
            }
        };

        new LogItem("PDP-TEMP") {
            @Override
            public String val() {
                return String.valueOf(GZPDP.getInstance().getTemperature());
            }
        };
        GZLog.addAverageLeft("PDP-TEMP-AVG");

        new LogItem("PDP-AMP") {
            @Override
            public String val() {
                return String.valueOf(GZPDP.getInstance().getTotalCurrent());
            }
        };
        GZLog.addAverageLeft("PDP-AMP-AVG");

        new LogItem("PDP-VOLT") {

            @Override
            public String val() {
                return String.valueOf(GZPDP.getInstance().getVoltage());
            }
        };
        GZLog.addAverageLeft("PDP-VOLT-AVG");

        new LogItem("DRIVE-STATE") {
            @Override
            public String val() {
                return Drive.getInstance().getStateString();
            }
        };

        new LogItem("SUPR-STATE") {
            @Override
            public String val() {
                return Superstructure.getInstance().getStateString();
            }
        };

    }

    /**
     * A physical key on the robot to shut off
     */
    public boolean getSafteyKey() {
        return false;
        // return mKey.get();
    }

    public void setSafteyDisableForAllSystems(boolean disable) {
        this.mSafetyDisable = disable;
    }

    public boolean isFMS() {
        return DriverStation.getInstance().isFMSAttached();
    }

    public synchronized Alliance getAlliance() {
        return DriverStation.getInstance().getAlliance();
    }

    public boolean isRed() {
        if (DriverStation.getInstance().getAlliance() == Alliance.Red)
            return true;

        return false;
    }

    public double getMatchTime() {
        return DriverStation.getInstance().getMatchTime();
    }

    public boolean isAuto() {
        return DriverStation.getInstance().isAutonomous() && isEnabled();
    }

    public boolean isDisabled() {
        return DriverStation.getInstance().isDisabled();
    }

    public boolean isEnabled() {
        return DriverStation.getInstance().isEnabled();
    }

    public boolean isTele() {
        return isEnabled() && !isAuto() && !isTest();
    }

    public boolean isTest() {
        return DriverStation.getInstance().isTest();
    }

    public boolean wasTele() {
        return mWasTele;
    }

    public boolean wasAuto() {
        return mWasAuto;
    }

    public boolean wasTest() {
        return mWasTest;
    }

    @Override
    public String getStateString() {
        return "NA";
    }

    public void stop() {
    }

    protected void in() {
    }

    protected void out() {
    }

    protected void initDefaultCommand() {
    }

    public enum Level {
        LOW, MEDIUM, HIGH
    }
}