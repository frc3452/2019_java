package frc.robot.auto.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.commands.functions.Print;
import frc.robot.util.GZCommandGroup;

public class MarkerCommandGroup extends GZCommandGroup {
	public MarkerCommandGroup() {

		// addParallel(new ResetPoseDrivePath(new Marker_Path(), true));
		addSequential(new CommandGroup() {
			{
				waitForMarker("Marker1");
				addSequential(new Print("Marker one hit"));
			}
		});
	}
}
