package frc.robot.ConfigurableDrive;

import frc.robot.ConfigurableDrive.ConfigurableDrive.ArrayLoopAround.ArrayResult;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.util.drivers.GZJoystick;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * This configurable drive controller was written as a senior project by Max
 * Dreher from FRC Team GreengineerZ (#3452)
 * <p>
 * Designed to be drop in and configurable,
 */
public class ConfigurableDrive {

    private final Button s_upTick;
    private final Button s_downTick;
    private final Supplier<Boolean> requiredToChange;

    private final ArrayList<DriveStyle> mStyles = new ArrayList<DriveStyle>();
    private DriveStyle mStyle = null;
    private int mCurrentStyle = 0;
    private int mPrevStyle = -1;

    private final boolean shouldLoopAroundList;

    private static final double kARCADE_DEADBAND = 0.05;
    private static final boolean kSHOULD_LOOP_LIST = false;

    private final double ARCADE_Z_MODIFIER;

    public ConfigurableDrive(Supplier<Boolean> conditionsToChange, Supplier<Boolean> moveUpList,
                             Supplier<Boolean> moveDownList, double arcadeMod, boolean shouldLoopAroundList) {
        requiredToChange = conditionsToChange;

        s_upTick = new Button(moveUpList);
        s_downTick = new Button(moveDownList);

        this.ARCADE_Z_MODIFIER = arcadeMod;

        this.shouldLoopAroundList = shouldLoopAroundList;

        configDriveMessage("Constructed!");
//         update();
    }

    public double getModifier() {
        return 1;
    }

    public ConfigurableDrive(Supplier<Boolean> moveUpList, Supplier<Boolean> moveDownList,
                             boolean shouldLoopAroundList) {
        this(() -> true, moveUpList, moveDownList, 1, shouldLoopAroundList);
    }

    public ConfigurableDrive(Supplier<Boolean> moveUpList, Supplier<Boolean> moveDownList) {
        this(moveUpList, moveDownList, kSHOULD_LOOP_LIST);
    }

    public ConfigurableDrive(Supplier<Boolean> conditionsToChange, Supplier<Boolean> moveUpList,
                             Supplier<Boolean> moveDownList) {
        this(conditionsToChange, moveUpList, moveDownList, 1, kSHOULD_LOOP_LIST);
    }

    public DriveSignal update() {
        if (mStyles == null) {
            configDriveThrowError("Array of drive styles null");
            return DriveSignal.NEUTRAL;
        }

        if (s_upTick.updated()) {
            if (requiredToChange.get())
                mCurrentStyle++;
            else
                cannotChangeDriveMode();
        } else if (s_downTick.updated()) {
            if (requiredToChange.get())
                mCurrentStyle--;
            else
                cannotChangeDriveMode();
        }

        if (shouldLoopAroundList) {
            ArrayLoopAround limit = limitArrayLoopAround(mCurrentStyle, mStyles);
            mCurrentStyle = limit.value;
        } else {
            ArrayLoopAround limit = limitArray(mCurrentStyle, mStyles);
            mCurrentStyle = limit.value;
            if (!limit.result.equals(ArrayResult.NONE)) {
                configDriveThrowError("No more drive styles, go back the other way!");
            }
        }

        if (goodRange(mCurrentStyle, mStyles)) {
            mStyle = mStyles.get(mCurrentStyle);

            if (mCurrentStyle != mPrevStyle) {
                configDriveMessage("Updated drive style: " + mStyle.toString());
                mPrevStyle = mCurrentStyle;
            }

            DriveSignal output = mStyle.produceDriveSignal();

            if (output == null) {
                configDriveThrowError(
                        "Could not produce drive signal, output from " + mStyle.toString() + " returned null");
                return DriveSignal.NEUTRAL;
            } else {

                if (!mStyle.ignoresLimits()) {
                    output.applyModifier(getModifier());
                }

                return output;
            }
        } else {
            configDriveThrowError("Tried to get style " + mCurrentStyle + " in array of size " + mStyles.size());
            return DriveSignal.NEUTRAL;
        }

    }

    public void addDriveStyle(DriveStyle style) {
        this.mStyles.add(style);
    }

    public void addStandardDriveStyles(GZJoystick joy) {
        addTankDrive(joy);
        // addTankDriveWithModifiers(joy);
        addSingleAxisArcade(joy);
        addDualAxisArcade(joy);
        addRacingArcade(joy);
        // addRacingArcadeWithModifier(joy);
    }

    public boolean isDisabled() {
        if (mStyle == null)
            return false;

        return mStyle.isDisabled();
    }

    public void addDisabled() {
        addDriveStyle(new DriveStyle("Disabled") {
            @Override
            public DriveSignal produceDriveSignal() {
                return new DriveSignal(0, 0);
            }

            @Override
            public boolean isDisabled() {
                return true;
            }
        });
    }

    public void addTankDrive(Supplier<Double> left, Supplier<Double> right) {
        this.addDriveStyle(new DriveStyle("Tank", left, right) {

            @Override
            public DriveSignal produceDriveSignal() {
                double left = getAxis(1);
                double right = getAxis(2);

                DriveSignal output = new DriveSignal(left, right);
                return output;
            }
        });
    }

    public void addTankDriveWithModifiers(GZJoystick joy) {
        addTankDriveWithModifier(() -> joy.getLeftAnalogY(), () -> joy.getRightAnalogY(),
                () -> (joy.bButton.wasActivated() ? 1.0 : 0.0));
    }

    public void addTankDriveWithModifier(Supplier<Double> left, Supplier<Double> right, Supplier<Double> slowSpeed) {
        this.addDriveStyle(new DriveStyle("Tank with modifier", left, right, slowSpeed) {
            final double MODIFIER = 0.5;
            boolean shouldSlowSpeed = false;

            @Override
            public DriveSignal produceDriveSignal() {
                double left = getAxis(1);
                double right = getAxis(2);

                if (getAxis(3) == 1)
                    shouldSlowSpeed = !shouldSlowSpeed;

                if (shouldSlowSpeed) {
                    left *= MODIFIER;
                    right *= MODIFIER;
                }

                return new DriveSignal(left, right);
            }
        });
    }

    public void addTankDrive(GZJoystick joy) {
        addTankDrive(() -> joy.getLeftAnalogY(), () -> joy.getRightAnalogY());
    }

    public void addSingleAxisArcade(GZJoystick joy) {
        addArcadeDrive("Single axis arcade", () -> joy.getLeftAnalogY(), () -> joy.getLeftAnalogX());
    }

    public void addDualAxisArcade(GZJoystick joy) {
        addArcadeDrive("Dual axis arcade", () -> joy.getLeftAnalogY(), () -> joy.getRightAnalogX());
    }

    public void addRacingArcade(GZJoystick joy) {
        addArcadeDrive("Racing arcade", () -> joy.getRightTrigger() - joy.getLeftTrigger(), () -> joy.getLeftAnalogX());
    }

    public static double scaleBetween(double unscaledNum, double minAllowed, double maxAllowed, double min,
                                      double max) {
        return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
    }

    public void addRacingArcadeWithModifier(GZJoystick joy) {

        addDriveStyle(new DriveStyle("Racing arcade with modifier", () -> joy.getRightTrigger() - joy.getLeftTrigger(),
                () -> joy.getLeftAnalogX(), () -> (joy.bButton.isBeingPressed() ? 1.0 : 0.0)) {

            final double MODIFIER = 0.45;
            boolean shouldSlowSpeed = false;
            LatchedBoolean lb = new LatchedBoolean();

            @Override
            public DriveSignal produceDriveSignal() {
                if (lb.update(getAxis(3) == 1)) {
                    shouldSlowSpeed = !shouldSlowSpeed;
                }

                double x = getAxis(1);
                double z = getAxis(2);

                if (shouldSlowSpeed) {
                    x *= MODIFIER;
                    z *= MODIFIER;
                }

                DriveSignal output = arcade(x, z, false);

                return output;
            }
        });
    }

    public void addArcadeDrive(String name, Supplier<Double> xMovement, Supplier<Double> zRotation) {
        this.addDriveStyle(new DriveStyle(name, xMovement, zRotation) {

            @Override
            public DriveSignal produceDriveSignal() {
                double axis1 = getAxis(1);
                double axis2 = getAxis(2);

                DriveSignal output = arcade(axis1, axis2, false);
                return output;
            }
        });
    }

    /**
     * Gyro needs to be mapped with 0 degrees forward, then growing to 360 Clockwise
     * Use the GyroMapper function of this class to aid with conversions
     */
    public void addFieldCentric(Supplier<Double> fwdX, Supplier<Double> fwdY, Supplier<Double> revX,
                                Supplier<Double> revY, Supplier<Double> gyro, double turnToleranceDeg, double startingMagnitude,
                                double endingPercentage, double turnSpeed) {
        DriveStyle fieldCentric = new DriveStyle("Field centric", fwdX, fwdY, revX, revY, gyro) {

            @Override
            public boolean ignoresLimits() {
                return true;
            }

            @Override
            public DriveSignal produceDriveSignal() {
                Rotation2d currentAngle = new Rotation2d(getAxis(5));
                Translation2d joyInput = new Translation2d(getAxis(1), getAxis(2));

                final double magnitude = joyInput.norm();
                Rotation2d targetAngle = joyInput.direction().rotateBy(new Rotation2d(-90));

                if (magnitude > startingMagnitude) {
                    boolean turnRight = !Rotation2d.shouldTurnClockwise(currentAngle, targetAngle);

                    double degAway = Rotation2d.difference(currentAngle, targetAngle);
//                    System.out.println(currentAngle + "\t" + targetAngle + degAway + "\t" + turnToleranceDeg);

                    double desiredMove, move, rotate;
                    if (degAway < turnToleranceDeg) {
                        rotate = scaleBetween(degAway, 0, turnSpeed, 0, turnToleranceDeg);
                    } else {
                        rotate = turnSpeed;
                    }

                    // CW or right rotation is positive
                    if (!turnRight)
                        rotate *= -1;

                    desiredMove = scaleBetween(magnitude, 0, endingPercentage, startingMagnitude, Math.sqrt(2));

                    if (degAway > turnToleranceDeg) {
                        move = 0;
                    } else {
                        move = scaleBetween(turnToleranceDeg - degAway, 0, desiredMove, 0, turnToleranceDeg);
                    }

                    DriveSignal output = arcade(move, rotate, false);
                    return output;
                }

                return DriveSignal.NEUTRAL;
            }
        };

        addDriveStyle(fieldCentric);
    }

    private static void configDriveMessage(String message) {
        System.out.println("[ConfigurableDrive] " + message);
    }

    private static void cannotChangeDriveMode() {
        configDriveThrowError("Cannot change drive mode, conditions not met!");
    }

    private static void configDriveThrowError(String message) {
        System.out.println("ERROR [ConfigurableDrive] " + message);
    }

    private static <T> boolean goodRange(int value, ArrayList<T> list) {
        if (value < 0)
            return false;

        if (value > list.size() - 1)
            return false;

        return true;
    }

    public static class ArrayLoopAround {
        public enum ArrayResult {
            NONE, TOO_HIGH, TOO_LOW
        }

        public final ArrayResult result;
        public final int value;

        public ArrayLoopAround(int val, ArrayResult result) {

            this.value = val;
            this.result = result;
        }
    }

    public static <T> ArrayLoopAround limitArrayLoopAround(int value, ArrayList<T> list) {
        if (value < 0)
            return new ArrayLoopAround(list.size() - 1, ArrayResult.TOO_LOW);

        if (value > list.size() - 1) {
            return new ArrayLoopAround(0, ArrayResult.TOO_HIGH);
        }

        return new ArrayLoopAround(value, ArrayResult.NONE);
    }

    public static <T> ArrayLoopAround limitArray(int value, ArrayList<T> list) {
        if (value < 0)
            return new ArrayLoopAround(0, ArrayResult.TOO_LOW);

        if (value > list.size() - 1) {
            return new ArrayLoopAround(list.size() - 1, ArrayResult.TOO_HIGH);
        }
        return new ArrayLoopAround(value, ArrayResult.NONE);
    }

    public static class Button {
        private LatchedBoolean mLB = new LatchedBoolean();
        private Supplier<Boolean> supplier;

        public Button(Supplier<Boolean> supplier) {
            this.supplier = supplier;
        }

        public boolean get() {
            return supplier.get();
        }

        public boolean updated() {
            return mLB.update(get());
        }
    }

    public synchronized DriveSignal arcade(double xSpeed, double zRotation, boolean squaredInputs) {
        return arcade(xSpeed, zRotation, squaredInputs, kARCADE_DEADBAND);
    }

    public synchronized DriveSignal arcade(double xSpeed, double zRotation, boolean squaredInputs, double deadband) {
        xSpeed = limit1to1(xSpeed);
        xSpeed = applyDeadband(xSpeed, deadband);

        zRotation = limit1to1(zRotation);
        zRotation = applyDeadband(zRotation, deadband);

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

        DriveSignal retval = new DriveSignal(limit1to1(leftMotorOutput), limit1to1(rightMotorOutput));
        return retval;
    }

    public abstract static class DriveStyle {
        private final String name;
        private Supplier<Double>[] axises;

        /**
         * @param axises
         */
        @SafeVarargs
        public DriveStyle(String name, Supplier<Double>... axises) {
            this.axises = axises;
            this.name = name;
        }

        public boolean isDisabled() {
            return false;
        }

        public abstract DriveSignal produceDriveSignal();

        public boolean ignoresLimits() {
            return false;
        }

        /**
         * <b> First axis passed will be axis 1, not 0 </b>
         *
         * @param axis
         * @return
         */
        public double getAxis(int axis) {
            if (axis < 1) {
                throwError("Axis " + axis + " less than 1; first axis starts at 1!");
                return 0;
            }

            if (axis >= axises.length + 2) {
                throwError("Axis " + axis + " too high, this mode was only supplied " + axises.length + " axises!");
                return 0;
            }

            return axises[axis - 1].get();
        }

        private void throwError(String message) {
            configDriveThrowError("option [" + toString() + "] " + message);
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static double limit1to1(double value) {
        if (value > 1.0) {
            return 1.0;
        }
        if (value < -1.0) {
            return -1.0;
        }
        return value;
    }

    public static double applyDeadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }

}