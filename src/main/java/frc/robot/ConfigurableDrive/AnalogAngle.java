package frc.robot.ConfigurableDrive;

import java.text.DecimalFormat;

import frc.robot.poofs.util.math.Rotation2d;

public class AnalogAngle {
    public final double magnitude;
    public final Rotation2d angle;
    public final double x, y;

    public AnalogAngle(double x, double y) {
        this.x = x;
        this.y = y;
        this.magnitude = Math.hypot(x, y);
        this.angle = new Rotation2d(x, y, true).inverse();
    }

    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public String toString() {
        String out = "X: " + df.format(x) + "\tY: " + df.format(y) + "\t" + angle.toString() + "\tMagnitude: "
                + df.format(magnitude);
        return out;
    }
}