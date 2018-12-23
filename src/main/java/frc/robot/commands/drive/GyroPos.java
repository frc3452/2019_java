package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class GyroPos extends Command {
	private double m_gyro, m_target, m_speed, m_precise;

	private Drive drive = Drive.getInstance();

	/**
	 * @author macco
	 * @param targetangle
	 * @param speed
	 * @param precise
	 * @see Drive
	 */
	public GyroPos(double targetangle, double speed, double precise) {
		requires(drive);

		m_gyro = drive.getGyroFusedHeading();
		m_target = targetangle;
		m_speed = speed;
		m_precise = precise;
	}

	protected void initialize() {
		setTimeout(8);
	}

	protected void execute() {
		m_gyro = drive.getGyroFusedHeading();

		if (m_gyro < 360 && m_gyro > 180) {
			if ((m_gyro < (m_target + 180)) && (m_gyro > m_target)) {
				drive.arcade(0, -m_speed);
			} else {
				drive.arcade(0, m_speed);
			}
		}

		if (m_gyro > 0 && m_gyro < 180) {
			if ((m_gyro > (m_target - 180)) && (m_gyro < m_target)) {
				drive.arcade(0, m_speed);
			} else {
				drive.arcade(0, -m_speed);
			}
		}

	}

	protected boolean isFinished() {
		return ((m_gyro < (m_target + m_precise)) && (m_gyro > (m_target - m_precise))) || isTimedOut();
	}

	protected void end() {
		drive.stop();
	}

	protected void interrupted() {
		end();
	}
}
