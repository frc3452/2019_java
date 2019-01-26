package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class Straight_Path_Changing_Speed implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(20, 157, 0, 0));
        sWaypoints.add(new Waypoint(74, 157, 15, 100));
        sWaypoints.add(new Waypoint(135, 157, 15, 100));
        sWaypoints.add(new Waypoint(245, 157, 0, 100));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(20, 157), Rotation2d.fromDegrees(0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}