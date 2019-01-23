package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;


public class TestPath3 implements PathContainer {
    
    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(20,48,0,0));
        sWaypoints.add(new Waypoint(72,29,15,60));
        sWaypoints.add(new Waypoint(100,63,15,60));
        sWaypoints.add(new Waypoint(133,22,15,60));
        sWaypoints.add(new Waypoint(156,55,15,60));
        sWaypoints.add(new Waypoint(187,22,15,60));
        sWaypoints.add(new Waypoint(227,47,15,60));
        sWaypoints.add(new Waypoint(251,19,0,60));

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
}