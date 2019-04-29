package frc.robot.ConfigurableDrive;

/**
 * A drivetrain command consisting of the left, right motor values
 */
public class DriveSignal {

    protected double mLeftMotor;
    protected double mRightMotor;

    public DriveSignal(double left, double right) {
        mLeftMotor = left;
        mRightMotor = right;
    }

    public static DriveSignal NEUTRAL = new DriveSignal(0, 0);

    public double getLeft() {
        return mLeftMotor;
    }

    public double getRight() {
        return mRightMotor;
    }

    @Override
    public String toString() {
        return "L: " + mLeftMotor + ", R: " + mRightMotor;
    }
}