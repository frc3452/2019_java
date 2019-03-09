package frc.robot.auto.commands.functions.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class DrivePath extends Command {

  private PathContainer mPathContainer;
  private Drive drive = Drive.getInstance();

  public DrivePath(PathContainer p) {
    requires(drive);
    mPathContainer = p;
  }

  protected void initialize() {
    System.out.println("Driving path " + mPathContainer.toString());
    drive.setWantDrivePath(mPathContainer);
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return drive.isDoneWithPath();
  }

  protected void end() {
    System.out.println("Path completed.");
  }

  protected void interrupted() {
    end();
  }
}
