/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.commands.WaitCommand;
import frc.robot.util.GZUtil;

public class ResetPoseDrivePath extends CommandGroup {

  private final PathContainer mPathContainer;

  private static final double DEFAULT_DELAY = 4;

  public ResetPoseDrivePath(PathContainer pathContainer) {
    this(pathContainer, false);
  }

  public ResetPoseDrivePath(PathContainer pathContainer, boolean andBack) {
    this(pathContainer, andBack, DEFAULT_DELAY);
  }

  public ResetPoseDrivePath(PathContainer pathContainer, boolean andBack, double delay) {
    mPathContainer = pathContainer;

    addSequential(new ResetPoseFromPath(mPathContainer));
    addSequential(new DrivePath(mPathContainer));

    if (andBack) {
      addSequential(new WaitCommand(delay));
      addSequential(new DrivePath(PathContainer.getReversed(mPathContainer)));
    }
  }

  public PathContainer getPathContainer() {
    return mPathContainer;
  }
}
