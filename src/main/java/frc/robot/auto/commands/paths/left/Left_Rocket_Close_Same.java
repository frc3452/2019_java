package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_Rocket_Close_Same extends PathContainer {
    public Left_Rocket_Close_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(67.5, 260, 25, 60));
        sWaypoints.add(new Waypoint(149.958, 270.894, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(173.71399489541494, 283.9270093044256, 1, 30)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(197.47, 296.96, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}