package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.ConfigurableDrive.ConfigurableDrive;
import frc.robot.ConfigurableDrive.DriveSignal;
import frc.robot.Constants.*;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.auto.commands.AutoModeBuilder.ZeroPositions;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.paths.left.Left_Rocket_Far_Same_Backwards;
import frc.robot.poofs.Kinematics;
import frc.robot.poofs.RobotState;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.control.PathFollower;
import frc.robot.poofs.util.drivers.NavX;
import frc.robot.poofs.util.math.*;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.*;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPID.GZPIDPair;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.motorcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.GZSRX.LimitSwitchDirections;
import frc.robot.util.drivers.motorcontrollers.GZSmartSpeedController;
import frc.robot.util.drivers.motorcontrollers.GZSmartSpeedController.Master;
import frc.robot.util.drivers.motorcontrollers.GZSmartSpeedController.Side;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController.Breaker;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.requests.Request;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Drive extends GZSubsystem {
    private static Drive mInstance = null;
    public IO mIO = new IO();
    // DRIVETRAIN
    public GZSRX L1, L2, L3, L4, R1, R2, R3, R4;
    DecimalFormat df = new DecimalFormat("#0.00");
    Notifier mShiftNotifier = new Notifier(new Runnable() {
        public void run() {
        }
    });
    GZPID oldPID = new GZPID();
    ShouldTurnAfterPath mTurnAfterPath = ShouldTurnAfterPath.NONE;
    private GZSolenoid mShifterFront, mShifterRear;
    // Force switch state to neutral on start up
    private DriveState mState = DriveState.OPEN_LOOP;
    private DriveState mWantedState = DriveState.NEUTRAL;
    private GZPIDPair mCurrentPID = kDrivetrain.PID;
    // GYRO
    private NavX mNavX;
    private double mModifyPercent = 1;
    private boolean mIsSlow = false;
    private double mTimeWithoutInputToAllowAutomatic = 0.125;
    private double mTimeLastDrivingInputReceived = 0.0;
    private boolean motionMagicMovementComplete = false;
    private Rotation2d mTurnToHeadingGoal = null;
    private boolean mTurnToHeadingComplete = false;
    private Double mTurnToHeadingLeftT = null;
    private Double mTurnToHeadingRightT = null;
    private double curvatureDriveQuickStopThreshold = .2;
    private double curvatureDriveQuickStopAlpha = .1; // .1
    private double curvatureDriveQuickStopAccumulator;
    private GZFile mPIDConfigFile = null;
    private PathFollower mPathFollower;
    private Path mCurrentPath = null;
    private RobotState mRobotState = RobotState.getInstance();
    private ClimbingState mClimbState = ClimbingState.NONE;
    private ConfigurableDrive mConfigurableDrive;
    private PathFollower.Parameters mParameters = kPathFollowing.pathFollowingConstants;
    private RobotPose mShuffleboardPose = new RobotPose();

    private Drive() {

        mConfigurableDrive = new ConfigurableDrive(() -> GZOI.getInstance().isDisabled(),
                () -> GZOI.driverJoy.POV0.isBeingPressed(), () -> GZOI.driverJoy.POV180.isBeingPressed(), .25, false) {
            public double getModifier() {
                return mModifyPercent;
            }
        };

        mConfigurableDrive.addDisabled();
        mConfigurableDrive.addFieldCentric(() -> GZOI.driverJoy.getLeftAnalogX(), () -> GZOI.driverJoy.getLeftAnalogY(),
                () -> GZOI.driverJoy.getRightAnalogX(), () -> GZOI.driverJoy.getRightAnalogY(),
                () -> getGyroAngle().inverse().getNormalDegrees(), 45, .15, .55, .23);
        mConfigurableDrive.addStandardDriveStyles(GZOI.driverJoy);

        L1 = new GZSRX.Builder(kDrivetrain.L1, this, "L1", kPDP.DRIVE_L_1).setMaster().setSide(Side.LEFT).build();
        L2 = new GZSRX.Builder(kDrivetrain.L2, this, "L2", kPDP.DRIVE_L_2).setFollower().setSide(Side.LEFT).build();
        L3 = new GZSRX.Builder(kDrivetrain.L3, this, "L3", kPDP.DRIVE_L_3).setFollower().setSide(Side.LEFT).build();
        L4 = new GZSRX.Builder(kDrivetrain.L4, this, "L4", kPDP.DRIVE_L_4).setFollower().setSide(Side.LEFT).build();

        R1 = new GZSRX.Builder(kDrivetrain.R1, this, "R1", kPDP.DRIVE_R_1).setMaster().setSide(Side.RIGHT).build();
        R2 = new GZSRX.Builder(kDrivetrain.R2, this, "R2", kPDP.DRIVE_R_2).setFollower().setSide(Side.RIGHT).build();
        R3 = new GZSRX.Builder(kDrivetrain.R3, this, "R3", kPDP.DRIVE_R_3).setFollower().setSide(Side.RIGHT).build();
        R4 = new GZSRX.Builder(kDrivetrain.R4, this, "R4", kPDP.DRIVE_R_4).setFollower().setSide(Side.RIGHT).build();

        mNavX = new NavX(SPI.Port.kMXP);

        mShifterFront = new GZSolenoid(kSolenoids.SHIFTER_FRONT, this, "Shifter-Front");
        mShifterRear = new GZSolenoid(kSolenoids.SHIFTER_REAR, this, "Shifter-Rear");

        mShifterFront.off();
        mShifterRear.off();

        try {
            mPIDConfigFile = GZFileMaker.getFile("DrivePID", new Folder(""), FileExtensions.CSV, false, false);
        } catch (Exception e) {
        }

        brake(false);

        talonInit();

        // REMOTE LIMIT SWITCHES
        // For applications where the Talon Tach is pointing to a non-reflective surface
        // or open air (LED is on) when motor
        // movement is allowed, the Talon Tach should be treated as a NC limit switch.
        // For applications where the Talon Tach is pointing to a reflective surface
        // when motor movement is allowed (LED is
        // off), it should be treated as a NO limit switch.

        // https://www.ctr-electronics.com/downloads/pdf/Talon%20Tach%20User's%20Guide.pdf

        // GZSRX.logError(
        // L1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
        // LimitSwitchNormal.NormallyOpen),
        // this, AlertLevel.ERROR, "Could not config forward limit on L1");

        // GZSRX.logError(
        // R1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
        // LimitSwitchNormal.NormallyOpen),
        // this, AlertLevel.ERROR, "Could not config forward limit on R1");

        L1.setUsingRemoteLimitSwitchOnTalon(this, L2, LimitSwitchNormal.NormallyOpen, LimitSwitchDirections.BOTH);
        R1.setUsingRemoteLimitSwitchOnTalon(this, R2, LimitSwitchNormal.NormallyOpen, LimitSwitchDirections.BOTH);

        L1.disabledLimitSwitch(this, LimitSwitchDirections.BOTH);
        R1.disabledLimitSwitch(this, LimitSwitchDirections.BOTH);

        L2.disabledLimitSwitch(this, LimitSwitchDirections.BOTH);
        R2.disabledLimitSwitch(this, LimitSwitchDirections.BOTH);

        setPID(L1, R1, kDrivetrain.PID);

        zeroEncoders();

        enableFollower();

        in();
        if (getFrontBottomLimit() && getFrontTopLimit())
            Health.getInstance().addAlert(this, AlertLevel.ERROR, "Both PTO front limit switches tripped!");
        if (getRearBottomLimit() && getRearTopLimit())
            Health.getInstance().addAlert(this, AlertLevel.ERROR, "Both PTO rear limit switches tripped!");

        mNavX.reset();

        checkFirmware();

        SmartDashboard.putData(mShuffleboardPose);
    }

    public synchronized static Drive getInstance() {
        if (mInstance == null)
            mInstance = new Drive();
        return mInstance;
    }

    private static double inchesPerSecondToTicksPer100ms(double inches_per_second) {
        return rpmToTicksPer100ms(inchesPerSecondToRpm(inches_per_second));
    }

    private static Double rotationsToInches(double rotations) {
        return rotations * (kDrivetrain.WHEEL_DIAMATER_IN * Math.PI);
    }

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    private static double rpmToTicksPer100ms(double rpm) {
        return rpm * (1.0 / 60.0) * 4096.0 * (1.0 / 10.0);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double inchesToRotations(double inches) {
        return inches / (kDrivetrain.WHEEL_DIAMATER_IN * Math.PI);
    }

    public PathFollower.Parameters getParameters() {
        return mParameters;
    }

    public void printNavX() {
        System.out.println(this.mNavX.toString());
    }

    public synchronized RigidTransform2d getOdometry() {
        return RobotState.getInstance().getOdometry();
    }

    public synchronized void printOdometry() {
        System.out.println("Odomet2ry: " + getOdometry().toString() + "\t" + mNavX.getFusedHeading());
    }

    public void setDefaultStartingPosition() {
        zeroOdometry(new RigidTransform2d(ZeroPositions.LEFT.position, new Rotation2d(0)));
    }

    private synchronized void updateVelocitySetpoint(double left_inches_per_sec, double right_inches_per_sec) {
        final double max_desired = Math.max(Math.abs(left_inches_per_sec), Math.abs(right_inches_per_sec));
        final double scale = max_desired > getParameters().high_gear_setpoint
                ? getParameters().high_gear_setpoint / max_desired
                : 1.0;
        mIO.left_desired_output = inchesPerSecondToTicksPer100ms(left_inches_per_sec * scale);
        mIO.right_desired_output = -inchesPerSecondToTicksPer100ms(right_inches_per_sec * scale);
    }

    public synchronized boolean hasPassedMarker(String marker) {
        if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
            return mPathFollower.hasPassedMarker(marker);
        } else {
            System.out.println("Robot is not in path following mode, cannot determine if marker passed");
            return false;
        }
    }

    private void updatePathFollower() {
        RigidTransform2d robot_pose = mRobotState.getLatestFieldToVehicle().getValue();
        // System.out.println(robot_pose.getTranslation().toString());
        Twist2d command = mPathFollower.update(Timer.getFPGATimestamp(), robot_pose,
                RobotState.getInstance().getDistanceDriven(), RobotState.getInstance().getPredictedVelocity().dx);
        if (!mPathFollower.isFinished()) {
            Kinematics.DriveVelocity setpoint = Kinematics.inverseKinematics(command);
            updateVelocitySetpoint(setpoint.left, setpoint.right);
        } else {
            // updateVelocitySetpoint(0, 0);
        }
    }

    public Request turnToHeadingRequest(Rotation2d headingTarget, boolean relative) {
        return new Request() {

            @Override
            public void act() {
                if (relative) {
                    turnRelative(headingTarget);
                } else {
                    turnToHeading(headingTarget);
                }
            }

            @Override
            public boolean isFinished() {
                return mTurnToHeadingComplete || wantsToTeleDrive();
            }
        };
    }

    public void velocityStop() {
        setVelocity(0, 0);
    }

    public synchronized void zeroOdometry(PathContainer pathContainer) {
        zeroOdometry(pathContainer.getStartPose());
    }

    public synchronized void zeroOdometry(final RigidTransform2d pose) {
        RobotState.getInstance().reset(Timer.getFPGATimestamp(), pose);
        Drive.getInstance().setGyroAngle(pose.getRotation());
    }

    public synchronized void zeroGyro() {
        setGyroAngle(Rotation2d.fromDegrees(0));
    }

    public synchronized void setWantDrivePath(PathContainer pathContainer) {
        setWantDrivePath(pathContainer, ShouldTurnAfterPath.NONE);
    }

    private synchronized void setWantDrivePath(PathContainer pathContainer, ShouldTurnAfterPath turnAfterPath) {
        mTurnAfterPath = turnAfterPath;

        Path mPath = pathContainer.buildPath();
        final boolean reversed = pathContainer.isReversed();
        GZPIDPair newPID = pathContainer.getPID();

        if (!newPID.equalTo(mCurrentPID)) {
            setPID(mCurrentPID);
        }

        mCurrentPID = newPID;

        if (mCurrentPath != mPath || mState != DriveState.PATH_FOLLOWING) {
            setWantedState(DriveState.PATH_FOLLOWING);
            handleStates();
            RobotState.getInstance().resetDistanceDriven();
            mPathFollower = new PathFollower(mPath, reversed, pathContainer.getParameters());
            mCurrentPath = mPath;
        } else {
            // stop();
            // setVelocitySetpoint(0, 0);
        }
    }

    public synchronized boolean isDoneWithPath() {
        if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
            return mPathFollower.isFinished();
        } else {
            System.out.println("Robot is not in path following mode, cannot determine if done.");
            return true;
        }
    }

    public synchronized void forceDoneWithPath() {
        if (mState == DriveState.PATH_FOLLOWING && mPathFollower != null) {
            mPathFollower.forceFinish();
        } else {
            System.out.println("Robot is not in path following mode, cannot force path done.");
        }
    }

    public double getLeftVelocityInchesPerSec() {
        return rpmToInchesPerSecond(Units.ticks_to_rotations(mIO.left_encoder_vel));
    }

    public double getRightVelocityInchesPerSec() {
        return rpmToInchesPerSecond(Units.ticks_to_rotations(-mIO.right_encoder_vel));
    }

    public double getLeftDistanceInches() {
        Double ret = rotationsToInches(getLeftRotations());
        if (ret.isNaN())
            return 0.0;

        return ret;
    }

    public double getRightDistanceInches() {
        Double ret = rotationsToInches(getRightRotations());
        if (ret.isNaN())
            return 0.0;

        return ret;
    }

    public synchronized Rotation2d getGyroAngle() {
        return mNavX.getYaw().inverse();
    }

    public synchronized void setGyroAngle(Rotation2d angle) {
        mNavX.reset();
        mNavX.setAngleAdjustment(angle);
    }

    public synchronized void setVelocity(double left, double right) {
        setWantedState(DriveState.VELOCITY);
        mIO.left_desired_output = left;
        mIO.right_desired_output = right;
    }

    public String getSmallString() {
        return "DRV";
    }

    private void handleTurnToHeading() {
        if (mState == DriveState.TURN_TO_HEADING) {
            if (encoderAngleIsDone()) {
                mTurnToHeadingComplete = true;
                // velocityStop();
            }
        }
    }

    public Request setAngleRequest(Rotation2d angle) {
        return new Request() {

            @Override
            public void act() {
                setGyroAngle(angle);
            }
        };
    }

    public Request setOdometryRequest(Translation2d translation) {
        return new Request() {

            @Override
            public void act() {
                Rotation2d r = getOdometry().getRotation();
                zeroOdometry(new RigidTransform2d(translation, r));
            }
        };
    }

    public Request openLoopRequest(ArcadeSignal signal) {
        return openLoopRequest(signal.getMove(), signal.getRotate(), signal.getTimeout());
    }

    public Request openLoopRequest(double move, double rotate, double timeout) {
        return new Request() {
            double startTime = 0.0;
            double waitTime = timeout;

            @Override
            public void act() {
                startTime = Timer.getFPGATimestamp();
                arcade(move, rotate);
            }

            @Override
            public boolean isFinished() {
                boolean done = false;

                if ((Timer.getFPGATimestamp() - startTime) > waitTime) {
                    System.out.println("Open loop request timed out");
                    done = true;
                }

                boolean tele = wantsToTeleDrive();
                if (tele) {
                    System.out.println("Wants to drive, cancelling open loop");
                }

                done |= tele;

                if (done) {
                    // velocityStop();
                }
                return done;
            }
        };
    }

    public Request headingRequest(Rotation2d heading, boolean absolute) {
        return new Request() {

            @Override
            public void act() {
                if (!isFinished()) {
                    if (absolute) {
                        turnToHeading(heading);
                    } else {
                        turnRelative(heading);
                    }
                }
            }

            @Override
            public boolean isFinished() {
                return mTurnToHeadingComplete || wantsToTeleDrive();
            }
        };
    }

    public Pose2d getFixedPose() {
        RigidTransform2d pose = new RigidTransform2d(getOdometry());
        return new Pose2d(pose.getTranslation(), getGyroAngle());
    }

    public Rocket odometryNearestRocket() {
        Pose2d pose = getFixedPose();
        Translation2d p = pose.getTranslation();

        if (p.insideRange(new Translation2d(165, 269), new Translation2d(229.28, 324))) {
            return Rocket.LEFT_NEAR;
        } else if (p.insideRange(new Translation2d(229.28, 269), new Translation2d(293.56, 324))) {
            return Rocket.LEFT_FAR;
        } else if (p.insideRange(new Translation2d(165, 0), new Translation2d(229.28, 324 - 269))) {
            return Rocket.RIGHT_NEAR;
        } else if (p.insideRange(new Translation2d(229.28, 0), new Translation2d(293.56, 324 - 269))) {
            return Rocket.RIGHT_FAR;
        } else {
            return Rocket.NONE;
        }
    }

    public RocketIdentifcation getRocket() {

        Rocket rocket = Rocket.NONE;
        final String how;
        boolean recalibrateWithGyro = false;

        Pose2d here = new Pose2d(getOdometry().getTranslation(), getGyroAngle());

        Rocket odometryRocket = odometryNearestRocket();
        Rocket gyroRocket = Rocket.NONE;

        // Gyro only valid if above hatch 1
        if (Elevator.getInstance().getHeightInches() > Heights.Cargo_1.inches) {
            // Use gyro angle to determine which one we're placing on
            // Pose2d editedPose = new Pose2d(here.getTranslation(),
            // here.getRotation().inverse());
            gyroRocket = Rocket.closestByAngle(getGyroAngle(), 10);
        }

        // Both forms of odometry acquired
        if (odometryRocket.identified() && gyroRocket.identified()) {
            // They agree
            if (odometryRocket.equals(gyroRocket)) {
                rocket = odometryRocket;
                how = "Rocket indentification: agree";
                recalibrateWithGyro = true;
            } else {
                // They don't agree
                rocket = Rocket.NONE;
                how = "NO Rocket identification: unequal";
            }
            // No way of identifying
        } else if (odometryRocket.unsure() && gyroRocket.unsure()) {
            rocket = Rocket.NONE;
            how = "NO Rocket identification: Both unsure";
        } else if (odometryRocket.identified()) {
            rocket = odometryRocket;
            how = "Rocket identification: odometry";
        } else if (gyroRocket.identified()) {
            rocket = gyroRocket;
            how = "Rocket identification: gyro";
            recalibrateWithGyro = true;
        } else {
            rocket = Rocket.NONE;
            how = "Rocket identification: What, this wasn't a real case: " + "Odom identified: " + odometryRocket
                    + "\tGyro identified: " + gyroRocket;
        }

        // how += odometryRocket + "\t" + gyroRocket;

        return new RocketIdentifcation(rocket, how, recalibrateWithGyro, here);
    }

    public Request jogRequest(EncoderMovement movement) {
        return jogRequest(movement, true);
    }

    public Request jogRequest(EncoderMovement movement, boolean wait) {
        return new Request() {

            @Override
            public void act() {
                if (!isFinished()) {
                    jog(movement);
                }
            }

            @Override
            public boolean isFinished() {
                if (wait)
                    return motionMagicMovementComplete || wantsToTeleDrive();
                return true;
            }
        };
    }

    public boolean configDriveDisabled() {
        return mConfigurableDrive.isDisabled();
    }

    private synchronized void out() {
        DriveSignal mConfigOutput = DriveSignal.NEUTRAL;
        final boolean useConfig = GZOI.getInstance().shouldUseConfigurableDrive();
        if (useConfig) {
            mConfigOutput = mConfigurableDrive.update();
        }

        switch (mState) {
            case PATH_FOLLOWING:
                if (mPathFollower != null) {
                    updatePathFollower();
                    if (isDoneWithPath()) {
                        if (mTurnAfterPath == ShouldTurnAfterPath.LEFT) {
                            turnFarRocketLeft();
                        } else if (mTurnAfterPath == ShouldTurnAfterPath.RIGHT) {
                            turnFarRocketRight();
                        }
                    }

                }
                break;
            case CLIMB:
                handleClimbing(GZOI.driverJoy);
                break;
            case OPEN_LOOP_DRIVER:
                if (!useConfig || mConfigurableDrive.isDisabled()) {
                    arcade(GZOI.driverJoy);
                } else {
                    mIO.left_desired_output = mConfigOutput.getLeft();
                    mIO.right_desired_output = -mConfigOutput.getRight();
                }
                break;
            case CLOSED_LOOP_DRIVER:
                arcadeClosedLoop(GZOI.driverJoy);
                break;
            case MOTION_MAGIC:
                if (encoderJogIsDone()) {
                    motionMagicMovementComplete = true;
                    velocityStop();
                }
                break;
            case DEMO:
                alternateArcade(GZOI.driverJoy);
                break;
            case TURN_TO_HEADING:
                handleTurnToHeading();
                break;
        }

        if (mState != DriveState.NEUTRAL) {
            handleLimitSwitches();
        } else {
            mIO.left_output = 0;
            mIO.right_output = 0;
        }

        L1.set(mState.controlMode, mIO.left_output);
        R1.set(mState.controlMode, mIO.right_output);

        if (kDrivetrain.TUNING) {
            GZPID temp = getGainsFromFile(0);
            if (!oldPID.equals(temp)) {
                oldPID = temp;
                setPID(L1, oldPID);
                setPID(R1, oldPID); // both top line
                System.out.println("PID Updated!" + "\t" + Timer.getFPGATimestamp());
                // GZOI.getInstance().addRumble(Rumble.HIGH);
            }
        }
    }

    private GZPID getGainsFromFile(int line) {
        return GZUtil.getGainsFromFile(mPIDConfigFile, line);
    }

    @Override
    public void addLoggingValues() {
        new LogItem("X-ODO") {
            @Override
            public String val() {
                return getOdometry().getTranslation().x() + "";
            }
        };
        new LogItem("Y-ODO") {
            @Override
            public String val() {
                return getOdometry().getTranslation().y() + "";
            }
        };

        new LogItem("R-ODO") {
            @Override
            public String val() {
                return getOdometry().getRotation().getDegrees() + "";
            }
        };

        new LogItem("L-RPM") {
            @Override
            public String val() {
                return Drive.getInstance().getLeftVelNonNativeUnis().toString();
            }
        };

        new LogItem("L-ENC-PRSNT") {
            @Override
            public String val() {
                return Drive.getInstance().mIO.leftEncoderValid.toString();
            }
        };

        new LogItem("R-RPM") {
            @Override
            public String val() {
                return Drive.getInstance().getRightVelNonNativeUnits().toString();
            }
        };

        new LogItem("R-ENC-PRSNT") {
            @Override
            public String val() {
                return Drive.getInstance().mIO.rightEncoderValid.toString();
            }
        };
    }

    @Override
    public synchronized void stop() {
        setWantedState(DriveState.NEUTRAL);
    }

    public synchronized boolean setWantedState(DriveState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == this.mState;
    }

    private synchronized void switchToState(DriveState state) {
        if (mState != state) {
            onStateExit(mState);
            mState = state;
            onStateStart(mState);
        }
    }

    public synchronized void handleStates() {
        boolean neutral = false;
        neutral |= this.isSafetyDisabled();
        neutral |= mWantedState == DriveState.NEUTRAL;

        neutral |= ((mState.usesClosedLoop || mWantedState.usesClosedLoop) && !mIO.encodersValid);

        neutral |= eitherShifterTransitioning();
        neutral |= mClimbState == ClimbingState.MOVING;

        if (neutral) {
            switchToState(DriveState.NEUTRAL);
        } else if (!shiftersInDrive()) {
            switchToState(DriveState.CLIMB);
        } else {
            switchToState(mWantedState);
        }

        if (GZOI.getInstance().isDisabled())
            mClimbState = ClimbingState.NONE;
    }

    private void talonInit() {
        for (GZSRX s : mTalons) {
            String name = s.getGZName();

            // s.setSafetyEnabled(true);
            // s.setExpiration(999);

            GZSRX.logError(() -> s.configFactoryDefault(GZSRX.LONG_TIMEOUT), this, AlertLevel.ERROR,
                    "Could not factory reset Talon " + name);
            s.setInverted((s.getSide() == Side.LEFT) ? kDrivetrain.L_INVERT : kDrivetrain.R_INVERT);

            s.enableVoltageCompensation(true);

            // CURRENT LIMIT
            GZSRX.logError(
                    () -> s.configContinuousCurrentLimit(
                            s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_CONTINUOUS
                                    : kDrivetrain.AMP_30_CONTINUOUS,
                            GZSRX.TIMEOUT),
                    this, AlertLevel.WARNING, "Could not set current-limit continuous for Talon " + name);

            GZSRX.logError(
                    () -> s.configPeakCurrentLimit(
                            s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_PEAK : kDrivetrain.AMP_30_PEAK,
                            GZSRX.TIMEOUT),
                    this, AlertLevel.WARNING, "Could not set current-limit peak for Talon " + name);

            GZSRX.logError(
                    () -> s.configPeakCurrentDuration(
                            s.getBreaker() == Breaker.AMP_40 ? kDrivetrain.AMP_40_TIME : kDrivetrain.AMP_30_TIME,
                            GZSRX.TIMEOUT),
                    this, AlertLevel.WARNING, "Could not set current limit time for Talon " + name);

            s.enableCurrentLimit(true);

            GZSRX.logError(() -> s.configOpenloopRamp(kDrivetrain.OPEN_LOOP_RAMP_TIME, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set open loop ramp time for Talon " + name);

            GZSRX.logError(() -> s.configNeutralDeadband(kDrivetrain.NEUTRAL_DEADBAND, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set Neutral Deadband for Talon " + name);

            s.setSubsystem("Drive train");

            if (s.getMaster() == Master.MASTER) {

                GZSRX.logError(
                        () -> s.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, GZSRX.TIMEOUT),
                        this, AlertLevel.ERROR, "Could not setup " + s.getSide() + " encoder");
                GZSRX.logError(() -> s.setSelectedSensorPosition(0, 0, GZSRX.TIMEOUT), this, AlertLevel.ERROR,
                        "Could not zero " + s.getSide() + " encoder");

                GZSRX.logError(
                        () -> s.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                                LimitSwitchNormal.NormallyClosed),
                        this, AlertLevel.ERROR, "Could not set up " + s.getSide() + " fwd limit switch");

                if (!s.isEncoderValid())
                    Health.getInstance().addAlert(this, AlertLevel.ERROR, s.getSide() + " encoder not found");

                s.setSensorPhase(false);
            }
        }
    }

    public void setPID(GZPIDPair pair) {
        setPID(L1, R1, pair);
    }

    public void setPID(GZSRX left, GZSRX right, GZPIDPair pid) {
        left.setPID(pid.pair1, this);
        right.setPID(pid.pair2, this);
    }

    public void setPID(GZSRX talon, GZPID pid) {
        talon.setPID(pid, this);
    }

    private synchronized void checkFirmware() {
        for (GZSRX s : mTalons)
            s.checkFirmware();
    }

    private void handleLimitSwitches() {
        boolean left = !mShifterFront.isOff();
        boolean right = !mShifterRear.isOff();

        // left = false;
        // right = false;

        // System.out.println(mIO.left_desired_output + "\t" +
        // mIO.right_desired_output);

        if (left) {

            if (mIO.left_desired_output > 0) {
                if (getFrontBottomLimit()) {
                    mIO.left_output = 0;
                    // System.out.println("Case 1");
                } else {
                    mIO.left_output = mIO.left_desired_output;
                    // System.out.println("Case 2");
                }
            } else if (mIO.left_desired_output < 0) {
                if (getFrontTopLimit() && !getFrontBottomLimit()) {
                    mIO.left_output = 0;
                    // System.out.println("Case 3");
                } else {
                    mIO.left_output = mIO.left_desired_output;
                    // System.out.println("Case 4");
                }
            } else if (mIO.left_desired_output == 0) {
                // System.out.println("Case 5");
                mIO.left_output = mIO.left_desired_output;
            }

        } else {
            mIO.left_output = mIO.left_desired_output;
        }
        if (right) {
            if (mIO.right_desired_output > 0) {
                if (getRearBottomLimit())
                    mIO.right_output = 0;
                else
                    mIO.right_output = mIO.right_desired_output;
            } else if (mIO.right_desired_output < 0) {
                if (getRearTopLimit())
                    mIO.right_output = 0;
                else
                    mIO.right_output = mIO.right_desired_output;
            } else if (mIO.right_desired_output == 0) {
                mIO.right_output = mIO.right_desired_output;
            }
        } else {
            mIO.right_output = mIO.right_desired_output;
        }
        // pos down
        // neg up
    }

    public synchronized void pathFarRocketLeft() {
        setWantDrivePath(new Left_Rocket_Far_Same_Backwards(), ShouldTurnAfterPath.LEFT);
    }

    public synchronized void pathFarRocketRight() {
        setWantDrivePath(new Left_Rocket_Far_Same_Backwards().getRight(), ShouldTurnAfterPath.RIGHT);
    }

    public synchronized void turnFarRocketLeft() {
        turnToHeading(Rotation2d.fromDegrees(180 + 61.25));
    }

    public synchronized void turnFarRocketRight() {
        turnToHeading(Rotation2d.fromDegrees(90 + 61.25));
    }

    public synchronized void turnToHeading(Rotation2d angle) {
        System.out.println("Turning to heading " + angle.toString());
        mTurnToHeadingComplete = false;
        mTurnToHeadingGoal = angle;
        setWantedState(DriveState.TURN_TO_HEADING);
    }

    public synchronized void turnRelative(Rotation2d angle) {
        Rotation2d target = getGyroAngle().rotateBy(angle);
        System.out.println("Turning relatively " + angle.getNormalDegrees());
        turnToHeading(target);
    }

    private synchronized void onStateStart(DriveState newState) {
        switch (newState) {
            case TURN_TO_HEADING:
                brake(true);

                if (mTurnToHeadingGoal == null) {
                    System.out.println("ERROR Turn to heading goal null!");
                    stop();
                    return;
                }

                final double initLeft = getLeftRotations();
                final double initRight = getRightRotations();

                Rotation2d current = getGyroAngle();

                boolean shouldTurnRight = Rotation2d.shouldTurnClockwise(current, mTurnToHeadingGoal);

                double toTurn = current.difference(mTurnToHeadingGoal);
                double leftTar = initLeft + (toTurn * kDrivetrain.L_ROTATIONS_PER_DEGREE) * (shouldTurnRight ? 1.0 : -1.0);
                double rightTar = initRight
                        + (toTurn * kDrivetrain.R_ROTATIONS_PER_DEGREE) * (shouldTurnRight ? -1.0 : 1.0);

                motionMagic(false, leftTar, rightTar, kDrivetrain.TURN_TO_HEADING_MOTION_MAGIC_ACCEL,
                        kDrivetrain.TURN_TO_HEADING_MOTION_MAGIC_VEL);
                break;
            case CLIMB:
                brake(true);
                mNavX.zeroRoll();
                Superstructure.getInstance().stow();
                // Superstructure.getInstance().runAction(Actions.STOW_LOW);
                break;
            case PATH_FOLLOWING:
                brake(true);
                break;
            case VELOCITY:
                brake(true);
                break;
            case MOTION_MAGIC:
                motionMagicMovementComplete = false;
                brake(true);
                break;
            case MOTION_PROFILE:
                brake(true);
                break;
            case CLOSED_LOOP_DRIVER:
                brake(false);
                break;
            case NEUTRAL:
                // brake(false);
                // if (mIO.encodersValid) {
                // if (!(GZOI.getInstance().wasTele() || GZOI.getInstance().wasAuto())) {
                // System.out.println("First enable, coasting!");
                // brake(false);

                // } else if (noSpeedGreaterThan(10)) {
                // System.out.println("Entering neutral, braking!");
                // brake(true);
                // } else {
                // System.out.println("Entering neutral, coasting!");
                // brake(false);
                // }

                // System.out.println("Speed: " + df.format(getLeftVelocityInchesPerSec()) +
                // "\t"
                // + df.format(getRightVelocityInchesPerSec()) + "\t" +
                // noSpeedGreaterThan(10.0));
                // } else {
                // System.out.println("Encoders not good going into neutral!!!");
                // brake(GZOI.getInstance().wasTele() || GZOI.getInstance().wasAuto());
                // }

                brake((GZOI.getInstance().wasTele() || GZOI.getInstance().wasAuto()));
                break;
            case OPEN_LOOP:
                brake(true);
                break;
            case OPEN_LOOP_DRIVER:
                brake(true);
                break;
            case DEMO:
                brake(false);
                break;
            default:
                break;
        }
    }

    public synchronized void onStateExit(DriveState prevState) {
        switch (prevState) {
            case CLIMB:
                slowSpeed(true);
                break;
            case MOTION_MAGIC:
                break;
            case MOTION_PROFILE:
                break;
            case NEUTRAL:
                break;
            case OPEN_LOOP:
                break;
            case OPEN_LOOP_DRIVER:
                break;
            case DEMO:
                break;
            default:
                break;
        }
    }

    private synchronized void updateShuffleboard() {
        RigidTransform2d odo = getOdometry();
        double setY = (12 * 27) - odo.getTranslation().y();
        Rotation2d flipped = odo.getRotation().inverse();

        mShuffleboardPose.SetX(odo.getTranslation().x());
        mShuffleboardPose.SetY(setY);
        mShuffleboardPose.SetHeading(flipped.getDegrees());
    }

    private synchronized void handleCoastOnTesting() {
        if (Pneumatics.getInstance().isMotorTesting()) {
            brake(false);
        }
    }

    public boolean wantsToTeleDrive() {
        ArcadeSignal ds = getDriveModification(GZOI.driverJoy);
        boolean wantsToDrive = (Math.abs(ds.getMove()) > 0.05 || (Math.abs(ds.getRotate()) > 0.04));

        if (wantsToDrive)
            mTimeLastDrivingInputReceived = Timer.getFPGATimestamp();

        if (Timer.getFPGATimestamp() - mTimeLastDrivingInputReceived < mTimeWithoutInputToAllowAutomatic) {
            wantsToDrive = true;
        }

        return wantsToDrive;
    }

    public synchronized void loop() {
        if (kDrivetrain.TUNING) {
            SmartDashboard.putNumber("Target", mIO.left_output);
            SmartDashboard.putNumber("Actual", mIO.left_encoder_vel);
        }

        // RocketIdentifcation r = getRocket();
        // System.out.println(r.rocket +"\t" + r.how);

        SmartDashboard.putBoolean("Fast", !mIsSlow);

        wantsToTeleDrive();
        handleCoastOnTesting();
        updateShuffleboard();
        handleStates();
        in();
        out();
    }

    public Double getLeftRotations() {
        return Units.ticks_to_rotations(mIO.left_encoder_ticks);
    }

    public boolean getTurnToHeadingComplete() {
        return mTurnToHeadingComplete;
    }

    public Double getLeftVelNonNativeUnis() {
        return Units.ticks_to_rotations(mIO.left_encoder_vel);
    }

    public Double getRightRotations() {
        return -Units.ticks_to_rotations(mIO.right_encoder_ticks);
    }

    public Double getRightVelNonNativeUnits() {
        return -Units.ticks_to_rotations(mIO.right_encoder_vel);

    }

    // TODO TUNE
    public boolean getFrontTopLimit() {
        // if (Constants.COMP_BOT)
        return mIO.ls_left_rev;
        // return mIO.ls_left_fwd;
    }

    public boolean getFrontBottomLimit() {
        // if (Constants.COMP_BOT)
        return mIO.ls_left_fwd;
        // return mIO.ls_left_rev;
    }

    public boolean getRearTopLimit() {
        // if (Constants.COMP_BOT)
        return mIO.ls_right_rev;

        // return mIO.ls_right_fwd;
    }

    public boolean getRearBottomLimit() {
        // if (Constants.COMP_BOT)
        return mIO.ls_right_fwd;
        // return mIO.ls_right_rev;
    }

    private synchronized void in() {
        this.mModifyPercent = (mIsSlow ? 0.4 : .75);

        mIO.leftEncoderValid = L1.isEncoderValid();
        mIO.rightEncoderValid = R1.isEncoderValid();

        mIO.encodersValid = mIO.leftEncoderValid && mIO.rightEncoderValid;

        if (!mIO.encodersValid)
            mIO.encoder_invalid_loops++;

        if (mIO.encoder_invalid_loops >= mIO.encoder_loop_printout) {
            System.out.println("ERROR Drive encoder(s) not found!!!");
            mIO.encoder_invalid_loops = 0;
        }

        mIO.ls_left_fwd = L1.getFWDLimit();
        mIO.ls_left_rev = L1.getREVLimit();

        mIO.ls_right_fwd = R1.getFWDLimit();
        mIO.ls_right_rev = R1.getREVLimit();

        if (mIO.leftEncoderValid) {
            mIO.left_encoder_ticks = (double) L1.getSelectedSensorPosition(0);
            mIO.left_encoder_vel = (double) L1.getSelectedSensorVelocity(0);
        } else {
            mIO.left_encoder_ticks = Double.NaN;
            mIO.left_encoder_vel = Double.NaN;
        }

        if (mIO.rightEncoderValid) {
            mIO.right_encoder_ticks = (double) R1.getSelectedSensorPosition(0);
            mIO.right_encoder_vel = (double) R1.getSelectedSensorVelocity(0);
        } else {
            mIO.right_encoder_ticks = Double.NaN;
            mIO.right_encoder_vel = Double.NaN;
        }

        mIO.left_encoder_total_delta_rotations = L1.getTotalEncoderRotations(getLeftRotations());
        mIO.right_encoder_total_delta_rotations = R1.getTotalEncoderRotations(getRightRotations());
    }

    public void wantShift(ClimbingState state) {
        if (state != mClimbState && mClimbState != ClimbingState.MOVING) {
            GZOI.driverJoy.rumble(2, 1);
            // Lights.getInstance().blink(new TimeValue<Lights.Colors>(Colors.RED, .1), .1,
            // 5, true);
            shiftDelay(state);
            mClimbState = ClimbingState.MOVING;
        }
    }

    public void shiftDelay(ClimbingState state) {
        mShiftNotifier = new Notifier(() -> {
            shift(state);
            mShiftNotifier.close();
        });
        mShiftNotifier.startSingle(kDrivetrain.NEUTRAL_TIME_BETWEEN_SHIFTS);
    }

    private void shift(ClimbingState state) {
        mShifterFront.set(state == ClimbingState.BOTH || state == ClimbingState.FRONT);
        mShifterRear.set(state == ClimbingState.BOTH || state == ClimbingState.REAR);
        mClimbState = state;
    }

    /**
     * POSITIVE --> LIFTS SIDE
     */
    public synchronized void runClimber(double front, double rear) {
        tankNoState(-front, -rear);
    }

    public synchronized double getLeftPercent() {
        return L1.getMotorOutputPercent();
    }

    public synchronized double getRightPercent() {
        return R1.getMotorOutputPercent();
    }

    public synchronized boolean driveOutputLessThan(double percent) {
        percent = Math.abs(percent);
        return Math.abs(getLeftPercent()) < percent && Math.abs(getRightPercent()) < percent;
    }

    public synchronized boolean noSpeedGreaterThan(double inches_per_second) {
        if (Math.abs(getLeftVelocityInchesPerSec()) > inches_per_second)
            return false;
        if (Math.abs(getRightVelocityInchesPerSec()) > inches_per_second)
            return false;

        return true;
    }

    public synchronized boolean speedLessThan(double inches_per_second) {
        return Math.abs(getLeftVelocityInchesPerSec()) < inches_per_second
                && Math.abs(getRightVelocityInchesPerSec()) < inches_per_second;
    }

    private synchronized boolean stateIsnt(DriveState state) {
        if (mState == state)
            return false;
        if (mWantedState == state)
            return false;

        return true;
    }

    public synchronized void handleDriving(GZJoystick joy) {

        // System.out.println(wantsToTeleDrive());
        if (mState != DriveState.CLIMB) {

            boolean shouldDrive = true;

            if (mState == DriveState.TURN_TO_HEADING && !wantsToTeleDrive()) {
                shouldDrive = false;
            }

            if (mState == DriveState.MOTION_MAGIC && !wantsToTeleDrive()) {
                shouldDrive = false;
            }

            if (mState == DriveState.OPEN_LOOP && !wantsToTeleDrive()) {
                shouldDrive = false;
            }

            wantsToTeleDrive();
            // stateIsnt(DriveState.TURN_TO_HEADING)
            // || (mState == DriveState.TURN_TO_HEADING && (Math.abs(joy.getLeftAnalogY()) >
            // .135))

            if (shouldDrive) {
                setWantedState(DriveState.OPEN_LOOP_DRIVER);
            }
        }
    }

    private synchronized void handleAutomaticClimb(double desired_speed) {
        final double pitch = mNavX.getRoll();

        // tons of weird inversions but we're gonna leave it cause it works

        desired_speed *= -1;

        double front, rear;

        if (pitch > 0) {
            front = desired_speed - GZUtil.scaleBetween(pitch, 0, desired_speed, 0, kDrivetrain.CLIMB_PITCH_TOLERANCE);
            rear = desired_speed;
            // right = desired - scaleBetween(angle, minAllowed, maxAllowed, min, max)
        } else {
            front = desired_speed;
            rear = desired_speed
                    - GZUtil.scaleBetween(Math.abs(pitch), 0, desired_speed, 0, kDrivetrain.CLIMB_PITCH_TOLERANCE);
        }

        if (desired_speed < 0) {
            double temp = front;
            front = rear;
            rear = temp;
        }

        runClimber(-front, -rear);
    }

    private synchronized void handleClimbing(GZJoystick joy) {
        // RIGHT IS REAR
        if (!joy.leftTrigger.isBeingPressed() && !joy.rightTrigger.isBeingPressed()
                && mClimbState == ClimbingState.BOTH) {
            handleAutomaticClimb(joy.getLeftAnalogY() * kDrivetrain.AUTO_CLIMB_SPEED);
        } else {
            switch (mClimbState) {
                case BOTH:

                    final double val = joy.getLeftAnalogY() * 1;
                    double rear = 0, front = 0;

                    front = val * (1 - Math.abs(joy.getLeftTrigger()));
                    rear = val * (1 - Math.abs(joy.getRightTrigger()));

                    runClimber(front, rear);
                    break;
                case FRONT:
                    runClimber(joy.getRightAnalogY() * .75, joy.getLeftAnalogY() * .25);
                    break;
                case REAR:
                    runClimber(joy.getLeftAnalogY() * .75, joy.getRightAnalogY() * .25);
                    break;
            }
        }
    }

    private synchronized void arcadeClosedLoop(GZJoystick joy) {
        final double rot = .45 * (joy.getRightTrigger() - joy.getLeftTrigger());
        double temp[] = Drive.getInstance().arcadeToLR(joy.getLeftAnalogY(), rot, false);
        double left = temp[0];
        double right = -temp[1];

        left = GZUtil.applyDeadband(left, kDrivetrain.CLOSED_LOOP_JOYSTICK_DEADBAND);
        right = GZUtil.applyDeadband(right, kDrivetrain.CLOSED_LOOP_JOYSTICK_DEADBAND);

        left = GZUtil.scaleBetween(left, -kDrivetrain.CLOSED_LOOP_TOP_TICKS, kDrivetrain.CLOSED_LOOP_TOP_TICKS, -1, 1);
        right = -GZUtil.scaleBetween(right, -kDrivetrain.CLOSED_LOOP_TOP_TICKS, kDrivetrain.CLOSED_LOOP_TOP_TICKS, -1,
                1);

        left *= mModifyPercent * getModifier();
        right *= mModifyPercent * getModifier();

        setVelocity(left, right);
    }

    private ArcadeSignal getDriveModification(GZJoystick joy) {
        final double move = joy.getLeftAnalogY() * getTotalModifer();
        final double rotate = (joy.getRightTrigger() - joy.getLeftTrigger()) * getTurnModifier();
        return new ArcadeSignal(move, rotate);
    }

    private synchronized void arcade(GZJoystick joy) {
        // final double move = joy.getLeftAnalogY() * elv;
        // final double rotate = elv * turnScalar * ((joy.getRightTrigger() -
        // joy.getLeftTrigger()) * .6);
        // arcadeNoState(move, rotate, !joy.getButton(Buttons.RB));
        // arcadeNoState(move, rotate, false);

        ArcadeSignal ds = getDriveModification(joy);

        cheesyNoState(ds.getMove(), ds.getRotate(), !usingCurvature());
        // 0.6 or 0.65
    }

    public boolean usingCurvature() {
        return !driveOutputLessThan(.5);
    }

    private double getTurnModifier() {
        return mModifyPercent * (mIsSlow ? 0.9 : 0.9) * 0.45;
    }

    private double getTotalModifer() {
        return mModifyPercent * getModifier();
    }

    private synchronized void alternateArcade(GZJoystick joy) {
        arcadeNoState(joy.getLeftAnalogY() * getTotalModifer(), (joy.getRightAnalogX() * .85) * getTotalModifer());
    }

    private synchronized void arcadeNoState(double move, double rotate) {
        arcadeNoState(move, rotate, false);
    }

    private synchronized void arcadeNoState(double move, double rotate, boolean squaredInputs) {
        double[] temp = arcadeToLR(move, rotate, squaredInputs);

        mIO.left_desired_output = temp[0];
        mIO.right_desired_output = temp[1];
    }

    private synchronized void cheesyNoState(double move, double rotate, boolean quickTurn) {
        double[] temp = cheesyToLR(move, rotate, quickTurn);

        mIO.left_desired_output = temp[0];
        mIO.right_desired_output = temp[1];
    }

    public synchronized void arcade(double move, double rotate) {
        setWantedState(DriveState.OPEN_LOOP);
        arcadeNoState(move, rotate);
    }

    public synchronized boolean eitherShifterTransitioning() {
        return mShifterFront.isMoving() || mShifterRear.isMoving();
    }

    public synchronized boolean shiftersInDrive() {
        return mShifterFront.isOff() && mShifterRear.isOff();
    }

    public synchronized double[] arcadeToLR(double xSpeed, double zRotation) {
        return arcadeToLR(xSpeed, zRotation, false);
    }

    // Modified from DifferentialDrive.java to produce double array, [0] being left
    // motor value, [1] being right motor value
    public synchronized double[] arcadeToLR(double xSpeed, double zRotation, boolean squaredInputs) {
        xSpeed = GZUtil.limit1to1(xSpeed);
        xSpeed = GZUtil.applyDeadband(xSpeed, kDrivetrain.DIFFERENTIAL_DRIVE_DEADBAND);

        zRotation = GZUtil.limit1to1(zRotation);
        zRotation = GZUtil.applyDeadband(zRotation, kDrivetrain.DIFFERENTIAL_DRIVE_DEADBAND);

        double leftMotorOutput;
        double rightMotorOutput;

        // Square the inputs (while preserving the sign) to increase fine control
        // while permitting full power.
        if (squaredInputs) {
            xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
            zRotation = Math.copySign(zRotation * zRotation, zRotation);
        }

        double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

        if (xSpeed >= 0.0) {
            // First quadrant, else second quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            } else {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            } else {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            }
        }

        double retval[] = {0, 0};
        retval[0] = GZUtil.limit1to1(leftMotorOutput);
        retval[1] = -GZUtil.limit1to1(rightMotorOutput);

        return retval;
    }

    public double[] cheesyToLR(double xSpeed, double zRotation, boolean isQuickTurn) {
        xSpeed = GZUtil.limit1to1(xSpeed);
        // xSpeed = applyDeadband(xSpeed, m_deadband);

        zRotation = GZUtil.limit1to1(zRotation);
        // zRotation = applyDeadband(zRotation, m_deadband);

        double angularPower;
        boolean overPower;

        if (isQuickTurn) {
            if (Math.abs(xSpeed) < curvatureDriveQuickStopThreshold) {
                curvatureDriveQuickStopAccumulator = (1 - curvatureDriveQuickStopAlpha)
                        * curvatureDriveQuickStopAccumulator
                        + curvatureDriveQuickStopAlpha * GZUtil.limit1to1(zRotation) * 2;
            }
            overPower = true;
            angularPower = zRotation;
        } else {
            overPower = false;
            angularPower = Math.abs(xSpeed) * zRotation - curvatureDriveQuickStopAccumulator;

            if (curvatureDriveQuickStopAccumulator > 1) {
                curvatureDriveQuickStopAccumulator -= 1;
            } else if (curvatureDriveQuickStopAccumulator < -1) {
                curvatureDriveQuickStopAccumulator += 1;
            } else {
                curvatureDriveQuickStopAccumulator = 0.0;
            }
        }

        double leftMotorOutput = xSpeed + angularPower;
        double rightMotorOutput = xSpeed - angularPower;

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (overPower) {
            if (leftMotorOutput > 1.0) {
                rightMotorOutput -= leftMotorOutput - 1.0;
                leftMotorOutput = 1.0;
            } else if (rightMotorOutput > 1.0) {
                leftMotorOutput -= rightMotorOutput - 1.0;
                rightMotorOutput = 1.0;
            } else if (leftMotorOutput < -1.0) {
                rightMotorOutput -= leftMotorOutput + 1.0;
                leftMotorOutput = -1.0;
            } else if (rightMotorOutput < -1.0) {
                leftMotorOutput -= rightMotorOutput + 1.0;
                rightMotorOutput = -1.0;
            }
        }

        // Normalize the wheel speeds
        double maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput));
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude;
            rightMotorOutput /= maxMagnitude;
        }

        double temp[] = {0, 0};
        temp[0] = leftMotorOutput;
        temp[1] = (rightMotorOutput * -1);
        return temp;
    }

    public double getModifier() {
        return Elevator.getInstance().getSpeedLimiting();
    }

    public synchronized void tank(double left, double right) {
        if (setWantedState(DriveState.OPEN_LOOP)) {
            tank(left * getModifier() * mModifyPercent, right * getModifier() * mModifyPercent);
        }
    }

    private synchronized void tankNoState(double left, double right) {
        // System.out.println(df.format(left) + "\t" + df.format(right));
        mIO.left_desired_output = left;
        mIO.right_desired_output = right;
    }

    public synchronized void tank(GZJoystick joy) {
        tank(joy.getLeftAnalogY(), joy.getRightAnalogY());
    }

    public synchronized void brake(boolean brake) {
        brake(brake ? NeutralMode.Brake : NeutralMode.Coast);
    }

    private synchronized void brake(NeutralMode mode) {
        for (GZSRX c : mTalons)
            c.setNeutralMode(mode);
    }

    public synchronized void jog(EncoderMovement movement) {
        jog(movement.left, movement.right);
    }

    private synchronized void jog(double leftInches, double rightInches) {
        double left = getLeftRotations() + inchesToRotations(leftInches);
        double right = getRightRotations() + inchesToRotations(rightInches);

        motionMagic(left, right, kDrivetrain.JOG_MOTION_MAGIC_ACCEL, kDrivetrain.JOG_MOTION_MAGIC_VEL);
    }

    private synchronized void motionMagic(boolean motionMagic, double leftRotations, double rightRotations,
                                          double accel, double vel) {
        motionMagic(motionMagic, leftRotations, rightRotations, accel, accel, vel, vel);
    }

    private synchronized void motionMagic(double leftRotations, double rightRotations, double accel, double vel) {
        motionMagic(true, leftRotations, rightRotations, accel, vel);
    }

    private synchronized void motionMagic(boolean motionMagic, double leftRotations, double rightRotations,
                                          double leftAccelInPerSec, double rightAccelInPerSec, double leftVelInPerSec, double rightVelInPerSec) {
        if (motionMagic) {
            setWantedState(DriveState.MOTION_MAGIC);
        }

        // FIX THIS YUCK
        rightRotations *= -1;

        L1.configMotionAcceleration((int) inchesPerSecondToTicksPer100ms(leftAccelInPerSec), 10);
        R1.configMotionAcceleration((int) inchesPerSecondToTicksPer100ms(rightAccelInPerSec), 10);

        L1.configMotionCruiseVelocity((int) inchesPerSecondToTicksPer100ms(leftVelInPerSec), 10);
        R1.configMotionCruiseVelocity((int) inchesPerSecondToTicksPer100ms(rightVelInPerSec), 10);

        mIO.left_desired_output = Units.rotations_to_ticks(leftRotations);
        mIO.right_desired_output = Units.rotations_to_ticks(rightRotations);
    }

    public synchronized boolean encoderAngleIsDone() {
        return encoderIsDone(kDrivetrain.L_ROTATIONS_PER_DEGREE * kDrivetrain.TURN_TO_HEADING_ACCURACY_DEG);
    }

    public synchronized boolean encoderJogIsDone() {
        return encoderIsDone(inchesToRotations(kDrivetrain.JOG_ACCURACY_INCHES));
    }

    public synchronized boolean encoderIsDone(double rotationAccuracy) {
        if (GZUtil.epsilonEquals(getLeftRotations(), mIO.left_desired_output / 4096, rotationAccuracy)
                && GZUtil.epsilonEquals(-getRightRotations(), mIO.right_desired_output / 4096, rotationAccuracy))
            return true;

        return false;
    }

    public synchronized void enableFollower() {
        for (GZSmartSpeedController c : mSmartControllers) {
            if (c.getMaster() != Master.MASTER) {
                switch (c.getSide()) {
                    case LEFT:
                        c.follow(L1);
                        break;
                    case RIGHT:
                        c.follow(R1);
                        break;
                    case NO_INFO:
                        System.out.println("ERROR Drive talon " + c.getGZName() + " could not enter follower mode!");
                }
                // Timer.delay(.125); //beta
            }
        }
    }

    public synchronized void zeroEncoders() {
        L1.zero();
        R1.zero();
    }

    public synchronized void zeroSensors() {
        zeroEncoders();
    }

    public int getTotalShiftCountsFront() {
        return mShifterFront.getChangeCounts();
    }

    public int getTotalShiftCountsRear() {
        return mShifterRear.getChangeCounts();
    }

    public synchronized void toggleSlowSpeed() {
        slowSpeed(!isSlow());
    }

    public synchronized void slowSpeed(boolean isSlow) {
        if (mIsSlow != isSlow) {
            mIsSlow = isSlow;
            String speed = isSlow() ? "slow" : "full";
            // System.out.println("Drivetrain speed: " + speed);
            if (isSlow()) {
                GZOI.driverJoy.rumble(8, .125);
            } else {
                GZOI.driverJoy.rumble(8, .25);
            }
            GZFiles.getInstance().addLog(this, "Drive speed set to " + speed, true);
        }
    }

    public Boolean isSlow() {
        return mIsSlow;
    }

    public String getStateString() {
        return mState.toString();
    }

    public DriveState getState() {
        return mState;
    }

    protected void initDefaultCommand() {
    }

    public static enum Rocket {
        LEFT_NEAR(kAuton.Left_Rocket_Near, true, true), LEFT_FAR(kAuton.Left_Rocket_Far, true, false),
        RIGHT_NEAR(kAuton.Right_Rocket_Near, false, true), RIGHT_FAR(kAuton.Right_Rocket_Far, false, false),
        NONE(new Pose2d(new Translation2d(2000, 2000)), false, false);

        public final Pose2d position;
        public final boolean left, near;

        private Rocket(Pose2d position, boolean left, boolean near) {
            this.position = position;
            this.left = left;
            this.near = near;
        }

        public static Rocket closestByAngle(Rotation2d rotation, double tolerance) {
            Pose2d nearestRocket = rotation.nearestPoseByAngle(
                    Arrays.asList(LEFT_NEAR.position, LEFT_FAR.position, RIGHT_NEAR.position, RIGHT_FAR.position),
                    tolerance);
            if (nearestRocket == null)
                return NONE;

            if (nearestRocket.equals(Rocket.LEFT_NEAR.position)) {
                return LEFT_NEAR;
            }
            if (nearestRocket.equals(Rocket.LEFT_FAR.position)) {
                return LEFT_FAR;
            }
            if (nearestRocket.equals(Rocket.RIGHT_NEAR.position)) {
                return RIGHT_NEAR;
            }
            if (nearestRocket.equals(Rocket.RIGHT_FAR.position)) {
                return RIGHT_FAR;
            }

            return NONE;
        }

        public boolean identified() {
            return !this.equals(NONE);
        }

        public boolean unsure() {
            return !identified();
        }
    }

    public enum DriveState {
        OPEN_LOOP(false, ControlMode.PercentOutput), OPEN_LOOP_DRIVER(false, ControlMode.PercentOutput),
        TURN_TO_HEADING(true, ControlMode.MotionMagic), CLOSED_LOOP_DRIVER(true, ControlMode.Velocity),
        DEMO(false, ControlMode.PercentOutput), NEUTRAL(false, ControlMode.Disabled),
        MOTION_MAGIC(true, ControlMode.MotionMagic), MOTION_PROFILE(true, ControlMode.MotionProfile),
        PATH_FOLLOWING(true, ControlMode.Velocity), VELOCITY(true, ControlMode.Velocity),
        CLIMB(false, ControlMode.PercentOutput);

        private final boolean usesClosedLoop;
        private final ControlMode controlMode;

        DriveState(final boolean s, ControlMode c) {
            this.usesClosedLoop = s;
            this.controlMode = c;
        }
    }

    enum ShouldTurnAfterPath {
        NONE, LEFT, RIGHT
    }

    public enum ClimbingState {
        FRONT, BOTH, NONE, MOVING, REAR
    }


    public boolean isClimbing() {
        return getState() == DriveState.CLIMB && mClimbState != ClimbingState.NONE;
    }

    public static class RocketIdentifcation {
        public final Rocket rocket;
        public final String how;
        public final boolean recalibrateWithGyro;
        public final Pose2d here;

        public RocketIdentifcation(Rocket rocket, String how, boolean recalibrateWithGyro, Pose2d here) {
            this.rocket = rocket;
            this.how = how;
            this.recalibrateWithGyro = recalibrateWithGyro;
            this.here = here;
        }

    }

    public static class IO {

        public final double encoder_loop_printout = 20;
        public Double left_encoder_ticks = Double.NaN, left_encoder_vel = Double.NaN;

        public double left_encoder_total_delta_rotations = 0, right_encoder_total_delta_rotations = 0;

        public Double right_encoder_ticks = Double.NaN, right_encoder_vel = Double.NaN;

        public boolean ls_left_rev = false, ls_left_fwd = false;
        public boolean ls_right_rev = false, ls_right_fwd = false;

        public Boolean leftEncoderValid = false;
        public Boolean rightEncoderValid = false;
        public Boolean encodersValid = false;
        public double encoder_invalid_loops = 0;
        public double left_desired_output = 0;
        public double right_desired_output = 0;
        // out
        private double left_output = 0;
        private double right_output = 0;

        public IO() {
            left_encoder_total_delta_rotations = 0;
            right_encoder_total_delta_rotations = 0;
        }
    }
}
