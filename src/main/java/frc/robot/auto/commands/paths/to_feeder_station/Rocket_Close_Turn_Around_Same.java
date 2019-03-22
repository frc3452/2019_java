package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class Rocket_Close_Turn_Around_Same extends PathContainer {
    public Rocket_Close_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(192, 294, 0, 30).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(157, 270, 15, 30).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(184, 249, 15, 30).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(214, 243, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}