package frc.robot.commands.pwm;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Intake;

public class IntakeWhileDrive extends Command {

	private double m_value, m_percent, m_timeout;
	private boolean timeoutSet = false;

	private Timer timer = new Timer();

	private Intake intake = Intake.getInstance();

	/**
	 * @author macco
	 * @param value
	 * @param atPercent
	 * @param timeout
	 * @see Intake
	 */
	public IntakeWhileDrive(double value, double atPercent, double timeout) {
		requires(intake);

		m_value = value;
		m_percent = atPercent;
		m_timeout = timeout;
	}

	protected void initialize() {
		timer.stop();
		timer.reset();
		timer.start();
	}

	protected void execute() {
		//If drivetrain is certain percentage through movement, turn on intake for time
		
		if (Drive.getInstance().getPercentageComplete() > m_percent) {
			intake.manual(m_value);

			if (timeoutSet == false) {
				setTimeout(m_timeout + timer.get());
				timeoutSet = true;
			}

		} else {
			intake.stop();
		}
	}

	protected boolean isFinished() {
		return isTimedOut();
	}

	protected void end() {
		System.out.println("Done");
		intake.stop();
	}

	protected void interrupted() {
		end();
	}
}
