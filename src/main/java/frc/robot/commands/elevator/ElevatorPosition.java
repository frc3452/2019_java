package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kElevator;
import frc.robot.subsystems.Elevator;

public class ElevatorPosition extends Command {

	private double m_value;
//	private boolean l_rev = false, l_fwd = false;

private Elevator elevator = Elevator.getInstance();

	/**
	 * Encoder movement of elevator
	 * 
	 * @author macco
	 * @param value
	 * @see Elevator
	 */
	public ElevatorPosition(double value) {
		requires(elevator);

		m_value = value;
	}

	protected void initialize() {
		setTimeout(3);
	}

	protected void execute() {
		elevator.setHeight(m_value);
	}

	protected boolean isFinished() {
		return elevator.isEncoderMovementDone(kElevator.CLOSED_COMPLETION) || isTimedOut();
	}

	protected void end() {
		elevator.stop();
	}

	protected void interrupted() {
		end();
	}
}
