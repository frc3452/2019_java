package frc.robot.auto.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.commands.drive.pathfollowing.ResetPoseDrivePath;
import frc.robot.auto.commands.drive.pathfollowing.WaitForMarker;
import frc.robot.auto.commands.paths.Marker_Path;

public class MarkerCommandGroup extends CommandGroup {
	public MarkerCommandGroup() {
		
		
		
		addParallel(new ResetPoseDrivePath(new Marker_Path(), true));
		addSequential(new CommandGroup() {
			{
				addSequential(new WaitForMarker("Marker1"));
				addSequential(new PrintCommand("Marker one hit"));
			}
		});

		
	}
}