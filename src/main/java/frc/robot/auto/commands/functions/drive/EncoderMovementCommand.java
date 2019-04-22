package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.subsystems.Drive;

public class EncoderMovementCommand extends Command {

  private final EncoderMovement movement;

  private Drive drive = Drive.getInstance();

  public EncoderMovementCommand(EncoderMovement movement) {
    requires(drive);
    this.movement = movement;
  }

  protected void initialize() {
    System.out.println("Jogging " + movement);

    drive.driveJog(movement);

    setTimeout(5);
  }

  protected void execute() {
  }

  protected boolean isFinished() {
    return drive.encoderJogIsDone() || isTimedOut();
  }

  protected void end() {
    drive.velocityStop();
  }

  protected void interrupted() {
    end();
  }
}
