package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;

public class GoToHeight extends Command {

  private Heights mHeight;
  public GoToHeight(Heights h) {
    this.mHeight = h;
  }

  @Override
  protected void initialize() {
    Superstructure.getInstance().runHeight(mHeight);
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
