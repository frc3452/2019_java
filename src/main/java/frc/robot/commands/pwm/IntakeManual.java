package frc.robot.commands.pwm;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Intake;

public class IntakeManual extends Command {
	private double m_speed;

	private Intake intake = Intake.getInstance();

	/**
	 * @author macco
	 * @param speed
	 * @see Intake
	 */
	public IntakeManual(double speed) {
		requires(intake);
		
		m_speed = speed;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		intake.manual(m_speed);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		intake.stop();
	}

	@Override
	protected void interrupted() {
		end();
	}
}
