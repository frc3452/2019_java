package frc.robot.poofs.trajectory;

import frc.robot.poofs.geometry.Pose2d;
import frc.robot.poofs.geometry.Twist2d;

public interface IPathFollower {
    public Twist2d steer(Pose2d current_pose);

    public boolean isDone();
}
