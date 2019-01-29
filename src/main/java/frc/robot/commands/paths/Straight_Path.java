package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class Straight_Path extends PathContainer {

    @Override
    public Path buildPath() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 205, 0, 0));
        sWaypoints.add(new Waypoint(65, 205, 15, 60));
        sWaypoints.add(new Waypoint(130, 205, 0, 60));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public RigidTransform2d getStartPose()
    {
        return new RigidTransform2d(new Translation2d(0, 205), Rotation2d.fromDegrees(0.0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}