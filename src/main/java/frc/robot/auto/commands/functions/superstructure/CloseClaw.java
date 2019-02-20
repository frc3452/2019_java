package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Superstructure.Actions;

public class CloseClaw extends Command {
  public CloseClaw() {
  }

  protected void initialize() {
    Superstructure.getInstance().closeClaw();
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return Elevator.getInstance().isClawClosed();
  }
  
  protected void end() {
  }

  protected void interrupted() {
    end();
  }
}
