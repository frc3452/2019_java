package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;

public class ElevatorTime extends Command {

	private double m_speed, m_time;

	private Elevator elevator = Elevator.getInstance();

	/**
	 * @author macco
	 * @param speed positive = up
	 * @param time
	 * @see Elevator
	 */

	public ElevatorTime(double speed, double time) {
		requires(elevator);

		m_speed = speed;
		m_time = time;
	}

	protected void initialize() {
		setTimeout(m_time);
	}

	protected void execute() {
		elevator.manual(m_speed);

	}

	protected boolean isFinished() {

		if (elevator.getTopLimit() && m_speed > 0)
			return true;

		if (elevator.getBottomLimit() && m_speed < 0)
			return true;

		return isTimedOut();
	}

	protected void end() {
		elevator.stop();
	}

	protected void interrupted() {
		end();
	}
}
