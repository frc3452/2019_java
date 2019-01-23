package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class L_R_CRGO_SHIP_3 implements PathContainer {
    
    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(64,118,0,0));
        sWaypoints.add(new Waypoint(65,118,0,0));
        sWaypoints.add(new Waypoint(140,116,45,60,"LowerIntake"));
        sWaypoints.add(new Waypoint(260,50,45,60));
        sWaypoints.add(new Waypoint(260,117,15,60));
        sWaypoints.add(new Waypoint(260,274,0,60));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }
    
    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(64, 118), Rotation2d.fromDegrees(0)); 
    }

    @Override
    public boolean isReversed() {
        return false; 
    }
}