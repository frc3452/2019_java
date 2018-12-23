package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class DriveTime extends Command {
	private double m_speed, m_rotate, m_time;

	private Drive drive = Drive.getInstance();

	/**
	 * @author macco
	 * @param speed
	 * @param rotate
	 * @param time
	 * @see Drive
	 */
	public DriveTime(double speed, double rotate, double time) {
		requires(drive);
		m_speed = speed;
		m_rotate = rotate;
		m_time = time;
	}

	protected void initialize() {
		setTimeout(m_time);
	}

	protected void execute() {
		drive.arcade(m_speed, m_rotate);
	}

	protected boolean isFinished() {
		return isTimedOut();
	}

	protected void end() {
		drive.stop();
	}

	protected void interrupted() {
		end();
	}
}
