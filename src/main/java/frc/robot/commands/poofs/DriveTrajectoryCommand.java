package frc.robot.commands.poofs;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.poofs.RobotState;
import frc.robot.poofs.geometry.Pose2dWithCurvature;
import frc.robot.poofs.trajectory.TimedView;
import frc.robot.poofs.trajectory.Trajectory;
import frc.robot.poofs.trajectory.TrajectoryIterator;
import frc.robot.poofs.trajectory.timing.TimedState;
import frc.robot.subsystems.Drive;

public class DriveTrajectoryCommand extends Command {

  private static final Drive mDrive = Drive.getInstance();
  private static final RobotState mRobotState = RobotState.getInstance();

  private final TrajectoryIterator<TimedState<Pose2dWithCurvature>> mTrajectory;
  private final boolean mResetPose;

  public DriveTrajectoryCommand(Trajectory<TimedState<Pose2dWithCurvature>> trajectory) {
    this(trajectory, false);
  }

  public DriveTrajectoryCommand(Trajectory<TimedState<Pose2dWithCurvature>> trajectory, boolean resetPose) {
    mTrajectory = new TrajectoryIterator<>(new TimedView<>(trajectory));
    mResetPose = resetPose;
  }

  protected void initialize() {
    System.out.println("Starting trajectory! (length=" + mTrajectory.getRemainingProgress() + ")");
    if (mResetPose) {
      mRobotState.reset(Timer.getFPGATimestamp(), mTrajectory.getState().state().getPose());
    }
    mDrive.setTrajectory(mTrajectory);

    System.out.println("Starting odometry:");
    mDrive.printOdometry();
  }

  protected void execute() {
    mDrive.printOdometry();
  }

  protected boolean isFinished() {
    if (mDrive.isDoneWithTrajectory()) {
      System.out.println("Trajectory finished");
      mDrive.printOdometry();
      return true;
    }
    return false;
  }

  protected void end() {
  }

  protected void interrupted() {
  }
}
