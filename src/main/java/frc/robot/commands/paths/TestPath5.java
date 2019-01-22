package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class TestPath5 implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(64, 162, 0, 0));
        sWaypoints.add(new Waypoint(96, 162, 30, 60));
        sWaypoints.add(new Waypoint(210, 220, 30, 60));
        sWaypoints.add(new Waypoint(210, 101, 30, 60));
        sWaypoints.add(new Waypoint(96, 162, 30, 60));
        sWaypoints.add(new Waypoint(191, 163, 0, 60));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(64, 162), Rotation2d.fromDegrees(0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}