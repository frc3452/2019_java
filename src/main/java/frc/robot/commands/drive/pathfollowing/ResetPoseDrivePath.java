/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.WaitCommand;
import frc.robot.util.GZUtil;

public class ResetPoseDrivePath extends CommandGroup {

  private final PathContainer mPathContainer;

  private static final double DEFAULT_DELAY = 1;

  public ResetPoseDrivePath(PathContainer pathContainer) {
    this(pathContainer, false);
  }

  public ResetPoseDrivePath(PathContainer pathContainer, boolean andBack) {
    this(pathContainer, andBack, DEFAULT_DELAY);
  }

  public ResetPoseDrivePath(PathContainer pathContainer, boolean andBack, double delay) {
    mPathContainer = pathContainer;

    addPath(mPathContainer);
    if (andBack) {
      addSequential(new WaitCommand(delay));
      addPath(PathContainer.getReversed(mPathContainer));
    }
  }

  private void addPath(PathContainer container) {
    addSequential(new ResetPoseFromPath(container));
    addSequential(new DrivePath(container));
  }

  public PathContainer getPathContainer() {
    return mPathContainer;
  }
}
