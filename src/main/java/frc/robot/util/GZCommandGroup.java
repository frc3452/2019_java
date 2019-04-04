package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.commands.functions.Print;
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

    public synchronized void print(String message) {
        add(new Print(message));
    }

    public synchronized void waitTime(double delay) {
        add(new WaitCommand(delay));
    }

    public synchronized void waitForMarker(String marker) {
        add(new WaitForMarker(marker));
    }

    public synchronized void add(ArrayList<Command> commands) {
        for (Command c : commands)
            add(c);
    }

    public synchronized void and(Command c) {
        addParallel(c);
    }

    public synchronized void add(Command c) {
        addSequential(c);
    }

    public synchronized void tele() {
        add(new TeleDrive());
    }

    public synchronized void resetDrive(PathContainer pc) {
        add(new ResetPoseDrivePath(pc));
    }

    public synchronized void resetPos(PathContainer pc) {
        add(new ResetPoseFromPath(pc));
    }

    public synchronized void waitForMarkerThen(Command c) {
        waitForMarkerThen(DEFAULT_MARKER, c);
    }

    public synchronized void waitForMarkerThen(String marker, Command c) {
        GZCommandGroup ret = new GZCommandGroup();
        ret.waitForMarker(marker);
        ret.add(c);
        add(ret);
    }

    public synchronized void resetDrivePaths(ArrayList<PathContainer> paths) {
        resetDrivePaths(paths, false);
    }

    public synchronized ArrayList<GZCommandGroup> toList() {
        ArrayList<GZCommandGroup> ret = new ArrayList<GZCommandGroup>();
        ret.add(this);
        return ret;
    }

    public synchronized void resetDrivePaths(ArrayList<PathContainer> paths, boolean parallel) {
        GZCommandGroup ret = new GZCommandGroup();
        ret.resetPos(paths.get(0));
        ret.drivePaths(paths);
        if (parallel)
            and(ret);
        else
            add(ret);
    }

    public synchronized void drivePath(PathContainer pc) {
        add(new DrivePath(pc));
    }

    public synchronized void drivePathAnd(PathContainer pc) {
        and(new DrivePath(pc));
    }

    public synchronized void drivePaths(ArrayList<PathContainer> paths) {
        drivePaths(paths, false);
    }

    public synchronized void drivePaths(ArrayList<PathContainer> paths, boolean parallel) {
        GZCommandGroup ret = new GZCommandGroup();
        for (PathContainer p : paths)
            ret.drivePath(p);

        if (parallel)
            and(ret);
        else
            add(ret);
    }
}