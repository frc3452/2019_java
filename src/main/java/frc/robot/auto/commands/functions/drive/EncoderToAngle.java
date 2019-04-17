package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kElevator;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.subsystems.Drive;

public class EncoderToAngle extends Command {

  // private
  private Rotation2d mTar;
  private double initLeft, initRight;
  private boolean left;
  private Drive drive = Drive.getInstance();
  private double tarLeft, tarRight;

  private double accel = kDrivetrain.MOTION_MAGIC_ACCEL;
  private double vel = kDrivetrain.MOTION_MAGIC_VEL;

  public EncoderToAngle(Rotation2d angle) {
    mTar = angle;
  }

  public EncoderToAngle(Rotation2d angle, double accel, double vel) {
    this(angle);
    this.accel = accel;
    this.vel = vel;
  }

  @Override
  protected void initialize() {
    initLeft = drive.getLeftRotations();
    initRight = drive.getRightRotations();

    double tar = mTar.getNormalDegrees();
    Rotation2d mCur = drive.getGyroAngle();
    double cur = mCur.getNormalDegrees();
    if (tar > 180) {
      if (cur > tar - 180 && cur < tar) {
        left = false;
      } else {
        left = true;
      }
    } else {
      if (cur > tar && cur < tar + 180) {
        left = true;
      } else {
        left = false;
      }
    }

    double toTurn = mCur.difference(mTar);

    tarLeft = initLeft + (toTurn * kDrivetrain.ROTATIONS_PER_DEGREE) * (left ? -1.0 : 1.0);
    tarRight = initRight + (toTurn * kDrivetrain.ROTATIONS_PER_DEGREE) * (left ? 1.0 : -1.0);
  }

  @Override
  protected void execute() {
    drive.motionMagic(tarLeft, tarRight, accel, vel);
  }

  @Override
  protected boolean isFinished() {
    return drive.encoderAngleIsDone();
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
