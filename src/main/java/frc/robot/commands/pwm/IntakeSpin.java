package frc.robot.commands.pwm;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Intake;

public class IntakeSpin extends Command {

	private boolean m_clockwise;

	private Intake intake = Intake.getInstance();

	/**
	 * @author macco
	 * @param speed
	 * @param clockwise
	 * @see Intake
	 */
	public IntakeSpin(boolean clockwise) {
		requires(intake);

		m_clockwise = clockwise;
	}

	protected void initialize() {
	}

	protected void execute() {
		intake.spin(m_clockwise);
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
		intake.stop();
	}

	protected void interrupted() {
		end();
	}
}
