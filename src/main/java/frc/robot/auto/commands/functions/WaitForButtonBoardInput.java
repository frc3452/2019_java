package frc.robot.auto.commands.functions;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder;
import frc.robot.auto.commands.AutoModeBuilder.ScoringLocation;
import frc.robot.util.GZCommand;
import frc.robot.util.drivers.controllers.OperatorController;

public class WaitForButtonBoardInput extends Command {

  OperatorController op = GZOI.op;

  ScoringLocation mOutputLocation = null;

  public WaitForButtonBoardInput() {
  }

  protected void initialize() {
  }

  protected void execute() {
    // if (op.hatchPanel2.get()) {
    // mOutputLocation = new ScoringLocation(ScoringPosition.CARGO_SHIP_FACE,
    // ScoringSide.LEFT);
    // } else if (op.clawToggle.get()) {
    // mOutputLocation = new ScoringLocation(ScoringPosition.ROCKET_MID,
    // ScoringSide.LEFT);
    // }

  }

  protected boolean isFinished() {
    return mOutputLocation != null || !AutoModeBuilder.hasSelectedFeederStation();
  }

  protected void end() {
    if (!AutoModeBuilder.hasSelectedFeederStation()) {
      GZCommand command = AutoModeBuilder.getCommandFromFeederStation(mOutputLocation);
      System.out.println("WARNING Running command " + command.getName());
      command.start();
    } else {
      System.out.println("WARNING Could not select next move! Feeder station was null in builder!");
    }
  }

  protected void interrupted() {
    // end();
  }
}
