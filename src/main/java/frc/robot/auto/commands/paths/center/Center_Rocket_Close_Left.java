package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Center_Rocket_Close_Left extends PathContainer {
    public Center_Rocket_Close_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(120, 162, 15, 60));
        sWaypoints.add(new Waypoint(141, 255, 15, 60));
        sWaypoints.add(new Waypoint(172.325, 283.165, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(184.89750408828039, 290.0624925480436, 1, 30)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(197.47, 296.96, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}