package frc.robot.ConfigurableDrive;

import java.util.ArrayList;
import java.util.function.Supplier;

import frc.robot.ConfigurableDrive.GZJoystick.Buttons;

/**
 * This configurable drive controller was written as a senior project by Max
 * Dreher from FRC Team GreengineerZ (#3452)
 * 
 * Designed to be drop in and configurable,
 */
public class ConfigurableDrive {

    private Button s_upTick;
    private Button s_downTick;

    // private

    private ArrayList<DriveStyle> mStyles = new ArrayList<DriveStyle>();
    private final double kARCADE_DEADBAND = 0.05;

    public ConfigurableDrive() {
    }

    public void addDriveStyle(DriveStyle style) {
        this.mStyles.add(style);
    }

    public void addTankDrive(Supplier<Double> left, Supplier<Double> right) {
        this.addDriveStyle(new DriveStyle("Tank", left, right) {

            @Override
            public DriveSignal produceDriveSignal() {
                double left = getAxis(1);
                double right = getAxis(2);

                return new DriveSignal(left, right);
            }
        });
    }

    public void addTankDriveSlow(GZJoystick joy) {
        addTankDriveSlow(() -> joy.getLeftAnalogY(), () -> joy.getRightAnalogY(),
                () -> (joy.getButtonLatched(Buttons.RB) ? 1.0 : 0.0));
    }

    public void addTankDriveSlow(Supplier<Double> left, Supplier<Double> right, Supplier<Double> slowSpeed) {
        this.addDriveStyle(new DriveStyle("Tank with slow speed", left, right, slowSpeed) {
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
        addArcadeDrive("Dual axis arcade", () -> joy.getLeftAnalogY(), () -> joy.getRightAnalogY());
    }

    public void addArcadeDrive(String name, Supplier<Double> xMovement, Supplier<Double> zRotation) {
        this.addDriveStyle(new DriveStyle(name, xMovement, zRotation) {

            @Override
            public DriveSignal produceDriveSignal() {
                DriveSignal output = arcade(getAxis(1), getAxis(2), false);

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
         * First axis passed will be axis 1, not 0
         * 
         * @param axis
         * @return
         */
        public double getAxis(int axis) {
            if (axis < 1) {
                throwError("Axis " + axis + " less than 1; first axis starts at 1!");
                return 0;
            }

            if (axis >= axises.length) {
                throwError("Axis " + axis + " too high, this mode was only supplied " + axises.length + " axises!");
                return 0;
            }

            return axises[axis - 1].get();
        }

        private void throwError(String message) {
            System.out.println("ERROR ConfigurableDrive option [" + toString() + "]" + message);
        }

        @Override
        public String toString() {
            return name;
        }

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

        DriveSignal retval = new DriveSignal(limit1to1(leftMotorOutput), -limit1to1(rightMotorOutput));

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

}