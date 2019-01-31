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
            public Rotation2d getStartRotation()
            {
                return other.buildPath().getEndAngle();
            }
        };


        ret.getStartRotation();
        ArrayList<Waypoint> flippedPoints = new ArrayList<Waypoint>();
        
        //Flip waypoints
        for (int i = other.sWaypoints.size() - 1; i >= 0; i--){
            flippedPoints.add(new Waypoint(other.sWaypoints.get(i)));
        }

        double minSpeed = 3452;
        //Find slowest nonzero speed
        for (Waypoint p : other.sWaypoints)
        {
            if (p.speed != 0 && p.speed <= minSpeed)
            {
                minSpeed = p.speed;
            }
        }

        if (minSpeed == 3452)
        {
            System.out.println("ERROR Could not find nonzero speed for path " + other.getClass().getSimpleName());
            return null;
        }    
        
        //Apply speed
        for (int i = 0; i < flippedPoints.size(); i++)
        {
            flippedPoints.get(i).speed = minSpeed;
        }
        ret.sWaypoints = flippedPoints;
        return ret;
    }

    public PathContainer getReversed()
    {
        return getReversed(this);
    }

    public ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    
    public abstract boolean isReversed();

    public Path buildPath() {
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public abstract Rotation2d getStartRotation();

    public RigidTransform2d getStartPose() {
        final Waypoint firstPoint = sWaypoints.get(0);

        return new RigidTransform2d(new Translation2d(firstPoint.position.x(), firstPoint.position.y()),
                getStartRotation());
    }

    public PathContainer print()
    {
        System.out.println("PRINTING PATH  " + this.getClass().getSimpleName());
        System.out.println("Reversed: " + this.isReversed());
        System.out.println("Starting position: " + this.getStartPose().toString());
        System.out.println("Ending position: " + this.buildPath().getEndPosition());

        System.out.println("|X-Y| |Radius| |Speed|");
        int counter = 1;
        for (Waypoint o : sWaypoints)
        {
            System.out.println("Waypoint " + counter++ + " :" + o.position.toString() + "\t" + o.radius + "\t" + o.speed);
        }

        System.out.println("\n");
        return this;
    }
    
}
