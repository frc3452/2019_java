package frc.robot.auto.commands.functions.drive.pathfollowing;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public abstract class PathContainer {

    public static PathContainer getReversed(PathContainer other) {
        PathContainer ret = new PathContainer() {
            @Override
            public boolean isReversed() {
                return !other.isReversed();
            }

            @Override
            public Rotation2d getStartRotation() {
                return other.getEndRotation();
            }
        };

        ret.getStartRotation();
        ArrayList<Waypoint> flippedPoints = new ArrayList<Waypoint>();

        // Flip waypoints
        for (int i = other.sWaypoints.size() - 1; i >= 0; i--) {
            flippedPoints.add(new Waypoint(other.sWaypoints.get(i)));
        }

        double minSpeed = 3452;
        // Find slowest nonzero speed
        for (Waypoint p : other.sWaypoints) {
            if (p.speed != 0 && p.speed <= minSpeed) {
                minSpeed = p.speed;
            }
        }

        if (minSpeed == 3452) {
            System.out.println("ERROR Could not find nonzero speed for path " + other.getClass().getSimpleName());
            return null;
        }

        // Apply speed
        for (int i = 0; i < flippedPoints.size(); i++) {
            flippedPoints.get(i).speed = minSpeed;
        }
        ret.sWaypoints = flippedPoints;
        return ret;
    }

    public PathContainer getReversed() {
        return getReversed(this);
    }

    public ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();

    public abstract boolean isReversed();

    public Path buildPath() {
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public Rotation2d getStartRotation() {
        return buildPath().getStartAngle().rotateBy(Rotation2d.fromDegrees(isReversed() ? 180 : 0));
    }

    public Rotation2d getEndRotation() {
        return buildPath().getEndAngle().rotateBy(Rotation2d.fromDegrees(isReversed() ? 180 : 0));
    }

    public RigidTransform2d getStartPose() {
        final Waypoint firstPoint = sWaypoints.get(0);

        return new RigidTransform2d(new Translation2d(firstPoint.position.x(), firstPoint.position.y()),
                getStartRotation());
    }

    public RigidTransform2d getEndPose() {
        final Waypoint lastPoint = sWaypoints.get(sWaypoints.size() - 1);

        return new RigidTransform2d(new Translation2d(lastPoint.position.x(), lastPoint.position.y()),
                getEndRotation());
    }

    public ArrayList<PathContainer> toList()
    {
        ArrayList<PathContainer> ret = new ArrayList<PathContainer>();
        ret.add(this);
        return ret;
    }

    public PathContainer print() {
        System.out.println("PRINTING PATH  " + this.getClass().getSimpleName());
        System.out.println("Reversed: " + this.isReversed());
        System.out.println("Starting position: " + this.getStartPose());
        System.out.println("Ending position: " + this.getEndPose());

        System.out.println("|X-Y| |Radius| |Speed|");
        int counter = 1;
        for (Waypoint o : sWaypoints) {
            System.out.println("Waypoint " + counter++ + " :" + o.position.toString() + "\t" + o.radius + "\t" + o.speed);
        }

        // System.out.println(this.buildPath().toString());

        System.out.println("\n");
        return this;
    }

}
