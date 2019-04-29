package frc.robot.ConfigurableDrive;

import java.util.ArrayList;
import java.util.function.Supplier;

import frc.robot.ConfigurableDrive.ConfigurableDrive.ArrayLoopAround.ArrayResult;
import frc.robot.ConfigurableDrive.GZJoystick.Buttons;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.util.GZPrevious;

/**
 * This configurable drive controller was written as a senior project by Max
 * Dreher from FRC Team GreengineerZ (#3452)
 * 
 * Designed to be drop in and configurable,
 */
public class ConfigurableDrive {

    private final Button s_upTick;
    private final Button s_downTick;
    private final Supplier<Boolean> requiredToChange;

    private final ArrayList<DriveStyle> mStyles = new ArrayList<DriveStyle>();
    private int mCurrentStyle = 0;
    private int mPrevStyle = -1;

    private final boolean shouldLoopAroundList;

    private static final double kARCADE_DEADBAND = 0.05;
    private static final boolean kSHOULD_LOOP_LIST = false;

    public ConfigurableDrive(Supplier<Boolean> conditionsToChange, Supplier<Boolean> moveUpList,
            Supplier<Boolean> moveDownList, boolean shouldLoopAroundList) {
        requiredToChange = conditionsToChange;

        s_upTick = new Button(moveUpList);
        s_downTick = new Button(moveDownList);

        this.shouldLoopAroundList = shouldLoopAroundList;

        configDriveMessage("Constructed!");
        // update();
    }

    public ConfigurableDrive(Supplier<Boolean> moveUpList, Supplier<Boolean> moveDownList,
            boolean shouldLoopAroundList) {
        this(() -> true, moveUpList, moveDownList, shouldLoopAroundList);
    }

    public ConfigurableDrive(Supplier<Boolean> moveUpList, Supplier<Boolean> moveDownList) {
        this(moveUpList, moveDownList, kSHOULD_LOOP_LIST);
    }

    public ConfigurableDrive(Supplier<Boolean> conditionsToChange, Supplier<Boolean> moveUpList,
            Supplier<Boolean> moveDownList) {
        this(conditionsToChange, moveUpList, moveDownList, kSHOULD_LOOP_LIST);
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
            DriveStyle style = mStyles.get(mCurrentStyle);

            if (mCurrentStyle != mPrevStyle) {
                configDriveMessage("Updated drive style: " + style.toString());
                mPrevStyle = mCurrentStyle;
            }

            DriveSignal output = style.produceDriveSignal();

            if (output == null) {
                configDriveThrowError(
                        "Could not produce drive signal, output from " + style.toString() + " returned null");
                return DriveSignal.NEUTRAL;
            } else {
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
        addTankDriveWithModifiers(joy);
        addSingleAxisArcade(joy);
        addDualAxisArcade(joy);
        addRacingArcade(joy);
        addRacingArcadeWithModifier(joy);
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
                () -> (joy.getButtonLatched(Buttons.RB) ? 1.0 : 0.0));
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
        addArcadeDrive("Racing arcade", () -> joy.getLeftAnalogY(), () -> joy.getRightTrigger() - joy.getLeftTrigger());
    }

    /**
     * Gyro needs to be mapped with 0 degrees forward, then growing to 360 Clockwise
     * Use the GyroMapper function of this class to aid with conversions
     * 
     * @param joy
     * @param gyro
     */
    public void addFieldCentric(GZJoystick joy, Supplier<Double> gyro, double turnToleranceDeg) {
        DriveStyle fieldCentric = new DriveStyle("Field centric", () -> joy.getLeftAnalogY(),
                () -> joy.getLeftAnalogX(), gyro) {
            final double tolDeg = turnToleranceDeg;

            @Override
            public DriveSignal produceDriveSignal() {
                Rotation2d currentAngle = Rotation2d.fromDegrees(getAxis(3));
                AnalogAngle targetAngle = new AnalogAngle(getAxis(1), getAxis(2));

                // boolean turnLeft = 

                return null;
            }
        };
    }

    public void addRacingArcadeWithModifier(GZJoystick joy) {

        addDriveStyle(new DriveStyle("Racing arcade with modifier", () -> joy.getLeftAnalogY(),
                () -> joy.getRightTrigger() - joy.getLeftTrigger(), () -> (joy.getButton(Buttons.RB) ? 1.0 : 0.0)) {

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

    public abstract static class DriveStyle {
        private final String name;
        private Supplier<Double>[] axises;

        /**
         * 
         * @param axises
         */
        @SafeVarargs
        public DriveStyle(String name, Supplier<Double>... axises) {
            this.axises = axises;
            this.name = name;
        }

        public abstract DriveSignal produceDriveSignal();

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

    public static class Previous<T> {
        private T prev;

        public Previous(T start) {
            prev = start;
        }

        public boolean update(T newValue) {
            T temp = prev;
            prev = newValue;
            return !temp.equals(newValue);
        }

        public T getPrev() {
            return prev;
        }
    }

}