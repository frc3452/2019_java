package frc.robot.auto.commands.functions.superstructure;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.auto.commands.AutoModeBuilder.GamePiece;
import frc.robot.auto.commands.AutoModeBuilder.ScoringLocation;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZCommandGroup;

public class ScoringCommand extends Command {
  private GamePiece mGamePiece;
  private ScoringLocation mScoringLocation;
  public ScoringCommand(ScoringLocation scoringLocation, GamePiece gamePiece) {
    mGamePiece = gamePiece;
    mScoringLocation = scoringLocation;
  }

  @Override
  protected void initialize() {

    GZCommandGroup ret = new GZCommandGroup();

    switch (mGamePiece) {
      case CARGO:
          if (mScoringLocation.isOnCargoShip()) {
              ret.add(new GoToHeight(Heights.Cargo_Ship));
          } else {
              ret.add(new GoToHeight(Heights.Cargo_1));
          }
          ret.add(new RunAction(Actions.THROW_CARGO));
          break;
      case HATCH_PANEL:
          // ret.add(new GoToHeight(Heights.Cargo_1));
          if (mScoringLocation.isOnCargoShip()) {
              ret.add(new ExtendSlides());
              ret.add(new GoToHeight(Heights.HP_1));
          } else { // rocket
              ret.add(new GoToHeight(Heights.HP_2));
          }
          ret.add(new RunAction(Actions.SCORE_HATCH));
          break;
        }
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
    //end
  }
}
