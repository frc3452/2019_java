package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class Center_Rocket_Far_Left extends PathContainer {
    public Center_Rocket_Far_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(120, 162, 15, 30));
        sWaypoints.add(new Waypoint(205, 203, 15, 60));
        sWaypoints.add(new Waypoint(291, 245, 15, 60));
        sWaypoints.add(new Waypoint(289.973, 280.95, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(275.3814278800546, 288.95486854129393, 1, 30)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(260.79, 296.96, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}