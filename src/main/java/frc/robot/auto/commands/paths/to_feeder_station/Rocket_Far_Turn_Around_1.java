package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Rocket_Far_Turn_Around_1 extends PathContainer {
    public Rocket_Far_Turn_Around_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(262, 297, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(283, 285, 15, 30).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(295, 234, 0, 60).setFieldAdaption(PathAdapter.rocketFar));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}