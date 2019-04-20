package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;

public class Center_Rocket_Far_Left extends PathContainer {
    public Center_Rocket_Far_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(120, 162, 15, 30));
        sWaypoints.add(new Waypoint(215, 240, 15, 60));
        sWaypoints.add(new Waypoint(251, 270, 15, 60));
        sWaypoints.add(new Waypoint(275.3814278800546, 288.95486854129393, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(270 - 61.25);
    }
}