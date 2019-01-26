package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.paths.PathBuilder.Waypoint;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class Straight_Curve_Left_Back implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
      
        final double speed = 80;

        sWaypoints.add(new Waypoint(232, 103, 15, speed));
        sWaypoints.add(new Waypoint(232, 48, 30, speed));
        sWaypoints.add(new Waypoint(20, 48, 0, 0));
        
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(232, 116), Rotation2d.fromDegrees(-90));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}