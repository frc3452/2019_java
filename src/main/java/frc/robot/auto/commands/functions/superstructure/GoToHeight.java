package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.subsystems.Elevator;

public class GoToHeight extends Command {

  private double inches;

  public GoToHeight(double inches) {
    this.inches = inches;
  }

  public GoToHeight(Heights h) {
    this(h.inches);
  }

  @Override
  protected void initialize() {
    Elevator.getInstance().setHeight(inches);
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return Elevator.getInstance().nearTarget();
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
    end();
  }
}
