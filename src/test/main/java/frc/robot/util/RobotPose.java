package frc.robot.util;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class RobotPose extends SendableBase {

    private double x, y, heading;

    public RobotPose() {
        super(false);
        setName("RobotPose");
        x = 0;
        y = 0;
        heading = 0;
    }

    public void SetX(double newX) {
        this.x = newX;
    }

    public void SetY(double newY) {
        this.y = newY;
    }

    public void SetHeading(double newHeading) {
        this.heading = newHeading;
    }

    public double GetX() {
        return x;
    }

    public double GetY() {
        return y;
    }

    public double GetHeading() {
        return heading;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("RobotPose");
        builder.addDoubleProperty("x", () -> GetX(), (_x) -> SetX(_x));
        builder.addDoubleProperty("y", () -> GetY(), (_y) -> SetY(_y));
        builder.addDoubleProperty("heading", () -> GetHeading(), (_heading) -> SetX(_heading));
    }

}