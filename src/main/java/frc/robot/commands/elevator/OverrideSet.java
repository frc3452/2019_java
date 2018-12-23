package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ESO;

public class OverrideSet extends InstantCommand {

	private ESO m_override;

	/**
	 * @author macco
	 * @param override
	 * @see Drive
	 * @see Elevator
	 * @see ESO
	 */
	public OverrideSet(ESO override) {
		super();
		m_override = override;
	}

	protected void initialize() {
		Elevator.getInstance().setSpeedLimitingOverride(m_override);
	}
}
