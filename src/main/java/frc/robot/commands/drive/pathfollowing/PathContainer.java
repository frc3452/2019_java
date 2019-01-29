package frc.robot.commands.drive.pathfollowing;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public abstract class PathContainer {

    public static PathContainer getReversed(PathContainer other)
    {
        PathContainer ret = new PathContainer(){
        
            @Override
            public boolean isReversed() {
                return !other.isReversed();
            }
        
            @Override
            public Rotation2d getStartRotation() {
                return other.buildPath().getEndPosition().direction();
            }
        };

        ArrayList<Waypoint> flippedPoints = new ArrayList<Waypoint>();

        //Flip waypoints
        for (int i = other.sWaypoints.size() - 1; i >= 0; i--)
            flippedPoints.add(other.sWaypoints.get(i));

        //Apply speeds in first direction to flipped path
        for (int i = 0; i < flippedPoints.size(); i++)
        {
            flippedPoints.get(i).speed = other.sWaypoints.get(i).speed;
        }

        ret.sWaypoints = flippedPoints;
        return ret;
    }

    public ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    
    public abstract boolean isReversed();
    public abstract Rotation2d getStartRotation();

    public Path buildPath() {
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public RigidTransform2d getStartPose() {
        final Waypoint firstPoint = sWaypoints.get(0);

        return new RigidTransform2d(new Translation2d(firstPoint.position.x(), firstPoint.position.y()),
                getStartRotation());
    }

    public void print()
    {
        System.out.println("PRINTING PATH  " + this.getClass().getSimpleName());
        System.out.println("Reversed: " + this.isReversed());
        System.out.println("Starting rotation: " + this.getStartRotation().toString());
        System.out.println("Starting position: " + this.getStartPose().toString());
        System.out.println("RAWISFJSID: " + this.buildPath().getEndAngle());

        System.out.println("|X-Y| |Radius| |Speed|");
        int counter = 1;
        for (Waypoint o : sWaypoints)
        {
            System.out.println("Waypoint " + counter++ + " :" + o.position.toString() + "\t" + o.radius + "\t" + o.speed);
        }

        System.out.println("TO STRING");
        // System.out.println(this.buildPath().toString());

        System.out.println("\n");
    }
    
}
