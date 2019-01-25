/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.paths;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.drive.DrivePath;
import frc.robot.commands.drive.ResetPoseFromPath;

public class DrivePathGroup extends CommandGroup {
  public DrivePathGroup(PathContainer pathContainer) {

    addSequential(new ResetPoseFromPath(pathContainer));

    
    addSequential(new DrivePath(pathContainer));
  }
}
