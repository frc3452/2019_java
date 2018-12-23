package frc.robot.commands.pwm;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Intake;

public class IntakeTime extends Command {

	private double m_speed, m_timeout;

	private Intake intake = Intake.getInstance();

	/**
	 * @author macco
	 * @param speed
	 * @param timeout
	 * @see Intake
	 */
	public IntakeTime(double speed, double timeout) {
		requires(intake);

		m_speed = speed;
		m_timeout = timeout;
	}

	protected void initialize() {
		setTimeout(m_timeout);
	}

	protected void execute() {
		intake.manual(m_speed);
	}

	protected boolean isFinished() {
		return isTimedOut();
	}

	protected void end() {
		intake.stop();
	}

	protected void interrupted() {
		end();
	}
}
