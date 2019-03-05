package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import frc.robot.auto.commands.functions.WaitCommand;
import frc.robot.auto.commands.functions.drive.TeleDrive;
import frc.robot.auto.commands.functions.drive.pathfollowing.DrivePath;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.ResetPoseDrivePath;
import frc.robot.auto.commands.functions.drive.pathfollowing.ResetPoseFromPath;
import frc.robot.auto.commands.functions.drive.pathfollowing.WaitForMarker;

public class GZCommandGroup extends CommandGroup {
    private static final String DEFAULT_MARKER = "PrepForAction";

    public synchronized static GZCommandGroup getTeleDrive() {
        GZCommandGroup ret = new GZCommandGroup();
        ret.tele();
        return ret;
    }

    public static class InstantCompletion extends Command {
        public InstantCompletion() {
        }

        @Override
        protected boolean isFinished() {
            return true;
        }
    }

    public void print(String message) {
        add(new PrintCommand(message));
    }

    public void waitTime(double delay) {
        add(new WaitCommand(delay));
    }

    public void waitForMarker(String marker) {
        add(new WaitForMarker(marker));
    }

    public void add(ArrayList<Command> commands) {
        for (Command c : commands)
            add(c);
    }

    public void and(Command c) {
        addParallel(c);
    }

    public void add(Command c) {
        addSequential(c);
    }

    public void tele() {
        add(new TeleDrive());
    }

    public void resetDrive(PathContainer pc) {
        add(new ResetPoseDrivePath(pc));
    }

    public void resetPos(PathContainer pc) {
        add(new ResetPoseFromPath(pc));
    }

    public void waitForMarkerThen(ArrayList<Command> c) {
        waitForMarkerThen(DEFAULT_MARKER, c);
    }

    public void waitForMarkerThen(String marker, ArrayList<Command> c) {
        GZCommandGroup ret = new GZCommandGroup();
        ret.waitForMarker(marker);
        ret.add(c);
        add(ret);
    }

    public void resetDrivePaths(ArrayList<PathContainer> paths) {
        resetDrivePaths(paths, false);
    }

    public void resetDrivePaths(ArrayList<PathContainer> paths, boolean parallel) {
        GZCommandGroup ret = new GZCommandGroup();
        ret.resetPos(paths.get(0));
        ret.drivePaths(paths);
        if (parallel)
            and(ret);
        else
            add(ret);
    }

    public void drivePath(PathContainer pc) {
        add(new DrivePath(pc));
    }

    public void drivePathAnd(PathContainer pc) {
        and(new DrivePath(pc));
    }

    public void drivePaths(ArrayList<PathContainer> paths) {
        drivePaths(paths, false);
    }

    public void drivePaths(ArrayList<PathContainer> paths, boolean parallel) {
        GZCommandGroup ret = new GZCommandGroup();
        for (PathContainer p : paths)
            ret.drivePath(p);

        if (parallel)
            and(ret);
        else
            add(ret);
    }
}