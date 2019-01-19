/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.commands.paths.PathContainer;
import frc.robot.poofs.util.control.Path;
import frc.robot.subsystems.Drive;

public class DrivePath extends Command {

  private PathContainer mPathContainer;
  private Path mPath;
  private Drive drive = Drive.getInstance();

  public DrivePath(PathContainer p) {
    requires(drive);
    mPathContainer = p;
    mPath = mPathContainer.buildPath();
  }

  protected void initialize() {
    drive.setWantDrivePath(mPath, mPathContainer.isReversed());
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return drive.isDoneWithPath();
  }

  protected void end() {
  }

  protected void interrupted() {
    end();
  }
}
