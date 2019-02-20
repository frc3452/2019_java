package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;

public class RetractSlides extends Command {
  public RetractSlides() {
  }

  protected void initialize() {
    Superstructure.getInstance().retractSlides();
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return Elevator.getInstance().areSlidesOut();
  }

  protected void end() {
  }

  protected void interrupted() {
    end();
  }
}
