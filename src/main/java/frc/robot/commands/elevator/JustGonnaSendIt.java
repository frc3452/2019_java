package frc.robot.commands.elevator;

import frc.robot.commands.pwm.IntakeTime;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class JustGonnaSendIt extends CommandGroup {

    /**
     * <b>Just Gonna Send it</b>
     */
    public JustGonnaSendIt() {
    	addSequential(new ElevatorTime(1,10));
		addSequential(new IntakeTime(1, .5));
    }
}
