package frc.robot.commands.drive.pathfollowing;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.poofs.RobotState;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.subsystems.Drive;

public class ResetPoseFromPath extends InstantCommand {

    protected PathContainer mPathContainer;

    public ResetPoseFromPath(PathContainer pathContainer) {
        mPathContainer = pathContainer;
    }

    @Override
    public void initialize() {
        RigidTransform2d startPose = mPathContainer.getStartPose();
        RobotState.getInstance().reset(Timer.getFPGATimestamp(), startPose);
        Drive.getInstance().setGyroAngle(startPose.getRotation());
    }

}