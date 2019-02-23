package frc.robot.util;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import frc.robot.auto.commands.functions.WaitCommand;
import frc.robot.auto.commands.functions.drive.pathfollowing.DrivePath;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.ResetPoseDrivePath;
import frc.robot.auto.commands.functions.drive.pathfollowing.WaitForMarker;

public class GZCommandGroup extends CommandGroup {
    public void print(String message) {
        add(new PrintCommand(message));
    }

    public void waitTime(double delay) {
        add(new WaitCommand(delay));
    }

    public void waitForMarker(String marker) {
        add(new WaitForMarker(marker));
    }

    public void add(Command c) {
        addSequential(c);
    }

    public void resetDrive(PathContainer pc)
    {
        add(new ResetPoseDrivePath(pc));
    }

    public void resetDriveBack(PathContainer pc)
    {
        add(new ResetPoseDrivePath(pc, true));
    }

    public void drivePath(PathContainer pc)
    {
        add(new DrivePath(pc));
    }
}