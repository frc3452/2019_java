package frc.robot.auto.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.commands.functions.PrintCommand;
import frc.robot.auto.commands.functions.drive.pathfollowing.ResetPoseDrivePath;
import frc.robot.auto.commands.functions.paths.Marker_Path;
import frc.robot.util.GZCommandGroup;

public class MarkerCommandGroup extends GZCommandGroup {
	public MarkerCommandGroup() {

		addParallel(new ResetPoseDrivePath(new Marker_Path(), true));
		addSequential(new CommandGroup() {
			{
				waitForMarker("Marker1");
				addSequential(new PrintCommand("Marker one hit"));
			}
		});

	}
}
