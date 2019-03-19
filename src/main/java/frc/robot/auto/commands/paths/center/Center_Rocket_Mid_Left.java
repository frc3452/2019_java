package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class Center_Rocket_Mid_Left extends PathContainer {
    public Center_Rocket_Mid_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(120, 162, 15, 30));
        sWaypoints.add(new Waypoint(184, 213, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 250.961, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(229.28, 267.0105, 1, 30/*.setFieldAdaption(- Rocket -)*/));
        sWaypoints.add(new Waypoint(229.28, 277.06, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}