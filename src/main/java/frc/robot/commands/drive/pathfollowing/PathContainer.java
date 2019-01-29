package frc.robot.commands.drive.pathfollowing;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Translation2d;

public abstract class PathContainer {
    public ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();

    private boolean isFlipped = false;

    public abstract Path buildPath();

    public RigidTransform2d getStartPose() {
        final Waypoint firstPoint = sWaypoints.get(0);
        return new RigidTransform2d(new Translation2d(firstPoint.position.x(), firstPoint.position.y()),firstPoint.position.direction());
    }

    public abstract boolean isReversed();

    public boolean isFlipped()
    {
        return isFlipped;
    }

    public Path flip() {
        isFlipped = !isFlipped;

        buildPath();

        ArrayList<Waypoint> mTempWaypoints = new ArrayList<Waypoint>();

        for (int i = sWaypoints.size() - 1; i >= 0; i--) {
            mTempWaypoints.add(sWaypoints.get(i));
        }

        sWaypoints = mTempWaypoints;

        return buildPath();
    }
}
