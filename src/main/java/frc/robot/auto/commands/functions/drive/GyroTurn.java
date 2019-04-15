package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kAuton;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.subsystems.Drive;

public class GyroTurn extends Command {
  private double m_gyro, m_real_target, m_target, m_speed, m_precise, m_constantspeed;
  private int m_target_good = 0;

  private Drive drive = Drive.getInstance();

  /**
   * @author macco
   * @param targetangle
   * @param speedPerDegree
   * @param degreesToComplete
   * @param constantSpeed
   * @see Drive
   */
  public GyroTurn(double targetangle, double speedPerDegree, double constantSpeed, double degreesToComplete) {
    requires(drive);

    m_gyro = drive.getGyroAngle().getDegrees();
    m_real_target = targetangle;

    // m_target = m_real_target;
    m_target = Rotation2d.fromDegrees(m_real_target).inverse().getDegrees();

    m_speed = Math.abs(speedPerDegree);
    m_constantspeed = Math.abs(constantSpeed);
    m_precise = degreesToComplete;
  }

  public GyroTurn(double targetangle, double degreesToComplete) {
    this(targetangle, kAuton.GYRO_PERCENT_PER_DEGREE_OF_ERROR, kAuton.GYRO_CONSTANT_SPEED, degreesToComplete);
  }

  public GyroTurn(double targetangle) {
    this(targetangle, kAuton.GYRO_PRECISION_IN_DEGREES);
  }

  public GyroTurn(Rotation2d targetangle) {
    this(targetangle.getDegrees());
  }

  protected void initialize() {
    setTimeout(kAuton.GYRO_TIMEOUT);
    System.out.println("Gyro servo started at angle {" + m_real_target + "} with gain of {" + m_speed + "}.");
  }

  protected void execute() {
    m_gyro = drive.getGyroAngle().getDegrees();
    double error = Math.abs(m_gyro - m_target);

    // a curr, b target

    double flip = 1;
    if (m_gyro < m_target) {
      if (m_gyro - m_target <= 180)
        flip *= -1;
      else
        flip *= +1;
    } else {
      if (m_gyro - m_target <= 180)
        flip *= 1;
      else
        flip *= -1;
    }

    drive.arcade(0, flip * m_speed * error + (flip * m_constantspeed));
  }

  protected boolean isFinished() {
    double off = Math
        .abs(Rotation2d.fromDegrees(m_gyro).inverse().rotateBy(Rotation2d.fromDegrees(m_target)).getDegrees());
    if (off < m_precise)
      m_target_good++;

    return (m_target_good > 30);
    // return GZUtil.epsilonEquals(m_gyro, m_target, m_precise) || isTimedOut();
    // return ((m_gyro < (m_target + m_precise)) && (m_gyro > (m_target -
    // m_precise))) || isTimedOut();
  }

  protected void end() {
    System.out.println("Gyro servo finished.");
    drive.stop();
  }

  protected void interrupted() {
    end();
  }
}