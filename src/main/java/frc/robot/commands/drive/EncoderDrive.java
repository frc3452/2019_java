package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class EncoderDrive extends Command {
	private double m_left, m_right, m_laccel, m_raccel, m_topspeed;

	private Drive drive = Drive.getInstance();

	/**
	 * @author macco
	 * @param left
	 * @param right
	 * @param leftaccel
	 * @param rightaccel
	 * @param topspeed
	 * @see Drive
	 * @see Drive
	 */
	public EncoderDrive(double left, double right, double leftaccel, double rightaccel, double topspeed) {
		requires(drive);

		m_left = left;
		m_right = right;
		m_laccel = leftaccel;
		m_raccel = rightaccel;
		m_topspeed = topspeed;
	}

	protected void initialize() {
		setTimeout(7);
	}

	protected void execute() {
		drive.motionMagic(m_left, m_right, m_laccel, m_raccel, m_topspeed, m_topspeed);
	}

	protected boolean isFinished() {
		return false;
//		return isTimedOut();
		// return drive.encoderIsDone(2) || isTimedOut();
	}

	protected void end() {
		drive.stop();
		System.out.println("Encoder drive completed.");
	}

	protected void interrupted() {
		end();
	}
}
