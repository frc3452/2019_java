package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;
import frc.robot.util.GZTimer;

public class DriveAtVelocityForTime extends Command {

  private double leftVel, rightVel, time;

  private GZTimer timer = new GZTimer();

  private Drive drive = Drive.getInstance();

  public DriveAtVelocityForTime(double leftVel, double rightVel, double time) {
    requires(drive);
    this.leftVel = leftVel;
    this.rightVel = rightVel;
    this.time = time;
  }

  protected void initialize() {
    setTimeout(time);
    // timer.startTimer();
  }
  protected void execute() {
    drive.setVelocity(leftVel, rightVel);
  }
  protected boolean isFinished() {
    // return timer.get() > time;
    return isTimedOut();
  }
  protected void end() {
    System.out.println("COMMAND DONE!");
    drive.stop();
    drive.handleStates();
  }
  protected void interrupted() {
    end();
  }
}
