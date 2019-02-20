package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;

public class OpenClaw extends Command {
  public OpenClaw() {
  }

  @Override
  protected void initialize() {
    Superstructure.getInstance().openClaw();
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return Elevator.getInstance().isClawOpen();
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
    end();
  }
}
