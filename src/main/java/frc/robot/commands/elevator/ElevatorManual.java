package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.util.GZJoystick;

public class ElevatorManual extends Command {

//	private int m_axis;
	private GZJoystick m_joy;

	private Elevator elevator = Elevator.getInstance();

	/**
	 * Operator control of elevator
	 * 
	 * @author macco
	 * @param joy
	 * @see Elevator
	 */
	public ElevatorManual(GZJoystick joy) {
		requires(elevator);
		m_joy = joy;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		elevator.manualJoystick(m_joy);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		elevator.stop();
	}

	@Override
	protected void interrupted() {
		end();
	}
}
