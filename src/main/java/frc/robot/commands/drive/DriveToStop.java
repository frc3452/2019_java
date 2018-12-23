package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class DriveToStop extends Command {
	private double m_speed;

	private Drive drive = Drive.getInstance();

	/**
	 * @author macco
	 * @param speed
	 * @see Drive
	 */
	public DriveToStop(double speed) {
		requires(drive);
		
		m_speed = speed;
	}

	protected void initialize() {
		setTimeout(10);
	}

	protected void execute() {
		drive.arcade(m_speed, 0);
	}

	protected boolean isFinished() {
		return drive.encoderSpeedIsUnder(200) || isTimedOut();
	}

	protected void end() {
		drive.stop();
	}

	protected void interrupted() {
		end();
	}
}
