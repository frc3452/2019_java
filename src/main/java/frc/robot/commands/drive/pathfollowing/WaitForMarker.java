/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drive.pathfollowing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class WaitForMarker extends Command {

  private Drive drive = Drive.getInstance();
  private final String mMarker;

  public WaitForMarker(String marker) {
    mMarker = marker;
  }

  protected void initialize() {
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return drive.hasPassedMarker(mMarker);
  }

  protected void end() {
  }

  protected void interrupted() {
    end();
  }
}
