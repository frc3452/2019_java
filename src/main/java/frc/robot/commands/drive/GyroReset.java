package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class GyroReset extends Command {

	private Drive drive = Drive.getInstance();

	/**
	 * Gyro reset
	 * @author macco
	 * @see Drive
	 */
	public GyroReset() {
		requires(drive);
	}

	protected void initialize() {
		setTimeout(0.1);
		drive.zeroGyro();
	}
	protected void execute() {
		
	}
	protected boolean isFinished() {
		return isTimedOut();
	}
	protected void end() {
	}
	protected void interrupted() {
		end();
	}
}