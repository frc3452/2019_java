package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kElevator;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;

public class ElevatorWhileDrive extends Command {

	private double m_value, m_percent;

	private Elevator elevator = Elevator.getInstance();

	/**
	 * @author macco
	 * @param value
	 * @param atPercent
	 * @see Elevator
	 */
	public ElevatorWhileDrive(double value, double atPercent) {
		requires(elevator);

		m_value = value;
		m_percent = atPercent;
	}

	@Override
	protected void initialize() {
		setTimeout(15);
		elevator.setTarget(-3452);
	}

	@Override
	protected void execute() {
		if (Drive.getInstance().getPercentageComplete() > m_percent)
			elevator.setHeight(m_value);
		else
			elevator.stop();
	}

	@Override
	protected boolean isFinished() {
		return elevator.isEncoderMovementDone(kElevator.CLOSED_COMPLETION + .05) || isTimedOut();
	}

	@Override
	protected void end() {
		elevator.stop();
		System.out.println("Elevator position completed.");
	}

	@Override
	protected void interrupted() {
		end();
	}
}
