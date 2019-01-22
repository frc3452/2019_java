package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class TestPath implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();

        final double speed = 60;

        sWaypoints.add(new Waypoint(20, 48, 0, 0));
        sWaypoints.add(new Waypoint(145, 46, 40, speed));
        sWaypoints.add(new Waypoint(144, 134, 40, speed));
        sWaypoints.add(new Waypoint(49, 131, 40, speed));
        sWaypoints.add(new Waypoint(48, 49, 0, speed));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(20, 48), Rotation2d.fromDegrees(0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
    // WAYPOINT_DATA:
    // [{"position":{"x":50,"y":50},"speed":0,"radius":0,"comment":""},{"position":{"x":100,"y":50},"speed":60,"radius":50,"comment":""},{"position":{"x":140,"y":140},"speed":60,"radius":0,"comment":""}]
    // IS_REVERSED: false
    // FILE_NAME: TestPath
}