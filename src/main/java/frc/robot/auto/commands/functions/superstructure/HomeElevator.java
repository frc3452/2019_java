/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class HomeElevator extends Command {

  Elevator elev = Elevator.getInstance();

  public HomeElevator() {
    requires(elev);
  }
  @Override
  protected void initialize() {

  }

  @Override
  protected void execute() {
    elev.zero();
  }

  @Override
  protected boolean isFinished() {
    return elev.getState() == ElevatorState.NEUTRAL;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    elev.stop();
  }
  @Override
  protected void interrupted() {
    end();
  }
}
