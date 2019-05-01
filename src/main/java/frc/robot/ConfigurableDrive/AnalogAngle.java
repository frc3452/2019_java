package frc.robot.ConfigurableDrive;

import java.text.DecimalFormat;

public class AnalogAngle {
    public final double magnitude;
    public Rotation2d angle;
    public final double x, y;

    public AnalogAngle(double x, double y) {
        this.x = x;
        this.y = y;
        this.magnitude = Math.hypot(x, y);
        this.angle = new Rotation2d(x, y, true).inverse();
    }

    public void setAngle(Rotation2d angle)
    {
        this.angle = angle;
    }

    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public String toString() {
        String out = "X: " + df.format(x) + "\tY: " + df.format(y) + "\t" + angle.toString() + "\tMagnitude: "
                + df.format(magnitude);
        return out;
    }
}