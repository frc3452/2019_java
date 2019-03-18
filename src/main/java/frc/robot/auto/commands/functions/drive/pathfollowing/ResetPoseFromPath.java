package frc.robot.auto.commands.functions.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.subsystems.Drive;

public class ResetPoseFromPath extends InstantCommand {

    protected PathContainer mPathContainer;

    public ResetPoseFromPath(PathContainer pathContainer) {
        mPathContainer = pathContainer;
    }

    @Override
    public void initialize() {
        Drive.getInstance().zeroOdometry(mPathContainer);
    }

}