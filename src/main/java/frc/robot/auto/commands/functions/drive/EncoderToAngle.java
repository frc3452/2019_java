package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.ConfigurableDrive.Rotation2d;
import frc.robot.subsystems.Drive;

public class EncoderToAngle extends Command {

  // private
  private Rotation2d mTar;
  private Drive drive = Drive.getInstance();
  private boolean relative = false;

  public EncoderToAngle(Rotation2d angle, boolean rel) {
    mTar = angle;
    relative = rel;
  }

  public EncoderToAngle(Rotation2d angle) {
    this(angle, false);
  }

  @Override
  protected void initialize() {
    if (relative)
      drive.turnRelative(mTar);
    else
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
