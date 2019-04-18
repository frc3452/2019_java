package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kElevator;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.subsystems.Drive;

public class EncoderToAngle extends Command {

  // private
  private Rotation2d mTar;
  private Drive drive = Drive.getInstance();

  public EncoderToAngle(Rotation2d angle) {
    mTar = angle;
  }

  @Override
  protected void initialize() {
    drive.turnToHeading(mTar);
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return drive.getTurnToHeadingComplete();
  }

  @Override
  protected void end() {
    drive.velocityStop();
  }

  @Override
  protected void interrupted() {
    end();
  }
}
