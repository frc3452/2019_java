package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;


public class TestPath4 implements PathContainer {
    
    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(64,162,0,0));
        sWaypoints.add(new Waypoint(177,162,15,60));
        sWaypoints.add(new Waypoint(234,163,30,60));
        sWaypoints.add(new Waypoint(234,235,15,60));
        sWaypoints.add(new Waypoint(139,235,15,60));
        sWaypoints.add(new Waypoint(139,163,15,60));
        sWaypoints.add(new Waypoint(177,162,0,60));
        sWaypoints.add(new Waypoint(177,162,15,60));
        sWaypoints.add(new Waypoint(234,163,30,60));
        sWaypoints.add(new Waypoint(234,235,15,60));
        sWaypoints.add(new Waypoint(139,235,15,60));
        sWaypoints.add(new Waypoint(139,163,15,60));
        sWaypoints.add(new Waypoint(177,162,0,60));
        sWaypoints.add(new Waypoint(177,162,15,60));
        sWaypoints.add(new Waypoint(234,163,30,60));
        sWaypoints.add(new Waypoint(234,235,15,60));
        sWaypoints.add(new Waypoint(139,235,15,60));
        sWaypoints.add(new Waypoint(139,163,15,60));
        sWaypoints.add(new Waypoint(177,162,0,60));

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