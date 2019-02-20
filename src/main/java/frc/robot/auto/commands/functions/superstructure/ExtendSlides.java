package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;

public class ExtendSlides extends Command {
  public ExtendSlides() {
  }

  protected void initialize() {
    Superstructure.getInstance().extendSlides();
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
