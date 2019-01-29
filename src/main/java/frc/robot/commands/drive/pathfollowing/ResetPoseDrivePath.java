/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ResetPoseDrivePath extends CommandGroup {

  private final PathContainer mPathContainer;

  public ResetPoseDrivePath(PathContainer pathContainer) {
    this(pathContainer, false);
  }

  public ResetPoseDrivePath(PathContainer pathContainer, boolean andBack) {
    mPathContainer = pathContainer;

    addPath();
    if (andBack) {
      mPathContainer.flip();
      addPath();
    }
  }

  private void addPath() {
    addSequential(new ResetPoseFromPath(mPathContainer));
    addSequential(new DrivePath(mPathContainer));
  }

  public PathContainer getPathContainer() {
    return mPathContainer;
  }
}
