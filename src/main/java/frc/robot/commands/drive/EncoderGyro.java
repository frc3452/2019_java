package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;

public class EncoderGyro extends Command {

	private double l_pos, r_pos, l_accel, r_accel, m_speed, c_gyro, t_gyro, k;

	private Drive drive = Drive.getInstance();

	/**
	 * Encoder drive with gyro correction
	 * 
	 * @author macco
	 * @param leftpos
	 * @param rightpos
	 * @param leftaccel
	 * @param rightaccel
	 * @param speed
	 * @param angle
	 * @param constant
	 * @see Drive
	 */
	public EncoderGyro(double leftpos, double rightpos, double leftaccel, double rightaccel, double speed, double angle,
			double constant) {
		requires(drive);

		l_pos = leftpos;
		r_pos = rightpos;
		l_accel = leftaccel;
		r_accel = rightaccel;
		m_speed = speed;
		t_gyro = angle;
		c_gyro = 0;
		k = constant;
	}

	protected void initialize() {
		setTimeout(10);
	}

	protected void execute() {
		c_gyro = drive.getGyroAngle();

		if (c_gyro < t_gyro + .4 && c_gyro > t_gyro - .4)
			drive.motionMagic(l_pos, r_pos, l_accel, r_accel, m_speed, m_speed);

		else if (c_gyro > t_gyro)
			drive.motionMagic(l_pos, r_pos, l_accel, r_accel, m_speed * (1 - (k * Math.abs(c_gyro - t_gyro))),
					m_speed);

		else if (c_gyro < t_gyro)
			drive.motionMagic(l_pos, r_pos, l_accel, r_accel, m_speed,
					m_speed * (1 - (k * Math.abs(c_gyro - t_gyro))));
	}

	protected boolean isFinished() {
		return drive.encoderIsDoneEither(1.3) || isTimedOut();
	}

	protected void end() {
		drive.stop();
	}

	protected void interrupted() {
		end();
	}
}
